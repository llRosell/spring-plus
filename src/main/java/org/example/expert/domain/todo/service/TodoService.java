package org.example.expert.domain.todo.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.client.WeatherClient;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TodoService {

    private final TodoRepository todoRepository;
    private final UserRepository userRepository;
    private final WeatherClient weatherClient;

    /**
     * 새로운 Todo 항목을 저장합니다.
     * 사용자가 이미 존재하는 경우 해당 사용자를 재사용하고, 존재하지 않는 경우 새로 생성합니다.
     *
     * @param authUser        인증된 사용자 정보
     * @param todoSaveRequest 저장할 Todo 항목의 정보
     * @return TodoSaveResponse 저장된 Todo 항목의 정보
     */
    @Transactional
    public TodoSaveResponse saveTodo(AuthUser authUser, TodoSaveRequest todoSaveRequest) {
        // 기존 이메일로 사용자가 존재하는지 확인
        Optional<User> existingUser = userRepository.findByEmail(authUser.getEmail());

        // 기존 사용자 정보가 있으면 해당 사용자 사용, 없으면 새로 생성
        User user;
        if (existingUser.isPresent()) {
            user = existingUser.get();  // 이미 존재하는 사용자 사용
        } else {
            user = new User(authUser.getEmail(), authUser.getNickname(), authUser.getUserRole());
            user = userRepository.save(user);  // 새 사용자 생성
        }

        String weather = weatherClient.getTodayWeather();  // 오늘의 날씨를 가져옴

        Todo newTodo = new Todo(
                todoSaveRequest.getTitle(),
                todoSaveRequest.getContents(),
                weather,
                user
        );
        Todo savedTodo = todoRepository.save(newTodo);  // Todo 항목 저장

        return new TodoSaveResponse(
                savedTodo.getId(),
                savedTodo.getTitle(),
                savedTodo.getContents(),
                weather,
                new UserResponse(user.getId(), user.getEmail())
        );
    }

    /**
     * 특정 조건에 따라 Todo 항목을 조회합니다.
     *
     * @param page       페이지 번호 (1부터 시작)
     * @param size       페이지 크기
     * @param weather    날씨 필터
     * @param startDate  시작 날짜 (null 가능)
     * @param endDate    종료 날짜 (null 가능)
     * @return Page<TodoResponse> 조건에 맞는 Todo 항목의 페이지
     */
    public Page<TodoResponse> getTodos(int page, int size, String weather, LocalDateTime startDate, LocalDateTime endDate) {
        Pageable pageable = PageRequest.of(page - 1, size);

        Page<Todo> todos = todoRepository.findByWeatherAndOrderByModifiedAtDesc(
                weather, startDate, endDate, pageable
        );
        return todos.map(todo -> new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getContents(),
                todo.getWeather(),
                new UserResponse(todo.getUser().getId(), todo.getUser().getEmail()),
                todo.getCreatedAt(),
                todo.getModifiedAt()
        ));
    }

    /**
     * 특정 Todo 항목을 ID로 조회합니다.
     *
     * @param todoId 조회할 Todo의 ID
     * @return TodoResponse 해당 Todo 항목의 정보
     * @throws InvalidRequestException Todo 항목이 존재하지 않을 경우 예외 발생
     */
    public TodoResponse getTodo(long todoId) {
        Todo todo = todoRepository.findByIdWithUser(todoId)
                .orElseThrow(() -> new InvalidRequestException("Todo not found"));

        User user = todo.getUser();

        return new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getContents(),
                todo.getWeather(),
                new UserResponse(user.getId(), user.getEmail()),
                todo.getCreatedAt(),
                todo.getModifiedAt()
        );
    }

    /**
     * 제목, 날짜, 별명에 따라 Todo 항목을 검색합니다.
     *
     * @param authUser   인증된 사용자 정보
     * @param page       페이지 번호 (1부터 시작)
     * @param size       페이지 크기
     * @param title      검색할 Todo의 제목 (null 가능)
     * @param startDate  시작 날짜 (null 가능)
     * @param endDate    종료 날짜 (null 가능)
     * @param nickname   검색할 사용자 별명 (null 가능)
     * @return Page<TodoSearchResponse> 검색된 Todo 항목의 페이지
     */
    public Page<TodoSearchResponse> searchTodos(
            AuthUser authUser, int page, int size, String title, LocalDateTime startDate, LocalDateTime endDate, String nickname) {

        User user = User.fromAuthUser(authUser);
        Pageable pageable = PageRequest.of(page - 1, size); // Pageable 객체 생성

        // 레포지토리에서 쿼리 실행
        Page<TodoSearchResponse> todos = todoRepository.searchTodosByTitleAndCreatedAtAndNickname(
                title, startDate, endDate, nickname, pageable
        );

        // TodoSearchResponse -> TodoResponse 변환
        return todos;
    }
}
