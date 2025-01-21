package org.example.expert.domain.todo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.service.TodoService;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    /**
     * 새로운 Todo 항목을 저장하는 API 엔드포인트입니다.
     *
     * @param authUser 인증된 사용자 정보
     * @param todoSaveRequest 저장할 Todo 정보 요청 객체
     * @return ResponseEntity<TodoSaveResponse> 저장된 Todo의 응답 정보
     */
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @PostMapping("/todos")
    public ResponseEntity<TodoSaveResponse> saveTodo(
            @AuthenticationPrincipal AuthUser authUser,
            @Valid @RequestBody TodoSaveRequest todoSaveRequest
    ) {
        return ResponseEntity.ok(todoService.saveTodo(authUser, todoSaveRequest));
    }

    /**
     * Todo 항목 목록을 페이징 처리하여 조회하는 API 엔드포인트입니다.
     *
     * @param page 조회할 페이지 번호 (기본값: 1)
     * @param size 한 페이지에 표시할 항목 수 (기본값: 10)
     * @param weather 날씨 정보를 포함한 요청 매개변수
     * @param startDate 조회할 시작 날짜
     * @param endDate 조회할 종료 날짜
     * @return ResponseEntity<Page<TodoResponse>> Todo 목록 응답
     */
    @GetMapping("/todos")
    public ResponseEntity<Page<TodoResponse>> getTodosAPU(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam("weather") String weather,
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate
    ) {
        return ResponseEntity.ok(todoService.getTodos(page, size, weather, startDate, endDate));
    }

    /**
     * 특정 Todo 항목을 조회하는 API 엔드포인트입니다.
     *
     * @param todoId 조회할 Todo의 ID
     * @return ResponseEntity<TodoResponse> 조회된 Todo의 응답 정보
     */
    @GetMapping("/todos/{todoId}")
    public ResponseEntity<TodoResponse> getTodoAPI(@PathVariable long todoId) {
        return ResponseEntity.ok(todoService.getTodo(todoId));
    }

    /**
     * 조건에 맞는 Todo 항목을 검색하는 API 엔드포인트입니다.
     *
     * @param authUser 인증된 사용자 정보
     * @param page 조회할 페이지 번호 (기본값: 1)
     * @param size 한 페이지에 표시할 항목 수 (기본값: 10)
     * @param title 검색할 Todo 제목
     * @param startDate 검색할 시작 날짜
     * @param endDate 검색할 종료 날짜
     * @param nickname 작성자의 별명
     * @return ResponseEntity<Page<TodoSearchResponse>> 검색된 Todo 목록 응답
     */
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/todos/search")
    public ResponseEntity<Page<TodoSearchResponse>> searchTodosAPI(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam("title") String title,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam("nickname") String nickname
    ) {
        Page<TodoSearchResponse> result = todoService.searchTodos(authUser, page, size, title, startDate, endDate, nickname);
        return ResponseEntity.ok(result);
    }
}
