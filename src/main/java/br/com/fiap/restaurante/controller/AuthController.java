package br.com.fiap.restaurante.controller;

import br.com.fiap.restaurante.dto.LoginRequest;
import br.com.fiap.restaurante.dto.LoginResponse;
import br.com.fiap.restaurante.model.User;
import br.com.fiap.restaurante.security.JwtService;
import br.com.fiap.restaurante.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Controller de autenticação.
 * <p>
 * Expõe o endpoint público {@code POST /auth/login} que valida as credenciais
 * e devolve um token JWT para ser usado nas demais requisições protegidas.
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Endpoints de login e geração de token JWT")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;

    /**
     * Autentica o usuário e retorna um token JWT.
     *
     * <p>Uso:
     * <pre>
     * POST /auth/login
     * {
     *   "login": "joao",
     *   "senha": "minhasenha"
     * }
     * </pre>
     *
     * <p>O token retornado deve ser enviado nas próximas requisições protegidas:
     * <pre>
     * Authorization: Bearer &lt;token&gt;
     * </pre>
     */
    @PostMapping("/login")
    @Operation(summary = "Login", description = "Valida as credenciais e retorna um token JWT Bearer.")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        // Delega a validação de credenciais ao serviço (lança InvalidCredentialsException se inválido)
        User usuario = userService.validateLogin(request.getLogin(), request.getSenha());

        // Monta os claims extras que serão embutidos no token
        Map<String, Object> claims = Map.of(
                "nome", usuario.getNome(),
                "userType", usuario.getTipo().name()
        );

        String token = jwtService.generateToken(usuario.getLogin(), claims);

        LoginResponse response = new LoginResponse(
                token,
                jwtService.getExpirationMs(),
                usuario.getLogin(),
                usuario.getNome(),
                usuario.getTipo().name()
        );

        return ResponseEntity.ok(response);
    }
}