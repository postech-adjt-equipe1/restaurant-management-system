package br.com.fiap.restaurante.repository;

import br.com.fiap.restaurante.model.TipoUsuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TipoUsuarioRepository extends JpaRepository<TipoUsuario, Long> {

    Optional<TipoUsuario> findByNomeIgnoreCase(String nome);

    boolean existsByNomeIgnoreCase(String nome);
}
