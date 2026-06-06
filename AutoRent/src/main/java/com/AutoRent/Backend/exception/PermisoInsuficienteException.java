package com.AutoRent.Backend.exception;

public class PermisoInsuficienteException extends RuntimeException {

    public PermisoInsuficienteException(String mensaje) {
        super(mensaje);
    }
}
