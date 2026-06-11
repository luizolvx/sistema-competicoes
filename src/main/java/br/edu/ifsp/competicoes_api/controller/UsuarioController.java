package br.edu.ifsp.competicoes_api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @GetMapping("/teste")
    public String testarStatus() {
        return "Microsserviço de Usuários do IFSP está online e respondendo! 🚀";
    }
}