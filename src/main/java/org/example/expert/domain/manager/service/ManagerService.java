package org.example.expert.domain.manager.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.manager.dto.request.ManagerSaveRequest;
import org.example.expert.domain.manager.dto.response.ManagerResponse;
import org.example.expert.domain.manager.dto.response.ManagerSaveResponse;
import org.example.expert.domain.manager.entity.Manager;
import org.example.expert.domain.manager.repository.ManagerRepository;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.repository.UserRepository;
import org.example.expert.domain.manager.log.Log;
import org.example.expert.domain.manager.log.LogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ManagerService {

    private final ManagerRepository managerRepository;
    private final UserRepository userRepository;
    private final TodoRepository todoRepository;
    private final LogRepository logRepository; // LogRepository 주입 추가

    /**
     * 매니저를 저장하는 메서드입니다.
     *
     * @param authUser          현재 인증된 사용자
     * @param todoId           매니저를 추가할 todo의 ID
     * @param managerSaveRequest 저장할 매니저 정보 요청 객체
     * @return ManagerSaveResponse 저장된 매니저의 응답 정보
     * @throws InvalidRequestException 유효하지 않은 요청이 있는 경우
     */
    @Transactional
    public ManagerSaveResponse saveManager(AuthUser authUser, long todoId, ManagerSaveRequest managerSaveRequest) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new InvalidRequestException("Todo not found"));

        User managerUser = userRepository.findById(managerSaveRequest.getManagerUserId())
                .orElseThrow(() -> new InvalidRequestException("등록하려고 하는 담당자 유저가 존재하지 않습니다."));

        Manager newManager = new Manager(managerUser, todo);
        Manager savedManager = managerRepository.save(newManager);

        // 로그 기록
        saveLog("MANAGER_REGISTRATION", "매니저 등록 요청: " + savedManager.getId());

        return new ManagerSaveResponse(
                savedManager.getId(),
                new UserResponse(managerUser.getId(), managerUser.getEmail())
        );
    }

    /**
     * 로그 기록을 저장하는 메서드입니다.
     *
     * @param actionType 로그 유형
     * @param actionDetail 로그 상세 내용
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW) // 로그 기록을 위해 새로운 트랜잭션으로 설정
    public void saveLog(String actionType, String actionDetail) {
        Log log = new Log(actionType, actionDetail);
        logRepository.save(log);
    }

    /**
     * 특정 todo에 대한 매니저 목록을 조회하는 메서드입니다.
     *
     * @param todoId 조회할 todo의 ID
     * @return List<ManagerResponse> 해당 todo에 연관된 매니저 목록
     * @throws InvalidRequestException 유효하지 않은 요청이 있는 경우
     */
    public List<ManagerResponse> getManagers(long todoId) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new InvalidRequestException("Todo not found"));

        List<Manager> managerList = managerRepository.findByTodoIdWithUser(todo.getId());

        List<ManagerResponse> dtoList = new ArrayList<>();
        for (Manager manager : managerList) {
            User user = manager.getUser();
            dtoList.add(new ManagerResponse(
                    manager.getId(),
                    new UserResponse(user.getId(), user.getEmail())
            ));
        }
        return dtoList;
    }

    /**
     * 매니저를 삭제하는 메서드입니다.
     *
     * @param authUser  현재 인증된 사용자
     * @param todoId    매니저를 삭제할 todo의 ID
     * @param managerId 삭제할 매니저의 ID
     * @throws InvalidRequestException 유효하지 않은 요청이 있는 경우
     */
    @Transactional
    public void deleteManager(AuthUser authUser, long todoId, long managerId) {
        // AuthUser의 ID 검증
        if (authUser == null || authUser.getId() == null) {
            throw new InvalidRequestException("사용자 인증 정보가 유효하지 않습니다.");
        }

        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new InvalidRequestException("Todo not found"));

        if (!todo.getUser().getId().equals(authUser.getId())) {
            throw new InvalidRequestException("일정을 만든 작성자만 매니저를 삭제할 수 있습니다.");
        }

        Manager manager = managerRepository.findById(managerId)
                .orElseThrow(() -> new InvalidRequestException("Manager not found"));

        if (!todo.getId().equals(manager.getTodo().getId())) {
            throw new InvalidRequestException("해당 일정과 관련된 매니저가 아닙니다.");
        }

        managerRepository.delete(manager);
    }
}
