package com.postech.restaurantmanagement.application.dto;

import com.postech.restaurantmanagement.domain.model.TipoUsuario;

public class TipoUsuarioResponse {

    private Long id;
    private String nome;

    public TipoUsuarioResponse() {}

    public TipoUsuarioResponse(Long id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public static TipoUsuarioResponse from(TipoUsuario tipoUsuario) {
        return new TipoUsuarioResponse(tipoUsuario.getId(), tipoUsuario.getNome());
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
}
