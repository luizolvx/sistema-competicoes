package br.edu.ifsp.competicoes_api.dto.partida;

import java.time.LocalDateTime;

public record PartidaResponseDTO(
        Long id,
        String equipeA,
        String equipeB,
        Integer placarEquipeA,
        Integer placarEquipeB,
        LocalDateTime dataHora,
        String local,
        Long eventoId
) {}