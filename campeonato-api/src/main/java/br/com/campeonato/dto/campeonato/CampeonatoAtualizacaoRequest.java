package br.com.campeonato.dto.campeonato;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CampeonatoAtualizacaoRequest(
        @Pattern(regexp = "(?s).*\\S.*", message = "O nome não pode ficar em branco.")
        @Size(max = 255, message = "O nome deve ter até 255 caracteres.")
        String nome,

        @Positive(message = "A temporada deve ser maior que zero.")
        Integer temporada
) {
}