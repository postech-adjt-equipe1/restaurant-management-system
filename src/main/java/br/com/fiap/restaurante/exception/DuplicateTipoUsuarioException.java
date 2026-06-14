package br.com.fiap.restaurante.exception;

public class DuplicateTipoUsuarioException extends RuntimeException {

    public DuplicateTipoUsuarioException(String nome) {
        super("Tipo de usuário já cadastrado com o nome: " + nome);
    }
}
