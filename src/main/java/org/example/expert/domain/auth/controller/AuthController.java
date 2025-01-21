package org.example.expert.domain.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.auth.dto.request.SigninRequest;
import org.example.expert.domain.auth.dto.request.SignupRequest;
import org.example.expert.domain.auth.dto.response.SigninResponse;
import org.example.expert.domain.auth.dto.response.SignupResponse;
import org.example.expert.domain.auth.service.AuthService;
import org.example.expert.domain.common.dto.AuthUser;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;  // 인증 서비스

    /**
     * 사용자 회원가입 요청을 처리하는 메서드입니다.
     *
     * @param signupRequest 사용자 회원가입 정보
     * @return SignupResponse 회원가입 결과
     */
    @PostMapping("/auth/signup")
    public SignupResponse signup(@Valid @RequestBody SignupRequest signupRequest) {
        return authService.signup(signupRequest);  // 회원가입 서비스 호출
    }

    /**
     * 사용자 로그인 요청을 처리하는 메서드입니다.
     *
     * @param signinRequest 사용자 로그인 정보
     * @return ResponseEntity<SigninResponse> 로그인 결과와 JWT 토큰을 포함한 응답
     */
    @PostMapping("/auth/signin")
    public ResponseEntity<SigninResponse> signin(@Valid @RequestBody SigninRequest signinRequest) {
        // 1. 인증 처리 및 토큰 생성
        SigninResponse response = authService.signin(signinRequest);

        // 2. 생성된 JWT 토큰을 응답 헤더에 추가
        String jwtToken = response.getBearerToken(); // SigninResponse에 포함된 토큰 가져오기
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtToken);  // Authorization 헤더 설정

        // 3. AuthUser 객체 생성 (userService를 통해 User 정보 가져오기)
        AuthUser authUser = authService.getAuthUserFromSigninRequest(signinRequest);

        // 4. SecurityContext에 Authentication 설정
        Authentication authentication = new UsernamePasswordAuthenticationToken(authUser, null, authUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);  // SecurityContext에 인증 정보 저장

        // 5. ResponseEntity를 사용해 헤더와 바디 반환
        return ResponseEntity.ok()
                .headers(headers)  // 설정된 헤더 추가
                .body(response);  // 로그인 결과 반환
    }
}
