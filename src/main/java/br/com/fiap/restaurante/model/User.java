package br.com.fiap.restaurante.model;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 100)
    private String nome;

    @NotBlank
    @Email
    @Size(max = 150)
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank
    @Size(min = 3, max = 50)
    @Column(unique = true, nullable = false)
    private String login;

    @NotBlank
    private String senha;

    @Column(nullable = false)
    private LocalDateTime dataUltimaAlteracao;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserType tipo;

    @NotNull
    @Valid
    @Embedded
    private Address endereco;

    @PrePersist
    public void prePersist() {
        dataUltimaAlteracao = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        dataUltimaAlteracao = LocalDateTime.now();
    }
}
