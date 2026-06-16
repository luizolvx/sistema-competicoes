package br.edu.ifsp.competicoes_api.evento;

import br.edu.ifsp.competicoes_api.controller.EventoController;
import br.edu.ifsp.competicoes_api.dto.evento.EventoRequestDTO;
import br.edu.ifsp.competicoes_api.dto.evento.EventoResponseDTO;
import br.edu.ifsp.competicoes_api.service.EventoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Usa apenas o Mockito, ignorando totalmente o contexto do Spring Boot
@ExtendWith(MockitoExtension.class)
class EventoControllerFunctionalTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private EventoService eventoService;

    @InjectMocks
    private EventoController eventoController;

    @BeforeEach
    void setUp() {
        // Inicializa o MockMvc de forma isolada (Standalone) direto no seu Controller
        mockMvc = MockMvcBuilders.standaloneSetup(eventoController).build();

        // Configura o conversor JSON para aceitar o formato LocalDate perfeitamente
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @DisplayName("POST /eventos -> Deve retornar 201 Created ao enviar payload correto")
    void postEventoDeveRetornarCriado() throws Exception {
        EventoRequestDTO request = new EventoRequestDTO("Copa IFSP 2026", "Vôlei", "Quadra B", LocalDate.now().plusDays(5));
        EventoResponseDTO response = new EventoResponseDTO(10L, "Copa IFSP 2026", "Vôlei", "Quadra B", LocalDate.now().plusDays(5));

        when(eventoService.cadastrarEvento(any(EventoRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/eventos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.nome").value("Copa IFSP 2026"))
                .andExpect(jsonPath("$.modalidade").value("Vôlei"));
    }

    @Test
    @DisplayName("GET /eventos -> Deve retornar 200 OK e a lista de competições")
    void getEventosDeveRetornarLista() throws Exception {
        EventoResponseDTO response = new EventoResponseDTO(1L, "Jogos de Inverno", "Xadrez", "Auditório", LocalDate.now().plusDays(10));
        when(eventoService.listarTodos()).thenReturn(List.of(response));

        mockMvc.perform(get("/eventos")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Jogos de Inverno"))
                .andExpect(jsonPath("$[0].modalidade").value("Xadrez"));
    }
}