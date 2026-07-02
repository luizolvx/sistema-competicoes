package br.edu.ifsp.auth_service.controller;

import br.edu.ifsp.auth_service.dto.usuario.LoginRequestDTO;
import br.edu.ifsp.auth_service.dto.usuario.UsuarioRequestDTO;
import br.edu.ifsp.auth_service.dto.usuario.UsuarioResponseDTO;
import br.edu.ifsp.auth_service.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import br.edu.ifsp.auth_service.config.JwtUtil;
import br.edu.ifsp.auth_service.dto.usuario.LoginResponseDTO;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final JwtUtil jwtUtil;

    public UsuarioController(UsuarioService usuarioService, JwtUtil jwtUtil) {
        this.usuarioService = usuarioService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/teste")
    public String testarStatus() {
        return "Microsserviço de Usuários do IFSP está online e respondendo! 🚀";
    }

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> cadastrar(@RequestBody @Valid UsuarioRequestDTO requestDTO) {
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
    public ResponseEntity<UsuarioResponseDTO> atualizar(@PathVariable Long id, @RequestBody @Valid UsuarioRequestDTO requestDTO) {
        UsuarioResponseDTO response = usuarioService.atualizarUsuario(id, requestDTO);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        usuarioService.excluirUsuario(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO loginRequest) {
        try {
            UsuarioResponseDTO usuario = usuarioService.autenticar(loginRequest);
            String token = jwtUtil.gerarToken(usuario.email(), usuario.role().name());

            LoginResponseDTO response = new LoginResponseDTO(
                    usuario.id(),
                    usuario.nome(),
                    usuario.email(),
                    usuario.role(),
                    token
            );

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PutMapping("/{id}/interesses")
    public ResponseEntity<Void> atualizarInteresses(@PathVariable Long id, @RequestBody Map<String, List<String>> body) {
        List<String> esportes = body.get("interesses");
        usuarioService.atualizarInteresses(id, esportes);
        return ResponseEntity.ok().build();
    }
}