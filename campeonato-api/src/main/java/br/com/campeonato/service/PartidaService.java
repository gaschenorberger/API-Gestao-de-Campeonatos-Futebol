package br.com.campeonato.service;

import br.com.campeonato.dto.partida.PartidaAtualizacaoRequest;
import br.com.campeonato.dto.partida.PartidaCadastroRequest;
import br.com.campeonato.dto.partida.PartidaPlacarRequest;
import br.com.campeonato.dto.partida.PartidaResponse;
import br.com.campeonato.dto.partida.PartidaStatusRequest;
import br.com.campeonato.entity.Campeonato;
import br.com.campeonato.entity.Partida;
import br.com.campeonato.entity.StatusPartida;
import br.com.campeonato.entity.Time;
import br.com.campeonato.repository.CampeonatoRepository;
import br.com.campeonato.repository.PartidaRepository;
import br.com.campeonato.repository.TimeRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class PartidaService {

    private final PartidaRepository partidaRepository;
    private final CampeonatoRepository campeonatoRepository;
    private final TimeRepository timeRepository;

    public PartidaService(
            PartidaRepository partidaRepository,
            CampeonatoRepository campeonatoRepository,
            TimeRepository timeRepository
    ) {
        this.partidaRepository = partidaRepository;
        this.campeonatoRepository = campeonatoRepository;
        this.timeRepository = timeRepository;
    }

    public List<PartidaResponse> listar(StatusPartida status, Long timeId) {
        Specification<Partida> filtro = (partida, query, cb) ->
                status == null ? cb.conjunction() : cb.equal(partida.get("status"), status);

        if (timeId != null) {
            filtro = filtro.and(envolveTime(timeId));
        }

        return partidaRepository.findAll(filtro).stream()
                .map(PartidaResponse::from)
                .toList();
    }

    public PartidaResponse buscarPorId(Long id) {
        return partidaRepository.findById(id)
                .map(PartidaResponse::from)
                .orElseThrow(() -> partidaNaoEncontrada(id));
    }

    @Transactional
    public PartidaResponse cadastrar(PartidaCadastroRequest request) {
        Partida partida = new Partida();

        definirAgendamento(
                partida, request.campeonatoId(), request.mandanteId(), request.visitanteId(),
                request.rodada(), request.dataHora(), request.local()
        );

        return PartidaResponse.from(partidaRepository.save(partida));
    }

    @Transactional
    public PartidaResponse atualizar(Long id, PartidaAtualizacaoRequest request) {
        if (request.campeonatoId() == null && request.mandanteId() == null
                && request.visitanteId() == null && request.rodada() == null
                && request.dataHora() == null && request.local() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Informe pelo menos um campo para atualizar."
            );
        }

        Partida partida = buscarPartidaParaAtualizar(id);

        if (partida.getStatus() != StatusPartida.AGENDADA) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Somente partidas agendadas podem ter o agendamento editado."
            );
        }

        definirAgendamento(
                partida,
                request.campeonatoId() == null ? partida.getCampeonato().getId() : request.campeonatoId(),
                request.mandanteId() == null ? partida.getMandante().getId() : request.mandanteId(),
                request.visitanteId() == null ? partida.getVisitante().getId() : request.visitanteId(),
                request.rodada() == null ? partida.getRodada() : request.rodada(),
                request.dataHora() == null ? partida.getDataHora() : request.dataHora(),
                request.local() == null ? partida.getLocal() : request.local()
        );

        return PartidaResponse.from(partida);
    }

    @Transactional
    public void excluir(Long id) {
        partidaRepository.delete(buscarPartidaParaAtualizar(id));
    }

    private void definirAgendamento(
            Partida partida,
            Long campeonatoId,
            Long mandanteId,
            Long visitanteId,
            Integer rodada,
            LocalDateTime dataHora,
            String local
    ) {
        if (mandanteId.equals(visitanteId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Mandante e visitante devem ser times diferentes."
            );
        }

        LocalDateTime horario = dataHora.truncatedTo(ChronoUnit.MICROS);

        Campeonato campeonato = campeonatoRepository.findById(campeonatoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Campeonato " + campeonatoId + " não encontrado."
                ));

        List<Time> times = timeRepository.buscarParaAtualizar(List.of(mandanteId, visitanteId));
        Time mandante = buscarTime(times, mandanteId);
        Time visitante = buscarTime(times, visitanteId);

        if (!campeonatoId.equals(mandante.getCampeonato().getId())
                || !campeonatoId.equals(visitante.getCampeonato().getId())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Os dois times devem pertencer ao campeonato da partida."
            );
        }

        Specification<Partida> mesmoHorario = (outra, query, cb) ->
                cb.equal(outra.get("dataHora"), horario);

        Specification<Partida> conflito = mesmoHorario.and(
                envolveTime(mandanteId).or(envolveTime(visitanteId))
        );

        if (partida.getId() != null) {
            conflito = conflito.and((outra, query, cb) ->
                    cb.notEqual(outra.get("id"), partida.getId()));
        }

        if (partidaRepository.exists(conflito)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Um dos times já tem partida nesse horário."
            );
        }

        partida.setCampeonato(campeonato);
        partida.setMandante(mandante);
        partida.setVisitante(visitante);
        partida.setRodada(rodada);
        partida.setDataHora(horario);
        partida.setLocal(local.strip());
    }

    private Specification<Partida> envolveTime(Long timeId) {
        return (partida, query, cb) -> cb.or(
                cb.equal(partida.get("mandante").get("id"), timeId),
                cb.equal(partida.get("visitante").get("id"), timeId)
        );
    }

    private Time buscarTime(List<Time> times, Long id) {
        return times.stream()
                .filter(time -> id.equals(time.getId()))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Time " + id + " não encontrado."
                ));
    }

    private Partida buscarPartidaParaAtualizar(Long id) {
        return partidaRepository.buscarParaAtualizar(id)
                .orElseThrow(() -> partidaNaoEncontrada(id));
    }

    private ResponseStatusException partidaNaoEncontrada(Long id) {
        return new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Partida " + id + " não encontrada."
        );
    }

    @Transactional
    public PartidaResponse atualizarStatus(Long id, PartidaStatusRequest request) {
        Partida partida = buscarPartidaParaAtualizar(id);
        StatusPartida statusAtual = partida.getStatus();
        StatusPartida novoStatus = request.status();

        if (statusAtual == novoStatus && novoStatus != StatusPartida.AGENDADA) {
            return PartidaResponse.from(partida);
        }

        if (statusAtual == StatusPartida.AGENDADA && novoStatus == StatusPartida.EM_ANDAMENTO) {
            partida.setGolsMandante(0);
            partida.setGolsVisitante(0);
        } else if (statusAtual != StatusPartida.EM_ANDAMENTO || novoStatus != StatusPartida.FINALIZADA) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A transição de " + statusAtual + " para " + novoStatus + " não é permitida."
            );
        }

        partida.setStatus(novoStatus);
        return PartidaResponse.from(partida);
    }

    @Transactional
    public PartidaResponse atualizarPlacar(Long id, PartidaPlacarRequest request) {
        Partida partida = buscarPartidaParaAtualizar(id);

        if (partida.getStatus() == StatusPartida.AGENDADA) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Inicie a partida antes de atualizar o placar."
            );
        }

        partida.setGolsMandante(request.golsMandante());
        partida.setGolsVisitante(request.golsVisitante());

        return PartidaResponse.from(partida);
    }
}