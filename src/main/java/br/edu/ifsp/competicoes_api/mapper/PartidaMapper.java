package br.edu.ifsp.competicoes_api.mapper;

import br.edu.ifsp.competicoes_api.dto.partida.PartidaRequestDTO;
import br.edu.ifsp.competicoes_api.dto.partida.PartidaResponseDTO;
import br.edu.ifsp.competicoes_api.model.Partida;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PartidaMapper {

    PartidaMapper INSTANCE = Mappers.getMapper(PartidaMapper.class);

    // Ignora o ID, o objeto Evento inteiro e os placares na hora de criar a entidade
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "evento", ignore = true)
    @Mapping(target = "placarEquipeA", ignore = true)
    @Mapping(target = "placarEquipeB", ignore = true)
    Partida toModel(PartidaRequestDTO dto);

    // Transforma a entidade salva no DTO de resposta, extraindo o ID do evento pai
    @Mapping(target = "eventoId", source = "evento.id")
    PartidaResponseDTO toResponseDTO(Partida model);
}