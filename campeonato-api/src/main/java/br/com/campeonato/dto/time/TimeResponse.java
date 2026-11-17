package br.com.campeonato.dto.time;

import br.com.campeonato.entity.Time;

public record TimeResponse(Long id, String nome, String sigla, Long campeonatoId) {

    public static TimeResponse from(Time time) {
        return new TimeResponse(
                time.getId(),
                time.getNome(),
                time.getSigla(),
                time.getCampeonato().getId()
        );
    }
}