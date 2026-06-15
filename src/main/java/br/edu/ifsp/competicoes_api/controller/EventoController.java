package br.edu.ifsp.competicoes_api.controller;

import br.edu.ifsp.competicoes_api.model.Evento;
import br.edu.ifsp.competicoes_api.repository.EventoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/eventos")
public class EventoController {

    @Autowired
    private EventoRepository eventoRepository;

    /**
     * REQUISITO: Cadastro e Controle de Eventos Esportivos
     * Permite que os organizadores cadastrem novas competições e modalidades no IFSP.
     */
    @PostMapping
    public ResponseEntity<Evento> criarEvento(@RequestBody Evento evento) {
        Evento novoEvento = eventoRepository.save(evento);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoEvento);
    }

    /**
     * REQUISITO: Visualização de Eventos Disponíveis
     * Permite que atletas, treinadores e espectadores listem todas as competições.
     */
    @GetMapping
    public ResponseEntity<List<Evento>> listarTodos() {
        List<Evento> eventos = eventoRepository.findAll();
        return ResponseEntity.ok(eventos);
    }
}