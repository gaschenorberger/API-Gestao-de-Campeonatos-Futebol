package br.com.campeonato.dto.partida;

import br.com.campeonato.entity.Partida;
import br.com.campeonato.entity.StatusPartida;

import java.time.LocalDateTime;

public record PartidaResponse(
        Long id,
        Long campeonatoId,
        String mandanteNome,
        String visitanteNome,
        Integer rodada,
        LocalDateTime dataHora,
        String local,
        StatusPartida status,
        Integer golsMandante,
        Integer golsVisitante
) {
    public static PartidaResponse from(Partida partida) {
        return new PartidaResponse(
                partida.getId(),
                partida.getCampeonato().getId(),
                partida.getMandante().getNome(),
                partida.getVisitante().getNome(),
                partida.getRodada(),
                partida.getDataHora(),
                partida.getLocal(),
                partida.getStatus(),
                partida.getGolsMandante(),
                partida.getGolsVisitante()
        );
    }
}