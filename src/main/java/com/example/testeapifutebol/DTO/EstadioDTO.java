package com.example.testeapifutebol.DTO;

import lombok.Data;
import java.time.LocalDateTime;
@Data

public class EstadioDTO {
    
    // Nome do estádio (único campo necessário para criar/atualizar estádio)
    private String nome;

    // Construtor vazio (necessário para Jackson converter JSON → Java)
    public EstadioDTO() {
        // Construtor padrão - necessário para Jackson
    }

    // Construtor com parâmetros (para criar DTO com dados)
    public EstadioDTO(String nome) {
        this.nome = nome;
    }

    // Getter do nome (retorna nome do estádio para JSON)
    public String getName() {
        return nome;
    }

    // Setter do nome (recebe nome do estádio do JSON)
    public void setName(String name) {
        this.nome = name;
    }

}
