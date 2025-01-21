package org.example.expert.config;

import org.example.expert.domain.auth.exception.AuthException;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.common.exception.ServerException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * InvalidRequestException이 발생했을 때 처리하는 메서드입니다.
     *
     * @param ex 발생한 InvalidRequestException 객체
     * @return BAD_REQUEST 상태 코드와 함께 오류 메시지를 포함하는 응답
     */
    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<Map<String, Object>> invalidRequestExceptionException(InvalidRequestException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return getErrorResponse(status, ex.getMessage());
    }

    /**
     * AuthException이 발생했을 때 처리하는 메서드입니다.
     *
     * @param ex 발생한 AuthException 객체
     * @return UNAUTHORIZED 상태 코드와 함께 오류 메시지를 포함하는 응답
     */
    @ExceptionHandler(AuthException.class)
    public ResponseEntity<Map<String, Object>> handleAuthException(AuthException ex) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        return getErrorResponse(status, ex.getMessage());
    }

    /**
     * ServerException이 발생했을 때 처리하는 메서드입니다.
     *
     * @param ex 발생한 ServerException 객체
     * @return INTERNAL_SERVER_ERROR 상태 코드와 함께 오류 메시지를 포함하는 응답
     */
    @ExceptionHandler(ServerException.class)
    public ResponseEntity<Map<String, Object>> handleServerException(ServerException ex) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        return getErrorResponse(status, ex.getMessage());
    }

    /**
     * 오류 응답을 생성하는 메서드입니다.
     *
     * @param status HTTP 상태 코드
     * @param message 오류 메시지
     * @return 상태 코드와 오류 메시지를 포함하는 ResponseEntity
     */
    public ResponseEntity<Map<String, Object>> getErrorResponse(HttpStatus status, String message) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", status.name());
        errorResponse.put("code", status.value());
        errorResponse.put("message", message);

        return new ResponseEntity<>(errorResponse, status);
    }
}