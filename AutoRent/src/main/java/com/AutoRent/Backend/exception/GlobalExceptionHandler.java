package com.AutoRent.Backend.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorRespuesta> manejarErroresValidacion(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errores.put(error.getField(), error.getDefaultMessage());
        });

        return crearRespuesta(
                HttpStatus.BAD_REQUEST,
                "VALIDATION_ERROR",
                "Hay campos invalidos en la solicitud",
                errores
        );
    }

    @ExceptionHandler(IdNoEncontradoException.class)
    public ResponseEntity<ErrorRespuesta> manejarIdNoEncontrado(IdNoEncontradoException e) {
        return crearRespuesta(HttpStatus.NOT_FOUND, "NOT_FOUND", e.getMessage());
    }

    @ExceptionHandler(LoginRequeridoException.class)
    public ResponseEntity<ErrorRespuesta> manejarLoginRequerido(LoginRequeridoException e) {
        return crearRespuesta(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", e.getMessage());
    }

    @ExceptionHandler(PermisoInsuficienteException.class)
    public ResponseEntity<ErrorRespuesta> manejarPermisoInsuficiente(PermisoInsuficienteException e) {
        return crearRespuesta(HttpStatus.FORBIDDEN, "FORBIDDEN", e.getMessage());
    }

    @ExceptionHandler(DatoDuplicadoException.class)
    public ResponseEntity<ErrorRespuesta> manejarDatoDuplicado(DatoDuplicadoException e) {
        return crearRespuesta(HttpStatus.CONFLICT, "DUPLICATED_DATA", e.getMessage());
    }

    @ExceptionHandler(ParametroIncorrectoException.class)
    public ResponseEntity<ErrorRespuesta> manejarParametroIncorrecto(ParametroIncorrectoException e) {
        return crearRespuesta(HttpStatus.BAD_REQUEST, "BAD_REQUEST", e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorRespuesta> manejarGeneral(Exception e) {
        return crearRespuesta(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "INTERNAL_SERVER_ERROR",
                "Error interno del servidor"
        );
    }

    private ResponseEntity<ErrorRespuesta> crearRespuesta(HttpStatus status, String error, String mensaje) {
        return crearRespuesta(status, error, mensaje, null);
    }

    private ResponseEntity<ErrorRespuesta> crearRespuesta(
            HttpStatus status,
            String error,
            String mensaje,
            Map<String, String> detalles
    ) {
        ErrorRespuesta respuesta = new ErrorRespuesta(
                LocalDateTime.now(),
                status.value(),
                error,
                mensaje,
                detalles
        );

        return ResponseEntity.status(status).body(respuesta);
    }
}
