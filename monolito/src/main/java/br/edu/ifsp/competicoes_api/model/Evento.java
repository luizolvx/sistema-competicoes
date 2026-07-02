package br.edu.ifsp.competicoes_api.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "eventos") // Define o nome da tabela no banco de dados
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false) // Garante a trava NOT NULL física no banco
    private String nome;

    @Column(nullable = false)
    private String modalidade;

    @Column(nullable = false)
    private String local;

    // Faz a ponte entre o camelCase do Java e o snake_case do MySQL
    @Column(name = "data_realizacao", nullable = false)
    private LocalDate dataRealizacao;

    // Relacionamento: Um evento do IFSP pode ter várias partidas (Épico 3)
    @OneToMany(mappedBy = "evento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Partida> partidas = new ArrayList<>();

    // Relacionamento: Um evento possui um mural com vários comentários (Épico 5)
    @OneToMany(mappedBy = "evento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comentario> comentarios = new ArrayList<>();

    // Construtor padrão que o Hibernate exige para funcionar
    public Evento() {
    }

    // Construtor auxiliar (útil para quando formos escrever os testes unitários)
    public Evento(String nome, String modalidade, String local, LocalDate dataRealizacao) {
        this.nome = nome;
        this.modalidade = modalidade;
        this.local = local;
        this.dataRealizacao = dataRealizacao;
    }

    // --- GETTERS E SETTERS ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getModalidade() {
        return modalidade;
    }

    public void setModalidade(String modalidade) {
        this.modalidade = modalidade;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public LocalDate getDataRealizacao() {
        return dataRealizacao;
    }

    public void setDataRealizacao(LocalDate dataRealizacao) {
        this.dataRealizacao = dataRealizacao;
    }

    public List<Partida> getPartidas() {
        return partidas;
    }

    public void setPartidas(List<Partida> partidas) {
        this.partidas = partidas;
    }

    public List<Comentario> getComentarios() {
        return comentarios;
    }

    public void setComentarios(List<Comentario> comentarios) {
        this.comentarios = comentarios;
    }
}