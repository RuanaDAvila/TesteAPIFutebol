package com.example.testeapifutebol.DTO;

import com.example.testeapifutebol.Entity.PartidaEntity;
import lombok.Data;

import java.util.List;

@Data
public class ConfrontoDiretoDTO {
    private Long clube1Id;
    private String clube1Nome;
    private Long clube2Id;
    private String clube2Nome;
    private int totalJogos;
    private int vitoriasClube1;
    private int empates;
    private int vitoriasClube2;
    private int golsClube1;
    private int golsClube2;
    private List<PartidaEntity> partidas;

    public ConfrontoDiretoDTO() {
        this.totalJogos = 0;
        this.vitoriasClube1 = 0;
        this.empates = 0;
        this.vitoriasClube2 = 0;
        this.golsClube1 = 0;
        this.golsClube2 = 0;
    }
}
