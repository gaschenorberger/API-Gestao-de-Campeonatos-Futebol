package br.com.campeonato.dto.partida;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record PartidaCadastroRequest(
        @NotNull @Positive Long campeonatoId,
        @NotNull @Positive Long mandanteId,
        @NotNull @Positive Long visitanteId,
        @NotNull @Positive Integer rodada,
        @NotNull LocalDateTime dataHora,
        @NotBlank @Size(max = 255) String local
) {
}