package br.edu.ifsp.competicoes_api.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comentarios")
public class Comentario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Limitamos o textão do fórum em 500 caracteres para o MVP
    @Column(nullable = false, length = 500)
    private String texto;

    @Column(name = "data_publicacao", nullable = false)
    private LocalDateTime dataPublicacao;

    // Relacionamento: Muitos comentários podem ser feitos por um único Usuário (Épico 2/5)
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    // Relacionamento: Muitos comentários pertencem a um único Evento Esportivo (Épico 1/5)
    @ManyToOne
    @JoinColumn(name = "evento_id", nullable = false)
    private Evento evento;

    // Construtor padrão que o Hibernate exige
    public Comentario() {
    }

    // --- GETTERS E SETTERS ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public LocalDateTime getDataPublicacao() {
        return dataPublicacao;
    }

    public void setDataPublicacao(LocalDateTime dataPublicacao) {
        this.dataPublicacao = dataPublicacao;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Evento getEvento() {
        return evento;
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
    }
}