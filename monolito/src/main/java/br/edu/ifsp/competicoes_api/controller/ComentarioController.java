package br.edu.ifsp.competicoes_api.controller;

import br.edu.ifsp.competicoes_api.dto.comentario.ComentarioRequestDTO;
import br.edu.ifsp.competicoes_api.dto.comentario.ComentarioResponseDTO;
import br.edu.ifsp.competicoes_api.repository.ComentarioRepository;
import br.edu.ifsp.competicoes_api.service.ComentarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/comentarios")
public class ComentarioController {

    @Autowired
    private ComentarioService comentarioService;

    @Autowired
    private ComentarioRepository comentarioRepository;

    @PostMapping
    public ResponseEntity<ComentarioResponseDTO> cadastrar(@RequestBody @Valid ComentarioRequestDTO requestDTO) {
        ComentarioResponseDTO response = comentarioService.publicarComentario(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        if (!comentarioRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        comentarioRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/midias")
    public ResponseEntity<ComentarioResponseDTO> adicionarMidias(@PathVariable Long id, @RequestBody Map<String, List<String>> body) {
        List<String> linksMidia = body.get("midias");
        ComentarioResponseDTO response = comentarioService.adicionarMidias(id, linksMidia);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/evento/{eventoId}")
    public ResponseEntity<List<ComentarioResponseDTO>> listarPorEvento(@PathVariable Long eventoId) {
        List<ComentarioResponseDTO> comentarios = comentarioService.listarPorEvento(eventoId);
        return ResponseEntity.ok(comentarios);
    }
}