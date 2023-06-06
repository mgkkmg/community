package com.gym.modulecore.resolver;

import com.gym.modulecore.core.user.model.dto.User;
import com.gym.modulecore.exception.CommunityException;
import com.gym.modulecore.exception.ErrorCode;
import com.gym.modulecore.resolver.annotation.UserInfo;
import com.gym.modulecore.util.ClassUtils;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // hasParameterAnnotation(UserInfo.class) 메소드를 통해 UserInfo Annotation을 선언한 Parameter 객체만 바인딩 허용
        return parameter.getParameterType().equals(User.class) && parameter.hasParameterAnnotation(UserInfo.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return ClassUtils.getSafeCastInstance(authentication.getPrincipal(), User.class)
                .orElseThrow(() -> new CommunityException(ErrorCode.INTERNAL_SERVER_ERROR, "Casting to User class failed"));
    }
}
