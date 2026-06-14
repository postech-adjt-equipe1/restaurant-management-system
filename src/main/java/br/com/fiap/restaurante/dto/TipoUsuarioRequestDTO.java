package br.com.fiap.restaurante.dto;

import br.com.fiap.restaurante.model.TipoUsuario;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Dados para criação ou atualização de um tipo de usuário")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoUsuarioRequestDTO {

    @Schema(description = "Nome do tipo de usuário", example = "Dono de Restaurante")
    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    private String nome;

    public TipoUsuario toTipoUsuario() {
        return TipoUsuario.builder()
                .nome(nome)
                .build();
    }
}
