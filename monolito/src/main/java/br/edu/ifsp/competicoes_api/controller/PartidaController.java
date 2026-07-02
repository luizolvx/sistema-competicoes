package br.edu.ifsp.competicoes_api.controller;

import br.edu.ifsp.competicoes_api.dto.partida.PartidaRequestDTO;
import br.edu.ifsp.competicoes_api.dto.partida.PartidaResponseDTO;
import br.edu.ifsp.competicoes_api.dto.partida.PlacarUpdateDTO;
import br.edu.ifsp.competicoes_api.dto.partida.StatusUpdateDTO;
import br.edu.ifsp.competicoes_api.service.PartidaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/partidas")
@CrossOrigin(origins = "*")
public class PartidaController {

    private final PartidaService partidaService;

    public PartidaController(PartidaService partidaService) {
        this.partidaService = partidaService;
    }

    @PostMapping
    public ResponseEntity<PartidaResponseDTO> cadastrar(@RequestBody @Valid PartidaRequestDTO request) {
        PartidaResponseDTO response = partidaService.cadastrarPartida(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}/placar")
    public ResponseEntity<PartidaResponseDTO> atualizarPlacar(@PathVariable Long id, @RequestBody @Valid PlacarUpdateDTO placarDTO) {
        PartidaResponseDTO response = partidaService.atualizarPlacar(id, placarDTO);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<PartidaResponseDTO> atualizarStatus(@PathVariable Long id, @RequestBody @Valid StatusUpdateDTO statusDTO) {
        PartidaResponseDTO response = partidaService.atualizarStatus(id, statusDTO.status());
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<PartidaResponseDTO>> listarTodas() {
        List<PartidaResponseDTO> partidas = partidaService.listarTodas();
        return ResponseEntity.ok(partidas);
    }

    @PostMapping("/{eventoId}/chaveamento")
    public ResponseEntity<List<PartidaResponseDTO>> gerarChaveamento(@PathVariable Long eventoId, @RequestBody List<String> equipes) {
        List<PartidaResponseDTO> chaveamento = partidaService.gerarChaveamentoInicial(eventoId, equipes);
        return ResponseEntity.status(HttpStatus.CREATED).body(chaveamento);
    }

    @GetMapping("/evento/{eventoId}")
    public ResponseEntity<List<PartidaResponseDTO>> listarPorEvento(@PathVariable Long eventoId) {
        List<PartidaResponseDTO> partidas = partidaService.listarPorEvento(eventoId);
        return ResponseEntity.ok(partidas);
    }
}