package com.example.testeapifutebol.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table (name = "clube")
@Data

/**
 * Entidade que representa um Clube de futebol
 * Esta classe mapeia a tabela 'clube' no banco de dados MySQL
 */
    public class ClubeEntity {
    // ID único do clube (chave primária, gerada automaticamente)
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    // Nome do clube (mínimo 2 caracteres)
    @Column(nullable = false)
    private String nome;

    // Sigla do estado onde o clube tem sede (2 caracteres)
    @Column(nullable = false, length = 2)
    private String estado;

    // Data de criação do clube (não pode ser no futuro)
    @Column(nullable = false)
    private LocalDate datacriacao;

    // para verificar o Status, se o clube está ativo (usei S ou N)
    @Column(nullable = false, length = 1)
    private String ativo;

    // Construtor com todos os campos (eu poderia ter feito um a um, tipo separado)
    // Recebe String para datacriacao e converte internamente para LocalDate
    public ClubeEntity(String nome, String estado, String datacriacao, String ativo) {
        this.nome = nome;
        this.estado = estado;
        this.datacriacao = LocalDate.parse(datacriacao); // Converte String → LocalDate
        this.ativo = ativo;
    }
    // Construtor vazio (obrigatório para JPA)
    public ClubeEntity() {

    }
    // Getters e Setters (necessários para JPA e Spring)
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDate getDataCriacao() {
        return datacriacao;
    }

    public void setDataCriacao(LocalDate datacriacao) {
        this.datacriacao = datacriacao;
    }

    public String getAtivo() {
        return ativo;
    }

    public void setAtivo(String ativo) {
        this.ativo = ativo;
    }
}