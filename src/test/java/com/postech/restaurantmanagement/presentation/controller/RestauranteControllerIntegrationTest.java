package com.postech.restaurantmanagement.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.postech.restaurantmanagement.application.dto.RestauranteRequest;
import com.postech.restaurantmanagement.infrastructure.persistence.entity.TipoUsuarioEntity;
import com.postech.restaurantmanagement.infrastructure.persistence.entity.UsuarioEntity;
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

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RestauranteControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RestauranteJpaRepository restauranteJpaRepository;

    @Autowired
    private UsuarioJpaRepository usuarioJpaRepository;

    @Autowired
    private TipoUsuarioJpaRepository tipoUsuarioJpaRepository;

    private Long donoId;

    @BeforeEach
    void setUp() {
        restauranteJpaRepository.deleteAll();
        usuarioJpaRepository.deleteAll();
        tipoUsuarioJpaRepository.deleteAll();

        TipoUsuarioEntity tipoDono = tipoUsuarioJpaRepository.save(new TipoUsuarioEntity(null, "Dono de Restaurante"));
        UsuarioEntity dono = usuarioJpaRepository.save(
                new UsuarioEntity(null, "João Dono", "joao.dono@email.com", "senha123", tipoDono.getId()));
        donoId = dono.getId();
    }

    private RestauranteRequest criarRequestValido() {
        RestauranteRequest request = new RestauranteRequest();
        request.setNome("Sabor Caseiro");
        request.setEndereco("Rua das Flores, 123");
        request.setTipoCozinha("Brasileira");
        request.setHorarioFuncionamento("08:00-22:00");
        request.setDonoId(donoId);
        return request;
    }

    @Test
    void deveCriarRestauranteComSucesso() throws Exception {
        RestauranteRequest request = criarRequestValido();

        mockMvc.perform(post("/restaurante")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome").value("Sabor Caseiro"))
                .andExpect(jsonPath("$.donoId").value(donoId));
    }

    @Test
    void deveRetornarBadRequestQuandoCamposObrigatoriosAusentes() throws Exception {
        RestauranteRequest request = new RestauranteRequest();

        mockMvc.perform(post("/restaurante")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornarNotFoundQuandoDonoNaoExiste() throws Exception {
        RestauranteRequest request = criarRequestValido();
        request.setDonoId(9999L);

        mockMvc.perform(post("/restaurante")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRetornarNotFoundQuandoDonoNaoTemTipoCorreto() throws Exception {
        TipoUsuarioEntity tipoCliente = tipoUsuarioJpaRepository.save(new TipoUsuarioEntity(null, "Cliente"));
        UsuarioEntity cliente = usuarioJpaRepository.save(
                new UsuarioEntity(null, "Maria Cliente", "maria.cliente@email.com", "senha456", tipoCliente.getId()));

        RestauranteRequest request = criarRequestValido();
        request.setDonoId(cliente.getId());

        mockMvc.perform(post("/restaurante")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveListarTodosOsRestaurantes() throws Exception {
        mockMvc.perform(post("/restaurante")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(criarRequestValido())));

        mockMvc.perform(get("/restaurante"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void deveBuscarRestaurantePorId() throws Exception {
        String response = mockMvc.perform(post("/restaurante")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(criarRequestValido())))
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(get("/restaurante/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.nome").value("Sabor Caseiro"));
    }

    @Test
    void deveRetornarNotFoundAoBuscarRestauranteInexistente() throws Exception {
        mockMvc.perform(get("/restaurante/{id}", 9999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveAtualizarRestauranteComSucesso() throws Exception {
        String response = mockMvc.perform(post("/restaurante")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(criarRequestValido())))
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        RestauranteRequest atualizacao = criarRequestValido();
        atualizacao.setNome("Sabor Caseiro Renovado");
        atualizacao.setTipoCozinha("Fusion");

        mockMvc.perform(put("/restaurante/{id}", id)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(atualizacao)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Sabor Caseiro Renovado"))
                .andExpect(jsonPath("$.tipoCozinha").value("Fusion"));
    }

    @Test
    void deveRetornarNotFoundAoAtualizarRestauranteInexistente() throws Exception {
        mockMvc.perform(put("/restaurante/{id}", 9999L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(criarRequestValido())))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveDeletarRestauranteComSucesso() throws Exception {
        String response = mockMvc.perform(post("/restaurante")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(criarRequestValido())))
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(delete("/restaurante/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/restaurante/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRetornarNotFoundAoDeletarRestauranteInexistente() throws Exception {
        mockMvc.perform(delete("/restaurante/{id}", 9999L))
                .andExpect(status().isNotFound());
    }
}
