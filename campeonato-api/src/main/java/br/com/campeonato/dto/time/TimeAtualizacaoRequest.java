package br.com.campeonato.dto.time;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record TimeAtualizacaoRequest(
        @Pattern(regexp = "(?s).*\\S.*", message = "O nome não pode ficar em branco.")
        @Size(max = 255, message = "O nome deve ter até 255 caracteres.")
        String nome,

        @Pattern(regexp = "(?s).*\\S.*", message = "A sigla não pode ficar em branco.")
        @Size(max = 255, message = "A sigla deve ter até 255 caracteres.")
        String sigla,

        @Positive(message = "O campeonatoId deve ser maior que zero.")
        Long campeonatoId
) {
}