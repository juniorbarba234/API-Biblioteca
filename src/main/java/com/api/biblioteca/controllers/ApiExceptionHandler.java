package com.api.biblioteca.controllers;

import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.dao.DataIntegrityViolationException;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> negocio(ResponseStatusException exception) {
        return respuesta(exception.getStatusCode().value(), exception.getReason());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> validacion(MethodArgumentNotValidException exception) {
        var error = exception.getBindingResult().getFieldError();
        return respuesta(400, error == null ? "Solicitud inválida" : "Revisa el campo " + error.getField() + ": " + error.getDefaultMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> argumento(IllegalArgumentException exception) { return respuesta(400, exception.getMessage()); }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> json(Exception exception) { return respuesta(400, "JSON inválido: revisa los campos, las fechas y los valores permitidos"); }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> integridad(Exception exception) { return respuesta(409, "Los datos están duplicados o relacionados con otros registros"); }

    private ResponseEntity<Map<String, Object>> respuesta(int status, String message) {
        return ResponseEntity.status(status).body(Map.of("timestamp", LocalDateTime.now().toString(), "status", status, "error", message == null ? "Solicitud inválida" : message));
    }
}
