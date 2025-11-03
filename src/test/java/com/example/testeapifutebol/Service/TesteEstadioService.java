package com.example.testeapifutebol.Service;

import com.example.testeapifutebol.DTO.EstadioDTO;
import com.example.testeapifutebol.Entity.EstadioEntity;
import com.example.testeapifutebol.Repository.EstadioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import com.example.testeapifutebol.Excecao.RegraDeInvalidosExcecao400;
import com.example.testeapifutebol.Excecao.RegraDeExcecao409;

//AAA Pattern (Arrange, Act, Assert):
//Arrange: Preparar dados e mocks
//Act: Executar o metodo testado
//Assert: Verificar o resultado

@ExtendWith(MockitoExtension.class)
public class TesteEstadioService {
    @InjectMocks
    private EstadioService estadioService;

    @Mock
    private EstadioRepository estadioRepository;

    @Test
    void testeCadastrarEstadio_Sucesso() {
        //ARRANGE
        EstadioDTO estadioDTO = new EstadioDTO();
        estadioDTO.setName("Maracanã");

        EstadioEntity estadioSalvo = new EstadioEntity();
        estadioSalvo.setId(1L);
        estadioSalvo.setNome("Maracanã");

        when(estadioRepository.existsByNome("Maracanã")).thenReturn(false);
        when(estadioRepository.save(any(EstadioEntity.class))).thenReturn(estadioSalvo);

        //ACT
        EstadioDTO resultado = estadioService.cadastrarEstadio(estadioDTO);

        //ASSERT
        assertNotNull(resultado);
        assertEquals("Maracanã", resultado.getName());
    }

    @Test
    void testeCadastrarEstadio_NomeInvalido_RetornoExcecao400() {
        //ARRANGE
        EstadioDTO estadioDTO = new EstadioDTO();
        estadioDTO.setName("AB"); // Nome com menos de 3 letras

        //ACT & ASSERT
        RegraDeInvalidosExcecao400 excecao = assertThrows(RegraDeInvalidosExcecao400.class, () -> {
            estadioService.cadastrarEstadio(estadioDTO);
        });

        assertEquals("O nome do estádio deve ter ao menos 3 letras", excecao.getMessage());
    }

    @Test
    void testeCadastrarEstadio_NomeNulo_RetornoExcecao400() {
        //ARRANGE
        EstadioDTO estadioDTO = new EstadioDTO();
        estadioDTO.setName(null); // Nome nulo

        //ACT & ASSERT
        RegraDeInvalidosExcecao400 excecao = assertThrows(RegraDeInvalidosExcecao400.class, () -> {
            estadioService.cadastrarEstadio(estadioDTO);
        });

        assertEquals("O nome do estádio é obrigatório", excecao.getMessage());
    }

    @Test
    void testeCadastrarEstadio_NomeVazio_RetornoExcecao400() {
        //ARRANGE
        EstadioDTO estadioDTO = new EstadioDTO();
        estadioDTO.setName("   "); // Nome vazio

        //ACT & ASSERT
        RegraDeInvalidosExcecao400 excecao = assertThrows(RegraDeInvalidosExcecao400.class, () -> {
            estadioService.cadastrarEstadio(estadioDTO);
        });

        assertEquals("O nome do estádio é obrigatório", excecao.getMessage());
    }

    @Test
    void testeCadastrarEstadio_NomeComCaracteresEspeciais_RetornoExcecao400() {
        //ARRANGE
        EstadioDTO estadioDTO = new EstadioDTO();
        estadioDTO.setName("Est@dio123"); // Nome com caracteres especiais

        //ACT & ASSERT
        RegraDeInvalidosExcecao400 excecao = assertThrows(RegraDeInvalidosExcecao400.class, () -> {
            estadioService.cadastrarEstadio(estadioDTO);
        });

        assertEquals("O nome do estádio deve ter apenas letras e espaços", excecao.getMessage());
    }

    @Test
    void testeCadastrarEstadio_EstadioJaExiste_RetornoExcecao409() {
        //ARRANGE
        EstadioDTO estadioDTO = new EstadioDTO();
        estadioDTO.setName("Maracanã");

        when(estadioRepository.existsByNome("Maracanã")).thenReturn(true);

        //ACT & ASSERT
        RegraDeExcecao409 excecao = assertThrows(RegraDeExcecao409.class, () -> {
            estadioService.cadastrarEstadio(estadioDTO);
        });

        assertEquals("Estádio 'Maracanã' já existe no sistema", excecao.getMessage());
    }

    @Test
    void testeAtualizarEstadio_Sucesso() {
        //ARRANGE
        Long estadioId = 1L;
        EstadioDTO estadioDTO = new EstadioDTO();
        estadioDTO.setName("Maracanã Atualizado");

        EstadioEntity estadioExistente = new EstadioEntity();
        estadioExistente.setId(estadioId);
        estadioExistente.setNome("Maracanã");

        when(estadioRepository.findById(estadioId)).thenReturn(Optional.of(estadioExistente));
        when(estadioRepository.existsByNome("Maracanã Atualizado")).thenReturn(false);
        when(estadioRepository.save(any(EstadioEntity.class))).thenReturn(estadioExistente);

        //ACT
        EstadioDTO resultado = estadioService.updateEstadioEntity(estadioId, estadioDTO);

        //ASSERT
        assertNotNull(resultado);
        assertEquals("Maracanã Atualizado", resultado.getName());
    }

    @Test
    void testeAtualizarEstadio_EstadioInexistente_RetornoNull() {
        //ARRANGE
        Long estadioId = 999L;
        EstadioDTO estadioDTO = new EstadioDTO();
        estadioDTO.setName("Estádio Teste");

        when(estadioRepository.findById(estadioId)).thenReturn(Optional.empty());

        //ACT
        EstadioDTO resultado = estadioService.updateEstadioEntity(estadioId, estadioDTO);

        //ASSERT
        assertNull(resultado);
    }

    @Test
    void testeAtualizarEstadio_NomeDuplicado_RetornoExcecao409() {
        //ARRANGE
        Long estadioId = 1L;
        EstadioDTO estadioDTO = new EstadioDTO();
        estadioDTO.setName("Arena Corinthians");

        EstadioEntity estadioExistente = new EstadioEntity();
        estadioExistente.setId(estadioId);
        estadioExistente.setNome("Maracanã");

        when(estadioRepository.findById(estadioId)).thenReturn(Optional.of(estadioExistente));
        when(estadioRepository.existsByNome("Arena Corinthians")).thenReturn(true);

        //ACT & ASSERT
        RegraDeExcecao409 excecao = assertThrows(RegraDeExcecao409.class, () -> {
            estadioService.updateEstadioEntity(estadioId, estadioDTO);
        });

        assertEquals("Já existe um estádio com o nome 'Arena Corinthians'", excecao.getMessage());
    }

    @Test
    void testeBuscarEstadio_Sucesso() {
        //ARRANGE
        Long estadioId = 1L;
        EstadioEntity estadio = new EstadioEntity();
        estadio.setId(estadioId);
        estadio.setNome("Maracanã");

        when(estadioRepository.findById(estadioId)).thenReturn(Optional.of(estadio));

        //ACT
        EstadioDTO resultado = estadioService.findEstadioById(estadioId);

        //ASSERT
        assertNotNull(resultado);
        assertEquals("Maracanã", resultado.getName());
    }

    @Test
    void testeBuscarEstadio_EstadioInexistente_RetornoNull() {
        //ARRANGE
        Long estadioId = 999L;
        when(estadioRepository.findById(estadioId)).thenReturn(Optional.empty());

        //ACT
        EstadioDTO resultado = estadioService.findEstadioById(estadioId);

        //ASSERT
        assertNull(resultado);
    }

    @Test
    void testeDeletarEstadio_Sucesso() {
        //ARRANGE
        Long estadioId = 1L;
        EstadioEntity estadio = new EstadioEntity();
        estadio.setId(estadioId);
        estadio.setNome("Maracanã");

        when(estadioRepository.findById(estadioId)).thenReturn(Optional.of(estadio));

        //ACT
        boolean resultado = estadioService.deleteEstadioEntity(estadioId);

        //ASSERT
        assertTrue(resultado);
        verify(estadioRepository).delete(estadio);
    }

    @Test
    void testeDeletarEstadio_EstadioInexistente_RetornoFalse() {
        //ARRANGE
        Long estadioId = 999L;
        when(estadioRepository.findById(estadioId)).thenReturn(Optional.empty());

        //ACT
        boolean resultado = estadioService.deleteEstadioEntity(estadioId);

        //ASSERT
        assertFalse(resultado);
    }

    @Test
    void testeListarEstadios_Sucesso() {
        //ARRANGE
        List<EstadioEntity> estadios = Arrays.asList(
            new EstadioEntity(), new EstadioEntity()
        );
        Page<EstadioEntity> estadiosPage = new PageImpl<>(estadios);
        
        when(estadioRepository.findAll(any(Pageable.class))).thenReturn(estadiosPage);

        //ACT
        Page<EstadioDTO> resultado = estadioService.findAllEstadios(Pageable.unpaged());

        //ASSERT
        assertNotNull(resultado);
        assertEquals(2, resultado.getContent().size());
    }
}
