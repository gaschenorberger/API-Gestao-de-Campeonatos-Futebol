package br.com.campeonato.dto.time;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record TimeCadastroRequest(
        @NotBlank(message = "Informe o nome do time.")
        @Size(max = 255, message = "O nome deve ter até 255 caracteres.")
        String nome,

        @NotBlank(message = "Informe a sigla do time.")
        @Size(max = 255, message = "A sigla deve ter até 255 caracteres.")
        String sigla,

        @NotNull(message = "Informe o campeonatoId.")
        @Positive(message = "O campeonatoId deve ser maior que zero.")
        Long campeonatoId
) {
}