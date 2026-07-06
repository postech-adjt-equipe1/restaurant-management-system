package com.postech.restaurantmanagement.domain.repository;

import com.postech.restaurantmanagement.domain.model.Restaurante;

import java.util.List;
import java.util.Optional;

public interface RestauranteRepository {
    Restaurante save(Restaurante restaurante);
    Optional<Restaurante> findById(Long id);
    List<Restaurante> findAll();
    void deleteById(Long id);
    boolean existsById(Long id);
}
