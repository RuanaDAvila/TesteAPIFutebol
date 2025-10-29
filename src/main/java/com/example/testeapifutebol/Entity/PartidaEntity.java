package com.example.testeapifutebol.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

//Entidade mapeia a tabela 'partida' no banco de dados MySQL

@Entity
@Table(name = "partida")
@Data
public class PartidaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // ID do clube da casa
    @Column(nullable = false)
    @NotNull(message = "ID do clube da casa é obrigatório")
    private Long clubeCasaId;

    // ID do clube visitante
    @Column(nullable = false)
    @NotNull(message = "ID do clube visitante é obrigatório")
    private Long clubeVisitanteId;

    // Resultado do clube da casa
    @Column(nullable = false)
    @Min(value = 0, message = "O resultado do time da casa não pode ser negativo")
    private Integer resultadoCasa;

    // Resultado do clube visitante
    @Column(nullable = false)
    @Min(value = 0, message = "O resultado do time visitante não pode ser negativo")
    private Integer resultadoVisitante;

    // Nome do estádio
    @Column(nullable = false)
    @NotBlank(message = "O nome do estádio é obrigatório")
    @Size(min = 3, message = "O nome do estádio deve ter no mínimo 3 caracteres")
    private String estadio;

    // Data e hora da partida
    @Column(nullable = false)
    @FutureOrPresent(message = "A data e hora da partida devem ser no presente ou futuro")
    private LocalDateTime dataHora;

    // Construtor vazio (obrigatório para JPA)
    public PartidaEntity() {
    }

    // Construtor com todos os campos (eu poderia ter feito cada um por vez)
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
