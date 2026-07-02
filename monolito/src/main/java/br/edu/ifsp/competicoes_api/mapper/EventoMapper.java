package br.edu.ifsp.competicoes_api.mapper;

import br.edu.ifsp.competicoes_api.dto.evento.EventoRequestDTO;
import br.edu.ifsp.competicoes_api.dto.evento.EventoResponseDTO;
import br.edu.ifsp.competicoes_api.model.Evento;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring") // Faz o Spring gerenciar essa classe como um Bean injetável
public interface EventoMapper {

    EventoMapper INSTANCE = Mappers.getMapper(EventoMapper.class);

    // 1. Converte o Record de requisição para a Entidade JPA antes de salvar no banco
    @Mapping(target = "id", ignore = true) // Ignora o ID porque o banco de dados vai gerá-lo automaticamente
    @Mapping(target = "partidas", ignore = true)     // Ignora as coleções no momento do cadastro inicial
    @Mapping(target = "comentarios", ignore = true)
    Evento toModel(EventoRequestDTO dto);

    // 2. Converte a Entidade salva no banco para o Record de resposta que volta para o cliente
    EventoResponseDTO toResponseDTO(Evento model);
}