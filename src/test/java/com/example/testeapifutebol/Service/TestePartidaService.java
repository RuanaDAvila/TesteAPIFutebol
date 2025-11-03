package com.example.testeapifutebol.Service;

import com.example.testeapifutebol.DTO.PartidaDTO;
import com.example.testeapifutebol.Entity.EstadioEntity;
import com.example.testeapifutebol.Entity.PartidaEntity;
import com.example.testeapifutebol.Repository.EstadioRepository;
import com.example.testeapifutebol.Repository.PartidaRepository;
import com.example.testeapifutebol.Entity.ClubeEntity;
import com.example.testeapifutebol.Repository.ClubeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import com.example.testeapifutebol.Excecao.RegraDeInvalidosExcecao400;
import com.example.testeapifutebol.Excecao.RegraDoNaoEncontradoExcecao404;
import com.example.testeapifutebol.Excecao.RegraDeExcecao409;

//AAA Pattern (Arrange, Act, Assert):
//Arrange: Preparar dados e mocks
//Act: Executar o metodo testado
//Assert: Verificar o resultado

@ExtendWith(MockitoExtension.class)
public class TestePartidaService {
    @InjectMocks
    private PartidaService partidaService;

    @Mock
    private PartidaRepository partidaRepository;
    @Mock
    private ClubeRepository clubeRepository;
    @Mock
    private EstadioRepository estadioRepository;

    @Test
    void testeSalvarPartida_Sucesso() {
        //ARRANGE
        PartidaDTO partidaDTO = new PartidaDTO();
        partidaDTO.setClubeCasaId(1L);
        partidaDTO.setClubeVisitanteId(2L);
        partidaDTO.setResultadoCasa(2);
        partidaDTO.setResultadoVisitante(1);
        partidaDTO.setEstadio("Maracanã");
        partidaDTO.setDataHora(LocalDateTime.now().plusDays(1));

        ClubeEntity clubeCasa = new ClubeEntity();
        clubeCasa.setId(1L);
        clubeCasa.setNome("Flamengo");
        clubeCasa.setDataCriacao(LocalDate.now().minusYears(1));
        clubeCasa.setAtivo("S");

        ClubeEntity clubeVisitante = new ClubeEntity();
        clubeVisitante.setId(2L);
        clubeVisitante.setNome("Vasco");
        clubeVisitante.setDataCriacao(LocalDate.now().minusYears(1));
        clubeVisitante.setAtivo("S");

        PartidaEntity partidaSalva = new PartidaEntity();
        partidaSalva.setId(1L);
        partidaSalva.setClubeCasaId(1L);
        partidaSalva.setClubeVisitanteId(2L);
        partidaSalva.setResultadoCasa(2);
        partidaSalva.setResultadoVisitante(1);
        partidaSalva.setEstadio("Maracanã");
        partidaSalva.setDataHora(partidaDTO.getDataHora());

        when(clubeRepository.findById(1L)).thenReturn(Optional.of(clubeCasa));
        when(clubeRepository.findById(2L)).thenReturn(Optional.of(clubeVisitante));
        when(clubeRepository.existsById(1L)).thenReturn(true);
        when(clubeRepository.existsById(2L)).thenReturn(true);
        when(estadioRepository.existsByNome("Maracanã")).thenReturn(true);
        when(partidaRepository.existsByEstadioAndDataHora(any(), any(), any())).thenReturn(false);
        when(partidaRepository.buscarPartidasPorClube(any(), any(), any())).thenReturn(Arrays.asList());
        when(partidaRepository.save(any(PartidaEntity.class))).thenReturn(partidaSalva);

        //ACT
        PartidaDTO resultado = partidaService.savePartidaEntity(partidaDTO);

        //ASSERT
        assertNotNull(resultado);
        assertEquals(1L, resultado.getClubeCasaId());
        assertEquals(2L, resultado.getClubeVisitanteId());
        assertEquals("Maracanã", resultado.getEstadio());
    }

    @Test
    void testeSalvarPartida_ClubesIguais_RetornoExcecao400() {
        //ARRANGE
        PartidaDTO partidaDTO = new PartidaDTO();
        partidaDTO.setClubeCasaId(1L);
        partidaDTO.setClubeVisitanteId(1L); // Mesmo clube
        partidaDTO.setResultadoCasa(2);
        partidaDTO.setResultadoVisitante(1);
        partidaDTO.setEstadio("Maracanã");
        partidaDTO.setDataHora(LocalDateTime.now().plusDays(1));

        //ACT & ASSERT
        RegraDeInvalidosExcecao400 excecao = assertThrows(RegraDeInvalidosExcecao400.class, () -> {
            partidaService.savePartidaEntity(partidaDTO);
        });

        assertEquals("Os clubes da casa e visitante não podem ser iguais", excecao.getMessage());
    }

    @Test
    void testeSalvarPartida_ClubeInexistente_RetornoExcecao400() {
        //ARRANGE
        PartidaDTO partidaDTO = new PartidaDTO();
        partidaDTO.setClubeCasaId(999L); // Clube inexistente
        partidaDTO.setClubeVisitanteId(2L);
        partidaDTO.setResultadoCasa(2);
        partidaDTO.setResultadoVisitante(1);
        partidaDTO.setEstadio("Maracanã");
        partidaDTO.setDataHora(LocalDateTime.now().plusDays(1));

        when(clubeRepository.existsById(999L)).thenReturn(false);
        when(clubeRepository.existsById(2L)).thenReturn(true);

        //ACT & ASSERT
        RegraDeInvalidosExcecao400 excecao = assertThrows(RegraDeInvalidosExcecao400.class, () -> {
            partidaService.savePartidaEntity(partidaDTO);
        });

        assertEquals("Clube da casa inexistente", excecao.getMessage());
    }

    @Test
    void testeSalvarPartida_EstadioInexistente_RetornoExcecao400() {
        //ARRANGE
        PartidaDTO partidaDTO = new PartidaDTO();
        partidaDTO.setClubeCasaId(1L);
        partidaDTO.setClubeVisitanteId(2L);
        partidaDTO.setResultadoCasa(2);
        partidaDTO.setResultadoVisitante(1);
        partidaDTO.setEstadio("Estádio Inexistente");
        partidaDTO.setDataHora(LocalDateTime.now().plusDays(1));

        when(clubeRepository.existsById(1L)).thenReturn(true);
        when(clubeRepository.existsById(2L)).thenReturn(true);
        when(estadioRepository.existsByNome("Estádio Inexistente")).thenReturn(false);

        //ACT & ASSERT
        RegraDeInvalidosExcecao400 excecao = assertThrows(RegraDeInvalidosExcecao400.class, () -> {
            partidaService.savePartidaEntity(partidaDTO);
        });

        assertEquals("Estádio inexistente", excecao.getMessage());
    }

    @Test
    void testeSalvarPartida_GolsNegativos_RetornoExcecao400() {
        //ARRANGE
        PartidaDTO partidaDTO = new PartidaDTO();
        partidaDTO.setClubeCasaId(1L);
        partidaDTO.setClubeVisitanteId(2L);
        partidaDTO.setResultadoCasa(-1); // Gols negativos
        partidaDTO.setResultadoVisitante(1);
        partidaDTO.setEstadio("Maracanã");
        partidaDTO.setDataHora(LocalDateTime.now().plusDays(1));

        when(clubeRepository.existsById(1L)).thenReturn(true);
        when(clubeRepository.existsById(2L)).thenReturn(true);
        when(estadioRepository.existsByNome("Maracanã")).thenReturn(true);

        //ACT & ASSERT
        RegraDeInvalidosExcecao400 excecao = assertThrows(RegraDeInvalidosExcecao400.class, () -> {
            partidaService.savePartidaEntity(partidaDTO);
        });

        assertEquals("O número de gols não pode ser negativo", excecao.getMessage());
    }

    @Test
    void testeSalvarPartida_ClubeInativo_RetornoExcecao409() {
        //ARRANGE
        PartidaDTO partidaDTO = new PartidaDTO();
        partidaDTO.setClubeCasaId(1L);
        partidaDTO.setClubeVisitanteId(2L);
        partidaDTO.setResultadoCasa(2);
        partidaDTO.setResultadoVisitante(1);
        partidaDTO.setEstadio("Maracanã");
        partidaDTO.setDataHora(LocalDateTime.now().plusDays(1));

        ClubeEntity clubeCasa = new ClubeEntity();
        clubeCasa.setId(1L);
        clubeCasa.setNome("Flamengo");
        clubeCasa.setDataCriacao(LocalDate.now().minusYears(1));
        clubeCasa.setAtivo("N"); // Clube inativo

        ClubeEntity clubeVisitante = new ClubeEntity();
        clubeVisitante.setId(2L);
        clubeVisitante.setNome("Vasco");
        clubeVisitante.setDataCriacao(LocalDate.now().minusYears(1));
        clubeVisitante.setAtivo("S");

        when(clubeRepository.findById(1L)).thenReturn(Optional.of(clubeCasa));
        when(clubeRepository.findById(2L)).thenReturn(Optional.of(clubeVisitante));
        when(clubeRepository.existsById(1L)).thenReturn(true);
        when(clubeRepository.existsById(2L)).thenReturn(true);
        when(estadioRepository.existsByNome("Maracanã")).thenReturn(true);

        //ACT & ASSERT
        RegraDeExcecao409 excecao = assertThrows(RegraDeExcecao409.class, () -> {
            partidaService.savePartidaEntity(partidaDTO);
        });

        assertEquals("O clube da casa, Flamengo, está inativo", excecao.getMessage());
    }

    @Test
    void testeBuscarPartidaPorId_Sucesso() {
        //ARRANGE
        Long partidaId = 1L;
        PartidaEntity partida = new PartidaEntity();
        partida.setId(partidaId);
        partida.setClubeCasaId(1L);
        partida.setClubeVisitanteId(2L);
        partida.setResultadoCasa(2);
        partida.setResultadoVisitante(1);
        partida.setEstadio("Maracanã");
        partida.setDataHora(LocalDateTime.now().plusDays(1));

        when(partidaRepository.findById(partidaId)).thenReturn(Optional.of(partida));

        //ACT
        PartidaDTO resultado = partidaService.findPartidaById(partidaId);

        //ASSERT
        assertNotNull(resultado);
        assertEquals(1L, resultado.getClubeCasaId());
        assertEquals(2L, resultado.getClubeVisitanteId());
        assertEquals("Maracanã", resultado.getEstadio());
    }

    @Test
    void testeBuscarPartidaPorId_PartidaInexistente_RetornoExcecao404() {
        //ARRANGE
        Long partidaId = 999L;
        when(partidaRepository.findById(partidaId)).thenReturn(Optional.empty());

        //ACT & ASSERT
        RegraDoNaoEncontradoExcecao404 excecao = assertThrows(RegraDoNaoEncontradoExcecao404.class, () -> {
            partidaService.findPartidaById(partidaId);
        });

        assertEquals("Partida não encontrada com o ID: " + partidaId, excecao.getMessage());
    }

    @Test
    void testeAtualizarPartida_Sucesso() {
        //ARRANGE
        Long partidaId = 1L;
        PartidaEntity partidaExistente = new PartidaEntity();
        partidaExistente.setId(partidaId);
        partidaExistente.setClubeCasaId(1L);
        partidaExistente.setClubeVisitanteId(2L);

        PartidaDTO partidaDTO = new PartidaDTO();
        partidaDTO.setClubeCasaId(1L);
        partidaDTO.setClubeVisitanteId(2L);
        partidaDTO.setResultadoCasa(3);
        partidaDTO.setResultadoVisitante(1);
        partidaDTO.setEstadio("Maracanã");
        partidaDTO.setDataHora(LocalDateTime.now().plusDays(1));

        ClubeEntity clubeCasa = new ClubeEntity();
        clubeCasa.setId(1L);
        clubeCasa.setNome("Flamengo");
        clubeCasa.setDataCriacao(LocalDate.now().minusYears(1));
        clubeCasa.setAtivo("S");

        ClubeEntity clubeVisitante = new ClubeEntity();
        clubeVisitante.setId(2L);
        clubeVisitante.setNome("Vasco");
        clubeVisitante.setDataCriacao(LocalDate.now().minusYears(1));
        clubeVisitante.setAtivo("S");

        when(partidaRepository.findById(partidaId)).thenReturn(Optional.of(partidaExistente));
        when(clubeRepository.findById(1L)).thenReturn(Optional.of(clubeCasa));
        when(clubeRepository.findById(2L)).thenReturn(Optional.of(clubeVisitante));
        when(clubeRepository.existsById(1L)).thenReturn(true);
        when(clubeRepository.existsById(2L)).thenReturn(true);
        when(estadioRepository.existsByNome("Maracanã")).thenReturn(true);
        when(partidaRepository.existsByEstadioAndDataHora(any(), any(), any())).thenReturn(false);
        when(partidaRepository.buscarPartidasPorClube(any(), any(), any())).thenReturn(Arrays.asList());
        when(partidaRepository.save(any(PartidaEntity.class))).thenReturn(partidaExistente);

        //ACT
        PartidaDTO resultado = partidaService.updatePartidaEntity(partidaId, partidaDTO);

        //ASSERT
        assertNotNull(resultado);
        assertEquals(1L, resultado.getClubeCasaId());
        assertEquals(2L, resultado.getClubeVisitanteId());
    }

    @Test
    void testeAtualizarPartida_PartidaInexistente_RetornoExcecao404() {
        //ARRANGE
        Long partidaId = 999L;
        PartidaDTO partidaDTO = new PartidaDTO();
        when(partidaRepository.findById(partidaId)).thenReturn(Optional.empty());

        //ACT & ASSERT
        RegraDoNaoEncontradoExcecao404 excecao = assertThrows(RegraDoNaoEncontradoExcecao404.class, () -> {
            partidaService.updatePartidaEntity(partidaId, partidaDTO);
        });

        assertEquals("Partida não encontrada com o ID: " + partidaId, excecao.getMessage());
    }

    @Test
    void testeDeletarPartida_Sucesso() {
        //ARRANGE
        Long partidaId = 1L;
        when(partidaRepository.existsById(partidaId)).thenReturn(true);

        //ACT
        assertDoesNotThrow(() -> {
            partidaService.deletePartidaEntity(partidaId);
        });

        //ASSERT
        verify(partidaRepository).deleteById(partidaId);
    }

    @Test
    void testeDeletarPartida_PartidaInexistente_RetornoExcecao404() {
        //ARRANGE
        Long partidaId = 999L;
        when(partidaRepository.existsById(partidaId)).thenReturn(false);

        //ACT & ASSERT
        RegraDoNaoEncontradoExcecao404 excecao = assertThrows(RegraDoNaoEncontradoExcecao404.class, () -> {
            partidaService.deletePartidaEntity(partidaId);
        });

        assertEquals("Partida não encontrada com o ID: " + partidaId, excecao.getMessage());
    }

    @Test
    void testeListarPartidas_Sucesso() {
        //ARRANGE
        List<PartidaEntity> partidas = Arrays.asList(
            new PartidaEntity(), new PartidaEntity()
        );
        when(partidaRepository.findAll()).thenReturn(partidas);

        //ACT
        List<PartidaDTO> resultado = partidaService.findAllPartidaEntity();

        //ASSERT
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
    }
}
