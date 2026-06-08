package br.com.fiap.restaurante.controller;

import br.com.fiap.restaurante.dto.TipoUsuarioRequestDTO;
import br.com.fiap.restaurante.exception.DuplicateTipoUsuarioException;
import br.com.fiap.restaurante.exception.TipoUsuarioNotFoundException;
import br.com.fiap.restaurante.model.TipoUsuario;
import br.com.fiap.restaurante.security.JwtService;
import br.com.fiap.restaurante.service.TipoUsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TipoUsuarioController.class)
class TipoUsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TipoUsuarioService tipoUsuarioService;

    @MockBean
    private JwtService jwtService;

    private TipoUsuario tipoUsuario;
    private TipoUsuarioRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        tipoUsuario = TipoUsuario.builder()
                .id(1L)
                .nome("Dono de Restaurante")
                .build();

        requestDTO = TipoUsuarioRequestDTO.builder()
                .nome("Dono de Restaurante")
                .build();
    }

    // --- POST /api/v1/tipo-usuario ---

    @Test
    @WithMockUser
    void create_deveRetornar201_quandoDadosValidos() throws Exception {
        when(tipoUsuarioService.create(any(TipoUsuario.class))).thenReturn(tipoUsuario);

        mockMvc.perform(post("/api/v1/tipo-usuario")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("Dono de Restaurante"));
    }

    @Test
    @WithMockUser
    void create_deveRetornar400_quandoNomeVazio() throws Exception {
        TipoUsuarioRequestDTO invalido = TipoUsuarioRequestDTO.builder().nome("").build();

        mockMvc.perform(post("/api/v1/tipo-usuario")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void create_deveRetornar409_quandoNomeDuplicado() throws Exception {
        when(tipoUsuarioService.create(any())).thenThrow(new DuplicateTipoUsuarioException("Dono de Restaurante"));

        mockMvc.perform(post("/api/v1/tipo-usuario")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isConflict());
    }

    // --- GET /api/v1/tipo-usuario ---

    @Test
    @WithMockUser
    void findAll_deveRetornar200ComLista() throws Exception {
        TipoUsuario outro = TipoUsuario.builder().id(2L).nome("Cliente").build();
        when(tipoUsuarioService.findAll()).thenReturn(List.of(tipoUsuario, outro));

        mockMvc.perform(get("/api/v1/tipo-usuario"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nome").value("Dono de Restaurante"))
                .andExpect(jsonPath("$[1].nome").value("Cliente"));
    }

    @Test
    @WithMockUser
    void findAll_deveRetornar200ComListaVazia() throws Exception {
        when(tipoUsuarioService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/tipo-usuario"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // --- GET /api/v1/tipo-usuario/{id} ---

    @Test
    @WithMockUser
    void findById_deveRetornar200_quandoEncontrado() throws Exception {
        when(tipoUsuarioService.findById(1L)).thenReturn(tipoUsuario);

        mockMvc.perform(get("/api/v1/tipo-usuario/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("Dono de Restaurante"));
    }

    @Test
    @WithMockUser
    void findById_deveRetornar404_quandoNaoEncontrado() throws Exception {
        when(tipoUsuarioService.findById(99L)).thenThrow(new TipoUsuarioNotFoundException(99L));

        mockMvc.perform(get("/api/v1/tipo-usuario/99"))
                .andExpect(status().isNotFound());
    }

    // --- PUT /api/v1/tipo-usuario/{id} ---

    @Test
    @WithMockUser
    void update_deveRetornar200_quandoDadosValidos() throws Exception {
        TipoUsuario atualizado = TipoUsuario.builder().id(1L).nome("Cliente VIP").build();
        when(tipoUsuarioService.update(eq(1L), any(TipoUsuario.class))).thenReturn(atualizado);

        TipoUsuarioRequestDTO updateRequest = TipoUsuarioRequestDTO.builder().nome("Cliente VIP").build();

        mockMvc.perform(put("/api/v1/tipo-usuario/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Cliente VIP"));
    }

    @Test
    @WithMockUser
    void update_deveRetornar404_quandoNaoEncontrado() throws Exception {
        when(tipoUsuarioService.update(eq(99L), any())).thenThrow(new TipoUsuarioNotFoundException(99L));

        mockMvc.perform(put("/api/v1/tipo-usuario/99")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void update_deveRetornar409_quandoNomeDuplicado() throws Exception {
        when(tipoUsuarioService.update(anyLong(), any())).thenThrow(new DuplicateTipoUsuarioException("Cliente"));

        mockMvc.perform(put("/api/v1/tipo-usuario/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isConflict());
    }

    // --- DELETE /api/v1/tipo-usuario/{id} ---

    @Test
    @WithMockUser
    void delete_deveRetornar204_quandoEncontrado() throws Exception {
        doNothing().when(tipoUsuarioService).delete(1L);

        mockMvc.perform(delete("/api/v1/tipo-usuario/1").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void delete_deveRetornar404_quandoNaoEncontrado() throws Exception {
        doThrow(new TipoUsuarioNotFoundException(99L)).when(tipoUsuarioService).delete(99L);

        mockMvc.perform(delete("/api/v1/tipo-usuario/99").with(csrf()))
                .andExpect(status().isNotFound());
    }
}
