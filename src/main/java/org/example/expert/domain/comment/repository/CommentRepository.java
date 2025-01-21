package org.example.expert.domain.comment.repository;

import org.example.expert.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * 주어진 todo 항목에 대한 댓글과 해당 댓글을 작성한 사용자를 함께 조회하는 메서드입니다.
     *
     * @param todoId 댓글을 조회할 todo 항목의 ID
     * @return List<Comment> 해당 todo 항목에 대한 댓글 목록 (사용자 정보 포함)
     */
    @Query("SELECT c FROM Comment c JOIN FETCH c.user WHERE c.todo.id = :todoId")
    List<Comment> findByTodoIdWithUser(@Param("todoId") Long todoId);
}
