package br.com.fiap.restaurante.exception;

public class DuplicateLoginException extends RuntimeException {

    public DuplicateLoginException(String login) {
        super("Já existe um usuário cadastrado com o login: " + login);
    }
}
