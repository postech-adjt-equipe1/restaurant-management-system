package br.com.fiap.restaurante.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "Dados para alteração de senha")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserChangePasswordRequestDTO {

    @Schema(description = "Senha atual do usuário", example = "Senha@123")
    @NotBlank
    private String senhaAtual;

    @Schema(description = "Nova senha (mínimo 8 caracteres)", example = "NovaSenha@456")
    @NotBlank
    @Size(min = 8, message = "Nova senha deve ter no mínimo 8 caracteres")
    private String novaSenha;
}
