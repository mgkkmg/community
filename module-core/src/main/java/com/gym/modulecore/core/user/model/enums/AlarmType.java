package com.gym.modulecore.core.user.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AlarmType {
    NEW_COMMENT_ON_POST("new comment"),
    NEW_LIKE_ON_POST("new like");

    private final String alarmText;
}
