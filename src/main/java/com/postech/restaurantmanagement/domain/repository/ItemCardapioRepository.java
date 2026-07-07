package com.postech.restaurantmanagement.domain.repository;

import com.postech.restaurantmanagement.domain.model.ItemCardapio;

import java.util.List;
import java.util.Optional;

public interface ItemCardapioRepository {
    ItemCardapio save(ItemCardapio itemCardapio);
    Optional<ItemCardapio> findById(Long id);
    List<ItemCardapio> findByRestauranteId(Long restauranteId);
    void deleteById(Long id);
    boolean existsById(Long id);
    boolean existsByRestauranteId(Long restauranteId);
}
