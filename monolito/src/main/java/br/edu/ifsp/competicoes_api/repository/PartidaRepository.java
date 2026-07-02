package br.edu.ifsp.competicoes_api.repository;

import br.edu.ifsp.competicoes_api.model.Partida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartidaRepository extends JpaRepository<Partida, Long> {
    // Busca todas as partidas associadas a um evento específico
    List<Partida> findByEventoId(Long eventoId);
}