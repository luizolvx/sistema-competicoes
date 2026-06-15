package br.edu.ifsp.competicoes_api.repository;

import br.edu.ifsp.competicoes_api.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // O Spring cria a query automaticamente baseada no nome do metodo
    Optional<Usuario> findByEmail(String email);
}