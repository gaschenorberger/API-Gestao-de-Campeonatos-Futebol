package br.com.campeonato.service;

import br.com.campeonato.dto.time.TimeAtualizacaoRequest;
import br.com.campeonato.dto.time.TimeCadastroRequest;
import br.com.campeonato.dto.time.TimeResponse;
import br.com.campeonato.entity.Campeonato;
import br.com.campeonato.entity.Time;
import br.com.campeonato.repository.CampeonatoRepository;
import br.com.campeonato.repository.PartidaRepository;
import br.com.campeonato.repository.TimeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class TimeService {

    private final TimeRepository timeRepository;
    private final CampeonatoRepository campeonatoRepository;
    private final PartidaRepository partidaRepository;

    public TimeService(TimeRepository timeRepository, CampeonatoRepository campeonatoRepository, PartidaRepository partidaRepository) {
        this.timeRepository = timeRepository;
        this.campeonatoRepository = campeonatoRepository;
        this.partidaRepository = partidaRepository;
    }

    public List<TimeResponse> listar(Long campeonatoId) {
        List<Time> times = campeonatoId == null
                ? timeRepository.findAll()
                : timeRepository.findAllByCampeonatoId(campeonatoId);

        return times.stream()
                .map(TimeResponse::from)
                .toList();
    }

    public TimeResponse buscarPorId(Long id) {
        return TimeResponse.from(buscarTime(id));
    }

    @Transactional
    public TimeResponse cadastrar(TimeCadastroRequest request) {
        Time time = new Time();
        time.setNome(request.nome().strip());
        time.setSigla(request.sigla().strip());
        time.setCampeonato(buscarCampeonato(request.campeonatoId()));

        return TimeResponse.from(timeRepository.save(time));
    }

    @Transactional
    public TimeResponse atualizar(Long id, TimeAtualizacaoRequest request) {
        if (request.nome() == null && request.sigla() == null && request.campeonatoId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Informe pelo menos um campo para atualizar."
            );
        }

        Time time = timeRepository.buscarParaAtualizar(List.of(id)).stream()
            .findFirst()
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Time " + id + " não encontrado."
            ));

        if (request.nome() != null) {
            time.setNome(request.nome().strip());
        }

        if (request.sigla() != null) {
            time.setSigla(request.sigla().strip());
        }

        if (request.campeonatoId() != null && !request.campeonatoId().equals(time.getCampeonato().getId())) {
            if (partidaRepository.existsByMandanteIdOrVisitanteId(id, id)) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT, "O time já tem partidas no campeonato atual."
                );
            }

            time.setCampeonato(buscarCampeonato(request.campeonatoId()));
        }

        return TimeResponse.from(time);
    }

    @Transactional
    public void excluir(Long id) {
        timeRepository.delete(buscarTime(id));
    }

    private Time buscarTime(Long id) {
        return timeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Time " + id + " não encontrado."
                ));
    }

    private Campeonato buscarCampeonato(Long id) {
        return campeonatoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Campeonato " + id + " não encontrado."
                ));
    }
}