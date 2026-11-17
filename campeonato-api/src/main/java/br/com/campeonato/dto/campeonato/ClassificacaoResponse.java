package br.com.campeonato.dto.campeonato;

public record ClassificacaoResponse(
        int posicao,
        Long timeId,
        String nomeTime,
        int jogos,
        int vitorias,
        int empates,
        int derrotas,
        int golsMarcados,
        int golsSofridos,
        int saldoGols,
        int pontos
) {
}