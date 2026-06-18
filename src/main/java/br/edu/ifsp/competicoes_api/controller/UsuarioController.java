package br.edu.ifsp.competicoes_api.controller;

import br.edu.ifsp.competicoes_api.dto.usuario.UsuarioRequestDTO;
import br.edu.ifsp.competicoes_api.dto.usuario.UsuarioResponseDTO;
import br.edu.ifsp.competicoes_api.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/teste")
    public String testarStatus() {
        return "Microsserviço de Usuários do IFSP está online e respondendo! 🚀";
    }

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> cadastrar(@RequestBody UsuarioRequestDTO requestDTO) {
        UsuarioResponseDTO response = usuarioService.cadastrarUsuario(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listarTodos() {
        List<UsuarioResponseDTO> response = usuarioService.listarTodos();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(@PathVariable Long id) {
        UsuarioResponseDTO response = usuarioService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> atualizar(@PathVariable Long id, @RequestBody UsuarioRequestDTO requestDTO) {
        UsuarioResponseDTO response = usuarioService.atualizarUsuario(id, requestDTO);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        usuarioService.excluirUsuario(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public ResponseEntity<UsuarioResponseDTO> login(@RequestBody Map<String, String> credenciais) {
        String email = credenciais.get("email");

        UsuarioResponseDTO usuario = usuarioService.listarTodos().stream()
                .filter(u -> u.email().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);

        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(usuario);
    }

    @PutMapping("/{id}/interesses")
    public ResponseEntity<Void> atualizarInteresses(@PathVariable Long id, @RequestBody Map<String, List<String>> body) {
        List<String> esportes = body.get("interesses");
        System.out.println("Interesses registrados para o ID " + id + ": " + esportes);
        return ResponseEntity.ok().build();
    }
}