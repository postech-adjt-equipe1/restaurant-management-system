package com.postech.restaurantmanagement.application.dto;

import com.postech.restaurantmanagement.domain.model.Restaurante;

public class RestauranteResponse {

    private Long id;
    private String nome;
    private String endereco;
    private String tipoCozinha;
    private String horarioFuncionamento;
    private Long donoId;

    public RestauranteResponse() {}

    public RestauranteResponse(Long id, String nome, String endereco, String tipoCozinha, String horarioFuncionamento, Long donoId) {
        this.id = id;
        this.nome = nome;
        this.endereco = endereco;
        this.tipoCozinha = tipoCozinha;
        this.horarioFuncionamento = horarioFuncionamento;
        this.donoId = donoId;
    }

    public static RestauranteResponse from(Restaurante restaurante) {
        return new RestauranteResponse(restaurante.getId(), restaurante.getNome(), restaurante.getEndereco(),
                restaurante.getTipoCozinha(), restaurante.getHorarioFuncionamento(), restaurante.getDonoId());
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    public String getTipoCozinha() { return tipoCozinha; }
    public void setTipoCozinha(String tipoCozinha) { this.tipoCozinha = tipoCozinha; }
    public String getHorarioFuncionamento() { return horarioFuncionamento; }
    public void setHorarioFuncionamento(String horarioFuncionamento) { this.horarioFuncionamento = horarioFuncionamento; }
    public Long getDonoId() { return donoId; }
    public void setDonoId(Long donoId) { this.donoId = donoId; }
}
