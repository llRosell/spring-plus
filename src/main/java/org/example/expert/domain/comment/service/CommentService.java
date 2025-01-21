package org.example.expert.domain.comment.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.comment.dto.request.CommentSaveRequest;
import org.example.expert.domain.comment.dto.response.CommentResponse;
import org.example.expert.domain.comment.dto.response.CommentSaveResponse;
import org.example.expert.domain.comment.entity.Comment;
import org.example.expert.domain.comment.repository.CommentRepository;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final TodoRepository todoRepository;
    private final CommentRepository commentRepository;

    /**
     * 댓글을 저장하는 메서드입니다.
     *
     * @param authUser         인증된 사용자 정보
     * @param todoId           댓글이 저장될 Todo 항목의 ID
     * @param commentSaveRequest 댓글 저장 요청 정보 (내용 포함)
     * @return CommentSaveResponse 저장된 댓글 정보 (ID, 내용, 사용자 정보 포함)
     * @throws InvalidRequestException Todo 항목을 찾을 수 없는 경우
     */
    @Transactional
    public CommentSaveResponse saveComment(AuthUser authUser, long todoId, CommentSaveRequest commentSaveRequest) {
        User user = User.fromAuthUser(authUser);  // AuthUser를 User 객체로 변환
        Todo todo = todoRepository.findById(todoId).orElseThrow(() ->
                new InvalidRequestException("Todo not found"));  // Todo 항목 검색

        // 새 댓글 객체 생성
        Comment newComment = new Comment(
                commentSaveRequest.getContents(),  // 댓글 내용
                user,  // 댓글 작성자
                todo   // 댓글이 속하는 Todo 항목
        );

        Comment savedComment = commentRepository.save(newComment);  // 댓글 저장

        // 저장된 댓글 정보를 포함한 응답 객체 생성
        return new CommentSaveResponse(
                savedComment.getId(),  // 댓글 ID
                savedComment.getContents(),  // 댓글 내용
                new UserResponse(user.getId(), user.getEmail())  // 사용자 정보
        );
    }

    /**
     * 특정 Todo 항목에 대한 댓글 목록을 조회하는 메서드입니다.
     *
     * @param todoId 댓글을 조회할 Todo 항목의 ID
     * @return List<CommentResponse> 댓글 정보 리스트 (ID, 내용, 사용자 정보 포함)
     */
    public List<CommentResponse> getComments(long todoId) {
        List<Comment> commentList = commentRepository.findByTodoIdWithUser(todoId);  // 댓글 목록 조회

        List<CommentResponse> dtoList = new ArrayList<>();
        for (Comment comment : commentList) {
            User user = comment.getUser();  // 댓글 작성자 정보 가져오기
            // 댓글 정보와 사용자 정보를 포함한 응답 DTO 생성
            CommentResponse dto = new CommentResponse(
                    comment.getId(),  // 댓글 ID
                    comment.getContents(),  // 댓글 내용
                    new UserResponse(user.getId(), user.getEmail())  // 사용자 정보
            );
            dtoList.add(dto);  // DTO 리스트에 추가
        }
        return dtoList;  // 댓글 목록 반환
    }
}
