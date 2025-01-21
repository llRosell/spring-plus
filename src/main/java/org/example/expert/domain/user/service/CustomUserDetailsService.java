package org.example.expert.domain.user.service;

import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.repository.UserRepository;
import org.example.expert.domain.common.dto.AuthUser;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * UserRepository를 주입 받아 사용자 정보를 로드하기 위한 서비스 클래스 생성자입니다.
     *
     * @param userRepository 사용자 정보를 관리하는 레포지토리
     */
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 사용자 인증 시 호출되는 메서드로, 사용자 이메일을 기반으로 사용자 정보를 로드합니다.
     *
     * @param username 사용자 이메일 (username으로 사용)
     * @return 인증된 사용자 정보를 담은 UserDetails 객체
     * @throws UsernameNotFoundException 사용자 정보를 찾을 수 없는 경우 발생
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 사용자 이메일로 찾기 (username은 기본적으로 이메일로 사용)
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));

        // 확인을 위한 로그 추가
        System.out.println("User found: " + user);
        System.out.println("User ID: " + user.getId());

        // User 엔티티를 기반으로 AuthUser 객체 반환
        return new AuthUser(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getUserRole(),
                user.getPassword()
        );
    }
}
