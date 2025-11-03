package com.example.testeapifutebol.Controller;

import com.example.testeapifutebol.DTO.EstadioDTO;
import com.example.testeapifutebol.Service.EstadioService;
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

@WebMvcTest(EstadioController.class)
public class TesteEstadioController {
    
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EstadioService estadioService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testeCadastrarEstadio_Sucesso() throws Exception {
        //ARRANGE
        EstadioDTO estadioDTO = new EstadioDTO();
        estadioDTO.setName("Maracanã");

        when(estadioService.cadastrarEstadio(any(EstadioDTO.class))).thenReturn(estadioDTO);

        //ACT & ASSERT
        mockMvc.perform(post("/estadios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(estadioDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Maracanã"));
    }

    @Test
    void testeAtualizarEstadio_Sucesso() throws Exception {
        //ARRANGE
        Long estadioId = 1L;
        EstadioDTO estadioDTO = new EstadioDTO();
        estadioDTO.setName("Maracanã Atualizado");

        when(estadioService.updateEstadioEntity(eq(estadioId), any(EstadioDTO.class))).thenReturn(estadioDTO);

        //ACT & ASSERT
        mockMvc.perform(put("/estadios/{id}", estadioId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(estadioDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Maracanã Atualizado"));
    }

    @Test
    void testeAtualizarEstadio_EstadioInexistente_Retorno404() throws Exception {
        //ARRANGE
        Long estadioId = 999L;
        EstadioDTO estadioDTO = new EstadioDTO();
        estadioDTO.setName("Estádio Teste");

        when(estadioService.updateEstadioEntity(eq(estadioId), any(EstadioDTO.class))).thenReturn(null);

        //ACT & ASSERT
        mockMvc.perform(put("/estadios/{id}", estadioId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(estadioDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testeBuscarEstadio_Sucesso() throws Exception {
        //ARRANGE
        Long estadioId = 1L;
        EstadioDTO estadioDTO = new EstadioDTO();
        estadioDTO.setName("Maracanã");

        when(estadioService.findEstadioById(estadioId)).thenReturn(estadioDTO);

        //ACT & ASSERT
        mockMvc.perform(get("/estadios/{id}", estadioId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Maracanã"));
    }

    @Test
    void testeBuscarEstadio_EstadioInexistente_Retorno404() throws Exception {
        //ARRANGE
        Long estadioId = 999L;
        when(estadioService.findEstadioById(estadioId)).thenReturn(null);

        //ACT & ASSERT
        mockMvc.perform(get("/estadios/{id}", estadioId))
                .andExpect(status().isNotFound());
    }

    @Test
    void testeDeletarEstadio_Sucesso() throws Exception {
        //ARRANGE
        Long estadioId = 1L;
        when(estadioService.deleteEstadioEntity(estadioId)).thenReturn(true);

        //ACT & ASSERT
        mockMvc.perform(delete("/estadios/{id}", estadioId))
                .andExpect(status().isNoContent());
    }

    @Test
    void testeDeletarEstadio_EstadioInexistente_Retorno404() throws Exception {
        //ARRANGE
        Long estadioId = 999L;
        when(estadioService.deleteEstadioEntity(estadioId)).thenReturn(false);

        //ACT & ASSERT
        mockMvc.perform(delete("/estadios/{id}", estadioId))
                .andExpect(status().isNotFound());
    }

    @Test
    void testeListarEstadios_Sucesso() throws Exception {
        //ARRANGE
        EstadioDTO estadio1 = new EstadioDTO();
        estadio1.setName("Maracanã");
        EstadioDTO estadio2 = new EstadioDTO();
        estadio2.setName("Arena Corinthians");

        List<EstadioDTO> estadios = Arrays.asList(estadio1, estadio2);
        Page<EstadioDTO> estadiosPage = new PageImpl<>(estadios);

        when(estadioService.findAllEstadios(any(Pageable.class))).thenReturn(estadiosPage);

        //ACT & ASSERT
        mockMvc.perform(get("/estadios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].name").value("Maracanã"))
                .andExpect(jsonPath("$.content[1].name").value("Arena Corinthians"));
    }
}
