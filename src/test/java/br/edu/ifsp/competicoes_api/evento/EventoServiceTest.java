package br.edu.ifsp.competicoes_api.evento;

import br.edu.ifsp.competicoes_api.dto.evento.EventoRequestDTO;
import br.edu.ifsp.competicoes_api.dto.evento.EventoResponseDTO;
import br.edu.ifsp.competicoes_api.mapper.EventoMapper;
import br.edu.ifsp.competicoes_api.model.Evento;
import br.edu.ifsp.competicoes_api.repository.EventoRepository;
import br.edu.ifsp.competicoes_api.service.EventoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventoServiceTest {

    @Mock
    private EventoRepository eventoRepository;

    // Mudamos de @Mock para @Spy usando a instância real do MapStruct
    @Spy
    private EventoMapper eventoMapper = Mappers.getMapper(EventoMapper.class);

    @InjectMocks
    private EventoService eventoService;

    private EventoRequestDTO requestValido;
    private Evento eventoMock;

    @BeforeEach
    void setUp() {
        requestValido = new EventoRequestDTO("Interclasses IFSP", "Futebol", "Quadra Principal", LocalDate.now().plusDays(2));
        eventoMock = new Evento("Interclasses IFSP", "Futebol", "Quadra Principal", LocalDate.now().plusDays(2));
        eventoMock.setId(1L);
    }

    @Test
    @DisplayName("Deve cadastrar um evento com sucesso quando os dados forem válidos")
    void deveCadastrarEventoComSucesso() {
        // Arrange
        // Como o mapper agora é real, NÃO precisamos dos 'when(eventoMapper...)'
        when(eventoRepository.save(any(Evento.class))).thenReturn(eventoMock);

        // Act
        EventoResponseDTO resultado = eventoService.cadastrarEvento(requestValido);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.id());
        assertEquals("Interclasses IFSP", resultado.nome());
        verify(eventoRepository, times(1)).save(any(Evento.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar cadastrar evento com data no passado")
    void deveLancarexcecaoParaDataNoPassado() {
        // Arrange
        EventoRequestDTO requestInvalido = new EventoRequestDTO("Campeonato Ruim", "Basquete", "Ginásio", LocalDate.now().minusDays(1));

        // Act & Assert
        IllegalArgumentException excecao = assertThrows(IllegalArgumentException.class, () -> {
            eventoService.cadastrarEvento(requestInvalido);
        });

        assertEquals("A data do evento não pode ser uma data no passado.", excecao.getMessage());
        verify(eventoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve listar todos os eventos cadastrados")
    void deveListarTodosOsEventos() {
        // Arrange
        when(eventoRepository.findAll()).thenReturn(List.of(eventoMock));

        // Act
        List<EventoResponseDTO> listagem = eventoService.listarTodos();

        // Assert
        assertFalse(listagem.isEmpty());
        assertEquals(1, listagem.size());
        assertEquals("Futebol", listagem.get(0).modalidade());
    }
}