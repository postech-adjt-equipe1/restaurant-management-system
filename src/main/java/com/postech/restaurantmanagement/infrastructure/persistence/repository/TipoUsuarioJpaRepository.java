package com.postech.restaurantmanagement.infrastructure.persistence.repository;

import com.postech.restaurantmanagement.infrastructure.persistence.entity.TipoUsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoUsuarioJpaRepository extends JpaRepository<TipoUsuarioEntity, Long> {
}
