package com.postech.restaurantmanagement.application.service;

import com.postech.restaurantmanagement.application.dto.UsuarioRequest;
import com.postech.restaurantmanagement.application.dto.UsuarioResponse;
import com.postech.restaurantmanagement.domain.model.Usuario;
import com.postech.restaurantmanagement.domain.repository.TipoUsuarioRepository;
import com.postech.restaurantmanagement.domain.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private TipoUsuarioRepository tipoUsuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;
    private UsuarioRequest request;

    @BeforeEach
    void setUp() {
        usuario = new Usuario(1L, "João Silva", "joao@email.com", "senha123", 1L);
        request = new UsuarioRequest();
        request.setNome("João Silva");
        request.setEmail("joao@email.com");
        request.setSenha("senha123");
        request.setTipoUsuarioId(1L);
    }

    @Test
    void criar_deveRetornarUsuarioCriado() {
        when(tipoUsuarioRepository.existsById(1L)).thenReturn(true);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        UsuarioResponse response = usuarioService.criar(request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getNome()).isEqualTo("João Silva");
        assertThat(response.getEmail()).isEqualTo("joao@email.com");
        assertThat(response.getTipoUsuarioId()).isEqualTo(1L);
    }

    @Test
    void criar_deveLancarExcecaoQuandoTipoUsuarioNaoExiste() {
        when(tipoUsuarioRepository.existsById(99L)).thenReturn(false);
        request.setTipoUsuarioId(99L);

        assertThatThrownBy(() -> usuarioService.criar(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("99");

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void listarTodos_deveRetornarListaCompleta() {
        Usuario usuario2 = new Usuario(2L, "Maria", "maria@email.com", "senha456", 2L);
        when(usuarioRepository.findAll()).thenReturn(Arrays.asList(usuario, usuario2));

        List<UsuarioResponse> lista = usuarioService.listarTodos();

        assertThat(lista).hasSize(2);
        assertThat(lista.get(0).getNome()).isEqualTo("João Silva");
        assertThat(lista.get(1).getNome()).isEqualTo("Maria");
    }

    @Test
    void listarTodos_deveRetornarListaVaziaQuandoNaoHaDados() {
        when(usuarioRepository.findAll()).thenReturn(List.of());

        List<UsuarioResponse> lista = usuarioService.listarTodos();

        assertThat(lista).isEmpty();
    }

    @Test
    void buscarPorId_deveRetornarUsuarioQuandoEncontrado() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        UsuarioResponse response = usuarioService.buscarPorId(1L);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getEmail()).isEqualTo("joao@email.com");
    }

    @Test
    void buscarPorId_deveLancarExcecaoQuandoNaoEncontrado() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.buscarPorId(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("99");
    }

    @Test
    void atualizar_deveAtualizarDadosComSucesso() {
        UsuarioRequest novoRequest = new UsuarioRequest();
        novoRequest.setNome("João Atualizado");
        novoRequest.setEmail("joao.novo@email.com");
        novoRequest.setSenha("novaSenha");
        novoRequest.setTipoUsuarioId(2L);

        Usuario atualizado = new Usuario(1L, "João Atualizado", "joao.novo@email.com", "novaSenha", 2L);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(tipoUsuarioRepository.existsById(2L)).thenReturn(true);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(atualizado);

        UsuarioResponse response = usuarioService.atualizar(1L, novoRequest);

        assertThat(response.getNome()).isEqualTo("João Atualizado");
        assertThat(response.getEmail()).isEqualTo("joao.novo@email.com");
        assertThat(response.getTipoUsuarioId()).isEqualTo(2L);
    }

    @Test
    void atualizar_deveLancarExcecaoQuandoUsuarioNaoExiste() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.atualizar(99L, request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("99");

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void atualizar_deveLancarExcecaoQuandoTipoUsuarioNaoExiste() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(tipoUsuarioRepository.existsById(99L)).thenReturn(false);
        request.setTipoUsuarioId(99L);

        assertThatThrownBy(() -> usuarioService.atualizar(1L, request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("99");

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void deletar_deveDeletarComSucesso() {
        when(usuarioRepository.existsById(1L)).thenReturn(true);
        doNothing().when(usuarioRepository).deleteById(1L);

        usuarioService.deletar(1L);

        verify(usuarioRepository, times(1)).deleteById(1L);
    }

    @Test
    void deletar_deveLancarExcecaoQuandoUsuarioNaoExiste() {
        when(usuarioRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> usuarioService.deletar(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("99");

        verify(usuarioRepository, never()).deleteById(any());
    }
}
