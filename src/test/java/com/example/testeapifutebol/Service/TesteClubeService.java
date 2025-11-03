package com.example.testeapifutebol.Service;

import com.example.testeapifutebol.Repository.PartidaRepository;
import com.example.testeapifutebol.Entity.ClubeEntity;
import com.example.testeapifutebol.DTO.ClubeDTO;
import com.example.testeapifutebol.Repository.ClubeRepository;
import com.example.testeapifutebol.Service.ClubeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import com.example.testeapifutebol.Excecao.RegraDeInvalidosExcecao400;
import com.example.testeapifutebol.Excecao.RegraDoNaoEncontradoExcecao404;
import com.example.testeapifutebol.Excecao.RegraDeExcecao409;

//AAA Pattern (Arrange, Act, Assert):
//Arrange: Preparar dados e mocks
//Act: Executar o metodo testado
//Assert: Verificar o resultado


//ela que diz ao junit para preparar a classe, iniciar biblioteca para criacao do mock(simulador)
@ExtendWith(MockitoExtension.class)


public class TesteClubeService {
    @Mock //ele é o simulador/duble do Repository, clube e partida, ela fica em cima da minha dependencia
    private ClubeRepository clubeRepository;
    @Mock
    private PartidaRepository partidaRepository;

    @InjectMocks//ele é o objeto que vai ser testado, cria o service e coloca o objeto dentro dela
    private ClubeService clubeService;

    @Test
    void testeSalvarClube() {
        //ARRANGE, preparar dados
        ClubeEntity clube = new ClubeEntity();
        clube.setNome("Flamengo");
        clube.setEstado("RJ"); //SP não é estado do Flamengo
        clube.setDataCriacao(LocalDate.now());
        clube.setAtivo("S");

        //Configurar mock
        when(clubeRepository.existsByNomeAndEstado("Flamengo", "RJ")).thenReturn(false);
        when(clubeRepository.save(clube)).thenReturn(clube);

        //ACT, executar o metodo
        ClubeEntity resultado = clubeService.salvarClube(clube);

        //ASSERT, verificar o resultado
        assertNotNull(resultado);
        assertEquals("Flamengo", resultado.getNome());
    }

    @Test
    void testeSalvarClube_NomeDuplicado_RetornoExcecao409() {
        //ARRANGE, preparar dados
        ClubeEntity clube = new ClubeEntity();
        clube.setNome("Flamengo");
        clube.setEstado("RJ");
        clube.setDataCriacao(LocalDate.now());
        clube.setAtivo("S");

        //Configurar mock para simular clube já existente
        when(clubeRepository.existsByNomeAndEstado("Flamengo", "RJ")).thenReturn(true);

        //ACT ,executar o metodo e captura a excecao
        RegraDeExcecao409 excecao409 = assertThrows(RegraDeExcecao409.class, () -> {
            clubeService.salvarClube(clube);
        });

        //ASSERT, verificar o resultado
        assertEquals("Já existe um clube com o nome '" + clube.getNome() + "' no estado '" + clube.getEstado() + "'", excecao409.getMessage());
    }


    @Test
    void testeSalvarClube_NomeInvalido_RetornoExcecao400() {
        //ARRANGE, preparar dados
        ClubeEntity clube = new ClubeEntity();
        clube.setNome("F");
        clube.setEstado("RJ");
        clube.setDataCriacao(LocalDate.now());
        clube.setAtivo("S");

        //ACT ,executar o metodo e captura a excecao
        RegraDeInvalidosExcecao400 excecao400 = assertThrows(RegraDeInvalidosExcecao400.class, () -> {
            clubeService.salvarClube(clube);
        });

        //ASSERT, verificar o resultado
        assertEquals("Nome deve ter pelo menos 2 caracteres", excecao400.getMessage());
    }

    @Test
    void testeSalvarClube_EstadoInvalido_RetornoExcecao400() {
        //ARRANGE, preparar dados
        ClubeEntity clube = new ClubeEntity();
        clube.setNome("Flamengo");
        clube.setEstado("XX"); // Estado inválido
        clube.setDataCriacao(LocalDate.now());
        clube.setAtivo("S");

        //ACT - executar o metodo
        RegraDeInvalidosExcecao400 excecao = assertThrows(RegraDeInvalidosExcecao400.class, () -> {
            clubeService.salvarClube(clube);
        });

        //ASSERT, verificar o resultado
        assertEquals("Estado inválido: XX", excecao.getMessage());
    }

    @Test
    void testeSalvarClube_DataFutura_RetornoExcecao400() {
        //ARRANGE, preparar dados
        ClubeEntity clube = new ClubeEntity();
        clube.setNome("Flamengo");
        clube.setEstado("RJ");
        clube.setDataCriacao(LocalDate.now().plusDays(1)); // Data futura
        clube.setAtivo("S");

        //ACT ,executar o metodo e captura a excecao
        RegraDeInvalidosExcecao400 excecao = assertThrows(RegraDeInvalidosExcecao400.class, () -> {
            clubeService.salvarClube(clube);
        });

        //ASSERT, verificar o resultado
        assertEquals("Data de criação não pode ser futura", excecao.getMessage());
    }

    @Test
    void testeSalvarClube_NomeNulo_RetornoExcecao400() {
        //ARRANGE, preparar dados
        ClubeEntity clube = new ClubeEntity();
        clube.setNome(null); // Nome nulo
        clube.setEstado("RJ");
        clube.setDataCriacao(LocalDate.now());
        clube.setAtivo("S");

        //ACT ,executar o metodo e captura a excecao
        RegraDeInvalidosExcecao400 excecao = assertThrows(RegraDeInvalidosExcecao400.class, () -> {
            clubeService.salvarClube(clube);
        });

        //ASSERT, verificar o resultado da exceção
        assertEquals("Nome é obrigatório", excecao.getMessage());
    }

    @Test
    void testeAtualizarClube_Sucesso() {
        //ARRANGE, preparar dados
        Long clubeId = 1L;
        ClubeEntity clubeExistente = new ClubeEntity();
        clubeExistente.setId(clubeId);
        clubeExistente.setNome("Flamengo");
        clubeExistente.setEstado("RJ");

        ClubeEntity clubeAtualizado = new ClubeEntity();
        clubeAtualizado.setNome("Flamengo Atualizado");
        clubeAtualizado.setEstado("RJ");
        clubeAtualizado.setDataCriacao(LocalDate.now());
        clubeAtualizado.setAtivo("S");

        //Configurar mocks
        when(clubeRepository.findById(clubeId)).thenReturn(Optional.of(clubeExistente));
        when(clubeRepository.existsByNomeAndEstadoAndIdNot("Flamengo Atualizado", "RJ", clubeId)).thenReturn(false);
        when(clubeRepository.save(clubeExistente)).thenReturn(clubeExistente);

        //ACT ,executar o metodo e captura a excecao
        ClubeDTO resultado = clubeService.updateClubeEntity(clubeId, new ClubeDTO());

        //ASSERT, verificar o resultado da exceção
        assertNotNull(resultado);
        assertEquals("Flamengo Atualizado", resultado.getNome());
    }

    @Test
    void testeAtualizarClube_ClubeInexistente_RetornoExcecao404() {
        //ARRANGE, preparar dados
        Long clubeId = 999L;
        ClubeEntity clubeAtualizado = new ClubeEntity();
        clubeAtualizado.setNome("Teste");
        clubeAtualizado.setEstado("SP");

        when(clubeRepository.findById(clubeId)).thenReturn(Optional.empty());

        //ACT ,executar o metodo e captura a excecao
        RegraDoNaoEncontradoExcecao404 excecao = assertThrows(RegraDoNaoEncontradoExcecao404.class, () -> {
            clubeService.updateClubeEntity(clubeId, new ClubeDTO());
        });

        //ASSERT, verificar o resultado da exceção
        assertEquals("Clube não encontrado com ID: " + clubeId, excecao.getMessage());
    }

    @Test
    void testeAtualizarClube_NomeDuplicado_RetornoExcecao409() {
        //ARRANGE, preparar dados
        Long clubeId = 1L;
        ClubeEntity clubeExistente = new ClubeEntity();
        clubeExistente.setId(clubeId);

        ClubeEntity clubeAtualizado = new ClubeEntity();
        clubeAtualizado.setNome("Vasco");
        clubeAtualizado.setEstado("RJ");

        when(clubeRepository.findById(clubeId)).thenReturn(Optional.of(clubeExistente));
        when(clubeRepository.existsByNomeAndEstadoAndIdNot("Vasco", "RJ", clubeId)).thenReturn(true);

        //ACT ,executar o metodo e captura a excecao
        RegraDeExcecao409 excecao = assertThrows(RegraDeExcecao409.class, () -> {
            clubeService.updateClubeEntity(clubeId, new ClubeDTO());
        });

        //ASSERT, verificar o resultado da exceção
        assertEquals("Já existe um clube com o nome 'Vasco' no estado 'RJ'", excecao.getMessage());
    }

    @Test
    void testeBuscarClubePorId_Sucesso() {
        //ARRANGE, preparar dados
        Long clubeId = 1L;
        ClubeEntity clube = new ClubeEntity();
        clube.setId(clubeId);
        clube.setNome("Flamengo");

        when(clubeRepository.findById(clubeId)).thenReturn(Optional.of(clube));

        //ACT ,executar o metodo e captura a excecao
        ClubeDTO resultado = clubeService.findClubeById(clubeId);

        //ASSERT, verificar o resultado da exceção
        assertNotNull(resultado);
        assertEquals(clubeId, resultado.getId());
        assertEquals("Flamengo", resultado.getNome());
    }

    @Test
    void testeBuscarClubePorId_ClubeInexistente_RetornoExcecao404() {
        //ARRANGE, preparar dados
        Long clubeId = 999L;
        when(clubeRepository.findById(clubeId)).thenReturn(Optional.empty());

        //ACT ,executar o metodo e captura a excecao
        RegraDoNaoEncontradoExcecao404 excecao = assertThrows(RegraDoNaoEncontradoExcecao404.class, () -> {
            clubeService.findClubeById(clubeId);
        });

        //ASSERT, verificar o resultado da exceção
        assertEquals("Clube não encontrado com ID: " + clubeId, excecao.getMessage());
    }

    @Test
    void testeInativarClube_Sucesso() {
        //ARRANGE, preparar dados
        Long clubeId = 1L;
        ClubeEntity clube = new ClubeEntity();
        clube.setId(clubeId);
        clube.setAtivo("S");

        when(clubeRepository.findById(clubeId)).thenReturn(Optional.of(clube));

        //ACT ,executar o metodo
        boolean resultado = clubeService.inativarClubeEntity(clubeId);

        //ASSERT, verificar o resultado
        assertTrue(resultado);
    }

    @Test
    void testeInativarClube_ClubeInexistente_RetornoExcecao404() {
        //ARRANGE, preparar dados
        Long clubeId = 999L;
        when(clubeRepository.findById(clubeId)).thenReturn(Optional.empty());

        //ACT ,executar o metodo e captura a excecao
        RegraDoNaoEncontradoExcecao404 excecao = assertThrows(RegraDoNaoEncontradoExcecao404.class, () -> {
            clubeService.inativarClubeEntity(clubeId);
        });

        //ASSERT, verificar o resultado da exceção
        assertEquals("Clube não encontrado com ID: " + clubeId, excecao.getMessage());
    }

    @Test
    void testeListarClubes_Sucesso() {
        //ARRANGE, preparar dados
        List<ClubeEntity> clubes = Arrays.asList(
            new ClubeEntity(), new ClubeEntity()
        );
        when(clubeRepository.findAll()).thenReturn(clubes);

        //ACT ,xecutar o metodo
        List<ClubeDTO> resultado = clubeService.findAllClubeEntity();

        //ASSERT, verificar o resultado
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
    }



}
