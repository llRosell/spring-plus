package org.example.expert.log;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "log")
public class Log {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String actionType; // 액션 유형 (예: "MANAGER_REGISTRATION")

    @Column(columnDefinition = "TEXT")
    private String actionDetail; // 액션 세부 정보

    @Column(nullable = false)
    private LocalDateTime timestamp; // 로그 생성 시간

    public Log(String actionType, String actionDetail) {
        this.actionType = actionType;
        this.actionDetail = actionDetail;
        this.timestamp = LocalDateTime.now();
    }
}
