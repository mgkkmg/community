package com.gym.modulecore.config.security.filter;

import com.gym.modulecore.core.user.model.dto.User;
import com.gym.modulecore.core.user.repository.LogoutTokenRepository;
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
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final String accessKey;
    private final UserService userService;
    private final LogoutTokenRepository logoutTokenRepository;

    private final static List<String> TOKEN_IN_PARAM_URLS = List.of("/api/v1/users/alarm/subscribe");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String token;

        try {
            if (TOKEN_IN_PARAM_URLS.contains(request.getRequestURI())) {
                log.info("Request with {} check the query param", request.getRequestURI());
                token = request.getQueryString().split("=")[1].trim();
            } else {
                // get header
                final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
                if (header == null || !header.startsWith("Bearer ")) {
                    log.info("Header is null or invalid {}", request.getRequestURL());
                    filterChain.doFilter(request, response);  // filterChain은 다음 필터를 가리키고 filterChain.doFilter()는 다음 필터를 호출한다.
                    return;
                }
                token = header.split(" ")[1].trim();
            }

            // AccessToken으로 Redis에서 logout 여부를 확인
            if (logoutTokenRepository.isLogoutToken(token)) {
                log.info("Tokens expired on logout");
                filterChain.doFilter(request, response);
                return;
            }

            // check token is valid
            // Access token 만료 시 클라이언트에서 Refresh token 을 사용하여 토큰 재발급
            if (JwtTokenUtils.isExpired(token, accessKey)) {
                log.info("Key is expired");
                filterChain.doFilter(request, response);
                return;
            }

//            if (!JwtTokenUtils.isValidated(token, key)) {
//                log.error("Token is invalid");
//                filterChain.doFilter(request, response);
//                return;
//            }

            // get userName from token
            String userName = JwtTokenUtils.getUserName(token, accessKey);

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
