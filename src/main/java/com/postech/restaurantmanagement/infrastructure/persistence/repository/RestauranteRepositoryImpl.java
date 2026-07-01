package com.postech.restaurantmanagement.infrastructure.persistence.repository;

import com.postech.restaurantmanagement.domain.model.Restaurante;
import com.postech.restaurantmanagement.domain.repository.RestauranteRepository;
import com.postech.restaurantmanagement.infrastructure.persistence.entity.RestauranteEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class RestauranteRepositoryImpl implements RestauranteRepository {

    private final RestauranteJpaRepository jpaRepository;

    public RestauranteRepositoryImpl(RestauranteJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Restaurante save(Restaurante restaurante) {
        RestauranteEntity entity = RestauranteEntity.from(restaurante);
        return jpaRepository.save(entity).toDomain();
    }

    @Override
    public Optional<Restaurante> findById(Long id) {
        return jpaRepository.findById(id).map(RestauranteEntity::toDomain);
    }

    @Override
    public List<Restaurante> findAll() {
        return jpaRepository.findAll()
                .stream()
                .map(RestauranteEntity::toDomain)
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
