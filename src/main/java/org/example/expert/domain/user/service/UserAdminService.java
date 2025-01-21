package org.example.expert.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.dto.request.UserRoleChangeRequest;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserAdminService {

    private final UserRepository userRepository;

    /**
     * 사용자의 역할을 변경하는 메서드입니다.
     *
     * @param userId 사용자의 ID
     * @param userRoleChangeRequest 역할 변경 요청 정보를 담고 있는 DTO
     * @throws InvalidRequestException 해당 사용자 ID로 사용자를 찾을 수 없는 경우 발생
     */
    @Transactional
    public void changeUserRole(long userId, UserRoleChangeRequest userRoleChangeRequest) {
        // 사용자 정보를 데이터베이스에서 찾습니다. 사용자가 존재하지 않을 경우 예외를 발생시킵니다.
        User user = userRepository.findById(userId).orElseThrow(() -> new InvalidRequestException("User not found"));

        // 요청된 역할로 사용자 역할을 업데이트합니다.
        user.updateRole(UserRole.of(userRoleChangeRequest.getRole()));
    }
}
