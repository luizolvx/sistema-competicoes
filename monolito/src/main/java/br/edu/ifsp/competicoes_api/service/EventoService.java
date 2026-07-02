package br.edu.ifsp.competicoes_api.service;

import br.edu.ifsp.competicoes_api.dto.evento.EventoRequestDTO;
import br.edu.ifsp.competicoes_api.dto.evento.EventoResponseDTO;
import br.edu.ifsp.competicoes_api.exception.ResourceNotFoundException;
import br.edu.ifsp.competicoes_api.mapper.EventoMapper;
import br.edu.ifsp.competicoes_api.model.Evento;
import br.edu.ifsp.competicoes_api.repository.EventoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class EventoService {

    private final EventoRepository eventoRepository;
    private final EventoMapper eventoMapper;

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

        // 3. SISTEMA DE NOTIFICAÇÕES: Dispara os avisos baseados no interesse da modalidade do evento
        this.dispararNotificacoesPorModalidade(eventoSalvo.getModalidade(), eventoSalvo.getNome());

        // 4. Converte a entidade salva de volta para o DTO de resposta da API
        return eventoMapper.toResponseDTO(eventoSalvo);
    }

    /**
     * Retorna a lista de todos os eventos.
     */
    public List<EventoResponseDTO> listarTodos() {
        return eventoRepository.findAll()
                .stream()
                .map(eventoMapper::toResponseDTO)
                .toList();
    }

    /**
     * Busca um evento específico pelo ID.
     */
    public EventoResponseDTO buscarPorId(Long id) {
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento não encontrado com o ID: " + id));
        return eventoMapper.toResponseDTO(evento);
    }

    /**
     * REQUISITO ADMINISTRATIVO: Atualiza os dados de um evento existente.
     */
    @Transactional
    public EventoResponseDTO atualizarEvento(Long id, EventoRequestDTO requestDTO) {
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento não encontrado com o ID: " + id));

        // REGRA DE NEGÓCIO: A nova data também não pode estar no passado
        if (requestDTO.dataRealizacao().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("A nova data do evento não pode ser uma data no passado.");
        }

        evento.setNome(requestDTO.nome());
        evento.setModalidade(requestDTO.modalidade());
        evento.setDataRealizacao(requestDTO.dataRealizacao());

        Evento eventoAtualizado = eventoRepository.save(evento);
        return eventoMapper.toResponseDTO(eventoAtualizado);
    }

    /**
     * REQUISITO ADMINISTRATIVO: Remove um evento do sistema de competições.
     */
    @Transactional
    public void excluirEvento(Long id) {
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento não encontrado com o ID: " + id));

        eventoRepository.delete(evento);
    }

    /**
     * Lógica interna para buscar usuários interessados e simular o envio de notificações via console.
     */
    private void dispararNotificacoesPorModalidade(String modalidade, String nomeEvento) {
        if (modalidade == null) return;

        // Como o monólito não tem mais acesso ao banco de Usuários, simulamos o aviso
        // para futura integração via rede com o microsserviço auth-service.
        System.out.println("==========================================================================");
        System.out.println("🔔 [AVISO DE ARQUITETURA] Um novo evento de " + modalidade + " foi criado: \"" + nomeEvento + "\"!");
        System.out.println("📢 O disparo de notificações (buscando interesses) deve ser roteado para o microsserviço de usuários.");
        System.out.println("==========================================================================");
    }
}