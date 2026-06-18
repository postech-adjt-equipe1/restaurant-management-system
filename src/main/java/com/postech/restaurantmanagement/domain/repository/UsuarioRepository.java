package com.postech.restaurantmanagement.domain.repository;

import com.postech.restaurantmanagement.domain.model.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository {
    Usuario save(Usuario usuario);
    Optional<Usuario> findById(Long id);
    List<Usuario> findAll();
    void deleteById(Long id);
    boolean existsById(Long id);
    Optional<Usuario> findByEmail(String email);
}
