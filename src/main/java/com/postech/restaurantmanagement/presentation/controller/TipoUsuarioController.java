package com.postech.restaurantmanagement.presentation.controller;

import com.postech.restaurantmanagement.application.dto.TipoUsuarioRequest;
import com.postech.restaurantmanagement.application.dto.TipoUsuarioResponse;
import com.postech.restaurantmanagement.application.service.TipoUsuarioService;
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
@RequestMapping("/tipo-usuario")
@Tag(name = "Tipo de Usuário", description = "Gestão dos tipos de usuário (ex.: Dono de Restaurante, Cliente)")
public class TipoUsuarioController {

    private final TipoUsuarioService tipoUsuarioService;

    public TipoUsuarioController(TipoUsuarioService tipoUsuarioService) {
        this.tipoUsuarioService = tipoUsuarioService;
    }

    @Operation(summary = "Cria um novo tipo de usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Tipo de usuário criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PostMapping
    public ResponseEntity<TipoUsuarioResponse> criar(@Valid @RequestBody TipoUsuarioRequest request) {
        TipoUsuarioResponse response = tipoUsuarioService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Lista todos os tipos de usuário")
    @GetMapping
    public ResponseEntity<List<TipoUsuarioResponse>> listarTodos() {
        return ResponseEntity.ok(tipoUsuarioService.listarTodos());
    }

    @Operation(summary = "Busca um tipo de usuário pelo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tipo de usuário encontrado"),
            @ApiResponse(responseCode = "404", description = "Tipo de usuário não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<TipoUsuarioResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(tipoUsuarioService.buscarPorId(id));
    }

    @Operation(summary = "Atualiza um tipo de usuário existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tipo de usuário atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Tipo de usuário não encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<TipoUsuarioResponse> atualizar(@PathVariable Long id,
                                                          @Valid @RequestBody TipoUsuarioRequest request) {
        return ResponseEntity.ok(tipoUsuarioService.atualizar(id, request));
    }

    @Operation(summary = "Remove um tipo de usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Tipo de usuário removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Tipo de usuário não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        tipoUsuarioService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
