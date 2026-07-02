package br.edu.ifsp.competicoes_api.dto.usuario;

import br.edu.ifsp.competicoes_api.model.Role;

public record UsuarioResponseDTO(
        Long id,
        String nome,
        String email,
        Role role
) {}