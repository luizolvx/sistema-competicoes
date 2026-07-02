package br.edu.ifsp.competicoes_api.service;

import br.edu.ifsp.competicoes_api.dto.partida.PartidaRequestDTO;
import br.edu.ifsp.competicoes_api.dto.partida.PartidaResponseDTO;
import br.edu.ifsp.competicoes_api.dto.partida.PlacarUpdateDTO;
import br.edu.ifsp.competicoes_api.exception.ResourceNotFoundException;
import br.edu.ifsp.competicoes_api.mapper.PartidaMapper;
import br.edu.ifsp.competicoes_api.model.Evento;
import br.edu.ifsp.competicoes_api.model.Partida;
import br.edu.ifsp.competicoes_api.model.StatusPartida;
import br.edu.ifsp.competicoes_api.repository.EventoRepository;
import br.edu.ifsp.competicoes_api.repository.PartidaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class PartidaService {

    private final PartidaRepository partidaRepository;
    private final EventoRepository eventoRepository;
    private final PartidaMapper partidaMapper;

    public PartidaService(PartidaRepository partidaRepository, EventoRepository eventoRepository, PartidaMapper partidaMapper) {
        this.partidaRepository = partidaRepository;
        this.eventoRepository = eventoRepository;
        this.partidaMapper = partidaMapper;
    }

    @Transactional
    public PartidaResponseDTO cadastrarPartida(PartidaRequestDTO requestDTO) {
        if (requestDTO.dataHora().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("A data da partida não pode ser no passado.");
        }

        if (requestDTO.equipeA().trim().equalsIgnoreCase(requestDTO.equipeB().trim())) {
            throw new IllegalArgumentException("Uma equipe não pode jogar contra ela mesma.");
        }

        Evento evento = eventoRepository.findById(requestDTO.eventoId())
                .orElseThrow(() -> new ResourceNotFoundException("Evento não encontrado com o ID: " + requestDTO.eventoId()));

        Partida partida = partidaMapper.toModel(requestDTO);
        partida.setEvento(evento);

        Partida partidaSalva = partidaRepository.save(partida);

        return partidaMapper.toResponseDTO(partidaSalva);
    }

    @Transactional
    public PartidaResponseDTO atualizarPlacar(Long partidaId, PlacarUpdateDTO placarDTO) {
        Partida partida = partidaRepository.findById(partidaId)
                .orElseThrow(() -> new ResourceNotFoundException("Partida não encontrada com o ID: " + partidaId));

        Evento eventoAtual = partida.getEvento();

        partida.setPlacarEquipeA(placarDTO.placarEquipeA());
        partida.setPlacarEquipeB(placarDTO.placarEquipeB());

        if (partida.getStatus() == StatusPartida.AGENDADA) {
            partida.setStatus(StatusPartida.EM_ANDAMENTO);
        }

        Partida partidaAtualizada = partidaRepository.save(partida);

        if (partidaAtualizada.getEvento() == null && eventoAtual != null) {
            partidaAtualizada.setEvento(eventoAtual);
        }

        return partidaMapper.toResponseDTO(partidaAtualizada);
    }

    @Transactional
    public PartidaResponseDTO atualizarStatus(Long partidaId, StatusPartida novoStatus) {
        Partida partida = partidaRepository.findById(partidaId)
                .orElseThrow(() -> new ResourceNotFoundException("Partida não encontrada com o ID: " + partidaId));

        partida.setStatus(novoStatus);

        Partida partidaAtualizada = partidaRepository.save(partida);
        return partidaMapper.toResponseDTO(partidaAtualizada);
    }

    @Transactional
    public List<PartidaResponseDTO> gerarChaveamentoInicial(Long eventoId, List<String> equipes) {
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new ResourceNotFoundException("Evento não encontrado com o ID: " + eventoId));

        if (equipes == null || equipes.size() < 2) {
            throw new IllegalArgumentException("São necessárias pelo menos 2 equipes para gerar um chaveamento automático.");
        }

        List<String> listaSorteio = new ArrayList<>(equipes);
        Collections.shuffle(listaSorteio);

        if (listaSorteio.size() % 2 != 0) {
            listaSorteio.add("AVANÇO AUTOMÁTICO (Folga)");
        }

        List<PartidaResponseDTO> listResponse = new ArrayList<>();

        for (int i = 0; i < listaSorteio.size(); i += 2) {
            Partida partida = new Partida();
            partida.setEvento(evento);
            partida.setEquipeA(listaSorteio.get(i));
            partida.setEquipeB(listaSorteio.get(i + 1));
            partida.setDataHora(LocalDateTime.now().plusDays(1));

            Partida partidaSalva = partidaRepository.save(partida);
            listResponse.add(partidaMapper.toResponseDTO(partidaSalva));
        }

        return listResponse;
    }

    public List<PartidaResponseDTO> listarTodas() {
        return partidaRepository.findAll()
                .stream()
                .map(partidaMapper::toResponseDTO)
                .toList();
    }

    public List<PartidaResponseDTO> listarPorEvento(Long eventoId) {
        return partidaRepository.findByEventoId(eventoId)
                .stream()
                .map(partidaMapper::toResponseDTO)
                .toList();
    }
}