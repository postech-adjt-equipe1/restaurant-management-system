package br.com.fiap.restaurante.controller;

import br.com.fiap.restaurante.dto.LoginResponse;
import br.com.fiap.restaurante.dto.UserChangePasswordRequestDTO;
import br.com.fiap.restaurante.dto.UserCreateRequestDTO;
import br.com.fiap.restaurante.dto.UserLoginRequestDTO;
import br.com.fiap.restaurante.dto.UserSearchResponseDTO;
import br.com.fiap.restaurante.dto.UserUpdateRequestDTO;
import br.com.fiap.restaurante.dto.UserResponseDTO;
import br.com.fiap.restaurante.security.JwtService;
import br.com.fiap.restaurante.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
public class UserController implements UserControllerDocs {

    private final UserService userService;
    private final JwtService jwtService;

    @Override
    @PostMapping
    public ResponseEntity<UserResponseDTO> create(@Valid @RequestBody UserCreateRequestDTO request) {
        var user = userService.create(request.toUser());
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponseDTO.from(user));
    }

    @Override
    @GetMapping
    public ResponseEntity<List<UserSearchResponseDTO>> findByName(@RequestParam String nome) {
        var users = userService.findByName(nome).stream()
                .map(UserSearchResponseDTO::from)
                .toList();
        return ResponseEntity.ok(users);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> findById(@PathVariable Long id) {
        var user = userService.findById(id);
        return ResponseEntity.ok(UserResponseDTO.from(user));
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> update(@PathVariable Long id,
                                                  @Valid @RequestBody UserUpdateRequestDTO request) {
        var user = userService.update(id, request.toUser());
        return ResponseEntity.ok(UserResponseDTO.from(user));
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody UserLoginRequestDTO request) {
        var user = userService.validateLogin(request.getLogin(), request.getSenha());

        Map<String, Object> claims = Map.of(
                "nome", user.getNome(),
                "userType", user.getTipo().name()
        );

        String token = jwtService.generateToken(user.getLogin(), claims);

        return ResponseEntity.ok(new LoginResponse(
                token,
                jwtService.getExpirationMs(),
                user.getLogin(),
                user.getNome(),
                user.getTipo().name()
        ));
    }

    @Override
    @PatchMapping("/{id}/senha")
    public ResponseEntity<Void> changePassword(@PathVariable Long id,
                                               @Valid @RequestBody UserChangePasswordRequestDTO request) {
        userService.changePassword(id, request.getSenhaAtual(), request.getNovaSenha());
        return ResponseEntity.noContent().build();
    }
}
