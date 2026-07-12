package com.postech.restaurantmanagement.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.postech.restaurantmanagement.application.dto.ItemCardapioRequest;
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

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ItemCardapioControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ItemCardapioJpaRepository itemCardapioJpaRepository;

    @Autowired
    private RestauranteJpaRepository restauranteJpaRepository;

    @Autowired
    private UsuarioJpaRepository usuarioJpaRepository;

    @Autowired
    private TipoUsuarioJpaRepository tipoUsuarioJpaRepository;

    private Long restauranteId;

    @BeforeEach
    void setUp() {
        itemCardapioJpaRepository.deleteAll();
        restauranteJpaRepository.deleteAll();
        usuarioJpaRepository.deleteAll();
        tipoUsuarioJpaRepository.deleteAll();

        TipoUsuarioEntity tipoDono = tipoUsuarioJpaRepository.save(new TipoUsuarioEntity(null, "Dono de Restaurante"));
        UsuarioEntity dono = usuarioJpaRepository.save(
                new UsuarioEntity(null, "João Dono", "joao.cardapio@email.com", "senha123", tipoDono.getId()));
        RestauranteEntity restaurante = restauranteJpaRepository.save(
                new RestauranteEntity(null, "Sabor Caseiro", "Rua das Flores, 123",
                        "Brasileira", "08:00-22:00", dono.getId()));
        restauranteId = restaurante.getId();
    }

    private ItemCardapioRequest criarRequestValido() {
        ItemCardapioRequest request = new ItemCardapioRequest();
        request.setNome("Feijoada");
        request.setDescricao("Feijoada completa com acompanhamentos");
        request.setPreco(new BigDecimal("39.90"));
        request.setApenasLocal(false);
        request.setCaminhoFoto("/fotos/feijoada.jpg");
        return request;
    }

    @Test
    void deveCriarItemDeCardapioComSucesso() throws Exception {
        ItemCardapioRequest request = criarRequestValido();

        mockMvc.perform(post("/restaurante/{restauranteId}/cardapio", restauranteId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome").value("Feijoada"))
                .andExpect(jsonPath("$.preco").value(39.90))
                .andExpect(jsonPath("$.apenasLocal").value(false))
                .andExpect(jsonPath("$.restauranteId").value(restauranteId));
    }

    @Test
    void deveRetornarBadRequestQuandoCamposObrigatoriosAusentes() throws Exception {
        ItemCardapioRequest request = new ItemCardapioRequest();

        mockMvc.perform(post("/restaurante/{restauranteId}/cardapio", restauranteId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornarBadRequestQuandoPrecoNaoForPositivo() throws Exception {
        ItemCardapioRequest request = criarRequestValido();
        request.setPreco(new BigDecimal("-1.00"));

        mockMvc.perform(post("/restaurante/{restauranteId}/cardapio", restauranteId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornarNotFoundQuandoRestauranteNaoExiste() throws Exception {
        ItemCardapioRequest request = criarRequestValido();

        mockMvc.perform(post("/restaurante/{restauranteId}/cardapio", 9999L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveListarItensDeCardapioPorRestaurante() throws Exception {
        mockMvc.perform(post("/restaurante/{restauranteId}/cardapio", restauranteId)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(criarRequestValido())));

        mockMvc.perform(get("/restaurante/{restauranteId}/cardapio", restauranteId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nome").value("Feijoada"));
    }

    @Test
    void deveRetornarNotFoundAoListarCardapioDeRestauranteInexistente() throws Exception {
        mockMvc.perform(get("/restaurante/{restauranteId}/cardapio", 9999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveBuscarItemDeCardapioPorId() throws Exception {
        String response = mockMvc.perform(post("/restaurante/{restauranteId}/cardapio", restauranteId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(criarRequestValido())))
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(get("/cardapio/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.nome").value("Feijoada"));
    }

    @Test
    void deveRetornarNotFoundAoBuscarItemDeCardapioInexistente() throws Exception {
        mockMvc.perform(get("/cardapio/{id}", 9999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveAtualizarItemDeCardapioComSucesso() throws Exception {
        String response = mockMvc.perform(post("/restaurante/{restauranteId}/cardapio", restauranteId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(criarRequestValido())))
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        ItemCardapioRequest atualizacao = criarRequestValido();
        atualizacao.setNome("Feijoada Premium");
        atualizacao.setPreco(new BigDecimal("49.90"));
        atualizacao.setApenasLocal(true);

        mockMvc.perform(put("/cardapio/{id}", id)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(atualizacao)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Feijoada Premium"))
                .andExpect(jsonPath("$.preco").value(49.90))
                .andExpect(jsonPath("$.apenasLocal").value(true));
    }

    @Test
    void deveRetornarNotFoundAoAtualizarItemDeCardapioInexistente() throws Exception {
        mockMvc.perform(put("/cardapio/{id}", 9999L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(criarRequestValido())))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveDeletarItemDeCardapioComSucesso() throws Exception {
        String response = mockMvc.perform(post("/restaurante/{restauranteId}/cardapio", restauranteId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(criarRequestValido())))
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(delete("/cardapio/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/cardapio/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRetornarNotFoundAoDeletarItemDeCardapioInexistente() throws Exception {
        mockMvc.perform(delete("/cardapio/{id}", 9999L))
                .andExpect(status().isNotFound());
    }
}