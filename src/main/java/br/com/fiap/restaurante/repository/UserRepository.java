package br.com.fiap.restaurante.repository;

import br.com.fiap.restaurante.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByLogin(String login);

    boolean existsByEmail(String email);

    boolean existsByLogin(String login);

    List<User> findByNomeContainingIgnoreCase(String nome);
}
