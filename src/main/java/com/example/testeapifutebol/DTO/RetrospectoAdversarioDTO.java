package com.example.testeapifutebol.DTO;

import lombok.Data;

@Data
public class RetrospectoAdversarioDTO {
    private Long adversarioId;
    private String adversarioNome;
    private int totalJogos;
    private int vitorias;
    private int empates;
    private int derrotas;
    private int golsFeitos;
    private int golsSofridos;
    private int saldoGols;

    public RetrospectoAdversarioDTO() {
        this.totalJogos = 0;
        this.vitorias = 0;
        this.empates = 0;
        this.derrotas = 0;
        this.golsFeitos = 0;
        this.golsSofridos = 0;
        this.saldoGols = 0;
    }
}
