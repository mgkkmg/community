package com.gym.moduleapi.api.user.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenRequest {

    private String accessToken;
    private String refreshToken;
}
