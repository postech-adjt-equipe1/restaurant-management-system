package com.postech.restaurantmanagement.presentation.controller;

import com.postech.restaurantmanagement.application.dto.RestauranteRequest;
import com.postech.restaurantmanagement.application.dto.RestauranteResponse;
import com.postech.restaurantmanagement.application.service.RestauranteService;
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
@RequestMapping("/restaurante")
@Tag(name = "Restaurante", description = "Gestão do cadastro de restaurantes")
public class RestauranteController {

    private final RestauranteService restauranteService;

    public RestauranteController(RestauranteService restauranteService) {
        this.restauranteService = restauranteService;
    }

    @Operation(summary = "Cria um novo restaurante",
            description = "O dono informado deve ser um usuário existente do tipo 'Dono de Restaurante'")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Restaurante criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Dono não encontrado ou tipo inválido")
    })
    @PostMapping
    public ResponseEntity<RestauranteResponse> criar(@Valid @RequestBody RestauranteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(restauranteService.criar(request));
    }

    @Operation(summary = "Lista todos os restaurantes")
    @GetMapping
    public ResponseEntity<List<RestauranteResponse>> listarTodos() {
        return ResponseEntity.ok(restauranteService.listarTodos());
    }

    @Operation(summary = "Busca um restaurante pelo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Restaurante encontrado"),
            @ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<RestauranteResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(restauranteService.buscarPorId(id));
    }

    @Operation(summary = "Atualiza um restaurante existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Restaurante atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<RestauranteResponse> atualizar(@PathVariable Long id,
                                                          @Valid @RequestBody RestauranteRequest request) {
        return ResponseEntity.ok(restauranteService.atualizar(id, request));
    }

    @Operation(summary = "Remove um restaurante",
            description = "A remoção é bloqueada enquanto o restaurante possuir itens de cardápio")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Restaurante removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Restaurante não encontrado"),
            @ApiResponse(responseCode = "409", description = "Restaurante possui itens de cardápio")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        restauranteService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
