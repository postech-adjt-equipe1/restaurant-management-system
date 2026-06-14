package br.com.fiap.restaurante.service;

import br.com.fiap.restaurante.model.TipoUsuario;

import java.util.List;

public interface TipoUsuarioService {

    TipoUsuario create(TipoUsuario tipoUsuario);

    List<TipoUsuario> findAll();

    TipoUsuario findById(Long id);

    TipoUsuario update(Long id, TipoUsuario dadosAtualizados);

    void delete(Long id);
}
