package br.edu.ifsp.competicoes_api.dto.partida;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PlacarUpdateDTO(
        @NotNull(message = "O placar da equipe A não pode ser nulo")
        @Min(value = 0, message = "O placar não pode ser um número negativo")
        Integer placarEquipeA,

        @NotNull(message = "O placar da equipe B não pode ser nulo")
        @Min(value = 0, message = "O placar não pode ser um número negativo")
        Integer placarEquipeB
) {}