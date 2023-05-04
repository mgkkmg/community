package com.gym.modulecore.exception;

import com.gym.modulecore.response.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalControllerHandler {

    @ExceptionHandler(CommunityException.class)
    private ResponseEntity<?> globalExceptionHandler(CommunityException e) {
        log.error("Error occurs {}", e.toString());
        return ResponseEntity.status(e.getErrorCode().getStatus())
                .body(CommonResponse.error(e.getErrorCode().name()));
    }

    @ExceptionHandler(RuntimeException.class)
    private ResponseEntity<?> globalExceptionHandler(RuntimeException e) {
        log.error("Error occurs {}", e.toString());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(CommonResponse.error(ErrorCode.INTERNAL_SERVER_ERROR.name()));
    }
}
