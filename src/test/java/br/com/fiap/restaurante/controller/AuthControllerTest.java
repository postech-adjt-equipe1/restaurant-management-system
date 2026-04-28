package br.com.fiap.restaurante.controller;

import br.com.fiap.restaurante.dto.LoginRequest;
import br.com.fiap.restaurante.exception.InvalidCredentialsException;
import br.com.fiap.restaurante.model.Address;
import br.com.fiap.restaurante.model.User;
import br.com.fiap.restaurante.model.UserType;
import br.com.fiap.restaurante.security.JwtService;
import br.com.fiap.restaurante.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtService jwtService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .nome("João Silva")
                .email("joao@email.com")
                .login("joao.silva")
                .senha("encodedPass")
                .tipo(UserType.CUSTOMER)
                .dataUltimaAlteracao(LocalDateTime.now())
                .endereco(Address.builder()
                        .logradouro("Rua das Flores")
                        .numero("123")
                        .bairro("Centro")
                        .cidade("São Paulo")
                        .estado("SP")
                        .cep("01310-100")
                        .build())
                .build();
    }

    @Test
    @WithMockUser
    void login_deveRetornar200ComToken_quandoCredenciaisValidas() throws Exception {
        LoginRequest request = new LoginRequest("joao.silva", "senha123");

        when(userService.validateLogin("joao.silva", "senha123")).thenReturn(user);
        when(jwtService.generateToken(eq("joao.silva"), any(Map.class))).thenReturn("mocked-jwt-token");
        when(jwtService.getExpirationMs()).thenReturn(3_600_000L);

        mockMvc.perform(post("/auth/login").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mocked-jwt-token"))
                .andExpect(jsonPath("$.tipo").value("Bearer"))
                .andExpect(jsonPath("$.login").value("joao.silva"))
                .andExpect(jsonPath("$.nome").value("João Silva"))
                .andExpect(jsonPath("$.userType").value("CUSTOMER"))
                .andExpect(jsonPath("$.expiresIn").value(3_600_000));
    }

    @Test
    @WithMockUser
    void login_deveRetornar401_quandoCredenciaisInvalidas() throws Exception {
        LoginRequest request = new LoginRequest("joao.silva", "errada");

        when(userService.validateLogin(anyString(), anyString())).thenThrow(new InvalidCredentialsException());

        mockMvc.perform(post("/auth/login").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void login_deveRetornar400_quandoLoginEmBranco() throws Exception {
        LoginRequest request = new LoginRequest("", "senha123");

        mockMvc.perform(post("/auth/login").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void login_deveRetornar400_quandoSenhaEmBranco() throws Exception {
        LoginRequest request = new LoginRequest("joao.silva", "");

        mockMvc.perform(post("/auth/login").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void login_deveRetornar400_quandoBodyVazio() throws Exception {
        mockMvc.perform(post("/auth/login").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }
}