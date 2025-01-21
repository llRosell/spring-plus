package org.example.expert.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.config.PasswordEncoder;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.dto.request.UserChangePasswordRequest;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 사용자 ID로 사용자의 정보를 조회하는 메서드입니다.
     *
     * @param userId 조회할 사용자의 ID
     * @return 사용자의 ID와 이메일을 포함한 UserResponse 객체
     * @throws InvalidRequestException 해당 사용자 ID로 사용자를 찾을 수 없는 경우 발생
     */
    public UserResponse getUser(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new InvalidRequestException("User not found"));
        return new UserResponse(user.getId(), user.getEmail());
    }

    /**
     * 사용자의 비밀번호를 변경하는 메서드입니다.
     *
     * @param userId 사용자의 ID
     * @param userChangePasswordRequest 비밀번호 변경 요청 정보를 담고 있는 DTO
     * @throws InvalidRequestException 비밀번호 변경에 필요한 조건을 만족하지 않거나 사용자를 찾을 수 없는 경우 발생
     */
    @Transactional
    public void changePassword(long userId, UserChangePasswordRequest userChangePasswordRequest) {
        // 새 비밀번호의 유효성을 검사합니다.
        validateNewPassword(userChangePasswordRequest);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidRequestException("User not found"));

        // 새 비밀번호가 기존 비밀번호와 같은지 확인합니다.
        if (passwordEncoder.matches(userChangePasswordRequest.getNewPassword(), user.getPassword())) {
            throw new InvalidRequestException("새 비밀번호는 기존 비밀번호와 같을 수 없습니다.");
        }

        // 기존 비밀번호가 맞는지 확인합니다.
        if (!passwordEncoder.matches(userChangePasswordRequest.getOldPassword(), user.getPassword())) {
            throw new InvalidRequestException("잘못된 비밀번호입니다.");
        }

        // 비밀번호를 변경합니다.
        user.changePassword(passwordEncoder.encode(userChangePasswordRequest.getNewPassword()));
    }

    /**
     * 새 비밀번호의 유효성을 검사하는 메서드입니다.
     *
     * @param userChangePasswordRequest 비밀번호 변경 요청 정보를 담고 있는 DTO
     * @throws InvalidRequestException 새 비밀번호가 8자 미만이거나, 숫자와 대문자를 포함하지 않은 경우 발생
     */
    private static void validateNewPassword(UserChangePasswordRequest userChangePasswordRequest) {
        if (userChangePasswordRequest.getNewPassword().length() < 8 ||
                !userChangePasswordRequest.getNewPassword().matches(".*\\d.*") ||
                !userChangePasswordRequest.getNewPassword().matches(".*[A-Z].*")) {
            throw new InvalidRequestException("새 비밀번호는 8자 이상이어야 하고, 숫자와 대문자를 포함해야 합니다.");
        }
    }
}
