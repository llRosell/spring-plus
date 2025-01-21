package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface TodoRepository extends JpaRepository<Todo, Long>, TodoRepositoryCustom {

    /**
     * 주어진 날씨와 수정 날짜를 기반으로 Todo 항목을 조회하고,
     * 수정 날짜 기준으로 내림차순 정렬하여 페이지로 반환합니다.
     *
     * @param weather 조회할 Todo의 날씨 (null인 경우 필터링하지 않음)
     * @param startDate 조회할 시작 날짜 (null인 경우 필터링하지 않음)
     * @param endDate 조회할 종료 날짜 (null인 경우 필터링하지 않음)
     * @param pageable 페이징 정보
     * @return Page<Todo> 조건에 맞는 Todo 항목의 페이지
     */
    @Query("SELECT t FROM Todo t LEFT JOIN FETCH t.user u " +
            "WHERE (:weather IS NULL OR t.weather = :weather) " +
            "AND (:startDate IS NULL OR t.modifiedAt >= :startDate) " +
            "AND (:endDate IS NULL OR t.modifiedAt <= :endDate) " +
            "ORDER BY t.modifiedAt DESC")
    Page<Todo> findByWeatherAndOrderByModifiedAtDesc(
            @Param("weather") String weather,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );
}
