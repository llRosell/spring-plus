package org.example.expert.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.expert.domain.user.enums.UserRole;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

@Slf4j(topic = "JwtFilter")
@RequiredArgsConstructor
@Component
public class JwtFilter implements Filter {

    private final JwtUtil jwtUtil;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        //
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String requestURI = httpRequest.getRequestURI();
        String username = null;
        String jwt = null;

        String authorizationHeader = httpRequest.getHeader("Authorization");

        if(requestURI.equals("/auth/login")) {
            chain.doFilter(request,response);
            return;
        }

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            log.info("JWT 토큰이 필요 합니다.");
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT 토큰이 필요 합니다.");
            return;
        }

        jwt = authorizationHeader.substring(7);

        if (!jwtUtil.validateToken(jwt)) {
            httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            httpResponse.getWriter().write("{\"error\": \"Unauthorized\"}");
        }

        // 사용자 이름과 역할 추출
        username = jwtUtil.extractUsername(jwt);
        String authRole = jwtUtil.extractRoles(jwt); // 역할 추출
        String nickname = jwtUtil.extractNickname(jwt); // 닉네임 추출 (추가한 부분)

        // 역할이 null인 경우 예외 처리
        if (authRole == null) {
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid role in token");
            return;
        }

        UserRole userRole = UserRole.valueOf(authRole);
        User user = new User(username, "", List.of(userRole::getRole));

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));

        chain.doFilter(request, response);

    }
}