package com.postech.restaurantmanagement.domain.model;

public class TipoUsuario {

    private Long id;
    private String nome;

    public TipoUsuario() {}

    public TipoUsuario(Long id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
}
