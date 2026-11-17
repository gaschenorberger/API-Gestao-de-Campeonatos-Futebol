package br.com.campeonato.service;

import br.com.campeonato.dto.campeonato.CampeonatoAtualizacaoRequest;
import br.com.campeonato.dto.campeonato.CampeonatoCadastroRequest;
import br.com.campeonato.dto.campeonato.CampeonatoResponse;
import br.com.campeonato.entity.Campeonato;
import br.com.campeonato.repository.CampeonatoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CampeonatoService {

    private final CampeonatoRepository campeonatoRepository;

    public CampeonatoService(CampeonatoRepository campeonatoRepository) {
        this.campeonatoRepository = campeonatoRepository;
    }

    public List<CampeonatoResponse> listar() {
        return campeonatoRepository.findAll().stream()
                .map(CampeonatoResponse::from)
                .toList();
    }

    public CampeonatoResponse buscarPorId(Long id) {
        return CampeonatoResponse.from(buscarCampeonato(id));
    }

    @Transactional
    public CampeonatoResponse cadastrar(CampeonatoCadastroRequest request) {
        Campeonato campeonato = new Campeonato();
        campeonato.setNome(request.nome().strip());
        campeonato.setTemporada(request.temporada());

        return CampeonatoResponse.from(campeonatoRepository.save(campeonato));
    }

    @Transactional
    public CampeonatoResponse atualizar(Long id, CampeonatoAtualizacaoRequest request) {
        if (request.nome() == null && request.temporada() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Informe pelo menos um campo para atualizar."
            );
        }

        Campeonato campeonato = buscarCampeonato(id);

        if (request.nome() != null) {
            campeonato.setNome(request.nome().strip());
        }

        if (request.temporada() != null) {
            campeonato.setTemporada(request.temporada());
        }

        return CampeonatoResponse.from(campeonato);
    }

    @Transactional
    public void excluir(Long id) {
        campeonatoRepository.delete(buscarCampeonato(id));
    }

    private Campeonato buscarCampeonato(Long id) {
        return campeonatoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Campeonato " + id + " não encontrado."
                ));
    }
}