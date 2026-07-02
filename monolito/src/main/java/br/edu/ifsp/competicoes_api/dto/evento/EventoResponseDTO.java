package br.edu.ifsp.competicoes_api.dto.evento;

import java.time.LocalDate;

public record EventoResponseDTO(
        Long id,
        String nome,
        String modalidade,
        String local,
        LocalDate dataRealizacao
) {}