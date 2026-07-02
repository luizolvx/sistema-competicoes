package br.edu.ifsp.competicoes_api.dto.partida;

import br.edu.ifsp.competicoes_api.model.StatusPartida;
import jakarta.validation.constraints.NotNull;

public record StatusUpdateDTO(
        @NotNull(message = "O status da partida é obrigatório")
        StatusPartida status
) {}