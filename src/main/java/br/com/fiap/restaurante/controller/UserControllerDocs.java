package br.com.fiap.restaurante.controller;

import br.com.fiap.restaurante.dto.LoginResponse;
import br.com.fiap.restaurante.dto.UserChangePasswordRequestDTO;
import br.com.fiap.restaurante.dto.UserCreateRequestDTO;
import br.com.fiap.restaurante.dto.UserLoginRequestDTO;
import br.com.fiap.restaurante.dto.UserSearchResponseDTO;
import br.com.fiap.restaurante.dto.UserUpdateRequestDTO;
import br.com.fiap.restaurante.dto.UserResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Usuários", description = "Gerenciamento de usuários do sistema de restaurantes")
public interface UserControllerDocs {

    @Operation(summary = "Buscar usuários por nome", description = "Retorna lista de usuários cujo nome contenha o trecho informado (case-insensitive)")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserSearchResponseDTO.class)))),
            @ApiResponse(responseCode = "400", description = "Parâmetro 'nome' ausente", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado — token JWT ausente ou inválido", content = @Content)
    })
    ResponseEntity<List<UserSearchResponseDTO>> findByName(
            @Parameter(description = "Trecho do nome a pesquisar", required = true, example = "João")
            @RequestParam String nome);

    @Operation(summary = "Criar usuário", description = "Cadastra um novo usuário no sistema")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso",
                    content = @Content(schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
            @ApiResponse(responseCode = "409", description = "Login ou e-mail já cadastrado", content = @Content)
    })
    ResponseEntity<UserResponseDTO> create(@Valid @RequestBody UserCreateRequestDTO request);

    @Operation(summary = "Buscar usuário por ID", description = "Retorna os dados de um usuário pelo seu identificador")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário encontrado",
                    content = @Content(schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Acesso negado — token JWT ausente ou inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content)
    })
    ResponseEntity<UserResponseDTO> findById(
            @Parameter(description = "ID do usuário", required = true) @PathVariable Long id);

    @Operation(summary = "Atualizar usuário", description = "Atualiza nome, e-mail, tipo e endereço do usuário")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso",
                    content = @Content(schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado — token JWT ausente ou inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content),
            @ApiResponse(responseCode = "409", description = "E-mail já cadastrado", content = @Content)
    })
    ResponseEntity<UserResponseDTO> update(
            @Parameter(description = "ID do usuário", required = true) @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequestDTO request);

    @Operation(summary = "Excluir usuário", description = "Remove permanentemente um usuário do sistema")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usuário excluído com sucesso", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado — token JWT ausente ou inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content)
    })
    ResponseEntity<Void> delete(
            @Parameter(description = "ID do usuário", required = true) @PathVariable Long id);

    @Operation(summary = "Login", description = "Valida as credenciais do usuário e retorna um token JWT Bearer")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
            @ApiResponse(responseCode = "401", description = "Credenciais incorretas", content = @Content)
    })
    ResponseEntity<LoginResponse> login(@Valid @RequestBody UserLoginRequestDTO request);

    @Operation(summary = "Alterar senha", description = "Altera a senha do usuário após confirmar a senha atual")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Senha alterada com sucesso", content = @Content),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou senha atual incorreta", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado — token JWT ausente ou inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content)
    })
    ResponseEntity<Void> changePassword(
            @Parameter(description = "ID do usuário", required = true) @PathVariable Long id,
            @Valid @RequestBody UserChangePasswordRequestDTO request);
}
