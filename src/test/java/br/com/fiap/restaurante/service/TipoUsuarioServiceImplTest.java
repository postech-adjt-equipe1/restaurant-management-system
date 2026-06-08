package br.com.fiap.restaurante.service;

import br.com.fiap.restaurante.exception.DuplicateTipoUsuarioException;
import br.com.fiap.restaurante.exception.TipoUsuarioNotFoundException;
import br.com.fiap.restaurante.model.TipoUsuario;
import br.com.fiap.restaurante.repository.TipoUsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TipoUsuarioServiceImplTest {

    @Mock
    private TipoUsuarioRepository tipoUsuarioRepository;

    @InjectMocks
    private TipoUsuarioServiceImpl tipoUsuarioService;

    private TipoUsuario tipoUsuario;

    @BeforeEach
    void setUp() {
        tipoUsuario = TipoUsuario.builder()
                .id(1L)
                .nome("Dono de Restaurante")
                .build();
    }

    // --- create ---

    @Test
    void create_deveRetornarTipoCriado_quandoNomeUnico() {
        when(tipoUsuarioRepository.existsByNomeIgnoreCase(tipoUsuario.getNome())).thenReturn(false);
        when(tipoUsuarioRepository.save(any(TipoUsuario.class))).thenReturn(tipoUsuario);

        TipoUsuario result = tipoUsuarioService.create(tipoUsuario);

        assertThat(result).isEqualTo(tipoUsuario);
        verify(tipoUsuarioRepository).save(tipoUsuario);
    }

    @Test
    void create_deveLancarDuplicateTipoUsuarioException_quandoNomeJaExiste() {
        when(tipoUsuarioRepository.existsByNomeIgnoreCase(tipoUsuario.getNome())).thenReturn(true);

        assertThatThrownBy(() -> tipoUsuarioService.create(tipoUsuario))
                .isInstanceOf(DuplicateTipoUsuarioException.class)
                .hasMessageContaining("Dono de Restaurante");

        verify(tipoUsuarioRepository, never()).save(any());
    }

    // --- findAll ---

    @Test
    void findAll_deveRetornarListaDeTipos() {
        TipoUsuario outro = TipoUsuario.builder().id(2L).nome("Cliente").build();
        when(tipoUsuarioRepository.findAll()).thenReturn(List.of(tipoUsuario, outro));

        List<TipoUsuario> result = tipoUsuarioService.findAll();

        assertThat(result).hasSize(2).contains(tipoUsuario, outro);
    }

    @Test
    void findAll_deveRetornarListaVazia_quandoNenhumCadastrado() {
        when(tipoUsuarioRepository.findAll()).thenReturn(List.of());

        List<TipoUsuario> result = tipoUsuarioService.findAll();

        assertThat(result).isEmpty();
    }

    // --- findById ---

    @Test
    void findById_deveRetornarTipo_quandoEncontrado() {
        when(tipoUsuarioRepository.findById(1L)).thenReturn(Optional.of(tipoUsuario));

        TipoUsuario result = tipoUsuarioService.findById(1L);

        assertThat(result).isEqualTo(tipoUsuario);
    }

    @Test
    void findById_deveLancarTipoUsuarioNotFoundException_quandoNaoEncontrado() {
        when(tipoUsuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tipoUsuarioService.findById(99L))
                .isInstanceOf(TipoUsuarioNotFoundException.class)
                .hasMessageContaining("99");
    }

    // --- update ---

    @Test
    void update_deveRetornarTipoAtualizado_quandoDadosValidos() {
        TipoUsuario dadosAtualizados = TipoUsuario.builder().nome("Cliente VIP").build();

        when(tipoUsuarioRepository.findById(1L)).thenReturn(Optional.of(tipoUsuario));
        when(tipoUsuarioRepository.existsByNomeIgnoreCase("Cliente VIP")).thenReturn(false);
        when(tipoUsuarioRepository.save(any(TipoUsuario.class))).thenReturn(tipoUsuario);

        TipoUsuario result = tipoUsuarioService.update(1L, dadosAtualizados);

        assertThat(result).isNotNull();
        assertThat(tipoUsuario.getNome()).isEqualTo("Cliente VIP");
        verify(tipoUsuarioRepository).save(tipoUsuario);
    }

    @Test
    void update_deveLancarTipoUsuarioNotFoundException_quandoNaoEncontrado() {
        when(tipoUsuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tipoUsuarioService.update(99L, tipoUsuario))
                .isInstanceOf(TipoUsuarioNotFoundException.class);
    }

    @Test
    void update_deveLancarDuplicateTipoUsuarioException_quandoNovoNomeJaExiste() {
        TipoUsuario dadosAtualizados = TipoUsuario.builder().nome("Cliente").build();

        when(tipoUsuarioRepository.findById(1L)).thenReturn(Optional.of(tipoUsuario));
        when(tipoUsuarioRepository.existsByNomeIgnoreCase("Cliente")).thenReturn(true);

        assertThatThrownBy(() -> tipoUsuarioService.update(1L, dadosAtualizados))
                .isInstanceOf(DuplicateTipoUsuarioException.class);

        verify(tipoUsuarioRepository, never()).save(any());
    }

    @Test
    void update_naoDeveLancarExcecao_quandoNomeNaoMudou() {
        TipoUsuario dadosAtualizados = TipoUsuario.builder().nome("Dono de Restaurante").build();

        when(tipoUsuarioRepository.findById(1L)).thenReturn(Optional.of(tipoUsuario));
        when(tipoUsuarioRepository.save(any(TipoUsuario.class))).thenReturn(tipoUsuario);

        TipoUsuario result = tipoUsuarioService.update(1L, dadosAtualizados);

        assertThat(result).isNotNull();
        verify(tipoUsuarioRepository, never()).existsByNomeIgnoreCase(any());
    }

    // --- delete ---

    @Test
    void delete_deveDeletarTipo_quandoEncontrado() {
        when(tipoUsuarioRepository.findById(1L)).thenReturn(Optional.of(tipoUsuario));

        tipoUsuarioService.delete(1L);

        verify(tipoUsuarioRepository).delete(tipoUsuario);
    }

    @Test
    void delete_deveLancarTipoUsuarioNotFoundException_quandoNaoEncontrado() {
        when(tipoUsuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tipoUsuarioService.delete(99L))
                .isInstanceOf(TipoUsuarioNotFoundException.class)
                .hasMessageContaining("99");
    }
}
