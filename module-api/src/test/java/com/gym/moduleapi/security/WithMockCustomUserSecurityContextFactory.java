package com.gym.moduleapi.security;

import com.gym.moduleapi.security.annotation.WithMockCustomUser;
import com.gym.modulecore.core.user.model.dto.User;
import com.gym.modulecore.core.user.model.entity.UserEntity;
import fixture.UserEntityFixture;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {
    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser annotation) {
        UserEntity userEntity = UserEntityFixture.get("userName", "password", 1L);

        User user = User.fromEntity(userEntity);
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        return context;
    }
}
