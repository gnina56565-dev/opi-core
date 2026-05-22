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

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneral(Exception ex) {
        return ResponseEntity.status(500).body("Ошибка сервера: " + ex.getMessage());
    }
}