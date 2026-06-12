package br.edu.ifsp.competicoes_api.service;

import br.edu.ifsp.competicoes_api.dto.partida.PartidaRequestDTO;
import br.edu.ifsp.competicoes_api.dto.partida.PartidaResponseDTO;
import br.edu.ifsp.competicoes_api.mapper.PartidaMapper;
import br.edu.ifsp.competicoes_api.model.Evento;
import br.edu.ifsp.competicoes_api.model.Partida;
import br.edu.ifsp.competicoes_api.repository.EventoRepository;
import br.edu.ifsp.competicoes_api.repository.PartidaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class PartidaService {

    private final PartidaRepository partidaRepository;
    private final EventoRepository eventoRepository;
    private final PartidaMapper partidaMapper;

    // Injeção de dependências via construtor
    public PartidaService(PartidaRepository partidaRepository, EventoRepository eventoRepository, PartidaMapper partidaMapper) {
        this.partidaRepository = partidaRepository;
        this.eventoRepository = eventoRepository;
        this.partidaMapper = partidaMapper;
    }

    @Transactional
    public PartidaResponseDTO cadastrarPartida(PartidaRequestDTO requestDTO) {

        // REGRA DE NEGÓCIO: A data da partida não pode ser no passado
        if (requestDTO.dataHora().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("A data da partida não pode ser no passado.");
        }

        // REGRA DE NEGÓCIO 2: Uma equipe não pode jogar contra ela mesma
        if (requestDTO.equipeA().trim().equalsIgnoreCase(requestDTO.equipeB().trim())) {
            throw new IllegalArgumentException("Uma equipe não pode jogar contra ela mesma.");
        }

        // 1. Busca o Evento no banco de dados. Se não existir, trava o cadastro e lança erro.
        Evento evento = eventoRepository.findById(requestDTO.eventoId())
                .orElseThrow(() -> new IllegalArgumentException("Evento não encontrado com o ID: " + requestDTO.eventoId()));

        // 2. Converte o DTO para a Entidade Partida (o Mapper ignora o evento e o id por enquanto)
        Partida partida = partidaMapper.toModel(requestDTO);

        // 3. Associa o evento real que buscamos do banco à nossa nova partida
        partida.setEvento(evento);

        // 4. Salva a partida no banco de dados
        Partida partidaSalva = partidaRepository.save(partida);

        // 5. Converte a partida salva de volta para o DTO de resposta e devolve para o Controller
        return partidaMapper.toResponseDTO(partidaSalva);
    }
}