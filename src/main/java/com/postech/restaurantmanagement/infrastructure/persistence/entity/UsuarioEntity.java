package com.postech.restaurantmanagement.infrastructure.persistence.entity;

import com.postech.restaurantmanagement.domain.model.Usuario;
import jakarta.persistence.*;

@Entity
@Table(name = "usuario")
public class UsuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", nullable = false, length = 150)
    private String nome;

    @Column(name = "email", nullable = false, unique = true, length = 200)
    private String email;

    @Column(name = "senha", nullable = false)
    private String senha;

    @Column(name = "tipo_usuario_id", nullable = false)
    private Long tipoUsuarioId;

    public UsuarioEntity() {}

    public UsuarioEntity(Long id, String nome, String email, String senha, Long tipoUsuarioId) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.tipoUsuarioId = tipoUsuarioId;
    }

    public static UsuarioEntity from(Usuario usuario) {
        return new UsuarioEntity(usuario.getId(), usuario.getNome(), usuario.getEmail(), usuario.getSenha(), usuario.getTipoUsuarioId());
    }

    public Usuario toDomain() {
        return new Usuario(this.id, this.nome, this.email, this.senha, this.tipoUsuarioId);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public Long getTipoUsuarioId() { return tipoUsuarioId; }
    public void setTipoUsuarioId(Long tipoUsuarioId) { this.tipoUsuarioId = tipoUsuarioId; }
}
