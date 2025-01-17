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

    // UserRepository를 주입 받아 사용자 정보 로드
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 인증할 때 호출되는 메서드
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 사용자 이메일로 찾기 (username은 기본적으로 이메일로 사용)
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));

        // User 엔티티를 기반으로 AuthUser 객체 반환
        return new AuthUser(
                user.getId(),            // id
                user.getEmail(),         // email
                user.getNickname(),      // nickname
                user.getUserRole(),      // userRole
                null                     // 비밀번호는 null로 설정 (권장되는 방식)
        );
    }
}
