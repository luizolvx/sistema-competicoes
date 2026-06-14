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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ComentarioServiceTest {

    @Mock
    private ComentarioRepository comentarioRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private EventoRepository eventoRepository;

    // Assumindo que você vai criar o Mapper no mesmo padrão dos anteriores
    private final ComentarioMapper comentarioMapper = ComentarioMapper.INSTANCE;

    private ComentarioService comentarioService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // O Service que ainda vamos criar, injetando os 3 repositórios!
        this.comentarioService = new ComentarioService(comentarioRepository, usuarioRepository, eventoRepository, comentarioMapper);
    }

    @Test
    @DisplayName("Deve publicar um comentário com sucesso")
    void devePublicarComentarioComSucesso() {
        // 1. GIVEN
        ComentarioRequestDTO request = new ComentarioRequestDTO("Ansioso para o torneio!", 1L, 10L);

        Usuario usuarioExistente = new Usuario();
        usuarioExistente.setId(1L);
        usuarioExistente.setNome("Gustavo");

        Evento eventoExistente = new Evento();
        eventoExistente.setId(10L);
        eventoExistente.setNome("Torneio de Vôlei");

        Comentario comentarioSalvo = new Comentario();
        comentarioSalvo.setId(100L);
        comentarioSalvo.setTexto(request.texto());
        comentarioSalvo.setDataPublicacao(LocalDateTime.now());
        comentarioSalvo.setUsuario(usuarioExistente);
        comentarioSalvo.setEvento(eventoExistente);

        // Simulamos que as validações encontram os dados no banco
        when(usuarioRepository.findById(request.usuarioId())).thenReturn(Optional.of(usuarioExistente));
        when(eventoRepository.findById(request.eventoId())).thenReturn(Optional.of(eventoExistente));

        when(comentarioRepository.save(any(Comentario.class))).thenReturn(comentarioSalvo);

        // 2. WHEN
        ComentarioResponseDTO response = comentarioService.publicarComentario(request);

        // 3. THEN
        assertNotNull(response);
        assertEquals(100L, response.id());
        assertEquals("Ansioso para o torneio!", response.texto());
        assertEquals(1L, response.usuarioId());
        assertEquals("Gustavo", response.nomeUsuario());
        assertEquals(10L, response.eventoId());

        verify(usuarioRepository, times(1)).findById(1L);
        verify(eventoRepository, times(1)).findById(10L);
        verify(comentarioRepository, times(1)).save(any(Comentario.class));
    }

    @Test
    @DisplayName("Deve lançar exceção se o usuário autor não existir")
    void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
        ComentarioRequestDTO request = new ComentarioRequestDTO("Vai ser top!", 99L, 10L);

        // O repositório de usuários retorna vazio para esse ID
        when(usuarioRepository.findById(request.usuarioId())).thenReturn(Optional.empty());

        ResourceNotFoundException excecao = assertThrows(ResourceNotFoundException.class, () -> {
            comentarioService.publicarComentario(request);
        });

        assertEquals("Usuário não encontrado com o ID: 99", excecao.getMessage());

        // Garante que o sistema travou antes de tentar buscar o evento ou salvar
        verify(eventoRepository, never()).findById(anyLong());
        verify(comentarioRepository, never()).save(any(Comentario.class));
    }

    @Test
    @DisplayName("Deve lançar exceção se o evento alvo não existir")
    void deveLancarExcecaoQuandoEventoNaoEncontrado() {
        ComentarioRequestDTO request = new ComentarioRequestDTO("Vai ser top!", 1L, 99L);

        Usuario usuarioExistente = new Usuario();
        usuarioExistente.setId(1L);

        // O usuário existe, mas o evento não
        when(usuarioRepository.findById(request.usuarioId())).thenReturn(Optional.of(usuarioExistente));
        when(eventoRepository.findById(request.eventoId())).thenReturn(Optional.empty());

        ResourceNotFoundException excecao = assertThrows(ResourceNotFoundException.class, () -> {
            comentarioService.publicarComentario(request);
        });

        assertEquals("Evento não encontrado com o ID: 99", excecao.getMessage());
        verify(comentarioRepository, never()).save(any(Comentario.class));
    }
}