package com.example.testeapifutebol.DTO;

import lombok.Data;

@Data
public class RankingClubeDTO {
    private Long id;
    private String nome;
    private int posicao;
    private int pontos;
    private int jogos;
    private int vitorias;
    private int empates;
    private int derrotas;
    private int golsFeitos;
    private int golsSofridos;
    private int saldoGols;
}
