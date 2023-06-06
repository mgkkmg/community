package com.gym.modulecore.core.user.service;

import com.gym.modulecore.core.user.model.dto.Alarm;
import com.gym.modulecore.core.user.model.dto.TokenInfo;
import com.gym.modulecore.core.user.model.dto.User;
import com.gym.modulecore.core.user.model.entity.UserEntity;
import com.gym.modulecore.core.user.repository.*;
import com.gym.modulecore.exception.CommunityException;
import com.gym.modulecore.exception.ErrorCode;
import com.gym.modulecore.util.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserEntityRepository userEntityRepository;
    private final AlarmEntityRepository alarmEntityRepository;
    private final BCryptPasswordEncoder encoder;
    private final UserCacheRepository userCacheRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final LogoutTokenRepository logoutTokenRepository;

    @Value("${jwt.token.access.secret-key}")
    private String accessKey;

    @Value("${jwt.token.refresh.secret-key}")
    private String refreshKey;

    @Value("${jwt.token.access.expired-time-ms}")
    private Long accessExpiredTimeMs;

    @Value("${jwt.token.refresh.expired-time-ms}")
    private Long refreshExpiredTimeMs;

    public User loadUserByUserName(String userName) {
        return userCacheRepository.getUser(userName).orElseGet(() ->
                userEntityRepository.findByUserName(userName)
                        .map(User::fromEntity)
                        .orElseThrow(() -> new CommunityException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", userName)))
        );
    }

    @Transactional
    public User join(String userName, String password) {
        // 회원가입하려는 userName으로 회원가입된 user가 있는지
        userEntityRepository.findByUserName(userName).ifPresent(it -> {
            throw new CommunityException(ErrorCode.DUPLICATED_USER_NAME, String.format("%s is duplicated", userName));
        });

        // 회원가입 진행 = user를 등록
        UserEntity userEntity = userEntityRepository.save(UserEntity.of(userName, encoder.encode(password)));
        return User.fromEntity(userEntity);
    }

    public TokenInfo login(String userName, String password) {
        // 회원가입 여부 체크
        User user = loadUserByUserName(userName);
        userCacheRepository.setUser(user);

        // 비밀번호 체크
        if (!encoder.matches(password, user.getPassword())) {
            throw new CommunityException(ErrorCode.INVALID_PASSWORD);
        }

        // Auth 토큰 생성
        TokenInfo tokenInfo = JwtTokenUtils.generateToken(userName, accessKey, refreshKey, accessExpiredTimeMs, refreshExpiredTimeMs);

        // Refresh 토큰을 레디스에 저장
        refreshTokenRepository.setRefreshToken(tokenInfo);

        return tokenInfo;
    }

    public void logout(String accessToken, String userName) {
        // 로그아웃 하고 싶은 토큰 유효성 검사
        if (!JwtTokenUtils.isValidated(accessToken, accessKey)) {
            throw new CommunityException(ErrorCode.INVALID_TOKEN, "Access token is invalid.");
        }

        // Refresh 토큰을 레디스에서 조회
        TokenInfo tokenInfoOfRedis = refreshTokenRepository.getRefreshToken(userName)
                .orElseThrow(() -> new CommunityException(ErrorCode.TOKEN_NOT_FOUND, "Refresh token was not found."));
        // Refresh 토큰을 레디스에서 삭제
        refreshTokenRepository.deleteRefreshToken(tokenInfoOfRedis.getUserName());

        // 해당 Access Token 만료시간을 가지고 와서 BlackList 등록
        Long accessTokenExpiration = JwtTokenUtils.getExpiration(accessToken, accessKey);
        logoutTokenRepository.setLogoutToken(accessToken, accessTokenExpiration);
    }

    public TokenInfo reissue(String refreshToken, String userName) {
        // Refresh 토큰 유효성 검사
        if (!JwtTokenUtils.isValidated(refreshToken, refreshKey)) {
            throw new CommunityException(ErrorCode.INVALID_TOKEN, "Refresh token is invalid.");
        }

        // Refresh 토큰을 레디스에서 조회
        TokenInfo tokenInfoOfRedis = refreshTokenRepository.getRefreshToken(userName)
                .orElseThrow(() -> new CommunityException(ErrorCode.TOKEN_NOT_FOUND, "Refresh token was not found."));

        // 레디스의 등록된 Refresh 토큰과 클라이언트에서 보낸 Refresh 토큰을 비교
        if (!refreshToken.equals(tokenInfoOfRedis.getRefreshToken())) {
            throw new CommunityException(ErrorCode.TOKEN_NOT_FOUND, "No matching Refresh Token exists.");
        }

        // Access 토큰과 Refresh 토큰 신규 발급 (RTR: Refresh Token Rotation)
        TokenInfo newTokenInfo = JwtTokenUtils.generateToken(userName, accessKey, refreshKey, accessExpiredTimeMs, refreshExpiredTimeMs);
        // 신규 발급 Refresh 토큰을 레디스에 업데이트
        refreshTokenRepository.setRefreshToken(newTokenInfo);

        return newTokenInfo;
    }

    public Page<Alarm> alarmList(Long userId, Pageable pageable) {
        return alarmEntityRepository.findAllByUserId(userId, pageable).map(Alarm::fromEntity);
    }
}
