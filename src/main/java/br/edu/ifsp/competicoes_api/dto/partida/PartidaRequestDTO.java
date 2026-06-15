package br.edu.ifsp.competicoes_api.dto.partida;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record PartidaRequestDTO(
        @NotBlank(message = "O nome da equipe A é obrigatório")
        String equipeA,

        @NotBlank(message = "O nome da equipe B é obrigatório")
        String equipeB,

        @NotNull(message = "A data e horário da partida são obrigatórios")
        @FutureOrPresent(message = "A partida não pode ser agendada no passado")
        LocalDateTime dataHora,

        @NotBlank(message = "O local da partida é obrigatório")
        String local,

        @NotNull(message = "O ID do evento vinculado é obrigatório")
        Long eventoId
) {}