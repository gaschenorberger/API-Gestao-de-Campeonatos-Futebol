package br.com.campeonato.dto.campeonato;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CampeonatoCadastroRequest(
        @NotBlank(message = "Informe o nome do campeonato.")
        @Size(max = 255, message = "O nome deve ter até 255 caracteres.")
        String nome,

        @NotNull(message = "Informe a temporada.")
        @Positive(message = "A temporada deve ser maior que zero.")
        Integer temporada
) {
}