package com.gym.modulecore.config.security.filter;

import com.gym.modulecore.core.user.model.User;
import com.gym.modulecore.core.user.service.UserService;
import com.gym.modulecore.util.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final String key;
    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // get header
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer ")) {
            log.error("Error occurs while getting header. Header is null or invalid {}", request.getRequestURL());
            filterChain.doFilter(request, response);  // filterChain은 다음 필터를 가리키고 filterChain.doFilter()는 다음 필터를 호출한다.
            return;
        }

        try {
            final String token = header.split(" ")[1].trim();

            // check token is valid
            if (JwtTokenUtils.isExpired(token, key)) {
                log.error("Key is expired");
                filterChain.doFilter(request, response);
                return;
            }

            // get userName from token
            String userName = JwtTokenUtils.getUserName(token, key);

            // check the user is valid
            User user = userService.loadUserByUserName(userName);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            // Authentication: 접근 주체의 정보와 권한을 담는 인터페이스
            // SecurityContext: Authentication 객체가 보관되는 저장소 역할. 필요시 SecurityContext로부터 Authentication 객체를 꺼내서 사용할 수 있다.
            // SecurityContextHolder: SecurityContext 객체를 보관하고 있는 wrapper 클래스
            // 아래와 같이 설정하여 @Controller의 메서드에서 request 매개변수로 입력받을 수 있다.
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (RuntimeException e) {
            log.error("Error occurs while validating. {}", e.toString());
            filterChain.doFilter(request, response);
            return;
        }

        filterChain.doFilter(request, response);
    }
}
