package br.edu.ifsp.competicoes_api.controller;

import br.edu.ifsp.competicoes_api.model.Partida;
import br.edu.ifsp.competicoes_api.repository.PartidaRepository;
import br.edu.ifsp.competicoes_api.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/partidas")
public class PartidaController {

    @Autowired
    private PartidaRepository partidaRepository;

    /**
     * REQUISITO DO PROFESSOR: Acompanhamento de Resultados em Tempo Real
     * Método PATCH: Permite que administradores modifiquem pontuações em tempo real.
     * Ajustado com os métodos exatos: setPlacarEquipeA e setPlacarEquipeB
     */
    @PatchMapping("/{id}/placar")
    public ResponseEntity<Partida> atualizarPlacar(@PathVariable Long id, @RequestBody Map<String, Object> atualizacao) {
        // 1. Busca a partida pelo ID.
        Partida partida = partidaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Partida não encontrada com o ID: " + id));

        // 2. Atualiza dinamicamente usando os métodos corretos do modelo Partida.java
        if (atualizacao.containsKey("placarEquipeA")) {
            partida.setPlacarEquipeA(((Number) atualizacao.get("placarEquipeA")).intValue());
        }
        if (atualizacao.containsKey("placarEquipeB")) {
            partida.setPlacarEquipeB(((Number) atualizacao.get("placarEquipeB")).intValue());
        }

        // 3. Salva a partida atualizada no banco
        Partida partidaAtualizada = partidaRepository.save(partida);
        
        return ResponseEntity.ok(partidaAtualizada);
    }

    /**
     * Auxiliar para os espectadores listarem os jogos e verem os placares atualizados
     */
    @GetMapping
    public ResponseEntity<List<Partida>> listarTodas() {
        List<Partida> partidas = partidaRepository.findAll();
        return ResponseEntity.ok(partidas);
    }
}