package br.edu.ifsp.competicoes_api.comentario;

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
import br.edu.ifsp.competicoes_api.service.ComentarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ComentarioServiceTest {

    @Mock private ComentarioRepository comentarioRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private EventoRepository eventoRepository;
    @Mock private ComentarioMapper comentarioMapper;

    private ComentarioService comentarioService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        this.comentarioService = new ComentarioService(comentarioRepository, usuarioRepository, eventoRepository, comentarioMapper);
    }

    @Test
    @DisplayName("Deve publicar um comentário com sucesso")
    void devePublicarComentarioComSucesso() {
        ComentarioRequestDTO request = new ComentarioRequestDTO("Ansioso para o torneio!", 1L, 10L);
        Usuario u = new Usuario(); u.setId(1L); u.setNome("Gustavo");
        Evento e = new Evento(); e.setId(10L);
        Comentario c = new Comentario(); c.setId(100L);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(u));
        when(eventoRepository.findById(10L)).thenReturn(Optional.of(e));
        when(comentarioMapper.toModel(any())).thenReturn(c);
        when(comentarioRepository.save(any())).thenReturn(c);

        when(comentarioMapper.toResponseDTO(any())).thenReturn(
            new ComentarioResponseDTO(100L, "Ansioso para o torneio!", LocalDateTime.now(), 1L, "Gustavo", 10L, List.of())
        );

        assertNotNull(comentarioService.publicarComentario(request));
    }

    @Test
    @DisplayName("Deve lançar exceção se o usuário autor não existir")
    void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
        ComentarioRequestDTO request = new ComentarioRequestDTO("Vai ser top!", 99L, 10L);
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> comentarioService.publicarComentario(request));
    }

    @Test
    @DisplayName("Deve lançar exceção se o evento alvo não existir")
    void deveLancarExcecaoQuandoEventoNaoEncontrado() {
        ComentarioRequestDTO request = new ComentarioRequestDTO("Vai ser top!", 1L, 99L);
        Usuario u = new Usuario(); u.setId(1L);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(u));
        when(eventoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> comentarioService.publicarComentario(request));
    }

    @Test
    @DisplayName("Deve listar todos os comentários de um evento com sucesso")
    void deveListarComentariosDoEventoComSucesso() {
        Long eventoId = 10L;
        Comentario c = new Comentario();
        when(comentarioRepository.findByEventoId(eventoId)).thenReturn(List.of(c));

        when(comentarioMapper.toResponseDTO(any())).thenReturn(
            new ComentarioResponseDTO(1L, "Teste", LocalDateTime.now(), 1L, "User", eventoId, List.of())
        );

        var lista = comentarioService.listarPorEvento(eventoId);

        assertFalse(lista.isEmpty());
        assertEquals(1, lista.size());
        verify(comentarioRepository, times(1)).findByEventoId(eventoId);
    }

    @Test
    @DisplayName("Deve excluir um comentário com sucesso")
    void deveExcluirComentarioComSucesso() {
        Long id = 100L;
        when(comentarioRepository.existsById(id)).thenReturn(true);
        comentarioService.excluirComentario(id);
        verify(comentarioRepository, times(1)).deleteById(id);
    }
}