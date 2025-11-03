package com.example.testeapifutebol.Controller;

import com.example.testeapifutebol.DTO.ClubeDTO;
import com.example.testeapifutebol.Service.ClubeService;
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

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClubeController.class)
public class TesteClubeController {
    
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClubeService clubeService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testeCadastrarClube_Sucesso() throws Exception {
        //ARRANGE
        ClubeDTO clubeDTO = new ClubeDTO();
        clubeDTO.setNome("Flamengo");
        clubeDTO.setEstado("RJ");
        clubeDTO.setDatacriacao("2023-01-01");
        clubeDTO.setAtivo("S");

        when(clubeService.saveClubeEntity(any(ClubeDTO.class))).thenReturn(clubeDTO);

        //ACT & ASSERT
        mockMvc.perform(post("/clubes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clubeDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Flamengo"))
                .andExpect(jsonPath("$.estado").value("RJ"));
    }

    @Test
    void testeAtualizarClube_Sucesso() throws Exception {
        //ARRANGE
        Long clubeId = 1L;
        ClubeDTO clubeDTO = new ClubeDTO();
        clubeDTO.setNome("Flamengo Atualizado");
        clubeDTO.setEstado("RJ");
        clubeDTO.setDatacriacao("2023-01-01");
        clubeDTO.setAtivo("S");

        when(clubeService.updateClubeEntity(eq(clubeId), any(ClubeDTO.class))).thenReturn(clubeDTO);

        //ACT & ASSERT
        mockMvc.perform(put("/clubes/{id}", clubeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clubeDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Flamengo Atualizado"));
    }

    @Test
    void testeBuscarClube_Sucesso() throws Exception {
        //ARRANGE
        Long clubeId = 1L;
        ClubeDTO clubeDTO = new ClubeDTO();
        clubeDTO.setId(clubeId);
        clubeDTO.setNome("Flamengo");
        clubeDTO.setEstado("RJ");

        when(clubeService.findClubeById(clubeId)).thenReturn(clubeDTO);

        //ACT & ASSERT
        mockMvc.perform(get("/clubes/{id}", clubeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Flamengo"))
                .andExpect(jsonPath("$.estado").value("RJ"));
    }

    @Test
    void testeInativarClube_Sucesso() throws Exception {
        //ARRANGE
        Long clubeId = 1L;
        when(clubeService.inativarClubeEntity(clubeId)).thenReturn(true);

        //ACT & ASSERT
        mockMvc.perform(delete("/clubes/{id}", clubeId))
                .andExpect(status().isNoContent());
    }

    @Test
    void testeListarClubes_Sucesso() throws Exception {
        //ARRANGE
        ClubeDTO clube1 = new ClubeDTO();
        clube1.setNome("Flamengo");
        clube1.setEstado("RJ");
        
        ClubeDTO clube2 = new ClubeDTO();
        clube2.setNome("Vasco");
        clube2.setEstado("RJ");

        List<ClubeDTO> clubes = Arrays.asList(clube1, clube2);

        when(clubeService.findAllClubeEntity()).thenReturn(clubes);

        //ACT & ASSERT
        mockMvc.perform(get("/clubes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nome").value("Flamengo"))
                .andExpect(jsonPath("$[1].nome").value("Vasco"));
    }

    @Test
    void testeBuscarClubesComFiltros_Sucesso() throws Exception {
        //ARRANGE
        ClubeDTO clube = new ClubeDTO();
        clube.setNome("Flamengo");
        clube.setEstado("RJ");

        List<ClubeDTO> clubes = Arrays.asList(clube);
        Page<ClubeDTO> clubesPage = new PageImpl<>(clubes);

        when(clubeService.findClubesComFiltros(any(), any(), any(), any(), any(Pageable.class))).thenReturn(clubesPage);

        //ACT & ASSERT
        mockMvc.perform(get("/clubes/buscar")
                .param("nome", "Flamengo")
                .param("estado", "RJ"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].nome").value("Flamengo"));
    }
}
