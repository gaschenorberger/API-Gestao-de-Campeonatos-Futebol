package br.com.campeonato.service;

import br.com.campeonato.dto.campeonato.ClassificacaoResponse;
import br.com.campeonato.entity.Partida;
import br.com.campeonato.entity.StatusPartida;
import br.com.campeonato.entity.Time;
import br.com.campeonato.repository.CampeonatoRepository;
import br.com.campeonato.repository.PartidaRepository;
import br.com.campeonato.repository.TimeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ClassificacaoService {

    private final CampeonatoRepository campeonatoRepository;
    private final TimeRepository timeRepository;
    private final PartidaRepository partidaRepository;

    public ClassificacaoService(
            CampeonatoRepository campeonatoRepository,
            TimeRepository timeRepository,
            PartidaRepository partidaRepository
    ) {
        this.campeonatoRepository = campeonatoRepository;
        this.timeRepository = timeRepository;
        this.partidaRepository = partidaRepository;
    }

    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    public List<ClassificacaoResponse> consultar(Long campeonatoId) {
        if (!campeonatoRepository.existsById(campeonatoId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Campeonato " + campeonatoId + " não encontrado."
            );
        }

        Map<Long, EstatisticaTime> estatisticasPorTime = new HashMap<>();

        for (Time time : timeRepository.findAllByCampeonatoId(campeonatoId)) {
            estatisticasPorTime.put(time.getId(), new EstatisticaTime(time));
        }

        List<Partida> partidas = partidaRepository.findAllByCampeonatoIdAndStatus(
                campeonatoId, StatusPartida.FINALIZADA
        );

        for (Partida partida : partidas) {
            EstatisticaTime mandante = estatisticasPorTime.get(partida.getMandante().getId());
            EstatisticaTime visitante = estatisticasPorTime.get(partida.getVisitante().getId());

            mandante.registrarPartida(partida.getGolsMandante(), partida.getGolsVisitante());
            visitante.registrarPartida(partida.getGolsVisitante(), partida.getGolsMandante());
        }

        List<EstatisticaTime> ordenadas = estatisticasPorTime.values().stream()
                .sorted(Comparator.comparingInt(EstatisticaTime::pontos)
                        .thenComparingInt(EstatisticaTime::saldoGols)
                        .thenComparingInt(estatistica -> estatistica.golsMarcados)
                        .reversed()
                        .thenComparingLong(estatistica -> estatistica.timeId))
                .toList();

        List<ClassificacaoResponse> classificacao = new ArrayList<>(ordenadas.size());

        for (int indice = 0; indice < ordenadas.size(); indice++) {
            classificacao.add(ordenadas.get(indice).toResponse(indice + 1));
        }

        return classificacao;
    }

    private static class EstatisticaTime {

        private final Long timeId;
        private final String nomeTime;
        private int vitorias;
        private int empates;
        private int derrotas;
        private int golsMarcados;
        private int golsSofridos;

        private EstatisticaTime(Time time) {
            this.timeId = time.getId();
            this.nomeTime = time.getNome();
        }

        private void registrarPartida(int marcados, int sofridos) {
            golsMarcados += marcados;
            golsSofridos += sofridos;

            if (marcados > sofridos) {
                vitorias++;
            } else if (marcados == sofridos) {
                empates++;
            } else {
                derrotas++;
            }
        }

        private int pontos() {
            return vitorias * 3 + empates;
        }

        private int saldoGols() {
            return golsMarcados - golsSofridos;
        }

        private ClassificacaoResponse toResponse(int posicao) {
            return new ClassificacaoResponse(
                    posicao,
                    timeId,
                    nomeTime,
                    vitorias + empates + derrotas,
                    vitorias,
                    empates,
                    derrotas,
                    golsMarcados,
                    golsSofridos,
                    saldoGols(),
                    pontos()
            );
        }
    }
}