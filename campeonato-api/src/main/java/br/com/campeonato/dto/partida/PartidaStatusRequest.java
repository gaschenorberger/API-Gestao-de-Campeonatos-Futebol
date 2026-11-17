package br.com.campeonato.dto.partida;

import br.com.campeonato.entity.StatusPartida;
import jakarta.validation.constraints.NotNull;

public record PartidaStatusRequest(
        @NotNull(message = "Informe o status da partida.")
        StatusPartida status
) {
}