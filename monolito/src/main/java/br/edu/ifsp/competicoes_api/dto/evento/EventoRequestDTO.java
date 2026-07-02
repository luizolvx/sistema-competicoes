package br.edu.ifsp.competicoes_api.dto.evento;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

public record EventoRequestDTO(
        @NotBlank(message = "O nome do evento é obrigatório")
        String nome,

        @NotBlank(message = "A modalidade esportiva é obrigatória")
        String modalidade,

        @NotBlank(message = "O local do evento é obrigatório")
        String local,

        @NotNull(message = "A data de realização é obrigatória")
        @FutureOrPresent(message = "A data do evento não pode ser no passado")
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate dataRealizacao
) {}