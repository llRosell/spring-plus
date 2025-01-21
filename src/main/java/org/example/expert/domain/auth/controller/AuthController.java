package org.example.expert.domain.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.auth.dto.request.SigninRequest;
import org.example.expert.domain.auth.dto.request.SignupRequest;
import org.example.expert.domain.auth.dto.response.SigninResponse;
import org.example.expert.domain.auth.dto.response.SignupResponse;
import org.example.expert.domain.auth.service.AuthService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/auth/signup")
    public SignupResponse signup(@Valid @RequestBody SignupRequest signupRequest) {
        return authService.signup(signupRequest);
    }

    @PostMapping("/auth/signin")
    public ResponseEntity<SigninResponse> signin(@Valid @RequestBody SigninRequest signinRequest) {
        // 1. 인증 처리 및 토큰 생성
        SigninResponse response = authService.signin(signinRequest);

        // 2. 생성된 JWT 토큰을 응답 헤더에 추가
        String jwtToken = response.getBearerToken(); // SigninResponse에 토큰 포함되어 있다고 가정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtToken);

        // 3. ResponseEntity를 사용해 헤더와 바디 반환
        return ResponseEntity.ok()
                .headers(headers)
                .body(response);
    }
}
