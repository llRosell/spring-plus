package org.example.expert.config;

import at.favre.lib.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

@Component
public class PasswordEncoder {

    /**
     * 비밀번호를 해시하여 인코딩하는 메서드입니다.
     *
     * @param rawPassword 인코딩할 원본 비밀번호
     * @return 인코딩된 비밀번호(해시 값)
     */
    public String encode(String rawPassword) {
        return BCrypt.withDefaults().hashToString(BCrypt.MIN_COST, rawPassword.toCharArray()); // 비밀번호를 해시하여 문자열로 반환
    }

    /**
     * 원본 비밀번호와 인코딩된 비밀번호가 일치하는지 검증하는 메서드입니다.
     *
     * @param rawPassword 원본 비밀번호
     * @param encodedPassword 인코딩된 비밀번호(해시 값)
     * @return 비밀번호가 일치하면 true, 그렇지 않으면 false
     */
    public boolean matches(String rawPassword, String encodedPassword) {
        BCrypt.Result result = BCrypt.verifyer().verify(rawPassword.toCharArray(), encodedPassword); // 비밀번호 검증
        return result.verified; // 검증 결과 반환
    }
}
