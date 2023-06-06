package com.gym.modulecore.config.security;

import com.gym.modulecore.config.security.filter.JwtTokenFilter;
import com.gym.modulecore.core.user.service.UserService;
import com.gym.modulecore.exception.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class AuthenticationConfig {

    private final UserService userService;

    @Value("${jwt.token.access.secret-key}")
    private String accessKey;

    @Value("${jwt.token.access.secret-key}")
    private String refreshKey;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .regexMatchers("^(?!/api/).*") // /api/로 시작하지 않는 모든 문자열을 일치
                .antMatchers(HttpMethod.POST, "/api/*/users/join", "/api/*/users/login", "/api/*/users/reissue");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
                .authorizeRequests(authorize -> authorize
//                        .regexMatchers("^(?!/api/).*").permitAll() // /api/로 시작하지 않는 모든 문자열을 일치
//                        .antMatchers(HttpMethod.POST, "/api/*/users/join", "/api/*/users/login").permitAll()
                        .antMatchers("/api/**").authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(new JwtTokenFilter(accessKey, userService), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(new CustomAuthenticationEntryPoint()) // 인증되지 않은 사용자의 리소스에 대한 접근 처리
                )
                .build();
    }
}
