package org.example.expert.domain.comment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.comment.dto.request.CommentSaveRequest;
import org.example.expert.domain.comment.dto.response.CommentResponse;
import org.example.expert.domain.comment.dto.response.CommentSaveResponse;
import org.example.expert.domain.comment.service.CommentService;
import org.example.expert.domain.common.dto.AuthUser;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService; // 댓글 서비스 객체

    /**
     * 주어진 todo 항목에 대한 댓글을 저장하는 메서드입니다.
     *
     * @param authUser          인증된 사용자 정보
     * @param todoId           댓글을 추가할 todo 항목의 ID
     * @param commentSaveRequest 댓글 저장 요청 데이터
     * @return ResponseEntity<CommentSaveResponse> 저장된 댓글 정보가 포함된 응답
     */
    @PostMapping("/todos/{todoId}/comments")
    public ResponseEntity<CommentSaveResponse> saveComment(
            @AuthenticationPrincipal AuthUser authUser, // 인증된 사용자 정보
            @PathVariable long todoId, // 요청 경로에서 todo ID 추출
            @Valid @RequestBody CommentSaveRequest commentSaveRequest // 유효성 검증을 위한 요청 바디
    ) {
        return ResponseEntity.ok(commentService.saveComment(authUser, todoId, commentSaveRequest));
    }

    /**
     * 주어진 todo 항목에 대한 모든 댓글을 조회하는 메서드입니다.
     *
     * @param todoId 댓글을 조회할 todo 항목의 ID
     * @return ResponseEntity<List<CommentResponse>> 조회된 댓글 목록이 포함된 응답
     */
    @GetMapping("/todos/{todoId}/comments")
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable long todoId) {
        return ResponseEntity.ok(commentService.getComments(todoId)); // 댓글 목록을 응답으로 반환
    }
}
