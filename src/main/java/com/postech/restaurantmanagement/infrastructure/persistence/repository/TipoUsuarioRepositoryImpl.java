package com.postech.restaurantmanagement.infrastructure.persistence.repository;

import com.postech.restaurantmanagement.domain.model.TipoUsuario;
import com.postech.restaurantmanagement.domain.repository.TipoUsuarioRepository;
import com.postech.restaurantmanagement.infrastructure.persistence.entity.TipoUsuarioEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class TipoUsuarioRepositoryImpl implements TipoUsuarioRepository {

    private final TipoUsuarioJpaRepository jpaRepository;

    public TipoUsuarioRepositoryImpl(TipoUsuarioJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public TipoUsuario save(TipoUsuario tipoUsuario) {
        TipoUsuarioEntity entity = TipoUsuarioEntity.from(tipoUsuario);
        return jpaRepository.save(entity).toDomain();
    }

    @Override
    public Optional<TipoUsuario> findById(Long id) {
        return jpaRepository.findById(id).map(TipoUsuarioEntity::toDomain);
    }

    @Override
    public List<TipoUsuario> findAll() {
        return jpaRepository.findAll()
                .stream()
                .map(TipoUsuarioEntity::toDomain)
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
}
