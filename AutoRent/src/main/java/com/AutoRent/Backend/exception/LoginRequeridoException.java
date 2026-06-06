package com.AutoRent.Backend.exception;

public class LoginRequeridoException extends RuntimeException {

    public LoginRequeridoException(String mensaje) {
        super(mensaje);
    }
}
