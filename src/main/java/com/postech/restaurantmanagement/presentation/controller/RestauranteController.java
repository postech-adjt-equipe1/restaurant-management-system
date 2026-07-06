package com.postech.restaurantmanagement.presentation.controller;

import com.postech.restaurantmanagement.application.dto.RestauranteRequest;
import com.postech.restaurantmanagement.application.dto.RestauranteResponse;
import com.postech.restaurantmanagement.application.service.RestauranteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/restaurante")
public class RestauranteController {

    private final RestauranteService restauranteService;

    public RestauranteController(RestauranteService restauranteService) {
        this.restauranteService = restauranteService;
    }

    @PostMapping
    public ResponseEntity<RestauranteResponse> criar(@Valid @RequestBody RestauranteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(restauranteService.criar(request));
    }

    @GetMapping
    public ResponseEntity<List<RestauranteResponse>> listarTodos() {
        return ResponseEntity.ok(restauranteService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestauranteResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(restauranteService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RestauranteResponse> atualizar(@PathVariable Long id,
                                                          @Valid @RequestBody RestauranteRequest request) {
        return ResponseEntity.ok(restauranteService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        restauranteService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
