package br.edu.ifsp.competicoes_api.dto.comentario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ComentarioRequestDTO(
        @NotBlank(message = "O texto do comentário não pode estar vazio")
        @Size(max = 500, message = "O comentário deve ter no máximo 500 caracteres")
        String texto,

        @NotNull(message = "O ID do usuário autor é obrigatório")
        Long usuarioId,

        @NotNull(message = "O ID do evento é obrigatório")
        Long eventoId
) {}