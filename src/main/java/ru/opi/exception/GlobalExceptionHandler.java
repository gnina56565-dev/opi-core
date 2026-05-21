package ru.opi.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.opi.service.RequestService;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RequestService.ResourceNotFoundException.class)
    public ResponseEntity<String> handleNotFound(RequestService.ResourceNotFoundException ex) {
        return ResponseEntity.status(404).body(ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.status(400).body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneric(Exception ex) {
        ex.printStackTrace();
        return ResponseEntity.status(500).body("Внутренняя ошибка сервера: " + ex.getMessage());
    }
}