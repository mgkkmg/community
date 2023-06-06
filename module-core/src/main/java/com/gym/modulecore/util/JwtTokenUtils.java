package com.gym.modulecore.util;

import com.gym.modulecore.core.user.model.dto.TokenInfo;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Slf4j
public class JwtTokenUtils {

    public static String getUserName(String token, String key) {
        return extractClaims(token, key).get("userName", String.class);
    }

    public static boolean isExpired(String token, String key) {
        Date expiredDate = extractClaims(token, key).getExpiration();
        return expiredDate.before(new Date());
    }

    public static Long getExpiration(String token, String key) {
        return extractClaims(token, key).getExpiration().getTime();
    }

    //토큰 정보를 검증하는 메서드
    public static boolean isValidated(String token, String key) {
        try {
            Jwts.parserBuilder().setSigningKey(getKey(key)).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
        }
        return false;
    }

    private static Claims extractClaims(String token, String key) {
        try {
            // JWT를 검증하기 위한 Jwts.parser() 메서드 제공
            // 검증 시에는 JWT 문자열과 비밀키를 전달하여 JWT를 검증
            // JWS(JSON Web Signature)는 JWT의 서명 부분을 담당하는 표준 기술로 JWT가 위조되지 않았음을 검증하기 위해 사용
            // parseClaimsJws()는  JWT의 서명 부분을 검증하여 JWT가 위조되었는지 여부를 확인하고, 검증에 성공하면 JWT의 내용인 Claim(페이로드)을 파싱하여 반환한다.
            return Jwts.parserBuilder().setSigningKey(getKey(key)).build().parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public static TokenInfo generateToken(String userName, String accessKey, String refreshKey, long accessExpiredTimeMs, long refreshExpiredTimeMs) {
        /* JWT의 페이로드 구조
        {
            "sub": "1234567890",
            "name": "John Doe",
            "admin": true
        }
        */
        // 페이로드 구성에 담을 key와 value의 한쌍으로 이루어 진 형태를 클레임이라고 한다.
        // 위에 예시로는 "sub": "1234567890"가 하나의 클레임이다.
        Claims claims = Jwts.claims();
        claims.put("userName", userName);

        // JWT를 생성하기 위한 Jwts.builder() 메서드 제공
        // JWT 생성 시 헤더와 페이로드 정보를 설정하고, 서명에 필요한 알고리즘과 비밀키를 설정
        String accessToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis())) // 토큰 발행 시간
                .setExpiration(new Date(System.currentTimeMillis() + accessExpiredTimeMs)) // 토큰 만료 시간
                .signWith(getKey(accessKey), SignatureAlgorithm.HS256) // 해시 알고리즘(256bits)
                .compact();

        String refreshToken = Jwts.builder()
                .setIssuedAt(new Date(System.currentTimeMillis())) //토큰 발행 시간
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpiredTimeMs)) //토큰 만료 시간 설정
                .signWith(getKey(refreshKey), SignatureAlgorithm.HS256)
                .compact();

        return TokenInfo.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpiredTimeMs(accessExpiredTimeMs)
                .refreshTokenExpiredTimeMs(refreshExpiredTimeMs)
                .userName(userName)
                .build();
    }

    private static Key getKey(String key) {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
