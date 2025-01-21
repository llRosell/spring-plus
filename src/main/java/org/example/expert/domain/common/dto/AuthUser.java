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

    private final Long id;  // 사용자 ID
    private final String email;  // 사용자 이메일
    private final String nickname;  // 사용자 닉네임
    private final UserRole userRole;  // 사용자 역할
    private final String password;  // 사용자 비밀번호

    // 생성자
    public AuthUser(Long id, String email, String nickname, UserRole userRole, String password) {
        this.id = id;  // 사용자 ID 초기화
        this.email = email;  // 사용자 이메일 초기화
        this.nickname = nickname;  // 사용자 닉네임 초기화
        this.userRole = userRole;  // 사용자 역할 초기화
        this.password = password;  // 사용자 비밀번호 초기화
    }

    /**
     * 사용자 권한을 반환하는 메서드입니다.
     *
     * @return Collection<? extends GrantedAuthority> 사용자 권한 목록
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + userRole.name()));  // 사용자 역할에 따른 권한 반환
    }

    /**
     * 사용자 이름을 반환하는 메서드입니다.
     *
     * @return String 사용자 이름 (이메일 사용)
     */
    @Override
    public String getUsername() {
        return email; // 사용자 이름으로 이메일을 사용
    }

    /**
     * 사용자 비밀번호를 반환하는 메서드입니다.
     *
     * @return String 사용자 비밀번호
     */
    @Override
    public String getPassword() {
        return password; // 실제 비밀번호를 반환
    }

    /**
     * 계정이 만료되지 않았는지 확인하는 메서드입니다.
     *
     * @return boolean 계정 만료 여부 (true: 만료되지 않음)
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;  // 계정은 만료되지 않음
    }

    /**
     * 계정이 잠기지 않았는지 확인하는 메서드입니다.
     *
     * @return boolean 계정 잠김 여부 (true: 잠기지 않음)
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;  // 계정은 잠기지 않음
    }

    /**
     * 비밀번호가 만료되지 않았는지 확인하는 메서드입니다.
     *
     * @return boolean 비밀번호 만료 여부 (true: 만료되지 않음)
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;  // 비밀번호는 만료되지 않음
    }

    /**
     * 계정이 활성화되어 있는지 확인하는 메서드입니다.
     *
     * @return boolean 계정 활성화 여부 (true: 활성화)
     */
    @Override
    public boolean isEnabled() {
        return true;  // 계정은 활성화되어 있음
    }
}
