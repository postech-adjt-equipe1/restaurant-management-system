package com.postech.restaurantmanagement.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.postech.restaurantmanagement.application.dto.UsuarioRequest;
import com.postech.restaurantmanagement.infrastructure.persistence.entity.TipoUsuarioEntity;
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
 * Testes de integração do módulo Usuário.
 * <p>
 * Cobre o CRUD completo do endpoint {@code /usuario} e valida a integração
 * cruzada com o módulo de Tipo de Usuário (um usuário só pode existir
 * vinculado a um tipo de usuário já cadastrado).
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UsuarioControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UsuarioJpaRepository usuarioJpaRepository;

    @Autowired
    private TipoUsuarioJpaRepository tipoUsuarioJpaRepository;

    private Long tipoUsuarioId;

    @BeforeEach
    void setUp() {
        usuarioJpaRepository.deleteAll();
        tipoUsuarioJpaRepository.deleteAll();

        TipoUsuarioEntity tipo = tipoUsuarioJpaRepository.save(new TipoUsuarioEntity(null, "Cliente"));
        tipoUsuarioId = tipo.getId();
    }

    private UsuarioRequest criarRequestValido() {
        UsuarioRequest request = new UsuarioRequest();
        request.setNome("Maria Cliente");
        request.setEmail("maria.cliente@email.com");
        request.setSenha("senha123");
        request.setTipoUsuarioId(tipoUsuarioId);
        return request;
    }

    @Test
    void deveCriarUsuarioComSucesso() throws Exception {
        mockMvc.perform(post("/usuario")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(criarRequestValido())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome").value("Maria Cliente"))
                .andExpect(jsonPath("$.email").value("maria.cliente@email.com"))
                .andExpect(jsonPath("$.tipoUsuarioId").value(tipoUsuarioId));
    }

    @Test
    void deveRetornarBadRequestQuandoCamposObrigatoriosAusentes() throws Exception {
        UsuarioRequest request = new UsuarioRequest();

        mockMvc.perform(post("/usuario")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornarBadRequestQuandoEmailForInvalido() throws Exception {
        UsuarioRequest request = criarRequestValido();
        request.setEmail("email-invalido");

        mockMvc.perform(post("/usuario")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornarNotFoundQuandoTipoUsuarioNaoExiste() throws Exception {
        UsuarioRequest request = criarRequestValido();
        request.setTipoUsuarioId(9999L);

        mockMvc.perform(post("/usuario")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveListarUsuarios() throws Exception {
        mockMvc.perform(post("/usuario")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(criarRequestValido())));

        mockMvc.perform(get("/usuario"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nome").value("Maria Cliente"));
    }

    @Test
    void deveBuscarUsuarioPorId() throws Exception {
        String response = mockMvc.perform(post("/usuario")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(criarRequestValido())))
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(get("/usuario/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.nome").value("Maria Cliente"));
    }

    @Test
    void deveRetornarNotFoundAoBuscarUsuarioInexistente() throws Exception {
        mockMvc.perform(get("/usuario/{id}", 9999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveAtualizarUsuarioComSucesso() throws Exception {
        String response = mockMvc.perform(post("/usuario")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(criarRequestValido())))
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        UsuarioRequest atualizacao = criarRequestValido();
        atualizacao.setNome("Maria Atualizada");
        atualizacao.setEmail("maria.atualizada@email.com");

        mockMvc.perform(put("/usuario/{id}", id)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(atualizacao)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Maria Atualizada"))
                .andExpect(jsonPath("$.email").value("maria.atualizada@email.com"));
    }

    @Test
    void deveRetornarNotFoundAoAtualizarUsuarioInexistente() throws Exception {
        mockMvc.perform(put("/usuario/{id}", 9999L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(criarRequestValido())))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRetornarNotFoundAoAtualizarUsuarioComTipoUsuarioInexistente() throws Exception {
        String response = mockMvc.perform(post("/usuario")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(criarRequestValido())))
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        UsuarioRequest atualizacao = criarRequestValido();
        atualizacao.setTipoUsuarioId(9999L);

        mockMvc.perform(put("/usuario/{id}", id)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(atualizacao)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveDeletarUsuarioComSucesso() throws Exception {
        String response = mockMvc.perform(post("/usuario")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(criarRequestValido())))
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(delete("/usuario/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/usuario/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRetornarNotFoundAoDeletarUsuarioInexistente() throws Exception {
        mockMvc.perform(delete("/usuario/{id}", 9999L))
                .andExpect(status().isNotFound());
    }
}