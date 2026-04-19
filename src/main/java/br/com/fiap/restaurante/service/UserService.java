package br.com.fiap.restaurante.service;

import br.com.fiap.restaurante.model.User;

import java.util.List;

public interface UserService {

    /**
     * Cadastra um novo usuário verificando unicidade de email e login,
     * e criptografando a senha com BCrypt.
     */
    User create(User user);

    /**
     * Busca um usuário pelo seu ID.
     */
    User findById(Long id);

    /**
     * Busca usuários cujo nome contenha o trecho informado (case-insensitive).
     */
    List<User> findByName(String nome);

    /**
     * Atualiza os dados do usuário (nome, email, endereço) sem alterar a senha.
     */
    User update(Long id, User dadosAtualizados);

    /**
     * Troca a senha do usuário após validar a senha atual.
     */
    void changePassword(Long id, String senhaAtual, String novaSenha);

    /**
     * Valida as credenciais de login e retorna o usuário autenticado.
     */
    User validateLogin(String login, String senha);

    /**
     * Remove um usuário pelo seu ID.
     */
    void delete(Long id);
}
