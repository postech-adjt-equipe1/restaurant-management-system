package br.com.fiap.restaurante.service;

import br.com.fiap.restaurante.exception.DuplicateTipoUsuarioException;
import br.com.fiap.restaurante.exception.TipoUsuarioNotFoundException;
import br.com.fiap.restaurante.model.TipoUsuario;
import br.com.fiap.restaurante.repository.TipoUsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TipoUsuarioServiceImpl implements TipoUsuarioService {

    private final TipoUsuarioRepository tipoUsuarioRepository;

    @Override
    @Transactional
    public TipoUsuario create(TipoUsuario tipoUsuario) {
        if (tipoUsuarioRepository.existsByNomeIgnoreCase(tipoUsuario.getNome())) {
            throw new DuplicateTipoUsuarioException(tipoUsuario.getNome());
        }
        return tipoUsuarioRepository.save(tipoUsuario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TipoUsuario> findAll() {
        return tipoUsuarioRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public TipoUsuario findById(Long id) {
        return tipoUsuarioRepository.findById(id)
                .orElseThrow(() -> new TipoUsuarioNotFoundException(id));
    }

    @Override
    @Transactional
    public TipoUsuario update(Long id, TipoUsuario dadosAtualizados) {
        TipoUsuario existente = tipoUsuarioRepository.findById(id)
                .orElseThrow(() -> new TipoUsuarioNotFoundException(id));

        if (!existente.getNome().equalsIgnoreCase(dadosAtualizados.getNome())
                && tipoUsuarioRepository.existsByNomeIgnoreCase(dadosAtualizados.getNome())) {
            throw new DuplicateTipoUsuarioException(dadosAtualizados.getNome());
        }

        existente.setNome(dadosAtualizados.getNome());
        return tipoUsuarioRepository.save(existente);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        TipoUsuario tipoUsuario = tipoUsuarioRepository.findById(id)
                .orElseThrow(() -> new TipoUsuarioNotFoundException(id));
        tipoUsuarioRepository.delete(tipoUsuario);
    }
}
