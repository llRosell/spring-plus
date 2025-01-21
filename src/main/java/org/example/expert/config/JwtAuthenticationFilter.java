package org.example.expert.config;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.user.enums.UserRole;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    /**
     * 요청에 대한 JWT 인증 필터를 적용하는 메서드입니다.
     *
     * @param request  HTTP 요청
     * @param response HTTP 응답
     * @param filterChain 필터 체인
     * @throws ServletException 요청 처리 중 발생할 수 있는 예외
     * @throws IOException 입출력 오류 발생 시 예외
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = resolveToken(request); // 요청에서 토큰을 추출

        // 토큰이 존재하고 유효한 경우
        if (token != null && jwtUtil.validateToken(token)) {
            try {
                Claims claims = jwtUtil.extractClaims(token); // 토큰에서 클레임 추출

                // Claims에서 userId, email, nickname, userRole을 가져옴
                Long userId = claims.get("userId", Long.class);
                String email = claims.get("email", String.class);
                String nickname = claims.get("nickname", String.class);
                UserRole userRole = UserRole.of(claims.get("userRole", String.class));

                // 비밀번호가 없다면 null을 전달
                String password = null;

                // AuthUser 객체 생성
                AuthUser authUser = new AuthUser(userId, email, nickname, userRole, password);

                // 인증 객체 생성
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(authUser, null, authUser.getAuthorities());

                // SecurityContext에 인증 정보 설정
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {
                log.error("JWT validation failed", e); // JWT 검증 실패 로그 기록
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token"); // 401 오류 응답
                return;
            }
        }

        filterChain.doFilter(request, response); // 필터 체인 계속 진행
    }

    /**
     * 요청에서 JWT 토큰을 추출하는 메서드입니다.
     *
     * @param request HTTP 요청
     * @return 추출된 토큰 또는 null
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization"); // Authorization 헤더에서 토큰 가져오기
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // "Bearer " 이후의 토큰만 반환
        }
        return null; // 토큰이 없으면 null 반환
    }
}
