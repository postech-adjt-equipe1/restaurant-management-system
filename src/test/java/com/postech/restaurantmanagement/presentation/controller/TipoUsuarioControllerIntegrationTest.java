package com.postech.restaurantmanagement.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.postech.restaurantmanagement.application.dto.TipoUsuarioRequest;
import com.postech.restaurantmanagement.infrastructure.persistence.repository.TipoUsuarioJpaRepository;
import com.postech.restaurantmanagement.infrastructure.persistence.repository.UsuarioJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de integração do módulo Tipo de Usuário.
 * Cobre o CRUD completo do endpoint {@code /tipo-usuario}.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TipoUsuarioControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TipoUsuarioJpaRepository tipoUsuarioJpaRepository;

    @Autowired
    private UsuarioJpaRepository usuarioJpaRepository;

    @BeforeEach
    void setUp() {
        usuarioJpaRepository.deleteAll();
        tipoUsuarioJpaRepository.deleteAll();
    }

    private TipoUsuarioRequest criarRequestValido() {
        return new TipoUsuarioRequest("Dono de Restaurante");
    }

    @Test
    void deveCriarTipoUsuarioComSucesso() throws Exception {
        mockMvc.perform(post("/tipo-usuario")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(criarRequestValido())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome").value("Dono de Restaurante"));
    }

    @Test
    void deveRetornarBadRequestQuandoNomeAusente() throws Exception {
        TipoUsuarioRequest request = new TipoUsuarioRequest();

        mockMvc.perform(post("/tipo-usuario")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveListarTiposDeUsuario() throws Exception {
        mockMvc.perform(post("/tipo-usuario")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(criarRequestValido())));

        mockMvc.perform(get("/tipo-usuario"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nome").value("Dono de Restaurante"));
    }

    @Test
    void deveBuscarTipoUsuarioPorId() throws Exception {
        String response = mockMvc.perform(post("/tipo-usuario")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(criarRequestValido())))
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(get("/tipo-usuario/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.nome").value("Dono de Restaurante"));
    }

    @Test
    void deveRetornarNotFoundAoBuscarTipoUsuarioInexistente() throws Exception {
        mockMvc.perform(get("/tipo-usuario/{id}", 9999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveAtualizarTipoUsuarioComSucesso() throws Exception {
        String response = mockMvc.perform(post("/tipo-usuario")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(criarRequestValido())))
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        TipoUsuarioRequest atualizacao = new TipoUsuarioRequest("Cliente");

        mockMvc.perform(put("/tipo-usuario/{id}", id)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(atualizacao)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Cliente"));
    }

    @Test
    void deveRetornarNotFoundAoAtualizarTipoUsuarioInexistente() throws Exception {
        mockMvc.perform(put("/tipo-usuario/{id}", 9999L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(criarRequestValido())))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveDeletarTipoUsuarioComSucesso() throws Exception {
        String response = mockMvc.perform(post("/tipo-usuario")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(criarRequestValido())))
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(delete("/tipo-usuario/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/tipo-usuario/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRetornarNotFoundAoDeletarTipoUsuarioInexistente() throws Exception {
        mockMvc.perform(delete("/tipo-usuario/{id}", 9999L))
                .andExpect(status().isNotFound());
    }
}