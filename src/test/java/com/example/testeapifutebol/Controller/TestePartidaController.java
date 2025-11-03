package com.example.testeapifutebol.Controller;

import com.example.testeapifutebol.Controller.PartidaController;
import com.example.testeapifutebol.DTO.PartidaDTO;
import com.example.testeapifutebol.Service.PartidaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PartidaController.class)
public class TestePartidaController {
    
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PartidaService partidaService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testeCadastrarPartida_Sucesso() throws Exception {
        //ARRANGE
        PartidaDTO partidaDTO = new PartidaDTO();
        partidaDTO.setClubeCasaId(1L);
        partidaDTO.setClubeVisitanteId(2L);
        partidaDTO.setResultadoCasa(2);
        partidaDTO.setResultadoVisitante(1);
        partidaDTO.setEstadio("Maracanã");
        partidaDTO.setDataHora(LocalDateTime.now().plusDays(1));

        when(partidaService.savePartidaEntity(any(PartidaDTO.class))).thenReturn(partidaDTO);

        //ACT & ASSERT
        mockMvc.perform(post("/partidas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(partidaDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.clubeCasaId").value(1))
                .andExpect(jsonPath("$.clubeVisitanteId").value(2))
                .andExpect(jsonPath("$.estadio").value("Maracanã"));
    }

    @Test
    void testeAtualizarPartida_Sucesso() throws Exception {
        //ARRANGE
        Long partidaId = 1L;
        PartidaDTO partidaDTO = new PartidaDTO();
        partidaDTO.setClubeCasaId(1L);
        partidaDTO.setClubeVisitanteId(2L);
        partidaDTO.setResultadoCasa(3);
        partidaDTO.setResultadoVisitante(1);
        partidaDTO.setEstadio("Maracanã");
        partidaDTO.setDataHora(LocalDateTime.now().plusDays(1));

        when(partidaService.updatePartidaEntity(eq(partidaId), any(PartidaDTO.class))).thenReturn(partidaDTO);

        //ACT & ASSERT
        mockMvc.perform(put("/partidas/{id}", partidaId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(partidaDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultadoCasa").value(3))
                .andExpect(jsonPath("$.resultadoVisitante").value(1));
    }

    @Test
    void testeBuscarPartida_Sucesso() throws Exception {
        //ARRANGE
        Long partidaId = 1L;
        PartidaDTO partidaDTO = new PartidaDTO();
        partidaDTO.setClubeCasaId(1L);
        partidaDTO.setClubeVisitanteId(2L);
        partidaDTO.setResultadoCasa(2);
        partidaDTO.setResultadoVisitante(1);
        partidaDTO.setEstadio("Maracanã");

        when(partidaService.findPartidaById(partidaId)).thenReturn(partidaDTO);

        //ACT & ASSERT
        mockMvc.perform(get("/partidas/{id}", partidaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clubeCasaId").value(1))
                .andExpect(jsonPath("$.clubeVisitanteId").value(2))
                .andExpect(jsonPath("$.estadio").value("Maracanã"));
    }

    @Test
    void testeDeletarPartida_Sucesso() throws Exception {
        //ARRANGE
        Long partidaId = 1L;

        //ACT & ASSERT
        mockMvc.perform(delete("/partidas/{id}", partidaId))
                .andExpect(status().isNoContent());
    }

    @Test
    void testeListarPartidas_Sucesso() throws Exception {
        //ARRANGE
        PartidaDTO partida1 = new PartidaDTO();
        partida1.setClubeCasaId(1L);
        partida1.setClubeVisitanteId(2L);
        partida1.setEstadio("Maracanã");
        
        PartidaDTO partida2 = new PartidaDTO();
        partida2.setClubeCasaId(3L);
        partida2.setClubeVisitanteId(4L);
        partida2.setEstadio("Arena Corinthians");

        List<PartidaDTO> partidas = Arrays.asList(partida1, partida2);

        when(partidaService.findAllPartidaEntity()).thenReturn(partidas);

        //ACT & ASSERT
        mockMvc.perform(get("/partidas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].estadio").value("Maracanã"))
                .andExpect(jsonPath("$[1].estadio").value("Arena Corinthians"));
    }

    @Test
    void testeBuscarPartidasComFiltros_Sucesso() throws Exception {
        //ARRANGE
        PartidaDTO partida = new PartidaDTO();
        partida.setClubeCasaId(1L);
        partida.setClubeVisitanteId(2L);
        partida.setEstadio("Maracanã");
        partida.setResultadoCasa(2);
        partida.setResultadoVisitante(1);

        List<PartidaDTO> partidas = Arrays.asList(partida);
        Page<PartidaDTO> partidasPage = new PageImpl<>(partidas);

        when(partidaService.findPartidasComFiltros(any(), any(), any(), any(), any(), any(), any(), any(), any(Pageable.class))).thenReturn(partidasPage);

        //ACT & ASSERT
        mockMvc.perform(get("/partidas/buscar")
                .param("estadio", "Maracanã")
                .param("golsCasa", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].estadio").value("Maracanã"));
    }

    @Test
    void testeBuscarPartidasPorClube_Sucesso() throws Exception {
        //ARRANGE
        Long clubeId = 1L;
        PartidaDTO partida = new PartidaDTO();
        partida.setClubeCasaId(clubeId);
        partida.setClubeVisitanteId(2L);
        partida.setEstadio("Maracanã");

        List<PartidaDTO> partidas = Arrays.asList(partida);

        when(partidaService.findPartidasByClubeComFiltros(eq(clubeId), any(), any())).thenReturn(partidas);

        //ACT & ASSERT
        mockMvc.perform(get("/partidas/clube/{clubeId}/filtros", clubeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].clubeCasaId").value(1));
    }

    @Test
    void testeBuscarGoleadasPorClube_Sucesso() throws Exception {
        //ARRANGE
        Long clubeId = 1L;
        PartidaDTO partida = new PartidaDTO();
        partida.setClubeCasaId(clubeId);
        partida.setClubeVisitanteId(2L);
        partida.setResultadoCasa(4);
        partida.setResultadoVisitante(0);
        partida.setEstadio("Maracanã");

        List<PartidaDTO> partidas = Arrays.asList(partida);

        when(partidaService.findGoleadasByClube(clubeId)).thenReturn(partidas);

        //ACT & ASSERT
        mockMvc.perform(get("/partidas/clube/{clubeId}/goleadas", clubeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].resultadoCasa").value(4));
    }
}
