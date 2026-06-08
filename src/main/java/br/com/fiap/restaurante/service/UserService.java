package br.com.fiap.restaurante.service;

import br.com.fiap.restaurante.model.User;

import java.util.List;

public interface UserService {

    User create(User user);

    User findById(Long id);

    List<User> findByName(String nome);

    User update(Long id, User dadosAtualizados);

    void changePassword(Long id, String senhaAtual, String novaSenha);

    User validateLogin(String login, String senha);

    void delete(Long id);
}
