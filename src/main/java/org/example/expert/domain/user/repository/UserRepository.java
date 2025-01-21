package org.example.expert.domain.user.repository;

import org.example.expert.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 주어진 이메일로 사용자 정보를 조회합니다.
     *
     * @param email 사용자의 이메일 주소
     * @return 이메일에 해당하는 사용자 정보가 존재할 경우 Optional<User>로 반환, 없으면 Optional.empty() 반환
     */
    Optional<User> findByEmail(String email);

    /**
     * 주어진 이메일로 사용자가 존재하는지 여부를 확인합니다.
     *
     * @param email 사용자의 이메일 주소
     * @return 이메일에 해당하는 사용자가 존재하면 true, 존재하지 않으면 false 반환
     */
    boolean existsByEmail(String email);
}
