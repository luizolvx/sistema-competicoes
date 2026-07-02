package br.edu.ifsp.competicoes_api.service;

import br.edu.ifsp.competicoes_api.dto.comentario.ComentarioRequestDTO;
import br.edu.ifsp.competicoes_api.dto.comentario.ComentarioResponseDTO;
import br.edu.ifsp.competicoes_api.exception.ResourceNotFoundException;
import br.edu.ifsp.competicoes_api.mapper.ComentarioMapper;
import br.edu.ifsp.competicoes_api.model.Comentario;
import br.edu.ifsp.competicoes_api.model.Evento;
import br.edu.ifsp.competicoes_api.repository.ComentarioRepository;
import br.edu.ifsp.competicoes_api.repository.EventoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ComentarioService {

    private final ComentarioRepository comentarioRepository;
    private final EventoRepository eventoRepository;
    private final ComentarioMapper comentarioMapper;

    public ComentarioService(ComentarioRepository comentarioRepository,
                             EventoRepository eventoRepository,
                             ComentarioMapper comentarioMapper) {
        this.comentarioRepository = comentarioRepository;
        this.eventoRepository = eventoRepository;
        this.comentarioMapper = comentarioMapper;
    }

    @Transactional
    public ComentarioResponseDTO publicarComentario(ComentarioRequestDTO request) {
        Evento evento = eventoRepository.findById(request.eventoId())
                .orElseThrow(() -> new ResourceNotFoundException("Evento não encontrado com o ID: " + request.eventoId()));

        Comentario comentario = comentarioMapper.toModel(request);
        comentario.setAutorId(request.usuarioId()); // Atribui apenas a referência do ID
        comentario.setEvento(evento);
        comentario.setDataPublicacao(LocalDateTime.now());

        return comentarioMapper.toResponseDTO(comentarioRepository.save(comentario));
    }

    public List<ComentarioResponseDTO> listarPorEvento(Long eventoId) {
        return comentarioRepository.findByEventoId(eventoId).stream()
                .map(comentarioMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void excluirComentario(Long id) {
        if (!comentarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Comentário não encontrado com o ID: " + id);
        }
        comentarioRepository.deleteById(id);
    }

    @Transactional
    public ComentarioResponseDTO adicionarMidias(Long id, List<String> midias) {
        Comentario comentario = comentarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comentário não encontrado com o ID: " + id));

        if (midias != null) {
            comentario.getMidias().addAll(midias);
        }

        Comentario comentarioSalvo = comentarioRepository.save(comentario);
        return comentarioMapper.toResponseDTO(comentarioSalvo);
    }
}