package org.example.expert.domain.todo.dto.response;

import lombok.Getter;

@Getter
public class TodoSearchResponse {
    private final String title; // 일정 제목
    private final Long nicknameCount; // 담당자 수
    private final Long commentCount; // 댓글 수

    public TodoSearchResponse(String title, long nicknameCount, long commentCount) {
        this.title = title;
        this.nicknameCount = nicknameCount;
        this.commentCount = commentCount;
    }
}
