package com.postech.restaurantmanagement.application.service;

import com.postech.restaurantmanagement.application.dto.ItemCardapioRequest;
import com.postech.restaurantmanagement.application.dto.ItemCardapioResponse;
import com.postech.restaurantmanagement.domain.model.ItemCardapio;
import com.postech.restaurantmanagement.domain.repository.ItemCardapioRepository;
import com.postech.restaurantmanagement.domain.repository.RestauranteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemCardapioServiceTest {

    @Mock
    private ItemCardapioRepository itemCardapioRepository;

    @Mock
    private RestauranteRepository restauranteRepository;

    @InjectMocks
    private ItemCardapioService itemCardapioService;

    private ItemCardapio item;
    private ItemCardapioRequest request;

    @BeforeEach
    void setUp() {
        item = new ItemCardapio(1L, "Feijoada Completa", "Feijoada com arroz, couve e farofa",
                new BigDecimal("45.90"), false, "/fotos/feijoada.jpg", 1L);

        request = new ItemCardapioRequest();
        request.setNome("Feijoada Completa");
        request.setDescricao("Feijoada com arroz, couve e farofa");
        request.setPreco(new BigDecimal("45.90"));
        request.setApenasLocal(false);
        request.setCaminhoFoto("/fotos/feijoada.jpg");
    }

    // ========== TESTES DE CRIAR ==========

    @Test
    void criar_deveRetornarItemCriado() {
        when(restauranteRepository.existsById(1L)).thenReturn(true);
        when(itemCardapioRepository.save(any(ItemCardapio.class))).thenReturn(item);

        ItemCardapioResponse response = itemCardapioService.criar(1L, request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getNome()).isEqualTo("Feijoada Completa");
        assertThat(response.getDescricao()).isEqualTo("Feijoada com arroz, couve e farofa");
        assertThat(response.getPreco()).isEqualByComparingTo(new BigDecimal("45.90"));
        assertThat(response.isApenasLocal()).isFalse();
        assertThat(response.getCaminhoFoto()).isEqualTo("/fotos/feijoada.jpg");
        assertThat(response.getRestauranteId()).isEqualTo(1L);
        verify(itemCardapioRepository, times(1)).save(any(ItemCardapio.class));
    }

    @Test
    void criar_deveLancarExcecaoQuandoRestauranteNaoExiste() {
        when(restauranteRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> itemCardapioService.criar(99L, request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Restaurante não encontrado com id: 99");

        verify(itemCardapioRepository, never()).save(any());
    }

    @Test
    void criar_devePermitirItemApenasLocal() {
        request.setApenasLocal(true);
        ItemCardapio itemLocal = new ItemCardapio(2L, "Feijoada Completa", "Feijoada com arroz, couve e farofa",
                new BigDecimal("45.90"), true, "/fotos/feijoada.jpg", 1L);

        when(restauranteRepository.existsById(1L)).thenReturn(true);
        when(itemCardapioRepository.save(any(ItemCardapio.class))).thenReturn(itemLocal);

        ItemCardapioResponse response = itemCardapioService.criar(1L, request);

        assertThat(response.isApenasLocal()).isTrue();
    }

    @Test
    void criar_devePermitirCaminhoFotoNulo() {
        request.setCaminhoFoto(null);
        ItemCardapio itemSemFoto = new ItemCardapio(3L, "Feijoada Completa", "Feijoada com arroz, couve e farofa",
                new BigDecimal("45.90"), false, null, 1L);

        when(restauranteRepository.existsById(1L)).thenReturn(true);
        when(itemCardapioRepository.save(any(ItemCardapio.class))).thenReturn(itemSemFoto);

        ItemCardapioResponse response = itemCardapioService.criar(1L, request);

        assertThat(response.getCaminhoFoto()).isNull();
    }

    // ========== TESTES DE LISTAR POR RESTAURANTE ==========

    @Test
    void listarPorRestaurante_deveRetornarListaDeItens() {
        ItemCardapio item2 = new ItemCardapio(2L, "Picanha Grelhada", "Picanha com arroz e vinagrete",
                new BigDecimal("79.90"), true, "/fotos/picanha.jpg", 1L);

        when(restauranteRepository.existsById(1L)).thenReturn(true);
        when(itemCardapioRepository.findByRestauranteId(1L)).thenReturn(Arrays.asList(item, item2));

        List<ItemCardapioResponse> lista = itemCardapioService.listarPorRestaurante(1L);

        assertThat(lista).hasSize(2);
        assertThat(lista.get(0).getNome()).isEqualTo("Feijoada Completa");
        assertThat(lista.get(1).getNome()).isEqualTo("Picanha Grelhada");
    }

    @Test
    void listarPorRestaurante_deveRetornarListaVazia() {
        when(restauranteRepository.existsById(1L)).thenReturn(true);
        when(itemCardapioRepository.findByRestauranteId(1L)).thenReturn(List.of());

        List<ItemCardapioResponse> lista = itemCardapioService.listarPorRestaurante(1L);

        assertThat(lista).isEmpty();
    }

    @Test
    void listarPorRestaurante_deveLancarExcecaoQuandoRestauranteNaoExiste() {
        when(restauranteRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> itemCardapioService.listarPorRestaurante(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Restaurante não encontrado com id: 99");

        verify(itemCardapioRepository, never()).findByRestauranteId(any());
    }

    // ========== TESTES DE BUSCAR POR ID ==========

    @Test
    void buscarPorId_deveRetornarItemQuandoEncontrado() {
        when(itemCardapioRepository.findById(1L)).thenReturn(Optional.of(item));

        ItemCardapioResponse response = itemCardapioService.buscarPorId(1L);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getNome()).isEqualTo("Feijoada Completa");
        assertThat(response.getRestauranteId()).isEqualTo(1L);
    }

    @Test
    void buscarPorId_deveLancarExcecaoQuandoNaoEncontrado() {
        when(itemCardapioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemCardapioService.buscarPorId(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Item do cardápio não encontrado com id: 99");
    }

    // ========== TESTES DE ATUALIZAR ==========

    @Test
    void atualizar_deveAtualizarDadosComSucesso() {
        ItemCardapioRequest novoRequest = new ItemCardapioRequest();
        novoRequest.setNome("Feijoada Premium");
        novoRequest.setDescricao("Feijoada com cortes nobres");
        novoRequest.setPreco(new BigDecimal("69.90"));
        novoRequest.setApenasLocal(true);
        novoRequest.setCaminhoFoto("/fotos/feijoada-premium.jpg");

        ItemCardapio atualizado = new ItemCardapio(1L, "Feijoada Premium", "Feijoada com cortes nobres",
                new BigDecimal("69.90"), true, "/fotos/feijoada-premium.jpg", 1L);

        when(itemCardapioRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemCardapioRepository.save(any(ItemCardapio.class))).thenReturn(atualizado);

        ItemCardapioResponse response = itemCardapioService.atualizar(1L, novoRequest);

        assertThat(response.getNome()).isEqualTo("Feijoada Premium");
        assertThat(response.getDescricao()).isEqualTo("Feijoada com cortes nobres");
        assertThat(response.getPreco()).isEqualByComparingTo(new BigDecimal("69.90"));
        assertThat(response.isApenasLocal()).isTrue();
        assertThat(response.getCaminhoFoto()).isEqualTo("/fotos/feijoada-premium.jpg");
    }

    @Test
    void atualizar_deveLancarExcecaoQuandoItemNaoExiste() {
        when(itemCardapioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemCardapioService.atualizar(99L, request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Item do cardápio não encontrado com id: 99");

        verify(itemCardapioRepository, never()).save(any());
    }

    // ========== TESTES DE DELETAR ==========

    @Test
    void deletar_deveDeletarComSucesso() {
        when(itemCardapioRepository.existsById(1L)).thenReturn(true);
        doNothing().when(itemCardapioRepository).deleteById(1L);

        itemCardapioService.deletar(1L);

        verify(itemCardapioRepository, times(1)).deleteById(1L);
    }

    @Test
    void deletar_deveLancarExcecaoQuandoItemNaoExiste() {
        when(itemCardapioRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> itemCardapioService.deletar(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Item do cardápio não encontrado com id: 99");

        verify(itemCardapioRepository, never()).deleteById(any());
    }
}
