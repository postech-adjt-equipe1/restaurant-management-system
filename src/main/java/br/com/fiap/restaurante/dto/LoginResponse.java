package br.com.fiap.restaurante.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {

    private String token;
    private String tipo;
    private Long expiresIn;
    private String login;
    private String nome;
    private String userType;

    public LoginResponse(String token, Long expiresIn, String login, String nome, String userType) {
        this.token = token;
        this.tipo = "Bearer";
        this.expiresIn = expiresIn;
        this.login = login;
        this.nome = nome;
        this.userType = userType;
    }
}