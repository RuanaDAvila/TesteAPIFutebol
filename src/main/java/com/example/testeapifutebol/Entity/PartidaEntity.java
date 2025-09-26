package com.example.testeapifutebol.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Entidade que representa uma Partida de futebol
 * Esta classe mapeia a tabela 'partida' no banco de dados MySQL
 */
@Entity
@Table(name = "partida")
@Data
public class PartidaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // ID do clube da casa
    @Column(nullable = false)
    private Long clubeCasaId;

    // ID do clube visitante
    @Column(nullable = false)
    private Long clubeVisitanteId;

    // Resultado do clube da casa
    @Column(nullable = false)
    private Integer resultadoCasa;

    // Resultado do clube visitante
    @Column(nullable = false)
    private Integer resultadoVisitante;

    // Nome do estádio
    @Column(nullable = false)
    private String estadio;

    // Data e hora da partida
    @Column(nullable = false)
    private LocalDateTime dataHora;

    // Construtor vazio (obrigatório para JPA)
    public PartidaEntity() {
    }

    // Construtor com todos os campos
    public PartidaEntity(Long clubeCasaId, Long clubeVisitanteId, Integer resultadoCasa, 
                        Integer resultadoVisitante, String estadio, LocalDateTime dataHora) {
        this.clubeCasaId = clubeCasaId;
        this.clubeVisitanteId = clubeVisitanteId;
        this.resultadoCasa = resultadoCasa;
        this.resultadoVisitante = resultadoVisitante;
        this.estadio = estadio;
        this.dataHora = dataHora;
    }
}
