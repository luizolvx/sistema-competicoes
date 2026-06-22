package br.edu.ifsp.competicoes_api.service;

import br.edu.ifsp.competicoes_api.dto.partida.PartidaRequestDTO;
import br.edu.ifsp.competicoes_api.dto.partida.PartidaResponseDTO;
import br.edu.ifsp.competicoes_api.dto.partida.PlacarUpdateDTO;
import br.edu.ifsp.competicoes_api.exception.ResourceNotFoundException;
import br.edu.ifsp.competicoes_api.mapper.PartidaMapper;
import br.edu.ifsp.competicoes_api.model.Evento;
import br.edu.ifsp.competicoes_api.model.Partida;
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
                .orElseThrow(() -> new ResourceNotFoundException("Evento não encontrado com o ID: " + requestDTO.eventoId()));

        // 2. Converte o DTO para a Entidade Partida (o Mapper ignora o evento e o id por enquanto)
        Partida partida = partidaMapper.toModel(requestDTO);

        // 3. Associa o evento real que buscamos do banco à nossa nova partida
        partida.setEvento(evento);

        // 4. Salva a partida no banco de dados
        Partida partidaSalva = partidaRepository.save(partida);

        // 5. Converte a partida salva de volta para o DTO de resposta e devolve para o Controller
        return partidaMapper.toResponseDTO(partidaSalva);
    }

    @Transactional
    public PartidaResponseDTO atualizarPlacar(Long partidaId, PlacarUpdateDTO placarDTO) {
        // 1. Busca a partida no banco de dados. Se não existir, lança erro.
        Partida partida = partidaRepository.findById(partidaId)
                .orElseThrow(() -> new ResourceNotFoundException("Partida não encontrada com o ID: " + partidaId));

        // 2. Guarda uma referência segura do evento antes da atualização
        Evento eventoAtual = partida.getEvento();

        // 3. Atualiza os dados da entidade com os novos placares vindos do DTO
        partida.setPlacarEquipeA(placarDTO.placarEquipeA());
        partida.setPlacarEquipeB(placarDTO.placarEquipeB());

        // 4. Salva a partida atualizada no banco de dados
        Partida partidaAtualizada = partidaRepository.save(partida);

        // 5. CORREÇÃO: Garante que a partida atualizada mantenha o vínculo estável do evento para o Mapper
        if (partidaAtualizada.getEvento() == null && eventoAtual != null) {
            partidaAtualizada.setEvento(eventoAtual);
        }

        // 6. Converte a partida atualizada de volta para o DTO de resposta
        return partidaMapper.toResponseDTO(partidaAtualizada);
    }

    /**
     * NOVA FUNCIONALIDADE: Algoritmo de Geração Automática da Chave Inicial do Torneio (Mata-Mata)
     * Embaralha as equipes para um sorteio justo e gera os confrontos iniciais do evento.
     */
    @Transactional
    public List<PartidaResponseDTO> gerarChaveamentoInicial(Long eventoId, List<String> equipes) {
        // 1. Valida se o evento de destino realmente existe
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new ResourceNotFoundException("Evento não encontrado com o ID: " + eventoId));

        // 2. Regra de Negócio: São necessárias no mínimo 2 equipes para haver disputa
        if (equipes == null || equipes.size() < 2) {
            throw new IllegalArgumentException("São necessárias pelo menos 2 equipes para gerar um chaveamento automático.");
        }

        // Clona a lista para evitar mutações indesejadas e realiza o sorteio (embaralhamento)
        List<String> listaSorteio = new ArrayList<>(equipes);
        Collections.shuffle(listaSorteio);

        // Se o número de inscritos for ímpar, adiciona uma vaga de avanço automático (Bye/W.O.)
        if (listaSorteio.size() % 2 != 0) {
            listaSorteio.add("AVANÇO AUTOMÁTICO (Folga)");
        }

        List<PartidaResponseDTO> listResponse = new ArrayList<>();

        // 3. Agrupa as equipes de duas em duas criando os confrontos
        for (int i = 0; i < listaSorteio.size(); i += 2) {
            Partida partida = new Partida();
            partida.setEvento(evento);
            partida.setEquipeA(listaSorteio.get(i));
            partida.setEquipeB(listaSorteio.get(i + 1));
            // Define uma data inicial padrão condizente (ex: data e hora atual ou do próprio evento)
            partida.setDataHora(LocalDateTime.now().plusDays(1)); 

            Partida partidaSalva = partidaRepository.save(partida);
            listResponse.add(partidaMapper.toResponseDTO(partidaSalva));
        }

        return listResponse;
    }

    /**
     * Retorna a lista de todas as partidas cadastradas.
     * Converte as entidades do banco em DTOs de resposta para o espectador.
     */
    public List<PartidaResponseDTO> listarTodas() {
        return partidaRepository.findAll()
                .stream()
                .map(partidaMapper::toResponseDTO)
                .toList();
    }
}