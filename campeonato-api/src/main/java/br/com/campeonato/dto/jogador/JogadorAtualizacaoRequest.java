package br.com.campeonato.dto.jogador;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record JogadorAtualizacaoRequest(
        @Pattern(regexp = "(?s).*\\S.*", message = "O nome não pode ficar em branco.")
        @Size(max = 255, message = "O nome deve ter até 255 caracteres.")
        String nome,

        @Pattern(regexp = "(?s).*\\S.*", message = "A posição não pode ficar em branco.")
        @Size(max = 255, message = "A posição deve ter até 255 caracteres.")
        String posicao,

        @PositiveOrZero(message = "O número da camisa deve ser maior ou igual a zero.")
        Integer numeroCamisa,

        @Positive(message = "O timeId deve ser maior que zero.")
        Long timeId
) {
}