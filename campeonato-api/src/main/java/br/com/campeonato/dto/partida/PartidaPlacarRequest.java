package br.com.campeonato.dto.partida;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record PartidaPlacarRequest(
        @NotNull(message = "Informe os gols do mandante.")
        @PositiveOrZero(message = "Os gols do mandante devem ser maiores ou iguais a zero.")
        Integer golsMandante,

        @NotNull(message = "Informe os gols do visitante.")
        @PositiveOrZero(message = "Os gols do visitante devem ser maiores ou iguais a zero.")
        Integer golsVisitante
) {
}