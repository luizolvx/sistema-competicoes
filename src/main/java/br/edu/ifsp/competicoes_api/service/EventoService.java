package br.edu.ifsp.competicoes_api.service;

import br.edu.ifsp.competicoes_api.dto.evento.EventoRequestDTO;
import br.edu.ifsp.competicoes_api.dto.evento.EventoResponseDTO;
import br.edu.ifsp.competicoes_api.mapper.EventoMapper;
import br.edu.ifsp.competicoes_api.model.Evento;
import br.edu.ifsp.competicoes_api.repository.EventoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventoService {

    private final EventoRepository eventoRepository;
    private final EventoMapper eventoMapper;

    public EventoService(EventoRepository eventoRepository, EventoMapper eventoMapper) {
        this.eventoRepository = eventoRepository;
        this.eventoMapper = eventoMapper;
    }

    @Transactional
    public EventoResponseDTO cadastrarEvento(EventoRequestDTO requestDTO) {
        if (requestDTO.dataRealizacao().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("A data do evento não pode ser uma data no passado.");
        }

        Evento evento = eventoMapper.toModel(requestDTO);
        Evento eventoSalvo = eventoRepository.save(evento);
        return eventoMapper.toResponseDTO(eventoSalvo);
    }

    @Transactional(readOnly = true)
    public List<EventoResponseDTO> listarTodos() {
        return eventoRepository.findAll()
                .stream()
                .map(eventoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}