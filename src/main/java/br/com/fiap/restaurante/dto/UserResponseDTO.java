package br.com.fiap.restaurante.dto;

import br.com.fiap.restaurante.model.User;
import br.com.fiap.restaurante.model.UserType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Schema(description = "Dados do usuário retornados pela API")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDTO {

    @Schema(description = "Identificador único do usuário", example = "1")
    private Long id;

    @Schema(description = "Nome completo", example = "João Silva")
    private String nome;

    @Schema(description = "E-mail", example = "joao@email.com")
    private String email;

    @Schema(description = "Login de acesso", example = "joao.silva")
    private String login;

    @Schema(description = "Data/hora da última alteração")
    private LocalDateTime dataUltimaAlteracao;

    @Schema(description = "Tipo do usuário")
    private UserType tipo;

    @Schema(description = "Endereço do usuário")
    private AddressResponseDTO endereco;

    public static UserResponseDTO from(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getNome(),
                user.getEmail(),
                user.getLogin(),
                user.getDataUltimaAlteracao(),
                user.getTipo(),
                AddressResponseDTO.from(user.getEndereco())
        );
    }
}
