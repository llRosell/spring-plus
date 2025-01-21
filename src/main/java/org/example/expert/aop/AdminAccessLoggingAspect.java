package org.example.expert.aop;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AdminAccessLoggingAspect {

    private final HttpServletRequest request;

    /**
     * 사용자의 역할 변경 요청에 대한 로그를 기록하는 AOP(Aspect Oriented Programming) 메서드입니다.
     * <p>
     * 이 메서드는 UserAdminController의 changeUserRole 메서드가 호출되기 전에 실행되며,
     * 다음과 같은 정보를 로그에 기록합니다:
     * - 요청을 수행한 관리자 사용자 ID
     * - 요청 URL
     * - 요청 발생 시간
     * - 호출된 메서드 이름
     *
     * @param joinPoint 현재 실행 중인 메서드에 대한 정보입니다.
     */
    @Before("execution(* org.example.expert.domain.user.controller.UserAdminController.changeUserRole(..))")
    public void logAfterChangeUserRole(JoinPoint joinPoint) {
        String userId = String.valueOf(request.getAttribute("userId"));
        String requestUrl = request.getRequestURI();
        LocalDateTime requestTime = LocalDateTime.now();

        log.info("Admin Access Log - User ID: {}, Request Time: {}, Request URL: {}, Method: {}",
                userId, requestTime, requestUrl, joinPoint.getSignature().getName());
    }
}
