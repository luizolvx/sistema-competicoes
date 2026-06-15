package br.edu.ifsp.competicoes_api.partida;

import br.edu.ifsp.competicoes_api.dto.partida.PartidaRequestDTO;
import br.edu.ifsp.competicoes_api.dto.partida.PartidaResponseDTO;
import br.edu.ifsp.competicoes_api.dto.partida.PlacarUpdateDTO;
import br.edu.ifsp.competicoes_api.exception.ResourceNotFoundException;
import br.edu.ifsp.competicoes_api.mapper.PartidaMapper;
import br.edu.ifsp.competicoes_api.model.Evento;
import br.edu.ifsp.competicoes_api.model.Partida;
import br.edu.ifsp.competicoes_api.repository.EventoRepository;
import br.edu.ifsp.competicoes_api.repository.PartidaRepository;
import br.edu.ifsp.competicoes_api.service.PartidaService;
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

class PartidaServiceTest {

    @Mock
    private PartidaRepository partidaRepository;

    @Mock
    private EventoRepository eventoRepository;

    private final PartidaMapper partidaMapper = PartidaMapper.INSTANCE;
    private PartidaService partidaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        this.partidaService = new PartidaService(partidaRepository, eventoRepository, partidaMapper);
    }

    @Test
    @DisplayName("Deve cadastrar uma partida com sucesso vinculada a um evento")
    void deveCadastrarPartidaComSucesso() {
        // 1. GIVEN (Preparação do cenário)
        Long eventoId = 1L;

        Evento eventoExistente = new Evento();
        eventoExistente.setId(eventoId);
        eventoExistente.setNome("Torneio de Futebol");

        // Usando o seu PartidaRequestDTO real
        PartidaRequestDTO request = new PartidaRequestDTO(
                "Vasco",
                "Flamengo",
                LocalDateTime.now().plusDays(2),
                "Maracanã",
                eventoId
        );

        // Simulando a Entidade que o banco de dados vai salvar
        Partida partidaSalva = new Partida();
        partidaSalva.setId(10L);
        partidaSalva.setEquipeA(request.equipeA());
        partidaSalva.setEquipeB(request.equipeB());
        partidaSalva.setDataHora(request.dataHora());
        partidaSalva.setLocal(request.local());
        partidaSalva.setEvento(eventoExistente);
        // O placar começa nulo por padrão

        when(eventoRepository.findById(eventoId)).thenReturn(Optional.of(eventoExistente));
        when(partidaRepository.save(any(Partida.class))).thenReturn(partidaSalva);

        // 2. WHEN (Ação)
        PartidaResponseDTO response = partidaService.cadastrarPartida(request);

        // 3. THEN (Validações)
        assertNotNull(response);
        assertEquals(10L, response.id());
        assertEquals("Vasco", response.equipeA());
        assertEquals("Flamengo", response.equipeB());
        assertEquals("Maracanã", response.local());
        assertEquals(eventoId, response.eventoId());
        assertNull(response.placarEquipeA()); // Confirma que o placar nasce zerado/nulo

        verify(eventoRepository, times(1)).findById(eventoId);
        verify(partidaRepository, times(1)).save(any(Partida.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar cadastrar uma partida com data no passado")
    void deveLancarExcecaoQuandoDataDaPartidaNoPassado() {
        // 1. GIVEN (Preparação do cenário com data inválida)
        Long eventoId = 1L;

        PartidaRequestDTO requestComDataNoPassado = new PartidaRequestDTO(
                "Vasco",
                "Flamengo",
                LocalDateTime.now().minusDays(1), // Data no passado (Ontem)
                "Maracanã",
                eventoId
        );

        // Simulamos que o evento existe (para a validação não estourar no evento não encontrado)
        Evento eventoExistente = new Evento();
        eventoExistente.setId(eventoId);
        when(eventoRepository.findById(eventoId)).thenReturn(Optional.of(eventoExistente));

        // 2. WHEN & THEN (Ação e Validação)
        IllegalArgumentException excecao = assertThrows(IllegalArgumentException.class, () -> {
            partidaService.cadastrarPartida(requestComDataNoPassado);
        });

        // Verificamos a mensagem de erro exata
        assertEquals("A data da partida não pode ser no passado.", excecao.getMessage());

        // Garante que o banco de dados nunca foi acionado para salvar essa aberração
        verify(partidaRepository, never()).save(any(Partida.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar cadastrar uma partida com equipes iguais")
    void deveLancarExcecaoQuandoEquipesForemIguais() {
        // 1. GIVEN (Preparação com times iguais)
        Long eventoId = 1L;

        PartidaRequestDTO requestComEquipesIguais = new PartidaRequestDTO(
                "Vasco",
                "Vasco", // ❌ Mesmo time nas duas posições!
                LocalDateTime.now().plusDays(2), // Data válida
                "São Januário",
                eventoId
        );

        // Simulamos que o evento existe para não estourar erro de evento não encontrado
        Evento eventoExistente = new Evento();
        eventoExistente.setId(eventoId);
        when(eventoRepository.findById(eventoId)).thenReturn(Optional.of(eventoExistente));

        // 2. WHEN & THEN (Ação e Validação)
        IllegalArgumentException excecao = assertThrows(IllegalArgumentException.class, () -> {
            partidaService.cadastrarPartida(requestComEquipesIguais);
        });

        // Verificamos a mensagem de erro exata que vamos programar no Service
        assertEquals("Uma equipe não pode jogar contra ela mesma.", excecao.getMessage());

        // Garante que a partida bizarra nunca chegue ao banco de dados
        verify(partidaRepository, never()).save(any(Partida.class));
    }

    @Test
    @DisplayName("Deve atualizar o placar de uma partida com sucesso")
    void deveAtualizarPlacarComSucesso() {
        // 1. GIVEN (Preparação)
        Long partidaId = 1L;

        PlacarUpdateDTO placarDTO = new PlacarUpdateDTO(2, 1);

        // Simulamos o evento e a partida já existentes no banco (antes do jogo, placar nulo)
        Evento evento = new Evento();
        evento.setId(10L);

        Partida partidaExistente = new Partida();
        partidaExistente.setId(partidaId);
        partidaExistente.setEquipeA("Vasco");
        partidaExistente.setEquipeB("Flamengo");
        partidaExistente.setEvento(evento);

        // Simulamos a partida que o banco vai retornar após salvar o placar
        Partida partidaAtualizada = new Partida();
        partidaAtualizada.setId(partidaId);
        partidaAtualizada.setEquipeA("Vasco");
        partidaAtualizada.setEquipeB("Flamengo");
        partidaAtualizada.setPlacarEquipeA(2);
        partidaAtualizada.setPlacarEquipeB(1);
        partidaAtualizada.setEvento(evento);

        // Ensinamos o repositório a encontrar a partida e depois salvá-la
        when(partidaRepository.findById(partidaId)).thenReturn(Optional.of(partidaExistente));
        when(partidaRepository.save(any(Partida.class))).thenReturn(partidaAtualizada);

        // 2. WHEN
        PartidaResponseDTO response = partidaService.atualizarPlacar(partidaId, placarDTO);

        // 3. THEN (Validação)
        assertNotNull(response);
        assertEquals(2, response.placarEquipeA());
        assertEquals(1, response.placarEquipeB());

        verify(partidaRepository, times(1)).findById(partidaId);
        verify(partidaRepository, times(1)).save(any(Partida.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar placar de uma partida inexistente")
    void deveLancarExcecaoQuandoAtualizarPlacarDePartidaInexistente() {
        // 1. GIVEN (Preparação do cenário)
        Long partidaIdInexistente = 999L; // Um ID que com certeza não existe

        PlacarUpdateDTO placarDTO = new PlacarUpdateDTO(2, 1);

        // Ensinamos o Mock a retornar "Vazio" (Optional.empty) quando procurarem por esse ID
        when(partidaRepository.findById(partidaIdInexistente)).thenReturn(Optional.empty());

        // 2. WHEN & THEN (Ação e Validação)
        ResourceNotFoundException excecao = assertThrows(ResourceNotFoundException.class, () -> {
            partidaService.atualizarPlacar(partidaIdInexistente, placarDTO);
        });

        // Validamos se a mensagem de erro é exatamente a que esperamos
        assertEquals("Partida não encontrada com o ID: 999", excecao.getMessage());

        // Garantimos que, diante do erro, o repositório nunca tentou salvar nada
        verify(partidaRepository, never()).save(any(Partida.class));
    }
}