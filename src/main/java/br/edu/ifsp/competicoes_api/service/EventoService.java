package br.edu.ifsp.competicoes_api.service;

import br.edu.ifsp.competicoes_api.dto.evento.EventoRequestDTO;
import br.edu.ifsp.competicoes_api.dto.evento.EventoResponseDTO;
import br.edu.ifsp.competicoes_api.mapper.EventoMapper;
import br.edu.ifsp.competicoes_api.model.Evento;
import br.edu.ifsp.competicoes_api.repository.EventoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class EventoService {

    private final EventoRepository eventoRepository;
    private final EventoMapper eventoMapper;

    // O Spring Boot injeta essas duas dependências automaticamente aqui
    public EventoService(EventoRepository eventoRepository, EventoMapper eventoMapper) {
        this.eventoRepository = eventoRepository;
        this.eventoMapper = eventoMapper;
    }

    @Transactional // Garante que a operação seja segura e atômica no banco de dados
    public EventoResponseDTO cadastrarEvento(EventoRequestDTO requestDTO) {

        // REGRA DE NEGÓCIO: A data do evento não pode ser menor (estar no passado) que a data de hoje
        if (requestDTO.dataRealizacao().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("A data do evento não pode ser uma data no passado.");
        }

        // 1. Converte o DTO recebido da requisição para a Entidade JPA
        Evento evento = eventoMapper.toModel(requestDTO);

        // 2. Salva a entidade no banco de dados através do Spring Data JPA
        Evento eventoSalvo = eventoRepository.save(evento);

        // 3. Converte a entidade salva de volta para o DTO de resposta da API
        return eventoMapper.toResponseDTO(eventoSalvo);
    }
}