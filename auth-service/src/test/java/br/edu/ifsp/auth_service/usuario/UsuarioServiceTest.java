package br.edu.ifsp.auth_service.usuario;

import br.edu.ifsp.competicoes_api.dto.usuario.UsuarioRequestDTO;
import br.edu.ifsp.competicoes_api.dto.usuario.UsuarioResponseDTO;
import br.edu.ifsp.competicoes_api.exception.ResourceNotFoundException;
import br.edu.ifsp.competicoes_api.mapper.UsuarioMapper;
import br.edu.ifsp.competicoes_api.model.Usuario;
import br.edu.ifsp.competicoes_api.repository.UsuarioRepository;
import br.edu.ifsp.competicoes_api.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    private final UsuarioMapper usuarioMapper = UsuarioMapper.INSTANCE;
    
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private UsuarioService usuarioService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        this.usuarioService = new UsuarioService(usuarioRepository, usuarioMapper, passwordEncoder);
    }

    @Test
    @DisplayName("Deve cadastrar um usuário com sucesso")
    void deveCadastrarUsuarioComSucesso() {
        UsuarioRequestDTO request = new UsuarioRequestDTO("Gustavo", "gustavo@email.com", "senha123");

        Usuario usuarioSalvo = new Usuario();
        usuarioSalvo.setId(1L);
        usuarioSalvo.setNome(request.nome());
        usuarioSalvo.setEmail(request.email());
        usuarioSalvo.setSenha(request.senha());

        when(usuarioRepository.findByEmail(request.email())).thenReturn(Optional.empty());
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioSalvo);

        UsuarioResponseDTO response = usuarioService.cadastrarUsuario(request);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("Gustavo", response.nome());
        assertEquals("gustavo@email.com", response.email());

        verify(usuarioRepository, times(1)).findByEmail(request.email());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));

        // SEGURANÇA: garante que a senha NÃO foi salva em texto puro
        verify(usuarioRepository).save(argThat(usuario ->
                !usuario.getSenha().equals(request.senha()) &&
                passwordEncoder.matches(request.senha(), usuario.getSenha())
        ));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar cadastrar um e-mail que já existe")
    void deveLancarExcecaoQuandoEmailJaExiste() {
        UsuarioRequestDTO request = new UsuarioRequestDTO("Luiz", "contato@email.com", "senha123");

        Usuario usuarioJaExistenteNoBanco = new Usuario();
        usuarioJaExistenteNoBanco.setId(2L);
        usuarioJaExistenteNoBanco.setEmail("contato@email.com");

        when(usuarioRepository.findByEmail(request.email())).thenReturn(Optional.of(usuarioJaExistenteNoBanco));

        IllegalArgumentException excecao = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.cadastrarUsuario(request);
        });

        assertEquals("E-mail já cadastrado no sistema.", excecao.getMessage());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    // --- TESTES DE BUSCA (READ) ---

    @Test
    @DisplayName("Deve buscar um usuário por ID com sucesso")
    void deveBuscarUsuarioPorIdComSucesso() {
        Long usuarioId = 1L;
        Usuario usuarioMock = new Usuario();
        usuarioMock.setId(usuarioId);
        usuarioMock.setNome("Gustavo");
        usuarioMock.setEmail("gustavo@email.com");

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuarioMock));

        UsuarioResponseDTO response = usuarioService.buscarPorId(usuarioId);

        assertNotNull(response);
        assertEquals(usuarioId, response.id());
        assertEquals("Gustavo", response.nome());

        verify(usuarioRepository, times(1)).findById(usuarioId);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar usuário com ID inexistente")
    void deveLancarExcecaoAoBuscarIdInexistente() {
        Long idInexistente = 99L;
        when(usuarioRepository.findById(idInexistente)).thenReturn(Optional.empty());

        ResourceNotFoundException excecao = assertThrows(ResourceNotFoundException.class, () -> {
            usuarioService.buscarPorId(idInexistente);
        });

        assertEquals("Usuário não encontrado com o ID: 99", excecao.getMessage());
    }

    // --- TESTES DE EXCLUSÃO (DELETE) ---

    @Test
    @DisplayName("Deve excluir um usuário com sucesso")
    void deveExcluirUsuarioComSucesso() {
        Long usuarioId = 1L;
        Usuario usuarioMock = new Usuario();
        usuarioMock.setId(usuarioId);

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuarioMock));
        doNothing().when(usuarioRepository).delete(usuarioMock);

        usuarioService.excluirUsuario(usuarioId);

        verify(usuarioRepository, times(1)).findById(usuarioId);
        verify(usuarioRepository, times(1)).delete(usuarioMock);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar excluir usuário inexistente")
    void deveLancarExcecaoAoExcluirUsuarioInexistente() {
        Long idInexistente = 99L;
        when(usuarioRepository.findById(idInexistente)).thenReturn(Optional.empty());

        ResourceNotFoundException excecao = assertThrows(ResourceNotFoundException.class, () -> {
            usuarioService.excluirUsuario(idInexistente);
        });

        assertEquals("Usuário não encontrado com o ID: 99", excecao.getMessage());
        verify(usuarioRepository, never()).delete(any(Usuario.class));
    }

    // --- TESTES DE LISTAGEM (READ ALL) ---

    @Test
    @DisplayName("Deve listar todos os usuários cadastrados")
    void deveListarTodosOsUsuarios() {
        Usuario user1 = new Usuario(); user1.setId(1L); user1.setNome("Gustavo");
        Usuario user2 = new Usuario(); user2.setId(2L); user2.setNome("Luiz");

        when(usuarioRepository.findAll()).thenReturn(java.util.List.of(user1, user2));

        java.util.List<UsuarioResponseDTO> response = usuarioService.listarTodos();

        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals("Gustavo", response.get(0).nome());
        assertEquals("Luiz", response.get(1).nome());

        verify(usuarioRepository, times(1)).findAll();
    }

    // --- TESTES DE ATUALIZAÇÃO (UPDATE) ---

    @Test
    @DisplayName("Deve atualizar um usuário com sucesso")
    void deveAtualizarUsuarioComSucesso() {
        Long usuarioId = 1L;
        Usuario usuarioExistente = new Usuario();
        usuarioExistente.setId(usuarioId);
        usuarioExistente.setNome("Gustavo Antigo");
        usuarioExistente.setEmail("gustavo@email.com");

        UsuarioRequestDTO requestAtualizacao = new UsuarioRequestDTO("Gustavo Novo", "gustavo.novo@email.com", "novaSenha");

        Usuario usuarioAtualizado = new Usuario();
        usuarioAtualizado.setId(usuarioId);
        usuarioAtualizado.setNome(requestAtualizacao.nome());
        usuarioAtualizado.setEmail(requestAtualizacao.email());

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuarioExistente));
        when(usuarioRepository.findByEmail(requestAtualizacao.email())).thenReturn(Optional.empty());
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioAtualizado);

        UsuarioResponseDTO response = usuarioService.atualizarUsuario(usuarioId, requestAtualizacao);

        assertNotNull(response);
        assertEquals("Gustavo Novo", response.nome());
        assertEquals("gustavo.novo@email.com", response.email());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar para um e-mail já usado por outro usuário")
    void deveLancarExcecaoAoAtualizarComEmailDeOutroUsuario() {
        // GIVEN
        Long meuId = 1L;
        Usuario meuUsuario = new Usuario();
        meuUsuario.setId(meuId);
        meuUsuario.setEmail("meu.email@email.com");

        UsuarioRequestDTO requestAtualizacao = new UsuarioRequestDTO("Gustavo", "luiz@email.com", "senha123");

        Usuario outroUsuario = new Usuario();
        outroUsuario.setId(2L);
        outroUsuario.setEmail("luiz@email.com");

        when(usuarioRepository.findById(meuId)).thenReturn(Optional.of(meuUsuario));
        when(usuarioRepository.findByEmail(requestAtualizacao.email())).thenReturn(Optional.of(outroUsuario));

        // WHEN & THEN
        IllegalArgumentException excecao = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.atualizarUsuario(meuId, requestAtualizacao);
        });

        assertEquals("E-mail já cadastrado no sistema por outro usuário.", excecao.getMessage());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }


    // --- TESTES DE AUTENTICAÇÃO (LOGIN COM BCRYPT) ---

    @Test
    @DisplayName("Deve autenticar usuário com sucesso quando a senha está correta")
    void deveAutenticarComSucesso() {
        String senhaPura = "senha123";
        String senhaCriptografada = passwordEncoder.encode(senhaPura);

        Usuario usuarioMock = new Usuario();
        usuarioMock.setId(1L);
        usuarioMock.setNome("Gustavo");
        usuarioMock.setEmail("gustavo@email.com");
        usuarioMock.setSenha(senhaCriptografada);

        var loginRequest = new br.edu.ifsp.competicoes_api.dto.usuario.LoginRequestDTO(
                "gustavo@email.com", senhaPura);

        when(usuarioRepository.findByEmail("gustavo@email.com"))
                .thenReturn(Optional.of(usuarioMock));

        UsuarioResponseDTO response = usuarioService.autenticar(loginRequest);

        assertNotNull(response);
        assertEquals("gustavo@email.com", response.email());
    }

    @Test
    @DisplayName("Deve lançar exceção ao autenticar com senha incorreta")
    void deveLancarExcecaoQuandoSenhaIncorreta() {
        String senhaCriptografada = passwordEncoder.encode("senhaCorreta");

        Usuario usuarioMock = new Usuario();
        usuarioMock.setEmail("gustavo@email.com");
        usuarioMock.setSenha(senhaCriptografada);

        var loginRequest = new br.edu.ifsp.competicoes_api.dto.usuario.LoginRequestDTO(
                "gustavo@email.com", "senhaErrada");

        when(usuarioRepository.findByEmail("gustavo@email.com"))
                .thenReturn(Optional.of(usuarioMock));

        IllegalArgumentException excecao = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.autenticar(loginRequest);
        });

        assertEquals("Senha incorreta.", excecao.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao autenticar com e-mail inexistente")
    void deveLancarExcecaoQuandoEmailNaoExisteNoLogin() {
        var loginRequest = new br.edu.ifsp.competicoes_api.dto.usuario.LoginRequestDTO(
                "naoexiste@email.com", "qualquerSenha");

        when(usuarioRepository.findByEmail("naoexiste@email.com"))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            usuarioService.autenticar(loginRequest);
        });
    }

}