package com.postech.restaurantmanagement.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UsuarioRequest {

    @NotBlank(message = "O nome é obrigatório")
    private String nome;

    @NotBlank(message = "O email é obrigatório")
    @Email(message = "Email inválido")
    private String email;

    @NotBlank(message = "A senha é obrigatória")
    private String senha;

    @NotNull(message = "O tipo de usuário é obrigatório")
    private Long tipoUsuarioId;

    public UsuarioRequest() {}

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public Long getTipoUsuarioId() { return tipoUsuarioId; }
    public void setTipoUsuarioId(Long tipoUsuarioId) { this.tipoUsuarioId = tipoUsuarioId; }
}
