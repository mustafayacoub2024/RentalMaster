package com.example.rentalmaster.exception;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler {
    @Bean
    public ErrorAttributes errorAttributes() {
        return new DefaultErrorAttributes() {
            @Override
            public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
                return super.getErrorAttributes(webRequest, ErrorAttributeOptions.defaults()
                        .including(ErrorAttributeOptions.Include.MESSAGE));
            }
        };
    }

    @ExceptionHandler(CommonBackendException.class)
    public ResponseEntity<ErrorMessage> handleCommonBackendException(CommonBackendException ex) {
        return ResponseEntity.status(ex.getStatus())
                .body(ErrorMessage.builder()
                        .timestamp(LocalDateTime.now())
                        .message(ex.getMessage())
                        .build());
    }


    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorMessage> handleMissingParams(MissingServletRequestParameterException ex, WebRequest request) {
        String parameter = ex.getParameterName();
        log.error("Отсутствует обязательный параметр: {}", parameter);

        return ResponseEntity.badRequest()
                .body(ErrorMessage.builder()
                        .timestamp(LocalDateTime.now())
                        .message(String.format("Отсутствует обязательный параметр: %s", parameter))
                        .path(request.getDescription(false).replace("uri=", ""))
                        .build());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorMessage> handleMismatchType(
            MethodArgumentTypeMismatchException ex,
            WebRequest request) {

        String parameter = ex.getParameter().getParameterName();
        log.error("Неверный тип данных для параметра: {}", parameter);

        return ResponseEntity.badRequest()
                .body(ErrorMessage.builder()
                        .timestamp(LocalDateTime.now())
                        .message(String.format("Неверный тип данных для параметра: %s", parameter))
                        .path(request.getDescription(false).replace("uri=", ""))
                        .build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessage> handleMismatchType(MethodArgumentNotValidException ex) {
        FieldError fieldError = ex.getFieldError();
        String message = fieldError != null ? fieldError.getField() + " " + fieldError.getDefaultMessage() : ex.getMessage();
        log.error(message);
        return ResponseEntity.badRequest().body(new ErrorMessage());
    }

}
