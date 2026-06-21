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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
        // 1. GIVEN
        Long eventoId = 1L;
        Evento eventoExistente = new Evento();
        eventoExistente.setId(eventoId);
        eventoExistente.setNome("Torneio de Futebol");

        PartidaRequestDTO request = new PartidaRequestDTO(
                "Vasco",
                "Flamengo",
                LocalDateTime.now().plusDays(2),
                "Maracanã",
                eventoId
        );

        Partida partidaSalva = new Partida();
        partidaSalva.setId(10L);
        partidaSalva.setEquipeA(request.equipeA());
        partidaSalva.setEquipeB(request.equipeB());
        partidaSalva.setDataHora(request.dataHora());
        partidaSalva.setLocal(request.local());
        partidaSalva.setEvento(eventoExistente);

        when(eventoRepository.findById(eventoId)).thenReturn(Optional.of(eventoExistente));
        when(partidaRepository.save(any(Partida.class))).thenReturn(partidaSalva);

        // 2. WHEN
        PartidaResponseDTO response = partidaService.cadastrarPartida(request);

        // 3. THEN
        assertNotNull(response);
        assertEquals(10L, response.id());
        assertEquals("Vasco", response.equipeA());
        assertEquals("Flamengo", response.equipeB());
        assertEquals("Maracanã", response.local());
        assertEquals(eventoId, response.eventoId());
        assertNull(response.placarEquipeA());

        verify(eventoRepository, times(1)).findById(eventoId);
        verify(partidaRepository, times(1)).save(any(Partida.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar cadastrar uma partida com data no passado")
    void deveLancarExcecaoQuandoDataDaPartidaNoPassado() {
        // 1. GIVEN
        Long eventoId = 1L;
        PartidaRequestDTO requestComDataNoPassado = new PartidaRequestDTO(
                "Vasco",
                "Flamengo",
                LocalDateTime.now().minusDays(1),
                "Maracanã",
                eventoId
        );

        Evento eventoExistente = new Evento();
        eventoExistente.setId(eventoId);
        when(eventoRepository.findById(eventoId)).thenReturn(Optional.of(eventoExistente));

        // 2. WHEN & THEN
        IllegalArgumentException excecao = assertThrows(IllegalArgumentException.class, () -> {
            partidaService.cadastrarPartida(requestComDataNoPassado);
        });

        assertEquals("A data da partida não pode ser no passado.", excecao.getMessage());
        verify(partidaRepository, never()).save(any(Partida.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar cadastrar uma partida com equipes iguais")
    void deveLancarExcecaoQuandoEquipesForemIguais() {
        // 1. GIVEN
        Long eventoId = 1L;
        PartidaRequestDTO requestComEquipesIguais = new PartidaRequestDTO(
                "Vasco",
                "Vasco",
                LocalDateTime.now().plusDays(2),
                "São Januário",
                eventoId
        );

        Evento eventoExistente = new Evento();
        eventoExistente.setId(eventoId);
        when(eventoRepository.findById(eventoId)).thenReturn(Optional.of(eventoExistente));

        // 2. WHEN & THEN
        IllegalArgumentException excecao = assertThrows(IllegalArgumentException.class, () -> {
            partidaService.cadastrarPartida(requestComEquipesIguais);
        });

        assertEquals("Uma equipe não pode jogar contra ela mesma.", excecao.getMessage());
        verify(partidaRepository, never()).save(any(Partida.class));
    }

    @Test
    @DisplayName("Deve atualizar o placar de uma partida com sucesso")
    void deveAtualizarPlacarComSucesso() {
        // 1. GIVEN
        Long partidaId = 1L;
        PlacarUpdateDTO placarDTO = new PlacarUpdateDTO(2, 1);

        Evento evento = new Evento();
        evento.setId(10L);

        Partida partidaExistente = new Partida();
        partidaExistente.setId(partidaId);
        partidaExistente.setEquipeA("Vasco");
        partidaExistente.setEquipeB("Flamengo");
        partidaExistente.setEvento(evento);

        Partida partidaAtualizada = new Partida();
        partidaAtualizada.setId(partidaId);
        partidaAtualizada.setEquipeA("Vasco");
        partidaAtualizada.setEquipeB("Flamengo");
        partidaAtualizada.setPlacarEquipeA(2);
        partidaAtualizada.setPlacarEquipeB(1);
        partidaAtualizada.setEvento(evento);

        when(partidaRepository.findById(partidaId)).thenReturn(Optional.of(partidaExistente));
        when(partidaRepository.save(any(Partida.class))).thenReturn(partidaAtualizada);

        // 2. WHEN
        PartidaResponseDTO response = partidaService.atualizarPlacar(partidaId, placarDTO);

        // 3. THEN
        assertNotNull(response);
        assertEquals(2, response.placarEquipeA());
        assertEquals(1, response.placarEquipeB());

        verify(partidaRepository, times(1)).findById(partidaId);
        verify(partidaRepository, times(1)).save(any(Partida.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar placar de uma partida inexistente")
    void deveLancarExcecaoQuandoAtualizarPlacarDePartidaInexistente() {
        // 1. GIVEN
        Long partidaIdInexistente = 999L;
        PlacarUpdateDTO placarDTO = new PlacarUpdateDTO(2, 1);

        when(partidaRepository.findById(partidaIdInexistente)).thenReturn(Optional.empty());

        // 2. WHEN & THEN
        ResourceNotFoundException excecao = assertThrows(ResourceNotFoundException.class, () -> {
            partidaService.atualizarPlacar(partidaIdInexistente, placarDTO);
        });

        assertEquals("Partida não encontrada com o ID: 999", excecao.getMessage());
        verify(partidaRepository, never()).save(any(Partida.class));
    }

    // ==========================================
    // NOVOS TESTES: CHAVEAMENTO AUTOMÁTICO E LISTAGEM
    // ==========================================

    @Test
    @DisplayName("Deve gerar chaveamento com sucesso quando o número de equipes for par")
    void deveGerarChaveamentoComSucessoQuandoNumeroDeEquipesForPar() {
        // 1. GIVEN
        Long eventoId = 1L;
        Evento evento = new Evento();
        evento.setId(eventoId);

        List<String> equipes = Arrays.asList("Intersistemas", "ADS Guarulhos", "Edificações FC", "Logística");

        when(eventoRepository.findById(eventoId)).thenReturn(Optional.of(evento));
        // Configura o mock do repositório para retornar a própria instância que receber no save
        when(partidaRepository.save(any(Partida.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // 2. WHEN
        List<PartidaResponseDTO> resultado = partidaService.gerarChaveamentoInicial(eventoId, equipes);

        // 3. THEN
        assertNotNull(resultado);
        assertEquals(2, resultado.size(), "4 equipes devem gerar exatamente 2 confrontos.");
        verify(eventoRepository, times(1)).findById(eventoId);
        verify(partidaRepository, times(2)).save(any(Partida.class));
    }

    @Test
    @DisplayName("Deve adicionar vaga de avanço automático se o número de equipes for ímpar")
    void deveAdicionarVagaDeAvancoAutomaticoQuandoNumeroDeEquipesForImpar() {
        // 1. GIVEN
        Long eventoId = 1L;
        Evento evento = new Evento();
        evento.setId(eventoId);

        List<String> equipes = Arrays.asList("Intersistemas", "ADS Guarulhos", "Logística");

        when(eventoRepository.findById(eventoId)).thenReturn(Optional.of(evento));
        when(partidaRepository.save(any(Partida.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // 2. WHEN
        List<PartidaResponseDTO> resultado = partidaService.gerarChaveamentoInicial(eventoId, equipes);

        // 3. THEN
        assertNotNull(resultado);
        assertEquals(2, resultado.size(), "3 equipes + 1 vaga de folga devem gerar 2 confrontos.");
        verify(partidaRepository, times(2)).save(any(Partida.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao gerar chaveamento automático com menos de 2 equipes")
    void deveLancarExcecaoQuandoChaveamentoTiverEquipesInsuficientes() {
        // 1. GIVEN
        Long eventoId = 1L;
        Evento evento = new Evento();
        evento.setId(eventoId);
        List<String> equipes = Collections.singletonList("Intersistemas");

        when(eventoRepository.findById(eventoId)).thenReturn(Optional.of(evento));

        // 2. WHEN & THEN
        IllegalArgumentException excecao = assertThrows(IllegalArgumentException.class, () -> {
            partidaService.gerarChaveamentoInicial(eventoId, equipes);
        });

        assertEquals("São necessárias pelo menos 2 equipes para gerar um chaveamento automático.", excecao.getMessage());
        verify(partidaRepository, never()).save(any(Partida.class));
    }

    @Test
    @DisplayName("Deve listar todas as partidas cadastradas com sucesso")
    void deveListarTodasAsPartidasComSucesso() {
        // 1. GIVEN
        Partida p1 = new Partida();
        p1.setId(101L);
        p1.setEquipeA("Time A");
        p1.setEquipeB("Time B");

        Partida p2 = new Partida();
        p2.setId(102L);
        p2.setEquipeA("Time C");
        p2.setEquipeB("Time D");

        when(partidaRepository.findAll()).thenReturn(Arrays.asList(p1, p2));

        // 2. WHEN
        List<PartidaResponseDTO> resultado = partidaService.listarTodas();

        // 3. THEN
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(101L, resultado.get(0).id());
        assertEquals(102L, resultado.get(1).id());
        verify(partidaRepository, times(1)).findAll();
    }
}