package org.example.expert.domain.manager.repository;

import org.example.expert.domain.manager.entity.Log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogRepository extends JpaRepository<Log, Long> {
}
