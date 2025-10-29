package com.example.testeapifutebol.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDateTime;
@Data

public class EstadioDTO {
    
    //Validações de no minimo 3 letras e sem caracteres especiais no Estado
    @NotBlank(message = "O nome do estádio é obrigatório")
    @Size(min = 3, message = "O nome do estádio deve ter no mínimo 3 caracteres")
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\s]*$", message = "O nome do estádio deve conter apenas letras e espaços")
    private String nome;

    //Intellij me pediu um construtor vazio (necessário para Jackson converter JSON → Java)
    public EstadioDTO() {
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
