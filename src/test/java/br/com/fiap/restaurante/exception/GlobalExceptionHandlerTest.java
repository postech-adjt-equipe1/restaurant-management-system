package br.com.fiap.restaurante.exception;

import br.com.fiap.restaurante.controller.UserController;
import br.com.fiap.restaurante.model.Address;
import br.com.fiap.restaurante.model.User;
import br.com.fiap.restaurante.model.UserType;
import br.com.fiap.restaurante.security.JwtService;
import br.com.fiap.restaurante.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

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
    void deveRetornar404_quandoUserNotFoundException() throws Exception {
        when(userService.findById(anyLong())).thenThrow(new UserNotFoundException(42L));

        mockMvc.perform(get("/api/v1/usuarios/42"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").value("Usuário não encontrado com id: 42"));
    }

    @Test
    @WithMockUser
    void deveRetornar409_quandoDuplicateEmailException() throws Exception {
        when(userService.create(any())).thenThrow(new DuplicateEmailException("joao@email.com"));

        String body = """
                {
                  "nome": "João Silva",
                  "email": "joao@email.com",
                  "login": "joao.silva",
                  "senha": "senha123",
                  "tipo": "CUSTOMER",
                  "endereco": {
                    "logradouro": "Rua das Flores",
                    "numero": "123",
                    "bairro": "Centro",
                    "cidade": "São Paulo",
                    "estado": "SP",
                    "cep": "01310-100"
                  }
                }
                """;

        mockMvc.perform(post("/api/v1/usuarios").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    @WithMockUser
    void deveRetornar409_quandoDuplicateLoginException() throws Exception {
        when(userService.create(any())).thenThrow(new DuplicateLoginException("joao.silva"));

        String body = """
                {
                  "nome": "João Silva",
                  "email": "joao@email.com",
                  "login": "joao.silva",
                  "senha": "senha123",
                  "tipo": "CUSTOMER",
                  "endereco": {
                    "logradouro": "Rua das Flores",
                    "numero": "123",
                    "bairro": "Centro",
                    "cidade": "São Paulo",
                    "estado": "SP",
                    "cep": "01310-100"
                  }
                }
                """;

        mockMvc.perform(post("/api/v1/usuarios").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    @WithMockUser
    void deveRetornar400_quandoValidacaoFalha() throws Exception {
        String bodyInvalido = """
                {
                  "nome": "",
                  "email": "nao-e-email",
                  "login": "ab",
                  "senha": "123",
                  "tipo": null
                }
                """;

        mockMvc.perform(post("/api/v1/usuarios").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyInvalido))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Erro de validação"))
                .andExpect(jsonPath("$.campos").exists());
    }

    @Test
    @WithMockUser
    void deveRetornar400_quandoIdNaoENumero() throws Exception {
        mockMvc.perform(get("/api/v1/usuarios/nao-numero"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @WithMockUser
    void deveRetornar401_quandoInvalidCredentialsException() throws Exception {
        when(userService.findById(anyLong())).thenThrow(new InvalidCredentialsException());

        mockMvc.perform(get("/api/v1/usuarios/1"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }
}