package com.nsc.kubernetes.demo.controller;

import com.nsc.kubernetes.demo.exception.InvalidFileException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class RestControllerAdvice {

    @ExceptionHandler(InvalidFileException.class)
    public ResponseEntity<Object> handleInvalidFileException(InvalidFileException ex, HandlerMethod handlerMethod, HttpServletRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.NON_AUTHORITATIVE_INFORMATION);
        body.put("statusCode", HttpStatus.NON_AUTHORITATIVE_INFORMATION.value());
        body.put("message", ex.getMessage());
        body.put("path", request.getRequestURL().toString());
        body.put("controller", handlerMethod.getMethod().getDeclaringClass());
        body.put("method", handlerMethod.getMethod().getName());
        return new ResponseEntity<>(body, HttpStatus.NON_AUTHORITATIVE_INFORMATION);
    }
}
