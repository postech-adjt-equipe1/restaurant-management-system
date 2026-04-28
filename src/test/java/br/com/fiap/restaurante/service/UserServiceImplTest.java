package br.com.fiap.restaurante.service;

import br.com.fiap.restaurante.exception.DuplicateEmailException;
import br.com.fiap.restaurante.exception.DuplicateLoginException;
import br.com.fiap.restaurante.exception.InvalidCredentialsException;
import br.com.fiap.restaurante.exception.InvalidPasswordException;
import br.com.fiap.restaurante.exception.UserNotFoundException;
import br.com.fiap.restaurante.model.Address;
import br.com.fiap.restaurante.model.User;
import br.com.fiap.restaurante.model.UserType;
import br.com.fiap.restaurante.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .nome("João Silva")
                .email("joao@email.com")
                .login("joao.silva")
                .senha("senha123")
                .tipo(UserType.CUSTOMER)
                .endereco(Address.builder()
                        .logradouro("Rua A")
                        .numero("100")
                        .bairro("Centro")
                        .cidade("São Paulo")
                        .estado("SP")
                        .cep("01310-100")
                        .build())
                .build();
    }

    // --- create ---

    @Test
    void create_deveRetornarUsuarioCriado_quandoDadosValidos() {
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(userRepository.existsByLogin(user.getLogin())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedSenha");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.create(user);

        assertThat(result).isEqualTo(user);
        verify(passwordEncoder).encode("senha123");
        verify(userRepository).save(user);
    }

    @Test
    void create_deveLancarDuplicateEmailException_quandoEmailJaExiste() {
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> userService.create(user))
                .isInstanceOf(DuplicateEmailException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    void create_deveLancarDuplicateLoginException_quandoLoginJaExiste() {
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(userRepository.existsByLogin(user.getLogin())).thenReturn(true);

        assertThatThrownBy(() -> userService.create(user))
                .isInstanceOf(DuplicateLoginException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    void create_deveEncodarSenha_antesDeeSalvar() {
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(userRepository.existsByLogin(any())).thenReturn(false);
        when(passwordEncoder.encode("senha123")).thenReturn("$2a$encodado");
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        userService.create(user);

        assertThat(user.getSenha()).isEqualTo("$2a$encodado");
    }

    // --- findById ---

    @Test
    void findById_deveRetornarUsuario_quandoEncontrado() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.findById(1L);

        assertThat(result).isEqualTo(user);
    }

    @Test
    void findById_deveLancarUserNotFoundException_quandoNaoEncontrado() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findById(99L))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("99");
    }

    // --- findByName ---

    @Test
    void findByName_deveRetornarLista_quandoEncontrados() {
        when(userRepository.findByNomeContainingIgnoreCase("João")).thenReturn(List.of(user));

        List<User> result = userService.findByName("João");

        assertThat(result).hasSize(1).contains(user);
    }

    @Test
    void findByName_deveRetornarListaVazia_quandoNenhumEncontrado() {
        when(userRepository.findByNomeContainingIgnoreCase("XYZ")).thenReturn(List.of());

        List<User> result = userService.findByName("XYZ");

        assertThat(result).isEmpty();
    }

    // --- update ---

    @Test
    void update_deveRetornarUsuarioAtualizado_quandoDadosValidos() {
        User dadosAtualizados = User.builder()
                .nome("João Atualizado")
                .email("joao@email.com")
                .tipo(UserType.OWNER)
                .endereco(user.getEndereco())
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.update(1L, dadosAtualizados);

        assertThat(result).isNotNull();
        verify(userRepository).save(user);
    }

    @Test
    void update_deveLancarUserNotFoundException_quandoNaoEncontrado() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.update(99L, user))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void update_deveLancarDuplicateEmailException_quandoNovoEmailJaExiste() {
        User dadosAtualizados = User.builder()
                .nome("João")
                .email("outro@email.com")
                .tipo(UserType.CUSTOMER)
                .endereco(user.getEndereco())
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("outro@email.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.update(1L, dadosAtualizados))
                .isInstanceOf(DuplicateEmailException.class);
    }

    @Test
    void update_naoDeveLancarExcecao_quandoEmailNaoMudou() {
        User dadosAtualizados = User.builder()
                .nome("Novo Nome")
                .email("joao@email.com")
                .tipo(UserType.CUSTOMER)
                .endereco(user.getEndereco())
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);

        userService.update(1L, dadosAtualizados);

        verify(userRepository, never()).existsByEmail(any());
    }

    // --- changePassword ---

    @Test
    void changePassword_deveTrocarSenha_quandoSenhaAtualCorreta() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("senha123", user.getSenha())).thenReturn(true);
        when(passwordEncoder.encode("novaSenha123")).thenReturn("encodedNova");

        userService.changePassword(1L, "senha123", "novaSenha123");

        assertThat(user.getSenha()).isEqualTo("encodedNova");
        verify(userRepository).save(user);
    }

    @Test
    void changePassword_deveLancarUserNotFoundException_quandoNaoEncontrado() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.changePassword(99L, "senha", "nova"))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void changePassword_deveLancarInvalidPasswordException_quandoSenhaAtualIncorreta() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("errada", user.getSenha())).thenReturn(false);

        assertThatThrownBy(() -> userService.changePassword(1L, "errada", "novaSenha123"))
                .isInstanceOf(InvalidPasswordException.class);

        verify(userRepository, never()).save(any());
    }

    // --- validateLogin ---

    @Test
    void validateLogin_deveRetornarUsuario_quandoCredenciaisValidas() {
        when(userRepository.findByLogin("joao.silva")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("senha123", user.getSenha())).thenReturn(true);

        User result = userService.validateLogin("joao.silva", "senha123");

        assertThat(result).isEqualTo(user);
    }

    @Test
    void validateLogin_deveLancarInvalidCredentialsException_quandoLoginInexistente() {
        when(userRepository.findByLogin("naoexiste")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.validateLogin("naoexiste", "senha"))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void validateLogin_deveLancarInvalidCredentialsException_quandoSenhaIncorreta() {
        when(userRepository.findByLogin("joao.silva")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("errada", user.getSenha())).thenReturn(false);

        assertThatThrownBy(() -> userService.validateLogin("joao.silva", "errada"))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    // --- delete ---

    @Test
    void delete_deveDeletarUsuario_quandoEncontrado() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.delete(1L);

        verify(userRepository).delete(user);
    }

    @Test
    void delete_deveLancarUserNotFoundException_quandoNaoEncontrado() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.delete(99L))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("99");
    }
}
