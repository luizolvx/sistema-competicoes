package br.edu.ifsp.competicoes_api.service;

import br.edu.ifsp.competicoes_api.dto.evento.EventoRequestDTO;
import br.edu.ifsp.competicoes_api.dto.evento.EventoResponseDTO;
import br.edu.ifsp.competicoes_api.mapper.EventoMapper;
import br.edu.ifsp.competicoes_api.model.Evento;
import br.edu.ifsp.competicoes_api.model.Usuario;
import br.edu.ifsp.competicoes_api.repository.EventoRepository;
import br.edu.ifsp.competicoes_api.repository.UsuarioRepository; // Importação adicionada
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class EventoService {

    private final EventoRepository eventoRepository;
    private final UsuarioRepository usuarioRepository; // Dependência adicionada para o sistema de notificações
    private final EventoMapper eventoMapper;

    // O Spring Boot injeta essas três dependências automaticamente aqui via construtor
    public EventoService(EventoRepository eventoRepository, UsuarioRepository usuarioRepository, EventoMapper eventoMapper) {
        this.eventoRepository = eventoRepository;
        this.usuarioRepository = usuarioRepository;
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
     * Lógica interna para buscar usuários interessados e simular o envio de notificações via console.
     */
    private void dispararNotificacoesPorModalidade(String modalidade, String nomeEvento) {
        if (modalidade == null) return;

        // Busca no banco todos os alunos que marcaram essa modalidade como favorita
        List<Usuario> interessados = usuarioRepository.findByInteressesContaining(modalidade);

        // Simulação do envio (Logs estruturados para validação das regras de negócio do MVP)
        for (Usuario usuario : interessados) {
            System.out.println("==========================================================================");
            System.out.println("🔔 [NOTIFICAÇÃO SISTEMA] Enviando aviso para: " + usuario.getNome() + " (" + usuario.getEmail() + ")");
            System.out.println("📢 Olá! Um novo evento de " + modalidade + " foi criado: \"" + nomeEvento + "\"!");
            System.out.println("==========================================================================");
        }
    }
}