package org.example.expert.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.example.expert.domain.common.exception.ServerException;
import org.example.expert.domain.user.enums.UserRole;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Slf4j(topic = "JwtUtil")
@Component
public class JwtUtil {
    // JWT 토큰의 접두사
    public static final String BEARER_PREFIX = "Bearer ";
    // JWT 토큰의 만료 시간 (밀리초 단위, 여기서는 60분)
    private final long TOKEN_TIME = 60 * 60 * 1000L; // 60분
    // JWT 서명 알고리즘
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
    // 애플리케이션 설정 파일에서 주입받은 비밀 키
    @Value("${jwt.secret.key}")
    private String secretKey;
    // 실제 서명에 사용되는 키 객체
    private Key key;

    /**
     * 빈 초기화 메서드
     * - 애플리케이션 시작 시 비밀 키를 Base64로 디코딩하여 Key 객체를 초기화합니다.
     */
    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    /**
     * JWT 토큰에서 사용자 이름을 추출합니다.
     * @param token JWT 토큰
     * @return 사용자 이름
     */
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

// 어떻게 동작하는지 디버깅 모드로 한줄씩 찍어보기
    // 아이유 권한으로 User 전용 API를 호출할 때

    /**
     * JWT 토큰에서 모든 클레임을 추출합니다.
     * @param token JWT 토큰
     * @return 클레임 객체
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(key) // 비밀 키를 사용하여 서명 검증
                .parseClaimsJws(token)
                .getBody();
    }

    public String generateToken(Long userId, String email, String nickname, UserRole userRole) {
        Date now = new Date();

        return BEARER_PREFIX +
                Jwts.builder()
                        .setSubject(String.valueOf(userId)) // 사용자 ID를 Subject에 설정
                        .claim("email", email) // 추가 정보: 이메일
                        .claim("nickname", nickname) // 추가 정보: 닉네임
                        .claim("auth", userRole.name()) // 권한 (문자열로 변환)
                        .setExpiration(new Date(now.getTime() + TOKEN_TIME)) // 만료 시간
                        .setIssuedAt(now) // 발급 시간
                        .signWith(key, signatureAlgorithm) // 서명 설정
                        .compact(); // 토큰 생성
    }

    // 이 문자열은 JWT야 약속이 되어 있음.

    /**
     * JWT 토큰에서 역할(권한) 정보를 추출합니다.
     * @param token JWT 토큰
     * @return 역할 정보 (문자열)
     */
    public String extractRoles(String token) {
        return extractAllClaims(token).get("auth", String.class);
    }

    /**
     * JWT 토큰에서 닉네임을 추출합니다.
     * @param token JWT 토큰
     * @return 닉네임
     */
    public String extractNickname(String token) {
        return extractAllClaims(token).get("nickname", String.class);
    }

    /**
     * JWT 토큰에서 특정 역할이 포함되어 있는지 확인합니다.
     * @param token JWT 토큰
     * @param role 확인할 역할
     * @return 역할 포함 여부 (true: 포함됨, false: 포함되지 않음)
     */
    public boolean hasRole(String token, String role) {
        String roles = extractRoles(token);
        return roles.contains(role);
    }

    /**
     * JWT 토큰의 유효성을 검증합니다.
     *
     * @param token 검증할 JWT 토큰
     * @return 토큰의 유효성 여부 (true: 유효함, false: 유효하지 않음)
     */
    public boolean validateToken(String token) {
        try {
            // JWT 파서 빌더를 사용하여 토큰의 서명을 검증합니다.
            // setSigningKey() 메서드를 사용하여 서명 검증에 사용할 비밀 키를 설정합니다.
            // parseClaimsJws() 메서드를 사용하여 토큰의 클레임을 파싱하고 서명을 검증합니다.
            Jwts.parserBuilder()
                    .setSigningKey(key) // 비밀 키 설정
                    .build() // 파서 빌더 빌드
                    .parseClaimsJws(token); // 토큰 파싱 및 검증
            return true; // 토큰이 유효한 경우
        } catch (SecurityException | MalformedJwtException | SignatureException e) {
            // 토큰 서명이 잘못되었거나, 잘못된 형식의 JWT가 전달된 경우
            log.error("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.", e);
        } catch (ExpiredJwtException e) {
            // 토큰이 만료된 경우
            log.error("Expired JWT token, 만료된 JWT token 입니다.", e);
        } catch (UnsupportedJwtException e) {
            // 지원되지 않는 JWT 형식이 전달된 경우
            log.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.", e);
        } catch (IllegalArgumentException e) {
            // JWT 클레임이 비어 있거나 잘못된 형식일 경우
            log.error("JWT claims is empty, 잘못된 JWT 토큰 입니다.", e);
        }
        return false; // 예외가 발생한 경우 토큰이 유효하지 않음
    }
}