package br.com.fiap.restaurante.exception;

public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException() {
        super("Login ou senha inválidos.");
    }
}
