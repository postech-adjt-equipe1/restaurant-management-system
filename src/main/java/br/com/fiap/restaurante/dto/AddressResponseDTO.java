package br.com.fiap.restaurante.dto;

import br.com.fiap.restaurante.model.Address;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Endereço do usuário")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressResponseDTO {

    @Schema(description = "Logradouro", example = "Rua das Flores")
    private String logradouro;

    @Schema(description = "Número", example = "123")
    private String numero;

    @Schema(description = "Complemento", example = "Apto 45")
    private String complemento;

    @Schema(description = "Bairro", example = "Centro")
    private String bairro;

    @Schema(description = "Cidade", example = "São Paulo")
    private String cidade;

    @Schema(description = "Estado (UF)", example = "SP")
    private String estado;

    @Schema(description = "CEP", example = "01310-100")
    private String cep;

    public static AddressResponseDTO from(Address address) {
        return new AddressResponseDTO(
                address.getLogradouro(),
                address.getNumero(),
                address.getComplemento(),
                address.getBairro(),
                address.getCidade(),
                address.getEstado(),
                address.getCep()
        );
    }
}
