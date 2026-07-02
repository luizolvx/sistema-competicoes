package br.edu.ifsp.auth_service.repository;

import br.edu.ifsp.auth_service.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // O Spring cria a query automaticamente baseada no nome do metodo
    Optional<Usuario> findByEmail(String email);

    // Nova funcionalidade: Busca todos os usuários que possuem a modalidade informada na lista de interesses
    @Query("SELECT u FROM Usuario u JOIN u.interesses i WHERE i = :esporte")
    List<Usuario> findByInteressesContaining(@Param("esporte") String esporte);
}