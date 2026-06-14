package br.com.fiap.restaurante.dto;

import br.com.fiap.restaurante.model.TipoUsuario;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Dados do tipo de usuário retornados pela API")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoUsuarioResponseDTO {

    @Schema(description = "Identificador único do tipo de usuário", example = "1")
    private Long id;

    @Schema(description = "Nome do tipo de usuário", example = "Dono de Restaurante")
    private String nome;

    public static TipoUsuarioResponseDTO from(TipoUsuario tipoUsuario) {
        return TipoUsuarioResponseDTO.builder()
                .id(tipoUsuario.getId())
                .nome(tipoUsuario.getNome())
                .build();
    }
}
