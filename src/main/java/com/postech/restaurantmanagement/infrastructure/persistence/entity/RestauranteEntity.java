package com.postech.restaurantmanagement.infrastructure.persistence.entity;

import com.postech.restaurantmanagement.domain.model.Restaurante;
import jakarta.persistence.*;

@Entity
@Table(name = "restaurante")
public class RestauranteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", nullable = false, length = 150)
    private String nome;

    @Column(name = "endereco", nullable = false, length = 250)
    private String endereco;

    @Column(name = "tipo_cozinha", nullable = false, length = 100)
    private String tipoCozinha;

    @Column(name = "horario_funcionamento", nullable = false, length = 100)
    private String horarioFuncionamento;

    @Column(name = "dono_id", nullable = false)
    private Long donoId;

    public RestauranteEntity() {}

    public RestauranteEntity(Long id, String nome, String endereco, String tipoCozinha, String horarioFuncionamento, Long donoId) {
        this.id = id;
        this.nome = nome;
        this.endereco = endereco;
        this.tipoCozinha = tipoCozinha;
        this.horarioFuncionamento = horarioFuncionamento;
        this.donoId = donoId;
    }

    public static RestauranteEntity from(Restaurante restaurante) {
        return new RestauranteEntity(restaurante.getId(), restaurante.getNome(), restaurante.getEndereco(),
                restaurante.getTipoCozinha(), restaurante.getHorarioFuncionamento(), restaurante.getDonoId());
    }

    public Restaurante toDomain() {
        return new Restaurante(this.id, this.nome, this.endereco, this.tipoCozinha, this.horarioFuncionamento, this.donoId);
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
