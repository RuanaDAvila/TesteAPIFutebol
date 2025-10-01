package com.example.testeapifutebol.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;


@Entity // Marca como entidade JPA (tabela do banco)
@Table(name = "estadio") // Nome da tabela no banco: "estadio"
@Data // Lombok: gera getters, setters, toString, equals, hashCode automaticamente
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder

public class EstadioEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //auto incremento
    private Long id;

    // Nome do estádio (campo obrigatório)
    @Column(nullable = false) // NOT NULL no banco de dados
    private String nome;

    // Construtor com parâmetros (para criar estádio com dados)
    public EstadioEntity(Long id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    // Construtor vazio (obrigatório para JPA)
    public EstadioEntity() {
    }

    // Getter do ID (retorna chave primária)
    public Long getId() {
        return id;
    }

    // Setter do ID (define chave primária)
    public void setId(Long id) {
        this.id = id;
    }

    // Getter do nome (retorna nome do estádio)
    public String getNome() {
        return nome;
    }

    // Setter do nome (define nome do estádio)
    public void setNome(String nome) {
        this.nome = nome;
    }
}
