package org.example.expert.domain.auth.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.config.JwtUtil;
import org.example.expert.config.PasswordEncoder;
import org.example.expert.domain.auth.dto.request.SigninRequest;
import org.example.expert.domain.auth.dto.request.SignupRequest;
import org.example.expert.domain.auth.dto.response.SigninResponse;
import org.example.expert.domain.auth.dto.response.SignupResponse;
import org.example.expert.domain.auth.exception.AuthException;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;  // 사용자 데이터 접근을 위한 레포지토리
    private final PasswordEncoder passwordEncoder; // 비밀번호 암호화를 위한 인코더
    private final JwtUtil jwtUtil; // JWT 토큰 생성을 위한 유틸리티

    /**
     * 사용자의 회원가입 요청을 처리하는 메서드입니다.
     *
     * @param signupRequest 회원가입 요청 데이터
     * @return SignupResponse 생성된 JWT 토큰을 포함한 응답
     */
    @Transactional
    public SignupResponse signup(SignupRequest signupRequest) {
        // 이메일 중복 확인
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new InvalidRequestException("이미 존재하는 이메일입니다.");
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(signupRequest.getPassword());

        // 사용자 역할 설정
        UserRole userRole = UserRole.of(signupRequest.getUserRole());

        // 새로운 사용자 객체 생성
        User newUser = new User(
                signupRequest.getEmail(),
                encodedPassword,
                signupRequest.getNickname(),
                userRole
        );

        // 사용자 저장
        User savedUser = userRepository.save(newUser);

        // JWT 토큰 생성
        String bearerToken = jwtUtil.createToken(savedUser.getId(), savedUser.getEmail(), savedUser.getNickname(), userRole);

        // 응답 객체 반환
        return new SignupResponse(bearerToken);
    }

    /**
     * 사용자의 로그인 요청을 처리하는 메서드입니다.
     *
     * @param signinRequest 로그인 요청 데이터
     * @return SigninResponse 생성된 JWT 토큰과 사용자 닉네임을 포함한 응답
     */
    public SigninResponse signin(SigninRequest signinRequest) {
        // 사용자 인증 처리
        User user = validateUser(signinRequest);

        // JWT 토큰 생성
        String token = jwtUtil.createToken(user.getId(), user.getEmail(), user.getNickname(), user.getUserRole());

        // 응답 객체에 토큰과 닉네임 포함
        return new SigninResponse(token, user.getNickname());
    }

    /**
     * 사용자의 유효성을 검증하는 메서드입니다.
     *
     * @param request 로그인 요청 데이터
     * @return User 유효한 사용자 객체
     * @throws IllegalArgumentException 이메일 또는 비밀번호가 유효하지 않을 경우
     */
    private User validateUser(SigninRequest request) {
        // 이메일로 사용자 검색
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        // 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        return user;
    }

    /**
     * 로그인 요청으로부터 인증된 사용자 정보를 반환하는 메서드입니다.
     *
     * @param signinRequest 로그인 요청 데이터
     * @return AuthUser 인증된 사용자 정보
     * @throws InvalidRequestException 사용자가 존재하지 않을 경우
     */
    public AuthUser getAuthUserFromSigninRequest(SigninRequest signinRequest) {
        // 로그인된 사용자 정보를 반환하는 로직 구현
        User user = userRepository.findByEmail(signinRequest.getEmail())
                .orElseThrow(() -> new InvalidRequestException("User not found"));

        return new AuthUser(user.getId(), user.getEmail(), user.getNickname(), user.getUserRole(), user.getPassword());
    }
}
