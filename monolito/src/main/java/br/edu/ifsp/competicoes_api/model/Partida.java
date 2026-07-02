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

    @Column(name = "placar_equipe_a")
    private Integer placarEquipeA;

    @Column(name = "placar_equipe_b")
    private Integer placarEquipeB;

    @Column(name = "data_hora")
    private LocalDateTime dataHora;

    private String local;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusPartida status = StatusPartida.AGENDADA;

    @ManyToOne
    @JoinColumn(name = "evento_id", nullable = false)
    private Evento evento;

    public Partida() {
    }

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

    public StatusPartida getStatus() {
        return status;
    }

    public void setStatus(StatusPartida status) {
        this.status = status;
    }

    public Evento getEvento() {
        return evento;
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
    }
}