package br.edu.ifsp.competicoes_api.dto.comentario;

import java.time.LocalDateTime;

public record ComentarioResponseDTO(
        Long id,
        String texto,
        LocalDateTime dataPublicacao,
        Long usuarioId,
        String nomeUsuario, // Nome do aluno que comentou para exibir na tela
        Long eventoId
) {}