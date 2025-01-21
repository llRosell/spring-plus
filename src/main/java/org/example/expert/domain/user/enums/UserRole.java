package org.example.expert.domain.user.enums;

import org.example.expert.domain.common.exception.InvalidRequestException;

import java.util.Arrays;

public enum UserRole {
    ADMIN, USER;

    /**
     * 주어진 문자열에 해당하는 UserRole을 반환합니다.
     *
     * @param role 사용자 역할을 나타내는 문자열 (예: "ADMIN", "USER")
     * @return 해당하는 UserRole 열거형 값
     * @throws InvalidRequestException 주어진 문자열이 유효한 UserRole과 일치하지 않을 경우 발생
     */
    public static UserRole of(String role) {
        return Arrays.stream(UserRole.values())
                .filter(r -> r.name().equalsIgnoreCase(role))
                .findFirst()
                .orElseThrow(() -> new InvalidRequestException("유효하지 않은 UerRole"));
    }
}
