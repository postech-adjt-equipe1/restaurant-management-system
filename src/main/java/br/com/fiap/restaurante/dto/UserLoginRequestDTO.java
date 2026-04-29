package br.com.fiap.restaurante.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Credenciais de autenticação")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLoginRequestDTO {

    @Schema(description = "Login do usuário", example = "joao.silva")
    @NotBlank
    private String login;

    @Schema(description = "Senha do usuário", example = "Senha@123")
    @NotBlank
    private String senha;
}
