package com.postech.restaurantmanagement.application.service;

import com.postech.restaurantmanagement.application.dto.RestauranteRequest;
import com.postech.restaurantmanagement.application.dto.RestauranteResponse;
import com.postech.restaurantmanagement.domain.model.Restaurante;
import com.postech.restaurantmanagement.domain.model.TipoUsuario;
import com.postech.restaurantmanagement.domain.model.Usuario;
import com.postech.restaurantmanagement.domain.repository.RestauranteRepository;
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
class RestauranteServiceTest {

    @Mock
    private RestauranteRepository restauranteRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private TipoUsuarioRepository tipoUsuarioRepository;

    @InjectMocks
    private RestauranteService restauranteService;

    private Restaurante restaurante;
    private RestauranteRequest request;
    private Usuario dono;
    private TipoUsuario tipoDonoRestaurante;

    @BeforeEach
    void setUp() {
        restaurante = new Restaurante(1L, "Sabor Caseiro", "Rua das Flores, 123", "Brasileira", "08:00-22:00", 1L);
        request = new RestauranteRequest();
        request.setNome("Sabor Caseiro");
        request.setEndereco("Rua das Flores, 123");
        request.setTipoCozinha("Brasileira");
        request.setHorarioFuncionamento("08:00-22:00");
        request.setDonoId(1L);

        dono = new Usuario(1L, "João Dono", "joao@email.com", "senha123", 10L);
        tipoDonoRestaurante = new TipoUsuario(10L, "Dono de Restaurante");
    }

    @Test
    void criar_deveRetornarRestauranteCriado() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(dono));
        when(tipoUsuarioRepository.findById(10L)).thenReturn(Optional.of(tipoDonoRestaurante));
        when(restauranteRepository.save(any(Restaurante.class))).thenReturn(restaurante);

        RestauranteResponse response = restauranteService.criar(request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getNome()).isEqualTo("Sabor Caseiro");
        assertThat(response.getDonoId()).isEqualTo(1L);
    }

    @Test
    void criar_deveLancarExcecaoQuandoDonoNaoExiste() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());
        request.setDonoId(99L);

        assertThatThrownBy(() -> restauranteService.criar(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("99");

        verify(restauranteRepository, never()).save(any());
    }

    @Test
    void criar_deveLancarExcecaoQuandoTipoDoDonoNaoExiste() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(dono));
        when(tipoUsuarioRepository.findById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> restauranteService.criar(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("10");

        verify(restauranteRepository, never()).save(any());
    }

    @Test
    void criar_deveLancarExcecaoQuandoUsuarioNaoEhDonoDeRestaurante() {
        TipoUsuario tipoCliente = new TipoUsuario(20L, "Cliente");
        Usuario cliente = new Usuario(2L, "Maria Cliente", "maria@email.com", "senha456", 20L);
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(cliente));
        when(tipoUsuarioRepository.findById(20L)).thenReturn(Optional.of(tipoCliente));
        request.setDonoId(2L);

        assertThatThrownBy(() -> restauranteService.criar(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Dono de Restaurante");

        verify(restauranteRepository, never()).save(any());
    }

    @Test
    void listarTodos_deveRetornarListaCompleta() {
        Restaurante restaurante2 = new Restaurante(2L, "Cantina Bella", "Av. Central, 456", "Italiana", "11:00-23:00", 1L);
        when(restauranteRepository.findAll()).thenReturn(Arrays.asList(restaurante, restaurante2));

        List<RestauranteResponse> lista = restauranteService.listarTodos();

        assertThat(lista).hasSize(2);
        assertThat(lista.get(0).getNome()).isEqualTo("Sabor Caseiro");
        assertThat(lista.get(1).getNome()).isEqualTo("Cantina Bella");
    }

    @Test
    void listarTodos_deveRetornarListaVaziaQuandoNaoHaDados() {
        when(restauranteRepository.findAll()).thenReturn(List.of());

        List<RestauranteResponse> lista = restauranteService.listarTodos();

        assertThat(lista).isEmpty();
    }

    @Test
    void buscarPorId_deveRetornarRestauranteQuandoEncontrado() {
        when(restauranteRepository.findById(1L)).thenReturn(Optional.of(restaurante));

        RestauranteResponse response = restauranteService.buscarPorId(1L);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getEndereco()).isEqualTo("Rua das Flores, 123");
    }

    @Test
    void buscarPorId_deveLancarExcecaoQuandoNaoEncontrado() {
        when(restauranteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> restauranteService.buscarPorId(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("99");
    }

    @Test
    void atualizar_deveAtualizarDadosComSucesso() {
        RestauranteRequest novoRequest = new RestauranteRequest();
        novoRequest.setNome("Sabor Caseiro Renovado");
        novoRequest.setEndereco("Rua Nova, 789");
        novoRequest.setTipoCozinha("Fusion");
        novoRequest.setHorarioFuncionamento("10:00-00:00");
        novoRequest.setDonoId(1L);

        Restaurante atualizado = new Restaurante(1L, "Sabor Caseiro Renovado", "Rua Nova, 789", "Fusion", "10:00-00:00", 1L);

        when(restauranteRepository.findById(1L)).thenReturn(Optional.of(restaurante));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(dono));
        when(tipoUsuarioRepository.findById(10L)).thenReturn(Optional.of(tipoDonoRestaurante));
        when(restauranteRepository.save(any(Restaurante.class))).thenReturn(atualizado);

        RestauranteResponse response = restauranteService.atualizar(1L, novoRequest);

        assertThat(response.getNome()).isEqualTo("Sabor Caseiro Renovado");
        assertThat(response.getTipoCozinha()).isEqualTo("Fusion");
    }

    @Test
    void atualizar_deveLancarExcecaoQuandoRestauranteNaoExiste() {
        when(restauranteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> restauranteService.atualizar(99L, request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("99");

        verify(restauranteRepository, never()).save(any());
    }

    @Test
    void atualizar_deveLancarExcecaoQuandoDonoNaoExiste() {
        when(restauranteRepository.findById(1L)).thenReturn(Optional.of(restaurante));
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());
        request.setDonoId(99L);

        assertThatThrownBy(() -> restauranteService.atualizar(1L, request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("99");

        verify(restauranteRepository, never()).save(any());
    }

    @Test
    void deletar_deveDeletarComSucesso() {
        when(restauranteRepository.existsById(1L)).thenReturn(true);
        doNothing().when(restauranteRepository).deleteById(1L);

        restauranteService.deletar(1L);

        verify(restauranteRepository, times(1)).deleteById(1L);
    }

    @Test
    void deletar_deveLancarExcecaoQuandoRestauranteNaoExiste() {
        when(restauranteRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> restauranteService.deletar(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("99");

        verify(restauranteRepository, never()).deleteById(any());
    }
}
