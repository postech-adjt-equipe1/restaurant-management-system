package br.com.fiap.restaurante.dto;

import br.com.fiap.restaurante.model.Address;
import br.com.fiap.restaurante.model.User;
import br.com.fiap.restaurante.model.UserType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "Dados para criação de um novo usuário")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCreateRequestDTO {

    @Schema(description = "Nome completo", example = "João Silva")
    @NotBlank
    @Size(max = 100)
    private String nome;

    @Schema(description = "E-mail do usuário", example = "joao@email.com")
    @NotBlank
    @Email
    @Size(max = 150)
    private String email;

    @Schema(description = "Login de acesso (único)", example = "joao.silva")
    @NotBlank
    @Size(min = 3, max = 50)
    private String login;

    @Schema(description = "Senha de acesso (mínimo 8 caracteres)", example = "Senha@123")
    @NotBlank
    @Size(min = 8, message = "Senha deve ter no mínimo 8 caracteres")
    private String senha;

    @Schema(description = "Tipo do usuário")
    @NotNull
    private UserType tipo;

    @Schema(description = "Endereço do usuário")
    @NotNull
    @Valid
    private AddressRequestDTO endereco;

    public User toUser() {
        return User.builder()
                .nome(nome)
                .email(email)
                .login(login)
                .senha(senha)
                .tipo(tipo)
                .endereco(Address.builder()
                        .logradouro(endereco.getLogradouro())
                        .numero(endereco.getNumero())
                        .complemento(endereco.getComplemento())
                        .bairro(endereco.getBairro())
                        .cidade(endereco.getCidade())
                        .estado(endereco.getEstado())
                        .cep(endereco.getCep())
                        .build())
                .build();
    }
}
