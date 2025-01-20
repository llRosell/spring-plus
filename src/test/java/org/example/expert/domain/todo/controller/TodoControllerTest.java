package org.example.expert.domain.todo.controller;

import org.example.expert.config.JwtFilter;
import org.example.expert.config.JwtUtil;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.service.TodoService;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(TodoController.class)
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TodoService todoService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private JwtFilter jwtFilter;

    private String token;

    @BeforeEach
    void setUp() {
        token = generateToken(); // Generate the token here
    }

    private String generateToken() {
        Long userId = 1L; // Fake user ID
        String email = "test@example.com"; // Fake email
        String username = "testUser"; // Fake username
        UserRole userRole = UserRole.USER; // User role

        AuthUser authUser = new AuthUser(userId, email, username, userRole, null); // Create AuthUser

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                authUser,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generate JWT token
        return jwtUtil.generateToken(userId, email, username, userRole);
    }

    @Test
    void todo_단건_조회에_성공한다() throws Exception {
        // given
        long todoId = 1L;
        String title = "title";
        AuthUser authUser = new AuthUser(1L, "email", "nickname", UserRole.USER, null);
        User user = User.fromAuthUser(authUser);
        UserResponse userResponse = new UserResponse(user.getId(), user.getEmail());
        TodoResponse response = new TodoResponse(
                todoId,
                title,
                "contents",
                "Sunny",
                userResponse,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        // when
        when(todoService.getTodo(todoId)).thenReturn(response);

        // then
        mockMvc.perform(get("/todos/{todoId}", todoId)
                        .header("Authorization", "Bearer " + token)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(todoId))
                .andDo(print()); // Print request and response
    }

    @Test
    void todo_단건_조회_시_todo가_존재하지_않아_예외가_발생한다() throws Exception {
        // given
        long todoId = 1L;

        // when
        when(todoService.getTodo(todoId))
                .thenThrow(new InvalidRequestException("Todo not found"));

        // then
        mockMvc.perform(get("/todos/{todoId}", todoId)
                        .header("Authorization", "Bearer " + token)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.name()))
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value("Todo not found"))
                .andDo(print()); // Print request and response
    }
}