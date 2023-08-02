package com.sandro.exception;

import com.sandro.domain.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.nio.file.AccessDeniedException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Alessandro Formica
 * @version 1.0
 * @since 16.07.2023
 */

@RestControllerAdvice
@Slf4j
public class HandleException extends ResponseEntityExceptionHandler implements ErrorController {

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        log.error("You have a new error: {}", ex.getMessage());
        return new ResponseEntity<>(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .reason(ex.getMessage())
                        .developerMessage(ex.getMessage())
                        .httpStatus(HttpStatus.resolve(statusCode.value()))
                        .statusCode(statusCode.value())
                        .build(),
                statusCode
        );
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        String fieldMessage = fieldErrors.stream().map(FieldError::getDefaultMessage).collect(Collectors.joining(" ~ "));
        log.error("You have a new error: {}", ex.getMessage());
        return new ResponseEntity<>(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .reason(fieldMessage)
                        .developerMessage(ex.getMessage())
                        .httpStatus(HttpStatus.resolve(statusCode.value()))
                        .statusCode(statusCode.value())
                        .build(),
                statusCode
        );
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<HttpResponse> sqlIntegrityViolationException(SQLIntegrityConstraintViolationException ex) {
        log.error("You have a new error: {}", ex.getMessage());
        return new ResponseEntity<>(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .reason(ex.getMessage().contains("Duplicate entry") ? "Information already exists" : ex.getMessage())
                        .developerMessage(ex.getMessage())
                        .httpStatus(HttpStatus.BAD_REQUEST)
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .build(),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<HttpResponse> badCredentialsException(BadCredentialsException ex) {
        log.error("You have a new error: {}", ex.getMessage());
        return new ResponseEntity<>(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .reason(ex.getMessage() + ", Incorrect email or password")
                        .developerMessage(ex.getMessage())
                        .httpStatus(HttpStatus.BAD_REQUEST)
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .build(),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<HttpResponse> apiException(ApiException ex) {
        log.error("You have a generic error: {}", ex.getMessage());
        return new ResponseEntity<>(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .reason(ex.getMessage())
                        .developerMessage(ex.getMessage())
                        .httpStatus(HttpStatus.BAD_REQUEST)
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .build(),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<HttpResponse> accessDeniedException(AccessDeniedException ex) {
        log.error("You have a new error: {}", ex.getMessage());
        return new ResponseEntity<>(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .reason(ex.getMessage() + ", You are not allowed to access this resource")
                        .developerMessage(ex.getMessage())
                        .httpStatus(HttpStatus.FORBIDDEN)
                        .statusCode(HttpStatus.FORBIDDEN.value())
                        .build(),
                HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<HttpResponse> exception(Exception ex) {
        log.error("You have a new error: {}", ex.getMessage());
        return new ResponseEntity<>(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .reason(ex.getMessage() != null && ex.getMessage().contains("expected 1, actual 0")
                                ? "record not found"
                                : ex.getMessage())
                        .developerMessage(ex.getMessage())
                        .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .build(),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<HttpResponse> emptyResultDataAccessException(EmptyResultDataAccessException ex) {
        log.error("You have a new error: {}", ex.getMessage());
        return new ResponseEntity<>(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .reason(ex.getMessage().contains("expected 1, actual 0") ? "Record not found" : ex.getMessage())
                        .developerMessage(ex.getMessage())
                        .httpStatus(HttpStatus.BAD_REQUEST)
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .build(),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<HttpResponse> disabledException(DisabledException ex) {
        log.error("You have a new error: {}", ex.getMessage());
        return new ResponseEntity<>(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .reason("User account is currently disabled")
                        .developerMessage(ex.getMessage())
                        .httpStatus(HttpStatus.BAD_REQUEST)
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .build(),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<HttpResponse> lockedException(LockedException ex) {
        log.error("You have a new error: {}", ex.getMessage());
        return new ResponseEntity<>(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .reason("User account is currently locked")
                        .developerMessage(ex.getMessage())
                        .httpStatus(HttpStatus.BAD_REQUEST)
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .build(),
                HttpStatus.BAD_REQUEST
        );
    }


}
