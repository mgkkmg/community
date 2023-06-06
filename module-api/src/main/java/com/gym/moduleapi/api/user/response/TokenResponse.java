package com.gym.moduleapi.api.user.response;

import com.gym.modulecore.core.user.model.dto.TokenInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenResponse {

    private String accessToken;
    private String refreshToken;

    public static TokenResponse fromTokenReissue(TokenInfo tokenInfo) {
        return new TokenResponse(
                tokenInfo.getAccessToken(),
                tokenInfo.getRefreshToken()
        );
    }
}
