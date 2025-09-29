package com.example.testeapifutebol.DTO;

import lombok.Data;
import java.time.LocalDateTime;
@Data

//uso DTO pata transferir objetos entre camadas
public class PartidaDTO {
    //para receber dados da partida
    private Long clubeCasaId;
    private Long clubeVisitanteId;
    private Integer resultadoCasa;
    private Integer resultadoVisitante;
    private String estadio;
    private LocalDateTime dataHora;  // String → LocalDate...(Cliente (Postman) → DTO (String) → Service (converte) → Entity (LocalDateTime) → Banco)

}
