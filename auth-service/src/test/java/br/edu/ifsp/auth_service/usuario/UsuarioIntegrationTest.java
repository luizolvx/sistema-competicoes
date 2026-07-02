package br.edu.ifsp.auth_service.usuario;

import br.edu.ifsp.auth_service.AuthServiceApplication;
import br.edu.ifsp.auth_service.dto.usuario.UsuarioRequestDTO;
import br.edu.ifsp.auth_service.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// AJUSTE CRÍTICO: Especifica a classe principal real do Auth Service
@SpringBootTest(classes = AuthServiceApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UsuarioIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Injetamos o repositório REAL apenas para limpar/verificar a base de dados nos testes
    @Autowired
    private UsuarioRepository usuarioRepository;

    // Antes de cada teste, limpamos a base de dados em memória para garantir isolamento
    @BeforeEach
    void setUp() {
        usuarioRepository.deleteAll();
    }

    @Test
    @DisplayName("Integração: Deve cadastrar um usuário e salvar no banco de dados H2")
    void deveCadastrarUsuarioESalvarNoBanco() throws Exception {
        // GIVEN: Um pedido de registo válido
        UsuarioRequestDTO request = new UsuarioRequestDTO("Gustavo Integração", "gustavo.int@email.com", "senhaForte123");

        // Verificamos que a base de dados começa vazia
        assertEquals(0, usuarioRepository.count());

        // WHEN: Fazemos o pedido POST para a API real
        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                // THEN: A API deve devolver 201 Created
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Gustavo Integração"))
                .andExpect(jsonPath("$.email").value("gustavo.int@email.com"));

        // AND THEN: Verificamos se o JPA realmente inseriu o registo na tabela do H2
        assertEquals(1, usuarioRepository.count());

        // Podemos até ir buscar o registo à base de dados para confirmar se gravou bem
        var usuarioSalvo = usuarioRepository.findAll().get(0);
        assertEquals("Gustavo Integração", usuarioSalvo.getNome());
        assertEquals("gustavo.int@email.com", usuarioSalvo.getEmail());
    }
}