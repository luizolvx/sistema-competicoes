package br.edu.ifsp.auth_service.dto.usuario;

import br.edu.ifsp.competicoes_api.model.Role;

public record LoginResponseDTO(
        Long id,
        String nome,
        String email,
        Role role,
        String token
) {}