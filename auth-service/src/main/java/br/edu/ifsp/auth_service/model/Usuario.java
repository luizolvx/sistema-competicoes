package br.edu.ifsp.auth_service.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true) // Garante que não existam dois cadastros com o mesmo e-mail
    private String email;

    @Column(nullable = false)
    private String senha;

    // Salva a Role como String no banco de dados (ex: "ROLE_USER" ou "ROLE_ADMIN")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // Relacionamento: Um usuário pode fazer muitos comentários no mural do fórum
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comentario> comentarios = new ArrayList<>();

    // Nova funcionalidade: Lista de modalidades/esportes de interesse do usuário para notificações
    @ElementCollection
    @CollectionTable(name = "usuario_interesses", joinColumns = @JoinColumn(name = "usuario_id"))
    @Column(name = "esporte")
    private List<String> interesses = new ArrayList<>();

    // Construtor padrão que o Hibernate exige
    public Usuario() {
    }

    // Construtor auxiliar útil para os testes
    public Usuario(String nome, String email, String senha, Role role) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.role = role;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public List<Comentario> getComentarios() {
        return comentarios;
    }

    public void setComentarios(List<Comentario> comentarios) {
        this.comentarios = comentarios;
    }

    public List<String> getInteresses() {
        return interesses;
    }

    public void setInteresses(List<String> interesses) {
        this.interesses = interesses;
    }
}