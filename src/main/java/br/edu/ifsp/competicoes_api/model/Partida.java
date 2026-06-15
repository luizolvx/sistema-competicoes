package br.edu.ifsp.competicoes_api.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "partidas")
public class Partida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "equipe_a", nullable = false)
    private String equipeA;

    @Column(name = "equipe_b", nullable = false)
    private String equipeB;

    // Placar usando Integer para aceitar nulo antes do jogo começar
    @Column(name = "placar_equipe_a")
    private Integer placarEquipeA;

    @Column(name = "placar_equipe_b")
    private Integer placarEquipeB;

    @Column(name = "data_hora")
    private LocalDateTime dataHora;

    private String local;

    // O lado "Many" do relacionamento: Muitas partidas pertencem a um Evento
    @ManyToOne
    @JoinColumn(name = "evento_id", nullable = false)
    private Evento evento;

    // Construtor padrão obrigatório pelo Hibernate
    public Partida() {
    }

    // --- GETTERS E SETTERS ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEquipeA() {
        return equipeA;
    }

    public void setEquipeA(String equipeA) {
        this.equipeA = equipeA;
    }

    public String getEquipeB() {
        return equipeB;
    }

    public void setEquipeB(String equipeB) {
        this.equipeB = equipeB;
    }

    public Integer getPlacarEquipeA() {
        return placarEquipeA;
    }

    public void setPlacarEquipeA(Integer placarEquipeA) {
        this.placarEquipeA = placarEquipeA;
    }

    public Integer getPlacarEquipeB() {
        return placarEquipeB;
    }

    public void setPlacarEquipeB(Integer placarEquipeB) {
        this.placarEquipeB = placarEquipeB;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public Evento getEvento() {
        return evento;
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
    }
}