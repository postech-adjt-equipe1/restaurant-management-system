package com.postech.restaurantmanagement.application.service;

import com.postech.restaurantmanagement.application.dto.TipoUsuarioRequest;
import com.postech.restaurantmanagement.application.dto.TipoUsuarioResponse;
import com.postech.restaurantmanagement.domain.model.TipoUsuario;
import com.postech.restaurantmanagement.domain.repository.TipoUsuarioRepository;
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
class TipoUsuarioServiceTest {

    @Mock
    private TipoUsuarioRepository tipoUsuarioRepository;

    @InjectMocks
    private TipoUsuarioService tipoUsuarioService;

    private TipoUsuario tipoUsuario;
    private TipoUsuarioRequest request;

    @BeforeEach
    void setUp() {
        tipoUsuario = new TipoUsuario(1L, "Dono de Restaurante");
        request = new TipoUsuarioRequest("Dono de Restaurante");
    }

    @Test
    void criar_deveRetornarTipoUsuarioCriado() {
        when(tipoUsuarioRepository.save(any(TipoUsuario.class))).thenReturn(tipoUsuario);

        TipoUsuarioResponse response = tipoUsuarioService.criar(request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getNome()).isEqualTo("Dono de Restaurante");
        verify(tipoUsuarioRepository, times(1)).save(any(TipoUsuario.class));
    }

    @Test
    void criar_deveSalvarComNomeCorreto() {
        TipoUsuario salvo = new TipoUsuario(2L, "Cliente");
        TipoUsuarioRequest clienteRequest = new TipoUsuarioRequest("Cliente");
        when(tipoUsuarioRepository.save(any(TipoUsuario.class))).thenReturn(salvo);

        TipoUsuarioResponse response = tipoUsuarioService.criar(clienteRequest);

        assertThat(response.getNome()).isEqualTo("Cliente");
        assertThat(response.getId()).isEqualTo(2L);
    }

    @Test
    void listarTodos_deveRetornarListaCompleta() {
        TipoUsuario tipo2 = new TipoUsuario(2L, "Cliente");
        when(tipoUsuarioRepository.findAll()).thenReturn(Arrays.asList(tipoUsuario, tipo2));

        List<TipoUsuarioResponse> lista = tipoUsuarioService.listarTodos();

        assertThat(lista).hasSize(2);
        assertThat(lista.get(0).getNome()).isEqualTo("Dono de Restaurante");
        assertThat(lista.get(1).getNome()).isEqualTo("Cliente");
    }

    @Test
    void listarTodos_deveRetornarListaVaziaQuandoNaoHaDados() {
        when(tipoUsuarioRepository.findAll()).thenReturn(List.of());

        List<TipoUsuarioResponse> lista = tipoUsuarioService.listarTodos();

        assertThat(lista).isEmpty();
    }

    @Test
    void buscarPorId_deveRetornarTipoUsuarioQuandoEncontrado() {
        when(tipoUsuarioRepository.findById(1L)).thenReturn(Optional.of(tipoUsuario));

        TipoUsuarioResponse response = tipoUsuarioService.buscarPorId(1L);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getNome()).isEqualTo("Dono de Restaurante");
    }

    @Test
    void buscarPorId_deveLancarExcecaoQuandoNaoEncontrado() {
        when(tipoUsuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tipoUsuarioService.buscarPorId(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("99");
    }

    @Test
    void atualizar_deveAtualizarNomeComSucesso() {
        TipoUsuarioRequest novoRequest = new TipoUsuarioRequest("Cliente Premium");
        TipoUsuario atualizado = new TipoUsuario(1L, "Cliente Premium");

        when(tipoUsuarioRepository.findById(1L)).thenReturn(Optional.of(tipoUsuario));
        when(tipoUsuarioRepository.save(any(TipoUsuario.class))).thenReturn(atualizado);

        TipoUsuarioResponse response = tipoUsuarioService.atualizar(1L, novoRequest);

        assertThat(response.getNome()).isEqualTo("Cliente Premium");
        assertThat(response.getId()).isEqualTo(1L);
    }

    @Test
    void atualizar_deveLancarExcecaoQuandoIdNaoExiste() {
        when(tipoUsuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tipoUsuarioService.atualizar(99L, request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("99");

        verify(tipoUsuarioRepository, never()).save(any());
    }

    @Test
    void deletar_deveDeletarComSucesso() {
        when(tipoUsuarioRepository.existsById(1L)).thenReturn(true);
        doNothing().when(tipoUsuarioRepository).deleteById(1L);

        tipoUsuarioService.deletar(1L);

        verify(tipoUsuarioRepository, times(1)).deleteById(1L);
    }

    @Test
    void deletar_deveLancarExcecaoQuandoIdNaoExiste() {
        when(tipoUsuarioRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> tipoUsuarioService.deletar(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("99");

        verify(tipoUsuarioRepository, never()).deleteById(any());
    }
}
