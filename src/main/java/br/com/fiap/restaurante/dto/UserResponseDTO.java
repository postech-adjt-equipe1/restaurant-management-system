package br.com.fiap.restaurante.dto;

import br.com.fiap.restaurante.model.User;
import br.com.fiap.restaurante.model.UserType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Schema(description = "Dados do usuário retornados pela API")
@Data
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

    @Schema(description = "Tipo de usuário associado")
    private TipoUsuarioResponseDTO tipoUsuario;

    public static UserResponseDTO from(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .nome(user.getNome())
                .email(user.getEmail())
                .login(user.getLogin())
                .dataUltimaAlteracao(user.getDataUltimaAlteracao())
                .tipo(user.getTipo())
                .endereco(AddressResponseDTO.from(user.getEndereco()))
                .tipoUsuario(user.getTipoUsuario() != null
                        ? TipoUsuarioResponseDTO.from(user.getTipoUsuario())
                        : null)
                .build();
    }
}
