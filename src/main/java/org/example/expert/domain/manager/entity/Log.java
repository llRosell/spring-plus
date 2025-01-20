package org.example.expert.domain.manager.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.entity.User;

@Entity
@Getter
@NoArgsConstructor
public class Log {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;

    @ManyToOne
    @JoinColumn(name = "manager_id")
    private Manager manager;

    @ManyToOne
    @JoinColumn(name = "todo_id")
    private Todo todo;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String createdAt;

    public Log(String message, Manager manager, Todo todo, User user) {
        this.message = message;
        this.manager = manager;
        this.todo = todo;
        this.user = user;
        this.createdAt = java.time.LocalDateTime.now().toString();
    }
}
