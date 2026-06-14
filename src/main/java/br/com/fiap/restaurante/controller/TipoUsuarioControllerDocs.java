package br.com.fiap.restaurante.controller;

import br.com.fiap.restaurante.dto.TipoUsuarioRequestDTO;
import br.com.fiap.restaurante.dto.TipoUsuarioResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "Tipos de Usuário", description = "Gerenciamento dos tipos de usuário do sistema")
@SecurityRequirement(name = "bearerAuth")
public interface TipoUsuarioControllerDocs {

    @Operation(summary = "Criar tipo de usuário", description = "Cria um novo tipo de usuário no sistema")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Tipo criado com sucesso",
                    content = @Content(schema = @Schema(implementation = TipoUsuarioResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
            @ApiResponse(responseCode = "409", description = "Nome já cadastrado", content = @Content)
    })
    ResponseEntity<TipoUsuarioResponseDTO> create(@Valid @RequestBody TipoUsuarioRequestDTO request);

    @Operation(summary = "Listar tipos de usuário", description = "Retorna todos os tipos de usuário cadastrados")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    })
    ResponseEntity<List<TipoUsuarioResponseDTO>> findAll();

    @Operation(summary = "Buscar tipo de usuário por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tipo encontrado",
                    content = @Content(schema = @Schema(implementation = TipoUsuarioResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Tipo não encontrado", content = @Content)
    })
    ResponseEntity<TipoUsuarioResponseDTO> findById(
            @Parameter(description = "ID do tipo de usuário", required = true) @PathVariable Long id);

    @Operation(summary = "Atualizar tipo de usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tipo atualizado com sucesso",
                    content = @Content(schema = @Schema(implementation = TipoUsuarioResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
            @ApiResponse(responseCode = "404", description = "Tipo não encontrado", content = @Content),
            @ApiResponse(responseCode = "409", description = "Nome já cadastrado", content = @Content)
    })
    ResponseEntity<TipoUsuarioResponseDTO> update(
            @Parameter(description = "ID do tipo de usuário", required = true) @PathVariable Long id,
            @Valid @RequestBody TipoUsuarioRequestDTO request);

    @Operation(summary = "Excluir tipo de usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Tipo excluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Tipo não encontrado", content = @Content)
    })
    ResponseEntity<Void> delete(
            @Parameter(description = "ID do tipo de usuário", required = true) @PathVariable Long id);
}
