package com.postech.restaurantmanagement.application.service;

import com.postech.restaurantmanagement.application.dto.UsuarioRequest;
import com.postech.restaurantmanagement.application.dto.UsuarioResponse;
import com.postech.restaurantmanagement.domain.model.Usuario;
import com.postech.restaurantmanagement.domain.repository.TipoUsuarioRepository;
import com.postech.restaurantmanagement.domain.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final TipoUsuarioRepository tipoUsuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository, TipoUsuarioRepository tipoUsuarioRepository) {
        this.usuarioRepository = usuarioRepository;
        this.tipoUsuarioRepository = tipoUsuarioRepository;
    }

    public UsuarioResponse criar(UsuarioRequest request) {
        if (!tipoUsuarioRepository.existsById(request.getTipoUsuarioId())) {
            throw new RuntimeException("Tipo de usuário não encontrado com id: " + request.getTipoUsuarioId());
        }
        Usuario usuario = new Usuario(null, request.getNome(), request.getEmail(), request.getSenha(), request.getTipoUsuarioId());
        Usuario salvo = usuarioRepository.save(usuario);
        return UsuarioResponse.from(salvo);
    }

    public List<UsuarioResponse> listarTodos() {
        return usuarioRepository.findAll()
                .stream()
                .map(UsuarioResponse::from)
                .collect(Collectors.toList());
    }

    public UsuarioResponse buscarPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com id: " + id));
        return UsuarioResponse.from(usuario);
    }

    public UsuarioResponse atualizar(Long id, UsuarioRequest request) {
        Usuario existente = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com id: " + id));
        if (!tipoUsuarioRepository.existsById(request.getTipoUsuarioId())) {
            throw new RuntimeException("Tipo de usuário não encontrado com id: " + request.getTipoUsuarioId());
        }
        existente.setNome(request.getNome());
        existente.setEmail(request.getEmail());
        existente.setSenha(request.getSenha());
        existente.setTipoUsuarioId(request.getTipoUsuarioId());
        Usuario atualizado = usuarioRepository.save(existente);
        return UsuarioResponse.from(atualizado);
    }

    public void deletar(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuário não encontrado com id: " + id);
        }
        usuarioRepository.deleteById(id);
    }
}
