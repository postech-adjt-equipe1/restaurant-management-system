package com.postech.restaurantmanagement.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class ItemCardapioRequest {

    @NotBlank(message = "O nome é obrigatório")
    private String nome;

    @NotBlank(message = "A descrição é obrigatória")
    private String descricao;

    @NotNull(message = "O preço é obrigatório")
    @Positive(message = "O preço deve ser um valor positivo")
    private BigDecimal preco;

    @NotNull(message = "A disponibilidade apenas no local é obrigatória")
    private Boolean apenasLocal;

    private String caminhoFoto;

    public ItemCardapioRequest() {}

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }
    public Boolean getApenasLocal() { return apenasLocal; }
    public void setApenasLocal(Boolean apenasLocal) { this.apenasLocal = apenasLocal; }
    public String getCaminhoFoto() { return caminhoFoto; }
    public void setCaminhoFoto(String caminhoFoto) { this.caminhoFoto = caminhoFoto; }
}
