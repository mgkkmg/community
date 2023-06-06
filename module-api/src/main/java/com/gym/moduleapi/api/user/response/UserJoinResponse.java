package com.gym.moduleapi.api.user.response;

import com.gym.modulecore.core.user.model.dto.User;
import com.gym.modulecore.core.user.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserJoinResponse {

    private Long id;
    private String userName;
    private UserRole userRole;

    public static UserJoinResponse fromUserJoin(User user) {
        return new UserJoinResponse(
                user.getId(),
                user.getUsername(),
                user.getUserRole()
        );
    }
}
