package com.photogen.exception;

import ai.fal.client.exception.FalException;

import com.photogen.dto.StringResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    @ExceptionHandler(FalException.class)
    public ResponseEntity<StringResponse> handleConstraintViolation(FalException ex) {
        log.error(ex.getMessage(), ex);
        return new ResponseEntity<>(new StringResponse(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }
}