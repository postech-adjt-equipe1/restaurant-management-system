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

    @Override
    @Transactional
    public User create(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new DuplicateEmailException(user.getEmail());
        }
        if (userRepository.existsByLogin(user.getLogin())) {
            throw new DuplicateLoginException(user.getLogin());
        }
        user.setSenha(passwordEncoder.encode(user.getSenha()));
        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findByName(String nome) {
        return userRepository.findByNomeContainingIgnoreCase(nome);
    }

    @Override
    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    @Override
    @Transactional
    public User update(Long id, User dadosAtualizados) {
        User existente = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (!existente.getEmail().equalsIgnoreCase(dadosAtualizados.getEmail())
                && userRepository.existsByEmail(dadosAtualizados.getEmail())) {
            throw new DuplicateEmailException(dadosAtualizados.getEmail());
        }

        existente.setNome(dadosAtualizados.getNome());
        existente.setEmail(dadosAtualizados.getEmail());
        existente.setTipo(dadosAtualizados.getTipo());
        existente.setEndereco(dadosAtualizados.getEndereco());

        return userRepository.save(existente);
    }

    @Override
    @Transactional
    public void changePassword(Long id, String senhaAtual, String novaSenha) {
        User usuario = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (!passwordEncoder.matches(senhaAtual, usuario.getSenha())) {
            throw new InvalidPasswordException();
        }

        usuario.setSenha(passwordEncoder.encode(novaSenha));
        userRepository.save(usuario);
    }

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

    @Override
    @Transactional
    public void delete(Long id) {
        User usuario = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        userRepository.delete(usuario);
    }
}
