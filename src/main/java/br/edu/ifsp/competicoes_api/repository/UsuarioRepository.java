package br.edu.ifsp.competicoes_api.repository;

import br.edu.ifsp.competicoes_api.model.Evento;
import br.edu.ifsp.competicoes_api.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // Ao estender JpaRepository<Evento, Long>, o Spring adiciona automaticamente
    // todos os métodos de CRUD (save, findById, delete, etc.) em segundo plano.
}