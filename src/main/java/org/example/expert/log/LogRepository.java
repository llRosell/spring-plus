package org.example.expert.log;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LogRepository extends JpaRepository<Log, Long> {
    // 추가적인 메서드가 필요하면 여기에 정의
}
