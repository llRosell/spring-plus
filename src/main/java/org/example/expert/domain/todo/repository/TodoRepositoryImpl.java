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

    /**
     * 주어진 Todo ID를 사용하여 Todo 항목과 해당 사용자 정보를 조회합니다.
     *
     * @param todoId 조회할 Todo의 ID
     * @return Optional<Todo> 해당 ID에 대한 Todo 항목 및 사용자 정보 (존재하지 않을 경우 빈 Optional)
     */
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
