package br.edu.ifsp.competicoes_api.controller;

import br.edu.ifsp.competicoes_api.dto.evento.EventoRequestDTO;
import br.edu.ifsp.competicoes_api.dto.evento.EventoResponseDTO;
import br.edu.ifsp.competicoes_api.service.EventoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/eventos")
public class EventoController {

    private final EventoService eventoService;

    // Construtor público para injeção de dependência limpa sem @Autowired no atributo
    public EventoController(EventoService eventoService) {
        this.eventoService = eventoService;
    }

    /**
     * REQUISITO: Cadastro e Controle de Eventos Esportivos
     * Permite que os organizadores cadastrem novas competições e modalidades no IFSP.
     */
    @PostMapping
    public ResponseEntity<EventoResponseDTO> criarEvento(@RequestBody EventoRequestDTO requestDTO) {
        EventoResponseDTO response = eventoService.cadastrarEvento(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * REQUISITO: Visualização de Eventos Disponíveis
     * Permite que atletas, treinadores e espectadores listem todas as competições.
     */
    @GetMapping
    public ResponseEntity<List<EventoResponseDTO>> listarTodos() {
        List<EventoResponseDTO> eventos = eventoService.listarTodos();
        return ResponseEntity.ok(eventos);
    }

    /**
     * Permite buscar os detalhes de uma competição pelo ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<EventoResponseDTO> buscarPorId(@PathVariable Long id) {
        EventoResponseDTO response = eventoService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    /**
     * REQUISITO ADMINISTRATIVO: Editar Evento
     * Endpoint exclusivo para que os administradores editem dados da competição.
     */
    @PutMapping("/{id}")
    public ResponseEntity<EventoResponseDTO> atualizar(@PathVariable Long id, @RequestBody EventoRequestDTO requestDTO) {
        EventoResponseDTO response = eventoService.atualizarEvento(id, requestDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * REQUISITO ADMINISTRATIVO: Excluir Evento
     * Endpoint exclusivo para que os administradores removam competições antigas ou canceladas.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        eventoService.excluirEvento(id);
        return ResponseEntity.noContent().build();
    }
}