package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TodoRepositoryCustom {

    /**
     * 주어진 Todo ID를 사용하여 Todo 항목과 해당 사용자 정보를 조회합니다.
     *
     * @param todoId 조회할 Todo의 ID
     * @return Optional<Todo> 해당 ID에 대한 Todo 항목 및 사용자 정보 (존재하지 않을 경우 빈 Optional)
     */
    Optional<Todo> findByIdWithUser(Long todoId);

    /**
     * 제목, 생성 날짜, 그리고 사용자 별명을 기반으로 Todo 항목을 검색합니다.
     * 검색된 결과는 페이지로 반환됩니다.
     *
     * @param title 검색할 Todo의 제목
     * @param startDate 검색할 시작 날짜
     * @param endDate 검색할 종료 날짜
     * @param nickname 검색할 사용자 별명
     * @param pageable 페이징 정보
     * @return Page<TodoSearchResponse> 조건에 맞는 Todo 항목의 페이지
     */
    Page<TodoSearchResponse> searchTodosByTitleAndCreatedAtAndNickname(
            String title, LocalDateTime startDate, LocalDateTime endDate, String nickname, Pageable pageable);
}
