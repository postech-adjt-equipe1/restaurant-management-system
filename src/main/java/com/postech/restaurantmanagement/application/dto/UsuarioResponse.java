package com.postech.restaurantmanagement.application.dto;

import com.postech.restaurantmanagement.domain.model.Usuario;

public class UsuarioResponse {

    private Long id;
    private String nome;
    private String email;
    private Long tipoUsuarioId;

    public UsuarioResponse() {}

    public UsuarioResponse(Long id, String nome, String email, Long tipoUsuarioId) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.tipoUsuarioId = tipoUsuarioId;
    }

    public static UsuarioResponse from(Usuario usuario) {
        return new UsuarioResponse(usuario.getId(), usuario.getNome(), usuario.getEmail(), usuario.getTipoUsuarioId());
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Long getTipoUsuarioId() { return tipoUsuarioId; }
    public void setTipoUsuarioId(Long tipoUsuarioId) { this.tipoUsuarioId = tipoUsuarioId; }
}
