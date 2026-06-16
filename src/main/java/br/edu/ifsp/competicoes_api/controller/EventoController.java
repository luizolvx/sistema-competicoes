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

    // Injeção via construtor (Boa prática recomendada pelo Spring)
    public EventoController(EventoService eventoService) {
        this.eventoService = eventoService;
    }

    /**
     * REQUISITO: Cadastro e Controle de Eventos Esportivos
     */
    @PostMapping
    public ResponseEntity<EventoResponseDTO> criarEvento(@RequestBody @Valid EventoRequestDTO requestDTO) {
        EventoResponseDTO novoEvento = eventoService.cadastrarEvento(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoEvento);
    }

    /**
     * REQUISITO: Visualização de Eventos Disponíveis
     */
    @GetMapping
    public ResponseEntity<List<EventoResponseDTO>> listarTodos() {
        List<EventoResponseDTO> eventos = eventoService.listarTodos();
        return ResponseEntity.ok(eventos);
    }
}