package org.example.expert.domain.common.dto;

import lombok.Getter;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
public class AuthUser implements UserDetails {

    private final Long id;
    private final String email;
    private final String nickname;
    private final UserRole userRole;
    private final String password;

    // 생성자
    public AuthUser(Long id, String email, String nickname, UserRole userRole, String password) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.userRole = userRole;
        this.password = password;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + userRole.name()));
    }

    @Override
    public String getUsername() {
        return email; // 사용자 이름으로 이메일을 사용
    }

    @Override
    public String getPassword() {
        return password; // 실제 비밀번호를 반환
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
