package org.example.expert.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)  // 메서드 수준 보안 활성화
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;  // JWT 인증 필터

    /**
     * 요청에 대한 권한을 설정하는 메서드입니다.
     *
     * @param auth 요청 매처 등록을 위한 인증 매니저
     */
    private static void customize(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth) {
        auth
                .requestMatchers("/auth/signup", "/auth/signin").permitAll()  // 회원가입 및 로그인 경로는 인증 없이 접근 허용
                .requestMatchers("/admin/**").hasRole("ADMIN")  // /admin/** 경로는 ADMIN 권한을 가진 사용자만 접근 가능
                .requestMatchers("/users/**", "/todos/**").hasAnyRole("ADMIN", "USER")  // /users/** 및 /todos/** 경로는 ADMIN 또는 USER 권한을 가진 사용자 접근 가능
                .anyRequest().authenticated();  // 그 외의 모든 요청은 인증 필요
    }

    /**
     * BCryptPasswordEncoder를 빈으로 등록하는 메서드입니다.
     *
     * @return BCryptPasswordEncoder 객체
     */
    @Bean(name = "securityPasswordEncoder")
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();  // BCrypt 알고리즘을 사용하여 비밀번호를 인코딩하는 인코더 반환
    }

    /**
     * HTTP 보안을 구성하는 메서드입니다.
     *
     * @param http HttpSecurity 객체
     * @return SecurityFilterChain 객체
     * @throws Exception 보안 구성 중 발생할 수 있는 예외
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)  // CSRF 보호 비활성화
                .httpBasic(AbstractHttpConfigurer::disable) // BasicAuthenticationFilter 비활성화
                .formLogin(AbstractHttpConfigurer::disable) // 폼 로그인 비활성화
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // JWT 인증 필터를 UsernamePasswordAuthenticationFilter 앞에 추가
                .authorizeHttpRequests(SecurityConfig::customize)  // 요청에 대한 권한 설정 호출
                .build();  // SecurityFilterChain 빌드 및 반환
    }
}
