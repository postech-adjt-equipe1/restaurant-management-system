package com.postech.restaurantmanagement.application.service;

import com.postech.restaurantmanagement.application.dto.ItemCardapioRequest;
import com.postech.restaurantmanagement.application.dto.ItemCardapioResponse;
import com.postech.restaurantmanagement.domain.model.ItemCardapio;
import com.postech.restaurantmanagement.domain.repository.ItemCardapioRepository;
import com.postech.restaurantmanagement.domain.repository.RestauranteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemCardapioService {

    private final ItemCardapioRepository itemCardapioRepository;
    private final RestauranteRepository restauranteRepository;

    public ItemCardapioService(ItemCardapioRepository itemCardapioRepository,
                                RestauranteRepository restauranteRepository) {
        this.itemCardapioRepository = itemCardapioRepository;
        this.restauranteRepository = restauranteRepository;
    }

    public ItemCardapioResponse criar(Long restauranteId, ItemCardapioRequest request) {
        validarRestauranteExiste(restauranteId);
        ItemCardapio item = new ItemCardapio(null, request.getNome(), request.getDescricao(),
                request.getPreco(), request.getApenasLocal(), request.getCaminhoFoto(), restauranteId);
        ItemCardapio salvo = itemCardapioRepository.save(item);
        return ItemCardapioResponse.from(salvo);
    }

    public List<ItemCardapioResponse> listarPorRestaurante(Long restauranteId) {
        validarRestauranteExiste(restauranteId);
        return itemCardapioRepository.findByRestauranteId(restauranteId)
                .stream()
                .map(ItemCardapioResponse::from)
                .collect(Collectors.toList());
    }

    public ItemCardapioResponse buscarPorId(Long id) {
        ItemCardapio item = itemCardapioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item do cardápio não encontrado com id: " + id));
        return ItemCardapioResponse.from(item);
    }

    public ItemCardapioResponse atualizar(Long id, ItemCardapioRequest request) {
        ItemCardapio existente = itemCardapioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item do cardápio não encontrado com id: " + id));
        existente.setNome(request.getNome());
        existente.setDescricao(request.getDescricao());
        existente.setPreco(request.getPreco());
        existente.setApenasLocal(request.getApenasLocal());
        existente.setCaminhoFoto(request.getCaminhoFoto());
        ItemCardapio atualizado = itemCardapioRepository.save(existente);
        return ItemCardapioResponse.from(atualizado);
    }

    public void deletar(Long id) {
        if (!itemCardapioRepository.existsById(id)) {
            throw new RuntimeException("Item do cardápio não encontrado com id: " + id);
        }
        itemCardapioRepository.deleteById(id);
    }

    private void validarRestauranteExiste(Long restauranteId) {
        if (!restauranteRepository.existsById(restauranteId)) {
            throw new RuntimeException("Restaurante não encontrado com id: " + restauranteId);
        }
    }
}
