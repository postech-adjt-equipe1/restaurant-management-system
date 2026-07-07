package com.postech.restaurantmanagement.presentation.controller;

import com.postech.restaurantmanagement.application.dto.ItemCardapioRequest;
import com.postech.restaurantmanagement.application.dto.ItemCardapioResponse;
import com.postech.restaurantmanagement.application.service.ItemCardapioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Cardápio", description = "Gestão dos itens de cardápio de um restaurante")
public class ItemCardapioController {

    private final ItemCardapioService itemCardapioService;

    public ItemCardapioController(ItemCardapioService itemCardapioService) {
        this.itemCardapioService = itemCardapioService;
    }

    @Operation(summary = "Cria um item de cardápio para um restaurante existente")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Item criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
    })
    @PostMapping("/restaurante/{restauranteId}/cardapio")
    public ResponseEntity<ItemCardapioResponse> criar(@PathVariable Long restauranteId,
                                                       @Valid @RequestBody ItemCardapioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(itemCardapioService.criar(restauranteId, request));
    }

    @Operation(summary = "Lista os itens de cardápio de um restaurante")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
    })
    @GetMapping("/restaurante/{restauranteId}/cardapio")
    public ResponseEntity<List<ItemCardapioResponse>> listarPorRestaurante(@PathVariable Long restauranteId) {
        return ResponseEntity.ok(itemCardapioService.listarPorRestaurante(restauranteId));
    }

    @Operation(summary = "Busca um item de cardápio pelo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item encontrado"),
            @ApiResponse(responseCode = "404", description = "Item não encontrado")
    })
    @GetMapping("/cardapio/{id}")
    public ResponseEntity<ItemCardapioResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(itemCardapioService.buscarPorId(id));
    }

    @Operation(summary = "Atualiza um item de cardápio existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Item não encontrado")
    })
    @PutMapping("/cardapio/{id}")
    public ResponseEntity<ItemCardapioResponse> atualizar(@PathVariable Long id,
                                                           @Valid @RequestBody ItemCardapioRequest request) {
        return ResponseEntity.ok(itemCardapioService.atualizar(id, request));
    }

    @Operation(summary = "Remove um item de cardápio")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Item removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Item não encontrado")
    })
    @DeleteMapping("/cardapio/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        itemCardapioService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
