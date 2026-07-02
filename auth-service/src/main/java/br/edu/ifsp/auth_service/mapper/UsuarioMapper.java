package br.edu.ifsp.auth_service.mapper;

import br.edu.ifsp.auth_service.dto.usuario.UsuarioRequestDTO;
import br.edu.ifsp.auth_service.dto.usuario.UsuarioResponseDTO;
import br.edu.ifsp.auth_service.model.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    UsuarioMapper INSTANCE = Mappers.getMapper(UsuarioMapper.class);

    Usuario toModel(UsuarioRequestDTO requestDTO);
    UsuarioResponseDTO toResponseDTO(Usuario usuario);
}