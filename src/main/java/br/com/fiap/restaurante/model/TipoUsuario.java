package br.com.fiap.restaurante.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "tipo_usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, unique = true)
    private String nome;
}
