package br.edu.ifsp.auth_service.mapper;

import br.edu.ifsp.competicoes_api.dto.usuario.UsuarioRequestDTO;
import br.edu.ifsp.competicoes_api.dto.usuario.UsuarioResponseDTO;
import br.edu.ifsp.competicoes_api.model.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    // Instância estática para usarmos nos testes unitários com facilidade
    UsuarioMapper INSTANCE = Mappers.getMapper(UsuarioMapper.class);

    /**
     * Converte os dados que vieram da requisição (Web) para a Entidade que vai pro Banco.
     */
    Usuario toModel(UsuarioRequestDTO requestDTO);

    /**
     * Converte a Entidade salva no Banco de volta para o formato de resposta da API.
     */
    UsuarioResponseDTO toResponseDTO(Usuario usuario);
}