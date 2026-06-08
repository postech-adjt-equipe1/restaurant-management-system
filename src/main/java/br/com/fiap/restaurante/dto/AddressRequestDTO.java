package br.com.fiap.restaurante.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Endereço do usuário")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressRequestDTO {

    @Schema(description = "Logradouro (rua, avenida, etc.)", example = "Rua das Flores")
    @NotBlank
    private String logradouro;

    @Schema(description = "Número do imóvel", example = "123")
    @NotBlank
    private String numero;

    @Schema(description = "Complemento (opcional)", example = "Apto 45")
    private String complemento;

    @Schema(description = "Bairro", example = "Centro")
    @NotBlank
    private String bairro;

    @Schema(description = "Cidade", example = "São Paulo")
    @NotBlank
    private String cidade;

    @Schema(description = "Estado (UF)", example = "SP")
    @NotBlank
    private String estado;

    @Schema(description = "CEP no formato 00000-000", example = "01310-100")
    @NotBlank
    @Size(max = 9)
    @Pattern(regexp = "\\d{5}-?\\d{3}", message = "CEP deve estar no formato 00000-000")
    private String cep;
}
