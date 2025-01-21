package org.example.expert.domain.manager.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.manager.dto.request.ManagerSaveRequest;
import org.example.expert.domain.manager.dto.response.ManagerResponse;
import org.example.expert.domain.manager.dto.response.ManagerSaveResponse;
import org.example.expert.domain.manager.service.ManagerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ManagerController {

    private final ManagerService managerService;

    /**
     * 매니저를 저장하는 엔드포인트입니다.
     *
     * @param authUser           인증된 사용자 정보
     * @param todoId             연관된 todo ID
     * @param managerSaveRequest  저장할 매니저 정보
     * @return ResponseEntity<ManagerSaveResponse> 생성된 매니저 정보와 HTTP 상태 코드 201(CREATED)
     */
    @PostMapping("/todos/{todoId}/managers")
    public ResponseEntity<ManagerSaveResponse> saveManager(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable long todoId,
            @RequestBody @Valid ManagerSaveRequest managerSaveRequest) {

        ManagerSaveResponse response = managerService.saveManager(authUser, todoId, managerSaveRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 특정 todo에 대한 매니저 목록을 가져오는 엔드포인트입니다.
     *
     * @param todoId 연관된 todo ID
     * @return ResponseEntity<List<ManagerResponse>> 매니저 목록과 HTTP 상태 코드 200(OK)
     */
    @GetMapping("/todos/{todoId}/managers")
    public ResponseEntity<List<ManagerResponse>> getMembers(@PathVariable long todoId) {
        return ResponseEntity.ok(managerService.getManagers(todoId));
    }

    /**
     * 매니저를 삭제하는 엔드포인트입니다.
     *
     * @param authUser  인증된 사용자 정보
     * @param todoId    연관된 todo ID
     * @param managerId 삭제할 매니저 ID
     */
    @DeleteMapping("/todos/{todoId}/managers/{managerId}")
    public void deleteManager(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable long todoId,
            @PathVariable long managerId
    ) {
        managerService.deleteManager(authUser, todoId, managerId);
    }
}
