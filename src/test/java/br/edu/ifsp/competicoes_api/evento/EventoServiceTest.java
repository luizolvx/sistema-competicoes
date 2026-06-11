package br.edu.ifsp.competicoes_api.evento;

import br.edu.ifsp.competicoes_api.dto.evento.EventoRequestDTO;
import br.edu.ifsp.competicoes_api.dto.evento.EventoResponseDTO;
import br.edu.ifsp.competicoes_api.model.Evento;
import br.edu.ifsp.competicoes_api.repository.EventoRepository;
import br.edu.ifsp.competicoes_api.service.EventoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EventoServiceTest {

    @Mock
    private EventoRepository eventoRepository;

    @InjectMocks
    private EventoService eventoService; // Injeta o mock do repository na sua casca vazia

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Deve cadastrar um evento esportivo com sucesso no fluxo de TDD")
    void deveCadastrarEventoComSucesso() {
        // 1. GIVEN (Preparação do cenário)
        EventoRequestDTO request = new EventoRequestDTO(
                "Torneio de Basquete IFSP",
                "Basquete",
                "Quadra Poliesportiva",
                LocalDate.now().plusDays(7)
        );

        Evento eventoSalvo = new Evento("Torneio de Basquete IFSP", "Basquete", "Quadra Poliesportiva", LocalDate.now().plusDays(7));
        eventoSalvo.setId(1L); // Simula o ID que o banco de dados auto-incrementaria

        // Ensinando o Mockito a responder o evento com ID simulado quando o .save() for chamado
        when(eventoRepository.save(any(Evento.class))).thenReturn(eventoSalvo);

        // 2. WHEN (A ação que vai disparar a lógica)
        // ATENÇÃO: O IntelliJ vai deixar o método 'cadastrarEvento' vermelho. Isso é o TDD puro acontecendo!
        EventoResponseDTO response = eventoService.cadastrarEvento(request);

        // 3. THEN (As validações do resultado)
        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("Torneio de Basquete IFSP", response.nome());

        // Garante que a camada de serviço realmente acionou o repositório para salvar os dados
        verify(eventoRepository, times(1)).save(any(Evento.class));
    }
}