package br.com.campeonato.dto.jogador;

import br.com.campeonato.entity.Jogador;
import br.com.campeonato.entity.Time;

public record JogadorResponse(
        Long id,
        String nome,
        String posicao,
        Integer numeroCamisa,
        Long timeId
) {

    public static JogadorResponse from(Jogador jogador) {
        Time time = jogador.getTime();

        return new JogadorResponse(
                jogador.getId(),
                jogador.getNome(),
                jogador.getPosicao(),
                jogador.getNumeroCamisa(),
                time == null ? null : time.getId()
        );
    }
}