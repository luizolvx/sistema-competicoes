package br.edu.ifsp.competicoes_api.dto.comentario;

import java.time.LocalDateTime;
import java.util.List;

public record ComentarioResponseDTO(
        Long id,
        String texto,
        LocalDateTime dataPublicacao,
        Long usuarioId,
        String nomeUsuario,
        Long eventoId,
        List<String> midias
) {}