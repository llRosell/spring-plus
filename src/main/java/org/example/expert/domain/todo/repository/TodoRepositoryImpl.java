package org.example.expert.domain.todo.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.example.expert.domain.todo.entity.QTodo;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.entity.QUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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
}
