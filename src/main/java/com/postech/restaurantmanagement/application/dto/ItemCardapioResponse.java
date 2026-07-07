package com.postech.restaurantmanagement.application.dto;

import com.postech.restaurantmanagement.domain.model.ItemCardapio;

import java.math.BigDecimal;

public class ItemCardapioResponse {

    private Long id;
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private boolean apenasLocal;
    private String caminhoFoto;
    private Long restauranteId;

    public ItemCardapioResponse() {}

    public ItemCardapioResponse(Long id, String nome, String descricao, BigDecimal preco,
                                 boolean apenasLocal, String caminhoFoto, Long restauranteId) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.apenasLocal = apenasLocal;
        this.caminhoFoto = caminhoFoto;
        this.restauranteId = restauranteId;
    }

    public static ItemCardapioResponse from(ItemCardapio item) {
        return new ItemCardapioResponse(item.getId(), item.getNome(), item.getDescricao(),
                item.getPreco(), item.isApenasLocal(), item.getCaminhoFoto(), item.getRestauranteId());
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
