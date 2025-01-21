package org.example.expert.domain.manager.repository;

import org.example.expert.domain.manager.entity.Manager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ManagerRepository extends JpaRepository<Manager, Long> {

    /**
     * 주어진 todo ID에 대한 매니저 목록을 사용자 정보와 함께 조회하는 메서드입니다.
     *
     * @param todoId 조회할 todo의 ID
     * @return List<Manager> 주어진 todo ID에 연관된 매니저 목록
     */
    @Query("SELECT m FROM Manager m JOIN FETCH m.user WHERE m.todo.id = :todoId")
    List<Manager> findByTodoIdWithUser(@Param("todoId") Long todoId);
}
