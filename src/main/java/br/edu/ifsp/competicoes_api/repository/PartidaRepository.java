package br.edu.ifsp.competicoes_api.repository;

import br.edu.ifsp.competicoes_api.model.Evento;
import br.edu.ifsp.competicoes_api.model.Partida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PartidaRepository extends JpaRepository<Partida, Long> {
    // Ao estender JpaRepository<Evento, Long>, o Spring adiciona automaticamente
    // todos os métodos de CRUD (save, findById, delete, etc.) em segundo plano.
}