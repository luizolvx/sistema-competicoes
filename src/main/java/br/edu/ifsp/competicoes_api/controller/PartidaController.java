package br.edu.ifsp.competicoes_api.controller;

import br.edu.ifsp.competicoes_api.dto.partida.PartidaRequestDTO;
import br.edu.ifsp.competicoes_api.dto.partida.PartidaResponseDTO;
import br.edu.ifsp.competicoes_api.dto.partida.PlacarUpdateDTO;
import br.edu.ifsp.competicoes_api.service.PartidaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/partidas")
@CrossOrigin(origins = "*") // Permite integração futura com o front-end sem problemas de CORS
public class PartidaController {

    private final PartidaService partidaService;

    // Injeção de dependência via construtor, seguindo as melhores práticas do Spring
    public PartidaController(PartidaService partidaService) {
        this.partidaService = partidaService;
    }

    /**
     * CADASTRO MANUAL: Permite cadastrar uma partida avulsa vinculada a um evento.
     * Utiliza @Valid para garantir que as validações do DTO sejam respeitadas.
     */
    @PostMapping
    public ResponseEntity<PartidaResponseDTO> cadastrar(@RequestBody @Valid PartidaRequestDTO request) {
        PartidaResponseDTO response = partidaService.cadastrarPartida(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * REQUISITO DO PROFESSOR: Acompanhamento de Resultados em Tempo Real
     * Método PATCH: Permite que administradores modifiquem pontuações em tempo real.
     * Atualizado para seguir o fluxo correto passando pelo PartidaService e DTOs.
     */
    @PatchMapping("/{id}/placar")
    public ResponseEntity<PartidaResponseDTO> atualizarPlacar(@PathVariable Long id, @RequestBody @Valid PlacarUpdateDTO placarDTO) {
        PartidaResponseDTO response = partidaService.atualizarPlacar(id, placarDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * CORREÇÃO DEFINITIVA: Auxiliar para os espectadores listarem os jogos e verem os placares atualizados.
     * Agora chama o método correto do service (listarTodas()) retornando a lista do banco em DTOs.
     */
    @GetMapping
    public ResponseEntity<List<PartidaResponseDTO>> listarTodas() {
        List<PartidaResponseDTO> partidas = partidaService.listarTodas(); 
        return ResponseEntity.ok(partidas);
    }

    /**
     * NOVO ENDPOINT: Geração Automática da Chave Inicial do Torneio
     * POST /partidas/{eventoId}/chaveamento
     * Recebe o ID do evento pela URL e a lista de nomes das equipes no corpo da requisição.
     */
    @PostMapping("/{eventoId}/chaveamento")
    public ResponseEntity<List<PartidaResponseDTO>> gerarChaveamento(@PathVariable Long eventoId, @RequestBody List<String> equipes) {
        List<PartidaResponseDTO> chaveamento = partidaService.gerarChaveamentoInicial(eventoId, equipes);
        return ResponseEntity.status(HttpStatus.CREATED).body(chaveamento);
    }
}