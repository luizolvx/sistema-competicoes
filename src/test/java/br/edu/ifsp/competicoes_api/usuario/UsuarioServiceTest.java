package br.edu.ifsp.competicoes_api.usuario;

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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    // Assumindo que você tem o Mapper configurado no mesmo padrão dos anteriores
    private final UsuarioMapper usuarioMapper = UsuarioMapper.INSTANCE;

    private UsuarioService usuarioService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // O Service que ainda vamos criar!
        this.usuarioService = new UsuarioService(usuarioRepository, usuarioMapper);
    }

    @Test
    @DisplayName("Deve cadastrar um usuário com sucesso")
    void deveCadastrarUsuarioComSucesso() {
        // 1. GIVEN (Preparação)
        UsuarioRequestDTO request = new UsuarioRequestDTO("Gustavo", "gustavo@email.com", "senha123");

        Usuario usuarioSalvo = new Usuario();
        usuarioSalvo.setId(1L);
        usuarioSalvo.setNome(request.nome());
        usuarioSalvo.setEmail(request.email());
        usuarioSalvo.setSenha(request.senha());

        // Ensinamos o Mockito que, ao procurar esse e-mail no banco, ele NÃO vai encontrar nada (Optional vazio)
        when(usuarioRepository.findByEmail(request.email())).thenReturn(Optional.empty());

        // E ensinamos que, ao salvar, ele devolve o usuário com ID 1
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioSalvo);

        // 2. WHEN (Ação)
        UsuarioResponseDTO response = usuarioService.cadastrarUsuario(request);

        // 3. THEN (Validações)
        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("Gustavo", response.nome());
        assertEquals("gustavo@email.com", response.email());

        // Confirma que o sistema verificou a duplicidade e depois salvou
        verify(usuarioRepository, times(1)).findByEmail(request.email());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar cadastrar um e-mail que já existe")
    void deveLancarExcecaoQuandoEmailJaExiste() {
        // 1. GIVEN (Preparação de um cenário de colisão de dados)
        UsuarioRequestDTO request = new UsuarioRequestDTO("Luiz", "contato@email.com", "senha123");

        // Simulamos que já existe alguém no banco com esse e-mail
        Usuario usuarioJaExistenteNoBanco = new Usuario();
        usuarioJaExistenteNoBanco.setId(2L);
        usuarioJaExistenteNoBanco.setEmail("contato@email.com");

        when(usuarioRepository.findByEmail(request.email())).thenReturn(Optional.of(usuarioJaExistenteNoBanco));

        // 2. WHEN & THEN (Ação e Validação)
        // Como é uma quebra de regra de negócio, usamos a IllegalArgumentException
        IllegalArgumentException excecao = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.cadastrarUsuario(request);
        });

        assertEquals("E-mail já cadastrado no sistema.", excecao.getMessage());

        // Garante que o banco NUNCA foi chamado para salvar esse usuário duplicado
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
        // O método delete() é void, então o Mockito apenas não fará nada (o que é sucesso)
        doNothing().when(usuarioRepository).delete(usuarioMock);

        usuarioService.excluirUsuario(usuarioId);

        // Verifica se ele buscou e depois acionou o delete corretamente
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
        // Garante que o banco nunca tentou acionar o comando de delete
        verify(usuarioRepository, never()).delete(any(Usuario.class));
    }

    // --- TESTES DE LISTAGEM (READ ALL) ---

    @Test
    @DisplayName("Deve listar todos os usuários cadastrados")
    void deveListarTodosOsUsuarios() {
        // 1. GIVEN
        Usuario user1 = new Usuario(); user1.setId(1L); user1.setNome("Gustavo");
        Usuario user2 = new Usuario(); user2.setId(2L); user2.setNome("Luiz");

        when(usuarioRepository.findAll()).thenReturn(java.util.List.of(user1, user2));

        // 2. WHEN
        java.util.List<UsuarioResponseDTO> response = usuarioService.listarTodos();

        // 3. THEN
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
        // 1. GIVEN
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
        // Simula que o novo e-mail NÃO está sendo usado por ninguém
        when(usuarioRepository.findByEmail(requestAtualizacao.email())).thenReturn(Optional.empty());
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioAtualizado);

        // 2. WHEN
        UsuarioResponseDTO response = usuarioService.atualizarUsuario(usuarioId, requestAtualizacao);

        // 3. THEN
        assertNotNull(response);
        assertEquals("Gustavo Novo", response.nome());
        assertEquals("gustavo.novo@email.com", response.email());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar para um e-mail já usado por outro usuário")
    void deveLancarExcecaoAoAtualizarComEmailDeOutroUsuario() {
        // 1. GIVEN
        Long meuId = 1L;
        Usuario meuUsuario = new Usuario();
        meuUsuario.setId(meuId);
        meuUsuario.setEmail("meu.email@email.com");

        // Tento mudar meu e-mail para o e-mail do Luiz
        UsuarioRequestDTO requestAtualizacao = new UsuarioRequestDTO("Gustavo", "luiz@email.com", "senha123");

        // O banco diz: "Opa, o e-mail luiz@email.com já pertence ao ID 2"
        Usuario outroUsuario = new Usuario();
        outroUsuario.setId(2L); // ID DIFERENTE!
        outroUsuario.setEmail("luiz@email.com");

        when(usuarioRepository.findById(meuId)).thenReturn(Optional.of(meuUsuario));
        when(usuarioRepository.findByEmail(requestAtualizacao.email())).thenReturn(Optional.of(outroUsuario));

        // 2. WHEN & THEN
        IllegalArgumentException excecao = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.atualizarUsuario(meuId, requestAtualizacao);
        });

        assertEquals("E-mail já cadastrado no sistema por outro usuário.", excecao.getMessage());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }
}