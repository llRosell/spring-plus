package org.example.expert.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
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

    private static final String BEARER_PREFIX = "Bearer ";
    private static final long TOKEN_TIME = 60 * 60 * 1000L; // 60 minutes

    @Value("${jwt.secret.key}")
    private String secretKey; // JWT 서명에 사용될 비밀 키
    private Key key; // 서명에 사용할 Key 객체
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256; // 사용될 서명 알고리즘

    /**
     * JWT Util 초기화 메서드.
     * 비밀 키를 Base64로 디코딩하여 Key 객체를 생성합니다.
     */
    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes); // 비밀 키를 HMAC SHA-256 알고리즘에 맞게 설정
    }

    /**
     * 사용자 정보를 기반으로 JWT 토큰을 생성하는 메서드입니다.
     *
     * @param userId 사용자 ID
     * @param email 사용자 이메일
     * @param nickname 사용자 닉네임
     * @param userRole 사용자 역할
     * @return 생성된 JWT 토큰
     */
    public String createToken(Long userId, String email, String nickname, UserRole userRole) {
        Date date = new Date();

        return BEARER_PREFIX +
                Jwts.builder()
                        .setSubject(String.valueOf(userId)) // 토큰의 주제(subject) 설정
                        .claim("email", email) // 이메일 클레임 추가
                        .claim("nickname", nickname) // 닉네임 클레임 추가
                        .claim("userRole", userRole) // 사용자 역할 클레임 추가
                        .setExpiration(new Date(date.getTime() + TOKEN_TIME)) // 만료 시간 설정
                        .setIssuedAt(date) // 발급 시간 설정
                        .signWith(key, signatureAlgorithm) // 서명
                        .compact(); // 토큰 생성
    }

    /**
     * 인증 정보를 기반으로 JWT 토큰을 생성하는 메서드입니다.
     *
     * @param authentication Spring Security 인증 정보
     * @return 생성된 JWT 토큰
     */
    public String generateToken(Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName()); // 사용자 ID 추출 (사용자 이름이 ID라고 가정)
        String email = ""; // 이메일 추출 로직 구현 필요
        String nickname = ""; // 닉네임 추출 로직 구현 필요
        UserRole userRole = UserRole.valueOf(authentication.getAuthorities().stream()
                .findFirst()
                .map(authority -> authority.getAuthority())
                .orElse("USER")); // 기본 역할 USER로 설정

        return createToken(userId, email, nickname, userRole); // 토큰 생성
    }

    /**
     * 요청에서 JWT 토큰을 추출하는 메서드입니다.
     *
     * @param tokenValue 요청에서 받은 토큰 값
     * @return 추출된 토큰
     * @throws ServerException 토큰이 존재하지 않는 경우 예외 발생
     */
    public String substringToken(String tokenValue) {
        if (StringUtils.hasText(tokenValue) && tokenValue.startsWith(BEARER_PREFIX)) {
            return tokenValue.substring(7); // "Bearer " 이후의 토큰만 반환
        }
        throw new ServerException("Not Found Token"); // 토큰이 없으면 예외 발생
    }

    /**
     * JWT 토큰에서 클레임을 추출하는 메서드입니다.
     *
     * @param token JWT 토큰
     * @return 토큰에서 추출된 클레임
     */
    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key) // 서명 키 설정
                .build()
                .parseClaimsJws(token) // 토큰 파싱
                .getBody(); // 클레임 반환
    }

    /**
     * JWT 토큰의 유효성을 검증하는 메서드입니다.
     *
     * @param token JWT 토큰
     * @return 토큰이 유효하면 true, 그렇지 않으면 false
     */
    public boolean validateToken(String token) {
        try {
            Claims claims = extractClaims(token); // 클레임 추출
            return !claims.getExpiration().before(new Date()); // 만료일이 현재 날짜 이후인 경우 true 반환
        } catch (Exception e) {
            return false; // 유효하지 않은 토큰인 경우 false 반환
        }
    }
}
