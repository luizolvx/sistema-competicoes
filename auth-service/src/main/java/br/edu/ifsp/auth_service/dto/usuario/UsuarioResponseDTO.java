package br.edu.ifsp.auth_service.dto.usuario;

import br.edu.ifsp.auth_service.model.Role;

public record UsuarioResponseDTO(
        Long id,
        String nome,
        String email,
        Role role
) {}