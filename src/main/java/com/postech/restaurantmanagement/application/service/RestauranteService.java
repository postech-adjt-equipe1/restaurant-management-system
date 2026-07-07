package com.postech.restaurantmanagement.application.service;

import com.postech.restaurantmanagement.application.dto.RestauranteRequest;
import com.postech.restaurantmanagement.application.dto.RestauranteResponse;
import com.postech.restaurantmanagement.domain.model.Restaurante;
import com.postech.restaurantmanagement.domain.model.TipoUsuario;
import com.postech.restaurantmanagement.domain.model.Usuario;
import com.postech.restaurantmanagement.domain.repository.ItemCardapioRepository;
import com.postech.restaurantmanagement.domain.repository.RestauranteRepository;
import com.postech.restaurantmanagement.domain.repository.TipoUsuarioRepository;
import com.postech.restaurantmanagement.domain.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RestauranteService {

    private static final String TIPO_DONO_RESTAURANTE = "Dono de Restaurante";

    private final RestauranteRepository restauranteRepository;
    private final UsuarioRepository usuarioRepository;
    private final TipoUsuarioRepository tipoUsuarioRepository;
    private final ItemCardapioRepository itemCardapioRepository;

    public RestauranteService(RestauranteRepository restauranteRepository,
                               UsuarioRepository usuarioRepository,
                               TipoUsuarioRepository tipoUsuarioRepository,
                               ItemCardapioRepository itemCardapioRepository) {
        this.restauranteRepository = restauranteRepository;
        this.usuarioRepository = usuarioRepository;
        this.tipoUsuarioRepository = tipoUsuarioRepository;
        this.itemCardapioRepository = itemCardapioRepository;
    }

    public RestauranteResponse criar(RestauranteRequest request) {
        validarDono(request.getDonoId());
        Restaurante restaurante = new Restaurante(null, request.getNome(), request.getEndereco(),
                request.getTipoCozinha(), request.getHorarioFuncionamento(), request.getDonoId());
        Restaurante salvo = restauranteRepository.save(restaurante);
        return RestauranteResponse.from(salvo);
    }

    public List<RestauranteResponse> listarTodos() {
        return restauranteRepository.findAll()
                .stream()
                .map(RestauranteResponse::from)
                .collect(Collectors.toList());
    }

    public RestauranteResponse buscarPorId(Long id) {
        Restaurante restaurante = restauranteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurante não encontrado com id: " + id));
        return RestauranteResponse.from(restaurante);
    }

    public RestauranteResponse atualizar(Long id, RestauranteRequest request) {
        Restaurante existente = restauranteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurante não encontrado com id: " + id));
        validarDono(request.getDonoId());
        existente.setNome(request.getNome());
        existente.setEndereco(request.getEndereco());
        existente.setTipoCozinha(request.getTipoCozinha());
        existente.setHorarioFuncionamento(request.getHorarioFuncionamento());
        existente.setDonoId(request.getDonoId());
        Restaurante atualizado = restauranteRepository.save(existente);
        return RestauranteResponse.from(atualizado);
    }

    public void deletar(Long id) {
        if (!restauranteRepository.existsById(id)) {
            throw new RuntimeException("Restaurante não encontrado com id: " + id);
        }
        if (itemCardapioRepository.existsByRestauranteId(id)) {
            throw new IllegalStateException(
                    "Restaurante com id " + id + " possui itens de cardápio e não pode ser removido. Remova os itens primeiro.");
        }
        restauranteRepository.deleteById(id);
    }

    private void validarDono(Long donoId) {
        Usuario dono = usuarioRepository.findById(donoId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com id: " + donoId));
        TipoUsuario tipoUsuario = tipoUsuarioRepository.findById(dono.getTipoUsuarioId())
                .orElseThrow(() -> new RuntimeException("Tipo de usuário não encontrado com id: " + dono.getTipoUsuarioId()));
        if (!TIPO_DONO_RESTAURANTE.equalsIgnoreCase(tipoUsuario.getNome())) {
            throw new RuntimeException("Usuário informado não possui o tipo 'Dono de Restaurante'");
        }
    }
}
