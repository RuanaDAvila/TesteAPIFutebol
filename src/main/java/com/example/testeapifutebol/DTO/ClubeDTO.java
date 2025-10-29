package com.example.testeapifutebol.DTO;

import lombok.Data;

@Data

//DTO (Data Transfer Object) é uma classe simples que serve para transferir dados entre diferentes
// camadas da aplicação, especialmente entre:
//Controller ↔ Service
//API ↔ Cliente (Postman, frontend)

public class ClubeDTO {
    //para receber os dados
    private Long id;
    private String nome;
    private String estado;
    private String datacriacao;
    private String ativo;

}
