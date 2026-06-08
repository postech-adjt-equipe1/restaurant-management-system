package br.com.fiap.restaurante.exception;

public class TipoUsuarioNotFoundException extends RuntimeException {

    public TipoUsuarioNotFoundException(Long id) {
        super("Tipo de usuário não encontrado com id: " + id);
    }
}
