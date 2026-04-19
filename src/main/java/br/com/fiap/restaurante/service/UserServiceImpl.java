package br.com.fiap.restaurante.service;

import br.com.fiap.restaurante.exception.DuplicateEmailException;
import br.com.fiap.restaurante.exception.DuplicateLoginException;
import br.com.fiap.restaurante.exception.InvalidCredentialsException;
import br.com.fiap.restaurante.exception.InvalidPasswordException;
import br.com.fiap.restaurante.exception.UserNotFoundException;
import br.com.fiap.restaurante.model.User;
import br.com.fiap.restaurante.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // -----------------------------------------------------------------------
    // Tarefa 1 — Cadastro de usuário
    // -----------------------------------------------------------------------

    @Override
    @Transactional
    public User create(User user) {
        // Verifica unicidade do e-mail
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new DuplicateEmailException(user.getEmail());
        }

        // Verifica unicidade do login
        if (userRepository.existsByLogin(user.getLogin())) {
            throw new DuplicateLoginException(user.getLogin());
        }

        // Criptografa a senha com BCrypt antes de persistir
        user.setSenha(passwordEncoder.encode(user.getSenha()));

        return userRepository.save(user);
    }

    // -----------------------------------------------------------------------
    // Tarefa 2 — Busca de usuários por nome (parcial, case-insensitive)
    // -----------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public List<User> findByName(String nome) {
        return userRepository.findByNomeContainingIgnoreCase(nome);
    }

    // -----------------------------------------------------------------------
    // Busca por ID (suporte para controllers e outras operações)
    // -----------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    // -----------------------------------------------------------------------
    // Tarefa 3 — Atualização de dados do usuário (sem senha)
    // -----------------------------------------------------------------------

    @Override
    @Transactional
    public User update(Long id, User dadosAtualizados) {
        User existente = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        // Verifica unicidade do novo e-mail (se mudou)
        if (!existente.getEmail().equalsIgnoreCase(dadosAtualizados.getEmail())
                && userRepository.existsByEmail(dadosAtualizados.getEmail())) {
            throw new DuplicateEmailException(dadosAtualizados.getEmail());
        }

        // Verifica unicidade do novo login (se mudou)
        if (!existente.getLogin().equalsIgnoreCase(dadosAtualizados.getLogin())
                && userRepository.existsByLogin(dadosAtualizados.getLogin())) {
            throw new DuplicateLoginException(dadosAtualizados.getLogin());
        }

        // Atualiza apenas os campos permitidos — senha NÃO é alterada aqui
        existente.setNome(dadosAtualizados.getNome());
        existente.setEmail(dadosAtualizados.getEmail());
        existente.setLogin(dadosAtualizados.getLogin());
        existente.setTipo(dadosAtualizados.getTipo());
        existente.setEndereco(dadosAtualizados.getEndereco());
        // dataUltimaAlteracao é atualizado automaticamente pelo @PreUpdate da entidade

        return userRepository.save(existente);
    }

    // -----------------------------------------------------------------------
    // Tarefa 4 — Troca de senha (endpoint separado)
    // -----------------------------------------------------------------------

    @Override
    @Transactional
    public void changePassword(Long id, String senhaAtual, String novaSenha) {
        User usuario = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        // Valida a senha atual antes de permitir a troca
        if (!passwordEncoder.matches(senhaAtual, usuario.getSenha())) {
            throw new InvalidPasswordException();
        }

        // Criptografa a nova senha e persiste
        usuario.setSenha(passwordEncoder.encode(novaSenha));
        // dataUltimaAlteracao é atualizado automaticamente pelo @PreUpdate da entidade

        userRepository.save(usuario);
    }

    // -----------------------------------------------------------------------
    // Tarefa 5 — Validação de login
    // -----------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public User validateLogin(String login, String senha) {
        User usuario = userRepository.findByLogin(login)
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(senha, usuario.getSenha())) {
            throw new InvalidCredentialsException();
        }

        return usuario;
    }

    // -----------------------------------------------------------------------
    // Tarefa 7 — Exclusão de usuário
    // -----------------------------------------------------------------------

    @Override
    @Transactional
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
    }
}
