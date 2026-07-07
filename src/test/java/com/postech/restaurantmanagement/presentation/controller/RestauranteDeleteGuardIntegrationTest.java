package com.postech.restaurantmanagement.presentation.controller;

import com.postech.restaurantmanagement.infrastructure.persistence.entity.ItemCardapioEntity;
import com.postech.restaurantmanagement.infrastructure.persistence.entity.RestauranteEntity;
import com.postech.restaurantmanagement.infrastructure.persistence.entity.TipoUsuarioEntity;
import com.postech.restaurantmanagement.infrastructure.persistence.entity.UsuarioEntity;
import com.postech.restaurantmanagement.infrastructure.persistence.repository.ItemCardapioJpaRepository;
import com.postech.restaurantmanagement.infrastructure.persistence.repository.RestauranteJpaRepository;
import com.postech.restaurantmanagement.infrastructure.persistence.repository.TipoUsuarioJpaRepository;
import com.postech.restaurantmanagement.infrastructure.persistence.repository.UsuarioJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RestauranteDeleteGuardIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TipoUsuarioJpaRepository tipoUsuarioJpaRepository;

    @Autowired
    private UsuarioJpaRepository usuarioJpaRepository;

    @Autowired
    private RestauranteJpaRepository restauranteJpaRepository;

    @Autowired
    private ItemCardapioJpaRepository itemCardapioJpaRepository;

    @BeforeEach
    void setUp() {
        itemCardapioJpaRepository.deleteAll();
        restauranteJpaRepository.deleteAll();
        usuarioJpaRepository.deleteAll();
        tipoUsuarioJpaRepository.deleteAll();
    }

    @Test
    void deletarRestauranteComItensDeCardapioDeveRetornar409() throws Exception {
        RestauranteEntity restaurante = criarRestauranteComDono();
        itemCardapioJpaRepository.save(new ItemCardapioEntity(
                null, "Prato Teste", "Descrição do prato", new BigDecimal("10.00"), false, null, restaurante.getId()));

        mockMvc.perform(delete("/restaurante/" + restaurante.getId()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.erro", containsString("possui itens de cardápio")));
    }

    @Test
    void deletarRestauranteAposRemoverItensDeCardapioDeveRetornar204() throws Exception {
        RestauranteEntity restaurante = criarRestauranteComDono();
        ItemCardapioEntity item = itemCardapioJpaRepository.save(new ItemCardapioEntity(
                null, "Prato Teste", "Descrição do prato", new BigDecimal("10.00"), false, null, restaurante.getId()));

        mockMvc.perform(delete("/cardapio/" + item.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(delete("/restaurante/" + restaurante.getId()))
                .andExpect(status().isNoContent());
    }

    private RestauranteEntity criarRestauranteComDono() {
        TipoUsuarioEntity tipo = tipoUsuarioJpaRepository.save(new TipoUsuarioEntity(null, "Dono de Restaurante"));
        UsuarioEntity dono = usuarioJpaRepository.save(
                new UsuarioEntity(null, "Dono Teste", "dono.guard@email.com", "senha123", tipo.getId()));
        return restauranteJpaRepository.save(
                new RestauranteEntity(null, "Restaurante Guard", "Rua X, 1", "Brasileira", "08:00-22:00", dono.getId()));
    }
}
