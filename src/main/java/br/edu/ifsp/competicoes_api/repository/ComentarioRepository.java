package br.edu.ifsp.competicoes_api.repository;

import br.edu.ifsp.competicoes_api.model.Comentario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComentarioRepository extends JpaRepository<Comentario, Long> {
}