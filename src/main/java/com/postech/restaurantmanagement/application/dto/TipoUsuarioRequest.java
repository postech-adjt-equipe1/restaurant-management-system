package com.postech.restaurantmanagement.application.dto;

import jakarta.validation.constraints.NotBlank;

public class TipoUsuarioRequest {

    @NotBlank(message = "O nome do tipo de usuário é obrigatório")
    private String nome;

    public TipoUsuarioRequest() {}

    public TipoUsuarioRequest(String nome) {
        this.nome = nome;
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
}
