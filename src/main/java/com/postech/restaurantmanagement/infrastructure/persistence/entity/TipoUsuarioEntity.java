package com.postech.restaurantmanagement.infrastructure.persistence.entity;

import com.postech.restaurantmanagement.domain.model.TipoUsuario;
import jakarta.persistence.*;

@Entity
@Table(name = "tipo_usuario")
public class TipoUsuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    public TipoUsuarioEntity() {}

    public TipoUsuarioEntity(Long id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public static TipoUsuarioEntity from(TipoUsuario tipoUsuario) {
        return new TipoUsuarioEntity(tipoUsuario.getId(), tipoUsuario.getNome());
    }

    public TipoUsuario toDomain() {
        return new TipoUsuario(this.id, this.nome);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
}
