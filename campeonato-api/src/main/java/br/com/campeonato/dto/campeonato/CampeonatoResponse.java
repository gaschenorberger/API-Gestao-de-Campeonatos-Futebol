package br.com.campeonato.dto.campeonato;

import br.com.campeonato.entity.Campeonato;

public record CampeonatoResponse(Long id, String nome, Integer temporada) {

    public static CampeonatoResponse from(Campeonato campeonato) {
        return new CampeonatoResponse(
                campeonato.getId(),
                campeonato.getNome(),
                campeonato.getTemporada()
        );
    }
}