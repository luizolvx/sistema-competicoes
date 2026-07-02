package br.edu.ifsp.competicoes_api.controller;

import br.edu.ifsp.competicoes_api.dto.evento.EventoRequestDTO;
import br.edu.ifsp.competicoes_api.dto.evento.EventoResponseDTO;
import br.edu.ifsp.competicoes_api.service.EventoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/eventos")
public class EventoController {

    private final EventoService eventoService;

    public EventoController(EventoService eventoService) {
        this.eventoService = eventoService;
    }

    @PostMapping
    public ResponseEntity<EventoResponseDTO> criarEvento(@RequestBody @Valid EventoRequestDTO requestDTO) {
        EventoResponseDTO response = eventoService.cadastrarEvento(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<EventoResponseDTO>> listarTodos() {
        List<EventoResponseDTO> eventos = eventoService.listarTodos();
        return ResponseEntity.ok(eventos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventoResponseDTO> buscarPorId(@PathVariable Long id) {
        EventoResponseDTO response = eventoService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventoResponseDTO> atualizar(@PathVariable Long id, @RequestBody @Valid EventoRequestDTO requestDTO) {
        EventoResponseDTO response = eventoService.atualizarEvento(id, requestDTO);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        eventoService.excluirEvento(id);
        return ResponseEntity.noContent().build();
    }
}