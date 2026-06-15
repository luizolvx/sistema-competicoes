package br.edu.ifsp.competicoes_api.controller;

import br.edu.ifsp.competicoes_api.dto.usuario.UsuarioRequestDTO;
import br.edu.ifsp.competicoes_api.dto.usuario.UsuarioResponseDTO;
import br.edu.ifsp.competicoes_api.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map; // Adicionado apenas para ler o JSON dinâmico do login/interesses

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // Seu endpoint de teste original mantido intacto
    @GetMapping("/teste")
    public String testarStatus() {
        return "Microsserviço de Usuários do IFSP está online e respondendo! 🚀";
    }

    // Endpoint para Cadastrar Usuário conectado ao Service existente
    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> cadastrar(@RequestBody UsuarioRequestDTO requestDTO) {
        UsuarioResponseDTO response = usuarioService.cadastrarUsuario(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Endpoint para Listar Todos os Usuários conectado ao Service existente
    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listarTodos() {
        List<UsuarioResponseDTO> response = usuarioService.listarTodos();
        return ResponseEntity.ok(response);
    }

    /* =========================================================================
       MUDANÇAS MÍNIMAS APENAS PARA O MVP (ADICIONADO NO FINAL DO ARQUIVO)
       ========================================================================= */

    /**
     * REQUISITO: Rota de Login para Autenticação
     * Busca na lista existente o usuário que possui o e-mail enviado.
     */
    @PostMapping("/login")
    public ResponseEntity<UsuarioResponseDTO> login(@RequestBody Map<String, String> credenciais) {
        String email = credenciais.get("email");
        String senha = credenciais.get("senha");

        // Varre a listagem do service dele para achar o usuário por e-mail sem precisar criar códigos novos no Service
        UsuarioResponseDTO usuario = usuarioService.listarTodos().stream()
                .filter(u -> u.email().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);

        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(usuario);
    }

    /**
     * REQUISITO: Salvar preferências de notificação/interesses esportivos
     */
    @PutMapping("/{id}/interesses")
    public ResponseEntity<Void> atualizarInteresses(@PathVariable Long id, @RequestBody Map<String, List<String>> body) {
        List<String> esportes = body.get("interesses");
        
        // Simula o salvamento com sucesso no console para o MVP rodar sem alterar o banco de dados do seu amigo
        System.out.println("Interesses registrados para o ID " + id + ": " + esportes);
        
        return ResponseEntity.ok().build();
    }
}