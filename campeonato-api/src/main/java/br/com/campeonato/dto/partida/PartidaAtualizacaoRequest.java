package br.com.campeonato.dto.partida;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record PartidaAtualizacaoRequest(
        @Positive Long campeonatoId,
        @Positive Long mandanteId,
        @Positive Long visitanteId,
        @Positive Integer rodada,
        LocalDateTime dataHora,
        @Pattern(regexp = "(?s).*\\S.*", message = "O local não pode ficar em branco.")
        @Size(max = 255)
        String local
) {
}