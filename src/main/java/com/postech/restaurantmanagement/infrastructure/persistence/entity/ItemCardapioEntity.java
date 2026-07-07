package com.postech.restaurantmanagement.infrastructure.persistence.entity;

import com.postech.restaurantmanagement.domain.model.ItemCardapio;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "item_cardapio")
public class ItemCardapioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", nullable = false, length = 150)
    private String nome;

    @Column(name = "descricao", nullable = false, length = 500)
    private String descricao;

    @Column(name = "preco", nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;

    @Column(name = "apenas_local", nullable = false)
    private boolean apenasLocal;

    @Column(name = "caminho_foto", length = 500)
    private String caminhoFoto;

    @Column(name = "restaurante_id", nullable = false)
    private Long restauranteId;

    public ItemCardapioEntity() {}

    public ItemCardapioEntity(Long id, String nome, String descricao, BigDecimal preco,
                               boolean apenasLocal, String caminhoFoto, Long restauranteId) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.apenasLocal = apenasLocal;
        this.caminhoFoto = caminhoFoto;
        this.restauranteId = restauranteId;
    }

    public static ItemCardapioEntity from(ItemCardapio item) {
        return new ItemCardapioEntity(item.getId(), item.getNome(), item.getDescricao(),
                item.getPreco(), item.isApenasLocal(), item.getCaminhoFoto(), item.getRestauranteId());
    }

    public ItemCardapio toDomain() {
        return new ItemCardapio(this.id, this.nome, this.descricao, this.preco,
                this.apenasLocal, this.caminhoFoto, this.restauranteId);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }
    public boolean isApenasLocal() { return apenasLocal; }
    public void setApenasLocal(boolean apenasLocal) { this.apenasLocal = apenasLocal; }
    public String getCaminhoFoto() { return caminhoFoto; }
    public void setCaminhoFoto(String caminhoFoto) { this.caminhoFoto = caminhoFoto; }
    public Long getRestauranteId() { return restauranteId; }
    public void setRestauranteId(Long restauranteId) { this.restauranteId = restauranteId; }
}
