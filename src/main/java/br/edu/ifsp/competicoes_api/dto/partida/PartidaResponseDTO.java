package br.edu.ifsp.competicoes_api.dto.partida;

import br.edu.ifsp.competicoes_api.model.StatusPartida;
import java.time.LocalDateTime;

public record PartidaResponseDTO(
        Long id,
        String equipeA,
        String equipeB,
        Integer placarEquipeA,
        Integer placarEquipeB,
        LocalDateTime dataHora,
        String local,
        StatusPartida status,
        Long eventoId
) {}