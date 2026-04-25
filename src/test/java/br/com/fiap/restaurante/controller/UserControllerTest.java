package br.com.fiap.restaurante.controller;

import br.com.fiap.restaurante.dto.AddressRequestDTO;
import br.com.fiap.restaurante.dto.UserChangePasswordRequestDTO;
import br.com.fiap.restaurante.dto.UserCreateRequestDTO;
import br.com.fiap.restaurante.dto.UserLoginRequestDTO;
import br.com.fiap.restaurante.dto.UserUpdateRequestDTO;
import br.com.fiap.restaurante.exception.DuplicateEmailException;
import br.com.fiap.restaurante.exception.DuplicateLoginException;
import br.com.fiap.restaurante.exception.InvalidCredentialsException;
import br.com.fiap.restaurante.exception.InvalidPasswordException;
import br.com.fiap.restaurante.exception.UserNotFoundException;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtService jwtService;

    private User user;
    private AddressRequestDTO addressDTO;

    @BeforeEach
    void setUp() {
        addressDTO = AddressRequestDTO.builder()
                .logradouro("Rua das Flores")
                .numero("123")
                .bairro("Centro")
                .cidade("São Paulo")
                .estado("SP")
                .cep("01310-100")
                .build();

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

    // --- POST /api/v1/usuarios ---

    @Test
    @WithMockUser
    void create_deveRetornar201_quandoDadosValidos() throws Exception {
        UserCreateRequestDTO request = UserCreateRequestDTO.builder()
                .nome("João Silva")
                .email("joao@email.com")
                .login("joao.silva")
                .senha("senha123")
                .tipo(UserType.CUSTOMER)
                .endereco(addressDTO)
                .build();

        when(userService.create(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/api/v1/usuarios").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("João Silva"))
                .andExpect(jsonPath("$.email").value("joao@email.com"))
                .andExpect(jsonPath("$.login").value("joao.silva"));
    }

    @Test
    @WithMockUser
    void create_deveRetornar400_quandoNomeEmBranco() throws Exception {
        UserCreateRequestDTO request = UserCreateRequestDTO.builder()
                .nome("")
                .email("joao@email.com")
                .login("joao.silva")
                .senha("senha123")
                .tipo(UserType.CUSTOMER)
                .endereco(addressDTO)
                .build();

        mockMvc.perform(post("/api/v1/usuarios").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void create_deveRetornar400_quandoSenhaCurtaDemais() throws Exception {
        UserCreateRequestDTO request = UserCreateRequestDTO.builder()
                .nome("João Silva")
                .email("joao@email.com")
                .login("joao.silva")
                .senha("123")
                .tipo(UserType.CUSTOMER)
                .endereco(addressDTO)
                .build();

        mockMvc.perform(post("/api/v1/usuarios").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void create_deveRetornar409_quandoEmailDuplicado() throws Exception {
        UserCreateRequestDTO request = UserCreateRequestDTO.builder()
                .nome("João Silva")
                .email("joao@email.com")
                .login("joao.silva")
                .senha("senha123")
                .tipo(UserType.CUSTOMER)
                .endereco(addressDTO)
                .build();

        when(userService.create(any())).thenThrow(new DuplicateEmailException("joao@email.com"));

        mockMvc.perform(post("/api/v1/usuarios").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser
    void create_deveRetornar409_quandoLoginDuplicado() throws Exception {
        UserCreateRequestDTO request = UserCreateRequestDTO.builder()
                .nome("João Silva")
                .email("joao@email.com")
                .login("joao.silva")
                .senha("senha123")
                .tipo(UserType.CUSTOMER)
                .endereco(addressDTO)
                .build();

        when(userService.create(any())).thenThrow(new DuplicateLoginException("joao.silva"));

        mockMvc.perform(post("/api/v1/usuarios").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    // --- GET /api/v1/usuarios?nome=X ---

    @Test
    @WithMockUser
    void findByName_deveRetornar200ComLista_quandoEncontrado() throws Exception {
        when(userService.findByName("João")).thenReturn(List.of(user));

        mockMvc.perform(get("/api/v1/usuarios").param("nome", "João"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].nome").value("João Silva"))
                .andExpect(jsonPath("$[0].login").value("joao.silva"));
    }

    @Test
    @WithMockUser
    void findByName_deveRetornar200ComListaVazia_quandoNaoEncontrado() throws Exception {
        when(userService.findByName("XYZ")).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/usuarios").param("nome", "XYZ"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    // --- GET /api/v1/usuarios/{id} ---

    @Test
    @WithMockUser
    void findById_deveRetornar200_quandoEncontrado() throws Exception {
        when(userService.findById(1L)).thenReturn(user);

        mockMvc.perform(get("/api/v1/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("João Silva"))
                .andExpect(jsonPath("$.email").value("joao@email.com"));
    }

    @Test
    @WithMockUser
    void findById_deveRetornar404_quandoNaoEncontrado() throws Exception {
        when(userService.findById(99L)).thenThrow(new UserNotFoundException(99L));

        mockMvc.perform(get("/api/v1/usuarios/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void findById_deveRetornar400_quandoIdNaoNumerico() throws Exception {
        mockMvc.perform(get("/api/v1/usuarios/abc"))
                .andExpect(status().isBadRequest());
    }

    // --- PUT /api/v1/usuarios/{id} ---

    @Test
    @WithMockUser
    void update_deveRetornar200_quandoDadosValidos() throws Exception {
        UserUpdateRequestDTO request = UserUpdateRequestDTO.builder()
                .nome("João Atualizado")
                .email("joao@email.com")
                .tipo(UserType.OWNER)
                .endereco(addressDTO)
                .build();

        when(userService.update(eq(1L), any(User.class))).thenReturn(user);

        mockMvc.perform(put("/api/v1/usuarios/1").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser
    void update_deveRetornar404_quandoNaoEncontrado() throws Exception {
        UserUpdateRequestDTO request = UserUpdateRequestDTO.builder()
                .nome("João")
                .email("joao@email.com")
                .tipo(UserType.CUSTOMER)
                .endereco(addressDTO)
                .build();

        when(userService.update(eq(99L), any())).thenThrow(new UserNotFoundException(99L));

        mockMvc.perform(put("/api/v1/usuarios/99").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void update_deveRetornar409_quandoEmailDuplicado() throws Exception {
        UserUpdateRequestDTO request = UserUpdateRequestDTO.builder()
                .nome("João")
                .email("outro@email.com")
                .tipo(UserType.CUSTOMER)
                .endereco(addressDTO)
                .build();

        when(userService.update(eq(1L), any())).thenThrow(new DuplicateEmailException("outro@email.com"));

        mockMvc.perform(put("/api/v1/usuarios/1").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    // --- DELETE /api/v1/usuarios/{id} ---

    @Test
    @WithMockUser
    void delete_deveRetornar204_quandoEncontrado() throws Exception {
        doNothing().when(userService).delete(1L);

        mockMvc.perform(delete("/api/v1/usuarios/1").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void delete_deveRetornar404_quandoNaoEncontrado() throws Exception {
        doThrow(new UserNotFoundException(99L)).when(userService).delete(99L);

        mockMvc.perform(delete("/api/v1/usuarios/99").with(csrf()))
                .andExpect(status().isNotFound());
    }

    // --- POST /api/v1/usuarios/login ---

    @Test
    @WithMockUser
    void login_deveRetornar200_quandoCredenciaisValidas() throws Exception {
        UserLoginRequestDTO request = new UserLoginRequestDTO("joao.silva", "senha123");
        when(userService.validateLogin("joao.silva", "senha123")).thenReturn(user);

        mockMvc.perform(post("/api/v1/usuarios/login").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("joao.silva"))
                .andExpect(jsonPath("$.nome").value("João Silva"));
    }

    @Test
    @WithMockUser
    void login_deveRetornar401_quandoCredenciaisInvalidas() throws Exception {
        UserLoginRequestDTO request = new UserLoginRequestDTO("joao.silva", "errada");
        when(userService.validateLogin(anyString(), anyString())).thenThrow(new InvalidCredentialsException());

        mockMvc.perform(post("/api/v1/usuarios/login").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    // --- PATCH /api/v1/usuarios/{id}/senha ---

    @Test
    @WithMockUser
    void changePassword_deveRetornar204_quandoSenhaAlteradaComSucesso() throws Exception {
        UserChangePasswordRequestDTO request = new UserChangePasswordRequestDTO("senha123", "novaSenha456");
        doNothing().when(userService).changePassword(anyLong(), anyString(), anyString());

        mockMvc.perform(patch("/api/v1/usuarios/1/senha").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void changePassword_deveRetornar400_quandoSenhaAtualErrada() throws Exception {
        UserChangePasswordRequestDTO request = new UserChangePasswordRequestDTO("errada", "novaSenha456");
        doThrow(new InvalidPasswordException()).when(userService).changePassword(eq(1L), eq("errada"), anyString());

        mockMvc.perform(patch("/api/v1/usuarios/1/senha").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void changePassword_deveRetornar400_quandoNovaSenhaCurtaDemais() throws Exception {
        UserChangePasswordRequestDTO request = new UserChangePasswordRequestDTO("senha123", "curta");

        mockMvc.perform(patch("/api/v1/usuarios/1/senha").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void changePassword_deveRetornar404_quandoUsuarioNaoEncontrado() throws Exception {
        UserChangePasswordRequestDTO request = new UserChangePasswordRequestDTO("senha123", "novaSenha456");
        doThrow(new UserNotFoundException(99L)).when(userService).changePassword(eq(99L), anyString(), anyString());

        mockMvc.perform(patch("/api/v1/usuarios/99/senha").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }
}