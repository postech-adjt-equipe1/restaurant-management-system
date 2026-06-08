package br.com.fiap.restaurante.dto;

import br.com.fiap.restaurante.model.User;
import br.com.fiap.restaurante.model.UserType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Resultado resumido de usuário retornado na busca por nome")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSearchResponseDTO {

    @Schema(description = "Identificador único", example = "1")
    private Long id;

    @Schema(description = "Nome completo", example = "João Silva")
    private String nome;

    @Schema(description = "E-mail", example = "joao@email.com")
    private String email;

    @Schema(description = "Login de acesso", example = "joao.silva")
    private String login;

    @Schema(description = "Tipo do usuário")
    private UserType tipo;

    public static UserSearchResponseDTO from(User user) {
        return new UserSearchResponseDTO(
                user.getId(),
                user.getNome(),
                user.getEmail(),
                user.getLogin(),
                user.getTipo()
        );
    }
}
