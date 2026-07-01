package com.postech.restaurantmanagement.presentation.controller;

import com.postech.restaurantmanagement.application.dto.TipoUsuarioRequest;
import com.postech.restaurantmanagement.application.dto.TipoUsuarioResponse;
import com.postech.restaurantmanagement.application.service.TipoUsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tipo-usuario")
public class TipoUsuarioController {

    private final TipoUsuarioService tipoUsuarioService;

    public TipoUsuarioController(TipoUsuarioService tipoUsuarioService) {
        this.tipoUsuarioService = tipoUsuarioService;
    }

    @PostMapping
    public ResponseEntity<TipoUsuarioResponse> criar(@Valid @RequestBody TipoUsuarioRequest request) {
        TipoUsuarioResponse response = tipoUsuarioService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<TipoUsuarioResponse>> listarTodos() {
        return ResponseEntity.ok(tipoUsuarioService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoUsuarioResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(tipoUsuarioService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TipoUsuarioResponse> atualizar(@PathVariable Long id,
                                                          @Valid @RequestBody TipoUsuarioRequest request) {
        return ResponseEntity.ok(tipoUsuarioService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        tipoUsuarioService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
