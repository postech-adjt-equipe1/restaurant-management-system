package com.postech.restaurantmanagement.application.service;

import com.postech.restaurantmanagement.application.dto.TipoUsuarioRequest;
import com.postech.restaurantmanagement.application.dto.TipoUsuarioResponse;
import com.postech.restaurantmanagement.domain.model.TipoUsuario;
import com.postech.restaurantmanagement.domain.repository.TipoUsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TipoUsuarioService {

    private final TipoUsuarioRepository tipoUsuarioRepository;

    public TipoUsuarioService(TipoUsuarioRepository tipoUsuarioRepository) {
        this.tipoUsuarioRepository = tipoUsuarioRepository;
    }

    public TipoUsuarioResponse criar(TipoUsuarioRequest request) {
        TipoUsuario tipoUsuario = new TipoUsuario(null, request.getNome());
        TipoUsuario salvo = tipoUsuarioRepository.save(tipoUsuario);
        return TipoUsuarioResponse.from(salvo);
    }

    public List<TipoUsuarioResponse> listarTodos() {
        return tipoUsuarioRepository.findAll()
                .stream()
                .map(TipoUsuarioResponse::from)
                .collect(Collectors.toList());
    }

    public TipoUsuarioResponse buscarPorId(Long id) {
        TipoUsuario tipoUsuario = tipoUsuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tipo de usuário não encontrado com id: " + id));
        return TipoUsuarioResponse.from(tipoUsuario);
    }

    public TipoUsuarioResponse atualizar(Long id, TipoUsuarioRequest request) {
        TipoUsuario existente = tipoUsuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tipo de usuário não encontrado com id: " + id));
        existente.setNome(request.getNome());
        TipoUsuario atualizado = tipoUsuarioRepository.save(existente);
        return TipoUsuarioResponse.from(atualizado);
    }

    public void deletar(Long id) {
        if (!tipoUsuarioRepository.existsById(id)) {
            throw new RuntimeException("Tipo de usuário não encontrado com id: " + id);
        }
        tipoUsuarioRepository.deleteById(id);
    }
}
