package br.edu.ifsp.competicoes_api.service;

import br.edu.ifsp.competicoes_api.dto.usuario.LoginRequestDTO;
import br.edu.ifsp.competicoes_api.dto.usuario.UsuarioRequestDTO;
import br.edu.ifsp.competicoes_api.dto.usuario.UsuarioResponseDTO;
import br.edu.ifsp.competicoes_api.exception.ResourceNotFoundException;
import br.edu.ifsp.competicoes_api.mapper.UsuarioMapper;
import br.edu.ifsp.competicoes_api.model.Role;
import br.edu.ifsp.competicoes_api.model.Usuario;
import br.edu.ifsp.competicoes_api.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    public UsuarioService(UsuarioRepository usuarioRepository, UsuarioMapper usuarioMapper) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioMapper = usuarioMapper;
    }

    @Transactional
    public UsuarioResponseDTO cadastrarUsuario(UsuarioRequestDTO requestDTO) {
        // REGRA DE NEGÓCIO: Não permitir e-mails duplicados no banco
        if (usuarioRepository.findByEmail(requestDTO.email()).isPresent()) {
            throw new IllegalArgumentException("E-mail já cadastrado no sistema.");
        }

        // 1. Converte o DTO para Entidade
        Usuario usuario = usuarioMapper.toModel(requestDTO);

        usuario.setRole(Role.ROLE_USER);

        // 2. Salva no banco de dados
        Usuario usuarioSalvo = usuarioRepository.save(usuario);

        // 3. Converte a Entidade salva para o DTO de resposta
        return usuarioMapper.toResponseDTO(usuarioSalvo);
    }

    /**
     * Busca um usuário pelo ID.
     */
    public UsuarioResponseDTO buscarPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com o ID: " + id));

        return usuarioMapper.toResponseDTO(usuario);
    }

    /**
     * Exclui um usuário do sistema.
     */
    @Transactional
    public void excluirUsuario(Long id) {
        // Aproveitamos a mesma lógica de busca. Se não existir, ele já trava aqui e lança o erro.
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com o ID: " + id));

        // Se encontrou, manda o repositório deletar
        usuarioRepository.delete(usuario);
    }

    /**
     * Retorna a lista de todos os usuários cadastrados.
     */
    public List<UsuarioResponseDTO> listarTodos() {
        return usuarioRepository.findAll()
                .stream()
                .map(usuarioMapper::toResponseDTO)
                .toList();
    }

    /**
     * Atualiza os dados de um usuário existente.
     */
    @Transactional
    public UsuarioResponseDTO atualizarUsuario(Long id, UsuarioRequestDTO requestDTO) {
        // 1. Busca o usuário pelo ID. Se não achar, já trava aqui.
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com o ID: " + id));

        // 2. Regra de Negócio: Verifica colisão de E-mail
        usuarioRepository.findByEmail(requestDTO.email()).ifPresent(usuarioEncontrado -> {
            if (!usuarioEncontrado.getId().equals(id)) {
                throw new IllegalArgumentException("E-mail já cadastrado no sistema por outro usuário.");
            }
        });

        // 3. Atualiza os dados da Entidade
        usuario.setNome(requestDTO.nome());
        usuario.setEmail(requestDTO.email());
        usuario.setSenha(requestDTO.senha());

        // 4. Salva no banco e converte para resposta
        Usuario usuarioAtualizado = usuarioRepository.save(usuario);
        return usuarioMapper.toResponseDTO(usuarioAtualizado);
    }

    /**
     * Autentica as credenciais de login de um usuário (Regra do MVP).
     */
    public UsuarioResponseDTO autenticar(LoginRequestDTO loginRequest) {
        Usuario usuario = usuarioRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com o e-mail informado."));

        if (!usuario.getSenha().equals(loginRequest.senha())) {
            throw new IllegalArgumentException("Senha incorreta.");
        }

        return usuarioMapper.toResponseDTO(usuario);
    }

    /**
     * Atualiza e persiste a lista de interesses/modalidades favoritas do usuário.
     */
    @Transactional
    public void atualizarInteresses(Long id, List<String> esportes) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com o ID: " + id));
        
        // Define a lista de interesses na Entidade (Certifique-se de que sua classe Usuario tenha setInteresses ou setModalidades)
        usuario.setInteresses(esportes);
        usuarioRepository.save(usuario);
    }
}