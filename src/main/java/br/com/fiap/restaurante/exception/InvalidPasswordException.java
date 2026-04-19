package br.com.fiap.restaurante.exception;

public class InvalidPasswordException extends RuntimeException {

    public InvalidPasswordException() {
        super("Senha atual incorreta.");
    }
}
