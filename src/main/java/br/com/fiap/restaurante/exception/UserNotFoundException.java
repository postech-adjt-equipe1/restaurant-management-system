package br.com.fiap.restaurante.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Long id) {
        super("Usuário não encontrado com id: " + id);
    }

}
