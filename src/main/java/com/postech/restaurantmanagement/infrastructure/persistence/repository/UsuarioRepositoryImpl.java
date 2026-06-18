package com.postech.restaurantmanagement.infrastructure.persistence.repository;

import com.postech.restaurantmanagement.domain.model.Usuario;
import com.postech.restaurantmanagement.domain.repository.UsuarioRepository;
import com.postech.restaurantmanagement.infrastructure.persistence.entity.UsuarioEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class UsuarioRepositoryImpl implements UsuarioRepository {

    private final UsuarioJpaRepository jpaRepository;

    public UsuarioRepositoryImpl(UsuarioJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Usuario save(Usuario usuario) {
        UsuarioEntity entity = UsuarioEntity.from(usuario);
        return jpaRepository.save(entity).toDomain();
    }

    @Override
    public Optional<Usuario> findById(Long id) {
        return jpaRepository.findById(id).map(UsuarioEntity::toDomain);
    }

    @Override
    public List<Usuario> findAll() {
        return jpaRepository.findAll()
                .stream()
                .map(UsuarioEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public Optional<Usuario> findByEmail(String email) {
        return jpaRepository.findByEmail(email).map(UsuarioEntity::toDomain);
    }
}
