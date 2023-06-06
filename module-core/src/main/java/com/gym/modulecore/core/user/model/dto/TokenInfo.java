package com.gym.modulecore.core.user.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class TokenInfo {
    private String grantType;
    private String accessToken;
    private String refreshToken;
    private Long accessTokenExpiredTimeMs;
    private Long refreshTokenExpiredTimeMs;
    private String userName;
}
