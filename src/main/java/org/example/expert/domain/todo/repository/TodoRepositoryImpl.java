package org.example.expert.domain.todo.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.example.expert.domain.comment.entity.QComment;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.QTodo;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.entity.QUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class TodoRepositoryImpl implements TodoRepositoryCustom {

    private final EntityManager entityManager;

    @Autowired
    public TodoRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Optional<Todo> findByIdWithUser(Long todoId) {
        QTodo todo = QTodo.todo;
        QUser user = QUser.user;

        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);

        Todo result = queryFactory.selectFrom(todo)
                .leftJoin(todo.user, user)  // LEFT JOIN 수행
                .where(todo.id.eq(todoId))  // todoId로 필터링
                .fetchOne();  // 결과를 하나 가져오기

        return Optional.ofNullable(result);  // 결과가 없으면 Optional.empty() 반환
    }

    @Override
    public Page<TodoSearchResponse> searchTodosByTitleAndCreatedAtAndNickname(
            String title, LocalDateTime startDate, LocalDateTime endDate, String nickname, Pageable pageable) {

        QTodo qTodo = QTodo.todo;
        QUser qUser = QUser.user;
        QComment qComment = QComment.comment;

        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);

        // 기본 조건을 생성합니다
        BooleanExpression condition = null;

        // title 조건 추가 (title이 null이 아니면 조건 추가)
        if (title != null && !title.isEmpty()) {
            condition = qTodo.title.containsIgnoreCase(title);  // 일부 글자 포함 검색
        }

        // startDate, endDate 조건 추가 (날짜가 null이 아니면 조건 추가)
        if (startDate != null && endDate != null) {
            if (condition == null) {
                condition = qTodo.createdAt.between(startDate, endDate);
            } else {
                condition = condition.and(qTodo.createdAt.between(startDate, endDate));
            }
        }

        // nickname 조건 추가 (nickname이 null이 아니면 조건 추가)
        if (nickname != null && !nickname.isEmpty()) {
            if (condition == null) {
                condition = qUser.nickname.containsIgnoreCase(nickname);  // 일부 글자 포함 검색
            } else {
                condition = condition.and(qUser.nickname.containsIgnoreCase(nickname));
            }
        }

        // 쿼리 실행
        List<TodoSearchResponse> results = queryFactory
                .select(Projections.fields(TodoSearchResponse.class,
                        qTodo.title,
                        qUser.count().as("nicknameCount"),
                        qComment.count().as("commentCount")
                ))
                .from(qTodo)
                .leftJoin(qTodo.user, qUser)
                .leftJoin(qTodo.comments, qComment)
                .where(condition) // 동적으로 생성된 조건을 사용
                .groupBy(qTodo.id)
                .orderBy(qTodo.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 count 계산
        long total = queryFactory
                .selectFrom(qTodo)
                .where(condition)
                .fetchCount();

        return new PageImpl<>(results, pageable, total);
    }
}
