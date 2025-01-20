package org.example.expert.domain.user.service;

import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.repository.UserRepository;
import org.example.expert.domain.common.dto.AuthUser;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetails implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetails(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));

        return new AuthUser(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getUserRole(),
                user.getPassword() // 비밀번호는 가져오되, AuthUser에서 활용할 수 있도록 반환
        );
    }
}
