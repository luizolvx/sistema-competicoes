package br.edu.ifsp.auth_service.usuario;

import br.edu.ifsp.competicoes_api.config.JwtUtil;
import br.edu.ifsp.competicoes_api.controller.UsuarioController;
import br.edu.ifsp.competicoes_api.dto.usuario.UsuarioRequestDTO;
import br.edu.ifsp.competicoes_api.dto.usuario.UsuarioResponseDTO;
import br.edu.ifsp.competicoes_api.exception.ResourceNotFoundException;
import br.edu.ifsp.competicoes_api.model.Role;
import br.edu.ifsp.competicoes_api.service.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UsuarioController.class)
@WithMockUser
class UsuarioControllerUnitTest {

    // --- BLINDAGEM SUPREMA: Cria um contexto isolado só para este teste ---
    @SpringBootConfiguration
    @EnableAutoConfiguration(exclude = {
            DataSourceAutoConfiguration.class,
            HibernateJpaAutoConfiguration.class
    })
    @Import({UsuarioController.class, TestConfig.TratadorDeErrosFalso.class})
    static class TestConfig {

        // Ensina o teste a devolver 404 quando a exceção estourar
        @org.springframework.web.bind.annotation.ControllerAdvice
        static class TratadorDeErrosFalso {
            @org.springframework.web.bind.annotation.ExceptionHandler(ResourceNotFoundException.class)
            public org.springframework.http.ResponseEntity<Void> handleNotFound() {
                return org.springframework.http.ResponseEntity.notFound().build();
            }
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioService usuarioService;

    // NOVO: necessário pois o construtor de UsuarioController agora também exige um JwtUtil
    @MockitoBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Deve retornar 201 Created ao cadastrar usuário com sucesso")
    void deveRetornar201AoCadastrarUsuario() throws Exception {
        // GIVEN
        UsuarioRequestDTO request = new UsuarioRequestDTO("Gustavo", "gustavo@email.com", "senha123");
        UsuarioResponseDTO response = new UsuarioResponseDTO(1L, "Gustavo", "gustavo@email.com", Role.ROLE_USER);

        when(usuarioService.cadastrarUsuario(any(UsuarioRequestDTO.class))).thenReturn(response);

        // WHEN & THEN
        mockMvc.perform(post("/usuarios")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("Gustavo"))
                .andExpect(jsonPath("$.email").value("gustavo@email.com"));

        verify(usuarioService, times(1)).cadastrarUsuario(any(UsuarioRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar 200 OK ao buscar usuário por ID existente")
    void deveRetornar200AoBuscarPorIdExistente() throws Exception {
        // GIVEN
        Long idExistente = 1L;
        UsuarioResponseDTO response = new UsuarioResponseDTO(idExistente, "Gustavo", "gustavo@email.com", Role.ROLE_USER);

        when(usuarioService.buscarPorId(idExistente)).thenReturn(response);

        // WHEN & THEN
        mockMvc.perform(get("/usuarios/{id}", idExistente))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(idExistente))
                .andExpect(jsonPath("$.nome").value("Gustavo"));
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found ao buscar ID inexistente")
    void deveRetornar404AoBuscarIdInexistente() throws Exception {
        // GIVEN
        Long idInexistente = 99L;
        when(usuarioService.buscarPorId(idInexistente))
                .thenThrow(new ResourceNotFoundException("Usuário não encontrado com o ID: " + idInexistente));

        // WHEN & THEN
        mockMvc.perform(get("/usuarios/{id}", idInexistente))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve retornar 200 OK e atualizar dados do usuário")
    void deveRetornar200AoAtualizarUsuario() throws Exception {
        // GIVEN
        Long idUsuario = 1L;
        UsuarioRequestDTO request = new UsuarioRequestDTO("Gustavo Atualizado", "gustavo@email.com", "novaSenha");
        UsuarioResponseDTO response = new UsuarioResponseDTO(idUsuario, "Gustavo Atualizado", "gustavo@email.com", Role.ROLE_USER);

        when(usuarioService.atualizarUsuario(eq(idUsuario), any(UsuarioRequestDTO.class))).thenReturn(response);

        // WHEN & THEN
        mockMvc.perform(put("/usuarios/{id}", idUsuario)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Gustavo Atualizado"));
    }

    @Test
    @DisplayName("Deve retornar 204 No Content ao excluir usuário com sucesso")
    void deveRetornar204AoExcluirUsuario() throws Exception {
        // GIVEN
        Long idUsuario = 1L;
        doNothing().when(usuarioService).excluirUsuario(idUsuario);

        // WHEN & THEN
        mockMvc.perform(delete("/usuarios/{id}", idUsuario)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isNoContent());

        verify(usuarioService, times(1)).excluirUsuario(idUsuario);
    }
}