package com.gym.moduleapi.api.user.controller;

import com.gym.moduleapi.api.user.request.TokenRequest;
import com.gym.moduleapi.api.user.request.UserJoinRequest;
import com.gym.moduleapi.api.user.request.UserLoginRequest;
import com.gym.moduleapi.api.user.response.AlarmResponse;
import com.gym.moduleapi.api.user.response.TokenResponse;
import com.gym.moduleapi.api.user.response.UserJoinResponse;
import com.gym.moduleapi.api.user.response.UserLoginResponse;
import com.gym.modulecore.core.user.model.dto.TokenInfo;
import com.gym.modulecore.core.user.model.dto.User;
import com.gym.modulecore.core.user.service.AlarmService;
import com.gym.modulecore.core.user.service.UserService;
import com.gym.modulecore.resolver.annotation.UserInfo;
import com.gym.modulecore.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@RestController
public class UserController {

    private final UserService userService;
    private final AlarmService alarmService;

    @PostMapping("/join")
    public CommonResponse<UserJoinResponse> join(@RequestBody UserJoinRequest request) {
        User user = userService.join(request.getUserName(), request.getPassword());
        return CommonResponse.success(UserJoinResponse.fromUserJoin(user));
    }

    @PostMapping("/login")
    public CommonResponse<UserLoginResponse> login(@RequestBody UserLoginRequest request) {
        TokenInfo tokenInfo = userService.login(request.getUserName(), request.getPassword());
        return CommonResponse.success(UserLoginResponse.fromUserLogin(tokenInfo));
    }

    @PostMapping("/logout")
    public void logout(@RequestBody TokenRequest request, @UserInfo User user) {
        userService.logout(request.getAccessToken(), user.getUsername());
    }

    @PostMapping("/reissue")
    public CommonResponse<TokenResponse> reissue(@RequestBody TokenRequest request, @UserInfo User user) {
        TokenInfo tokenInfo = userService.reissue(request.getRefreshToken(), user.getUsername());
        return CommonResponse.success(TokenResponse.fromTokenReissue(tokenInfo));
    }

    @GetMapping("/alarm")
    public CommonResponse<Page<AlarmResponse>> alarm(Pageable pageable, @UserInfo User user) {
        return CommonResponse.success(userService.alarmList(user.getId(), pageable).map(AlarmResponse::fromAlarm));
    }

    @GetMapping("/alarm/subscribe")
    public SseEmitter subscribe(@UserInfo User user) {
        return alarmService.connectAlarm(user.getId());
    }
}
