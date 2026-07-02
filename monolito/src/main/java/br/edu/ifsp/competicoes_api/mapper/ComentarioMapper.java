package br.edu.ifsp.competicoes_api.mapper;

import br.edu.ifsp.competicoes_api.dto.comentario.ComentarioRequestDTO;
import br.edu.ifsp.competicoes_api.dto.comentario.ComentarioResponseDTO;
import br.edu.ifsp.competicoes_api.model.Comentario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ComentarioMapper {

    ComentarioMapper INSTANCE = Mappers.getMapper(ComentarioMapper.class);

    // Na ida (Request -> Model), ignoramos os objetos complexos pois vamos buscá-los no banco manualmente
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "evento", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dataPublicacao", ignore = true)
    Comentario toModel(ComentarioRequestDTO requestDTO);

    // Na volta (Model -> Response), ensinamos de onde puxar os dados
    @Mapping(source = "usuario.id", target = "usuarioId")
    @Mapping(source = "usuario.nome", target = "nomeUsuario")
    @Mapping(source = "evento.id", target = "eventoId")
    ComentarioResponseDTO toResponseDTO(Comentario comentario);
}