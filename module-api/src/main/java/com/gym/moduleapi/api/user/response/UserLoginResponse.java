package com.gym.moduleapi.api.user.response;

import com.gym.modulecore.core.user.model.dto.TokenInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserLoginResponse {

    private String accessToken;
    private String refreshToken;

    public static UserLoginResponse fromUserLogin(TokenInfo tokenInfo) {
        return new UserLoginResponse(
                tokenInfo.getAccessToken(),
                tokenInfo.getRefreshToken()
        );
    }
}
