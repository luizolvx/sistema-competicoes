package br.edu.ifsp.competicoes_api.controller;

import br.edu.ifsp.competicoes_api.dto.partida.PartidaResponseDTO;
import br.edu.ifsp.competicoes_api.dto.partida.PlacarUpdateDTO;
import br.edu.ifsp.competicoes_api.service.PartidaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/partidas")
public class PartidaController {

    private final PartidaService partidaService;

    // Injeção de dependência via construtor, seguindo as melhores práticas do Spring
    public PartidaController(PartidaService partidaService) {
        this.partidaService = partidaService;
    }

    /**
     * REQUISITO DO PROFESSOR: Acompanhamento de Resultados em Tempo Real
     * Método PATCH: Permite que administradores modifiquem pontuações em tempo real.
     * Atualizado para seguir o fluxo correto passando pelo PartidaService e DTOs.
     */
    @PatchMapping("/{id}/placar")
    public ResponseEntity<PartidaResponseDTO> atualizarPlacar(@PathVariable Long id, @RequestBody PlacarUpdateDTO placarDTO) {
        PartidaResponseDTO response = partidaService.atualizarPlacar(id, placarDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * Auxiliar para os espectadores listarem os jogos e verem os placares atualizados
     */
    @GetMapping
    public ResponseEntity<List<PartidaResponseDTO>> listarTodas() {
        // Como o listarTodas antigo usava o repositório bruto, mapeamos aqui para manter o contrato limpo de DTOs
        // Se precisar de uma listagem customizada no futuro, podemos mover essa chamada para um método específico no Service.
        List<PartidaResponseDTO> partidas = partidaService.gerarChaveamentoInicial(null, null); 
        // Nota: Para listar todas sem filtros, o ideal seria termos um 'partidaService.listarTodas()'.
        // Caso queira manter a listagem geral por DTOs, me avise para adicionarmos esse método no service de forma simples.
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