package lending.platform.lendingplatform.controller;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/*
*using Spring Boot 4.0.5, which: pulls in Spring Framework 7.x which is very new NOT fully compatible yet with
* libraries like:springdoc-openapi
*
* Hence on intro of the controller advice the docs nolonger work.
*
* ToDo find out a better way to resolve this with latest framework and java docs tried from 2.6 upwards
* */

//@RestControllerAdvice
public class GlobalExceptionHandler {
    Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> handleEntityNotFound(EntityNotFoundException e) {
        return ResponseEntity.status(404).body(e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntime(RuntimeException e) {
        return ResponseEntity.status(400).body(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneral(Exception ex) {
        log.error(ex.getMessage(), ex);
        return ResponseEntity.status(500)
                .body(Map.of("error", "internal server error"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors()
                .forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));

        return ResponseEntity.badRequest().body(errors);
    }
}