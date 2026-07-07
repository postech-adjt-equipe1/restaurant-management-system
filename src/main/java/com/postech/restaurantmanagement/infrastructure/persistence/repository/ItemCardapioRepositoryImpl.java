package com.postech.restaurantmanagement.infrastructure.persistence.repository;

import com.postech.restaurantmanagement.domain.model.ItemCardapio;
import com.postech.restaurantmanagement.domain.repository.ItemCardapioRepository;
import com.postech.restaurantmanagement.infrastructure.persistence.entity.ItemCardapioEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ItemCardapioRepositoryImpl implements ItemCardapioRepository {

    private final ItemCardapioJpaRepository jpaRepository;

    public ItemCardapioRepositoryImpl(ItemCardapioJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public ItemCardapio save(ItemCardapio itemCardapio) {
        ItemCardapioEntity entity = ItemCardapioEntity.from(itemCardapio);
        return jpaRepository.save(entity).toDomain();
    }

    @Override
    public Optional<ItemCardapio> findById(Long id) {
        return jpaRepository.findById(id).map(ItemCardapioEntity::toDomain);
    }

    @Override
    public List<ItemCardapio> findByRestauranteId(Long restauranteId) {
        return jpaRepository.findByRestauranteId(restauranteId)
                .stream()
                .map(ItemCardapioEntity::toDomain)
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
    public boolean existsByRestauranteId(Long restauranteId) {
        return jpaRepository.existsByRestauranteId(restauranteId);
    }
}
