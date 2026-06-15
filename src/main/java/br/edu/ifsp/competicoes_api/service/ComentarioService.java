package br.edu.ifsp.competicoes_api.service;

import br.edu.ifsp.competicoes_api.dto.comentario.ComentarioRequestDTO;
import br.edu.ifsp.competicoes_api.dto.comentario.ComentarioResponseDTO;
import br.edu.ifsp.competicoes_api.exception.ResourceNotFoundException;
import br.edu.ifsp.competicoes_api.mapper.ComentarioMapper;
import br.edu.ifsp.competicoes_api.model.Comentario;
import br.edu.ifsp.competicoes_api.model.Evento;
import br.edu.ifsp.competicoes_api.model.Usuario;
import br.edu.ifsp.competicoes_api.repository.ComentarioRepository;
import br.edu.ifsp.competicoes_api.repository.EventoRepository;
import br.edu.ifsp.competicoes_api.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ComentarioService {

    private final ComentarioRepository comentarioRepository;
    private final UsuarioRepository usuarioRepository;
    private final EventoRepository eventoRepository;
    private final ComentarioMapper comentarioMapper;

    public ComentarioService(ComentarioRepository comentarioRepository,
                             UsuarioRepository usuarioRepository,
                             EventoRepository eventoRepository,
                             ComentarioMapper comentarioMapper) {
        this.comentarioRepository = comentarioRepository;
        this.usuarioRepository = usuarioRepository;
        this.eventoRepository = eventoRepository;
        this.comentarioMapper = comentarioMapper;
    }

    @Transactional
    public ComentarioResponseDTO publicarComentario(ComentarioRequestDTO request) {

        // 1. Busca o Usuário. Se não existir, lança erro.
        Usuario usuario = usuarioRepository.findById(request.usuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com o ID: " + request.usuarioId()));

        // 2. Busca o Evento. Se não existir, lança erro.
        Evento evento = eventoRepository.findById(request.eventoId())
                .orElseThrow(() -> new ResourceNotFoundException("Evento não encontrado com o ID: " + request.eventoId()));

        // 3. Converte o DTO para Entidade
        Comentario comentario = comentarioMapper.toModel(request);

        // 4. Associa os dados complementares
        comentario.setUsuario(usuario);
        comentario.setEvento(evento);
        comentario.setDataPublicacao(LocalDateTime.now());

        // 5. Salva no banco de dados
        Comentario comentarioSalvo = comentarioRepository.save(comentario);

        // 6. Converte de volta para DTO e responde
        return comentarioMapper.toResponseDTO(comentarioSalvo);
    }
}