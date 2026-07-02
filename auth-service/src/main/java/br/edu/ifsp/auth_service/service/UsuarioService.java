package br.edu.ifsp.auth_service.service;

import br.edu.ifsp.auth_service.dto.usuario.LoginRequestDTO;
import br.edu.ifsp.auth_service.dto.usuario.UsuarioRequestDTO;
import br.edu.ifsp.auth_service.dto.usuario.UsuarioResponseDTO;
import br.edu.ifsp.auth_service.exception.ResourceNotFoundException;
import br.edu.ifsp.auth_service.mapper.UsuarioMapper;
import br.edu.ifsp.auth_service.model.Role;
import br.edu.ifsp.auth_service.model.Usuario;
import br.edu.ifsp.auth_service.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          UsuarioMapper usuarioMapper,
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioMapper = usuarioMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UsuarioResponseDTO cadastrarUsuario(UsuarioRequestDTO requestDTO) {
        if (usuarioRepository.findByEmail(requestDTO.email()).isPresent()) {
            throw new IllegalArgumentException("E-mail já cadastrado no sistema.");
        }

        Usuario usuario = usuarioMapper.toModel(requestDTO);
        usuario.setRole(Role.ROLE_USER);

        // SEGURANÇA: Salva a senha com hash BCrypt em vez de texto puro
        usuario.setSenha(passwordEncoder.encode(requestDTO.senha()));

        Usuario usuarioSalvo = usuarioRepository.save(usuario);
        return usuarioMapper.toResponseDTO(usuarioSalvo);
    }

    public UsuarioResponseDTO buscarPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com o ID: " + id));
        return usuarioMapper.toResponseDTO(usuario);
    }

    @Transactional
    public void excluirUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com o ID: " + id));
        usuarioRepository.delete(usuario);
    }

    public List<UsuarioResponseDTO> listarTodos() {
        return usuarioRepository.findAll()
                .stream()
                .map(usuarioMapper::toResponseDTO)
                .toList();
    }

    @Transactional
    public UsuarioResponseDTO atualizarUsuario(Long id, UsuarioRequestDTO requestDTO) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com o ID: " + id));

        usuarioRepository.findByEmail(requestDTO.email()).ifPresent(usuarioEncontrado -> {
            if (!usuarioEncontrado.getId().equals(id)) {
                throw new IllegalArgumentException("E-mail já cadastrado no sistema por outro usuário.");
            }
        });

        usuario.setNome(requestDTO.nome());
        usuario.setEmail(requestDTO.email());

        // SEGURANÇA: Também aplica hash ao atualizar a senha
        usuario.setSenha(passwordEncoder.encode(requestDTO.senha()));

        Usuario usuarioAtualizado = usuarioRepository.save(usuario);
        return usuarioMapper.toResponseDTO(usuarioAtualizado);
    }

    public UsuarioResponseDTO autenticar(LoginRequestDTO loginRequest) {
        Usuario usuario = usuarioRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com o e-mail informado."));

        // SEGURANÇA: Compara a senha digitada com o hash salvo no banco
        if (!passwordEncoder.matches(loginRequest.senha(), usuario.getSenha())) {
            throw new IllegalArgumentException("Senha incorreta.");
        }

        return usuarioMapper.toResponseDTO(usuario);
    }

    @Transactional
    public void atualizarInteresses(Long id, List<String> esportes) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com o ID: " + id));
        usuario.setInteresses(esportes);
        usuarioRepository.save(usuario);
    }
}