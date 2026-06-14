package br.com.fiap.restaurante.controller;

import br.com.fiap.restaurante.dto.TipoUsuarioRequestDTO;
import br.com.fiap.restaurante.dto.TipoUsuarioResponseDTO;
import br.com.fiap.restaurante.service.TipoUsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tipo-usuario")
@RequiredArgsConstructor
public class TipoUsuarioController implements TipoUsuarioControllerDocs {

    private final TipoUsuarioService tipoUsuarioService;

    @Override
    @PostMapping
    public ResponseEntity<TipoUsuarioResponseDTO> create(@Valid @RequestBody TipoUsuarioRequestDTO request) {
        var tipoUsuario = tipoUsuarioService.create(request.toTipoUsuario());
        return ResponseEntity.status(HttpStatus.CREATED).body(TipoUsuarioResponseDTO.from(tipoUsuario));
    }

    @Override
    @GetMapping
    public ResponseEntity<List<TipoUsuarioResponseDTO>> findAll() {
        var tipos = tipoUsuarioService.findAll().stream()
                .map(TipoUsuarioResponseDTO::from)
                .toList();
        return ResponseEntity.ok(tipos);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<TipoUsuarioResponseDTO> findById(@PathVariable Long id) {
        var tipoUsuario = tipoUsuarioService.findById(id);
        return ResponseEntity.ok(TipoUsuarioResponseDTO.from(tipoUsuario));
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<TipoUsuarioResponseDTO> update(@PathVariable Long id,
                                                         @Valid @RequestBody TipoUsuarioRequestDTO request) {
        var tipoUsuario = tipoUsuarioService.update(id, request.toTipoUsuario());
        return ResponseEntity.ok(TipoUsuarioResponseDTO.from(tipoUsuario));
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tipoUsuarioService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
