package com.example.testeapifutebol.Controller;

import com.example.testeapifutebol.DTO.PartidaDTO;
import com.example.testeapifutebol.Service.PartidaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

// Controller REST - Gerencia requisições HTTP para operações com partidas
@RestController
@RequestMapping("/partidas")
public class PartidaController {
    
    @Autowired
    private PartidaService partidaService;

    // POST /partidas - Criar nova partida
    @PostMapping
    public ResponseEntity<PartidaDTO> criarPartidaEntity(@RequestBody PartidaDTO partidaDTO) {
        PartidaDTO partidaCriada = partidaService.savePartidaEntity(partidaDTO);
        return new ResponseEntity<>(partidaCriada, HttpStatus.CREATED); // retorna 201
    }

    // GET /partidas - Listar todas as partidas
    @GetMapping
    public ResponseEntity<List<PartidaDTO>> findAllPartidaEntity() {
        List<PartidaDTO> partidas = partidaService.findAllPartidaEntity();
        return new ResponseEntity<>(partidas, HttpStatus.OK); // retorna 200
    }

    // GET /partidas/buscar - Busca avançada com filtros, paginação e ordenação
    @GetMapping("/buscar") // URL: /partidas/buscar
    public ResponseEntity<Page<PartidaDTO>> buscarPartidasComFiltros(
            // PARÂMETROS DE FILTRO (todos opcionais)
            @RequestParam(required = false) String estadio,
            @RequestParam(required = false) Integer golsCasa,
            @RequestParam(required = false) Integer golsVisitante,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataHora,

            // PARÂMETROS DE PAGINAÇÃO E ORDENAÇÃO
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dataHora") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        // Configura direção da ordenação (asc ou desc)
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? 
            Sort.Direction.DESC : Sort.Direction.ASC;
        
        // Cria configuração de paginação e ordenação
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        // Busca partidas aplicando filtros, paginação e ordenação
        Page<PartidaDTO> partidasEncontradas = partidaService.findPartidasComFiltros(estadio, golsCasa, golsVisitante, dataHora, pageable);
        
        return new ResponseEntity<>(partidasEncontradas, HttpStatus.OK); // 200
    }

    // GET /partidas/{id} - Buscar partida por ID
    @GetMapping("/{id}")
    public ResponseEntity<PartidaDTO> buscarPartidaPorId(@PathVariable Long id) {
        PartidaDTO partidaEncontrada = partidaService.findPartidaById(id);
        
        if (partidaEncontrada == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 - Partida não encontrada
        } else {
            return new ResponseEntity<>(partidaEncontrada, HttpStatus.OK); // 200 - Partida encontrada
        }
    }

    // PUT /partidas/{id} - Atualizar partida existente
    @PutMapping("/{id}")
    public ResponseEntity<PartidaDTO> updatePartida(@PathVariable Long id, @RequestBody PartidaDTO partidaDTO) {
        PartidaDTO partidaAtualizada = partidaService.updatePartidaEntity(id, partidaDTO);

        if (partidaAtualizada == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 - Partida não encontrada
        }

        return new ResponseEntity<>(partidaAtualizada, HttpStatus.OK); // 200 - Atualizada com sucesso
    }

    // DELETE /partidas/{id} - Deletar partida
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarPartida(@PathVariable Long id) {
        boolean deletado = partidaService.deletePartidaEntity(id);
        
        if (deletado) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 - Deletado com sucesso
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 - Partida não encontrada
        }
    }

    // GET /partidas/clube/{clubeId} - Buscar partidas por clube
    @GetMapping("/clube/{clubeId}")
    public ResponseEntity<List<PartidaDTO>> buscarPartidasPorClube(@PathVariable Long clubeId) {
        List<PartidaDTO> partidas = partidaService.buscarPartidasPorClube(clubeId);
        return new ResponseEntity<>(partidas, HttpStatus.OK);
    }

    // GET /partidas/estadio - Buscar partidas por estádio
    @GetMapping("/estadio")
    public ResponseEntity<List<PartidaDTO>> buscarPartidasPorEstadio(@RequestParam String estadio) {
        List<PartidaDTO> partidas = partidaService.buscarPartidasPorEstadio(estadio);
        return new ResponseEntity<>(partidas, HttpStatus.OK);
    }

    // GET /partidas/data - Buscar partidas por data específica
    @GetMapping("/data")
    public ResponseEntity<List<PartidaDTO>> buscarPartidasPorData(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime data) {
        List<PartidaDTO> partidas = partidaService.buscarPartidasPorData(data);
        return new ResponseEntity<>(partidas, HttpStatus.OK);
    }

    // GET /partidas/resultado - Buscar partidas por resultado específico
    @GetMapping("/resultado")
    public ResponseEntity<List<PartidaDTO>> buscarPartidasPorResultado(
            @RequestParam Integer golsCasa, 
            @RequestParam Integer golsVisitante) {
        List<PartidaDTO> partidas = partidaService.buscarPartidasPorResultado(golsCasa, golsVisitante);
        return new ResponseEntity<>(partidas, HttpStatus.OK);
    }

    // GET /partidas/periodo - Buscar partidas entre duas datas
    @GetMapping("/periodo")
    public ResponseEntity<List<PartidaDTO>> buscarPartidasEntreDatas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim) {
        List<PartidaDTO> partidas = partidaService.buscarPartidasEntreDatas(dataInicio, dataFim);
        return new ResponseEntity<>(partidas, HttpStatus.OK);
    }
}
