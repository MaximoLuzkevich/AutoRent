package com.AutoRent.Backend.exception;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> manejarErroresValidacion(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errores.put(error.getField(), error.getDefaultMessage());
        });

        return ResponseEntity.status(400).body(errores);
    }

    @ExceptionHandler(IdNoEncontradoException.class)
    public ResponseEntity<?> manejarIdNoEncontrado(IdNoEncontradoException e) {
        return ResponseEntity.status(404).body(e.getMessage());
    }

    @ExceptionHandler(LoginRequeridoException.class)
    public ResponseEntity<?> manejarLoginRequerido(LoginRequeridoException e) {
        return ResponseEntity.status(401).body(e.getMessage());
    }

    @ExceptionHandler(PermisoInsuficienteException.class)
    public ResponseEntity<?> manejarPermisoInsuficiente(PermisoInsuficienteException e) {
        return ResponseEntity.status(403).body(e.getMessage());
    }

    @ExceptionHandler(DatoDuplicadoException.class)
    public ResponseEntity<?> manejarDatoDuplicado(DatoDuplicadoException e) {
        return ResponseEntity.status(409).body(e.getMessage());
    }

    @ExceptionHandler(ParametroIncorrectoException.class)
    public ResponseEntity<?> manejarParametroIncorrecto(ParametroIncorrectoException e) {
        return ResponseEntity.status(400).body(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> manejarGeneral(Exception e) {
        return ResponseEntity.status(500).body("Error interno del servidor");
    }
}
