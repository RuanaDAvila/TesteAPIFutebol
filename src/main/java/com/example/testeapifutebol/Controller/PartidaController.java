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
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

// Controller REST - Gerencia requisições HTTP para operações com partidas
@RestController
@RequestMapping("/partidas")
public class PartidaController {
    
    @Autowired
    private PartidaService partidaService;

    //Cria nova partida
    @PostMapping
    public ResponseEntity<PartidaDTO> criarPartidaEntity(@RequestBody PartidaDTO partidaDTO) {
        PartidaDTO partidaCriada = partidaService.savePartidaEntity(partidaDTO);
        return new ResponseEntity<>(partidaCriada, HttpStatus.CREATED); // retorna 201
    }

    //Lista todas as partidas
    @GetMapping
    public ResponseEntity<List<PartidaDTO>> findAllPartidaEntity() {
        List<PartidaDTO> partidas = partidaService.findAllPartidaEntity();
        return new ResponseEntity<>(partidas, HttpStatus.OK); // retorna 200
    }

    //Busca avançada com filtros, paginação e ordenação
    @GetMapping("/buscar") // URL: /partidas/buscar
    public ResponseEntity<Page<PartidaDTO>> buscarPartidasComFiltros(
            // PARÂMETROS DE FILTRO (todos opcionais)
            @RequestParam(required = false) String estadio,
            @RequestParam(required = false) Integer golsCasa,
            @RequestParam(required = false) Integer golsVisitante,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataHora,
            @RequestParam(required = false) Boolean apenasGoleadas,
            @RequestParam(required = false) Long clubeId,
            @RequestParam(required = false) Boolean clubeCasa,
            @RequestParam(required = false) Boolean clubeVisitante,

            // PARÂMETROS DE PAGINAÇÃO E ORDENAÇÃO
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dataHora") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        //Configura direção da ordenação (asc ou desc)
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? 
            Sort.Direction.DESC : Sort.Direction.ASC;
        
        //Cria configuração de paginação e ordenação
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        //Busca partidas aplicando filtros, paginação e ordenação
        Page<PartidaDTO> partidasEncontradas = partidaService.findPartidasComFiltros(
            estadio, golsCasa, golsVisitante, dataHora, 
            apenasGoleadas, clubeId, clubeCasa, clubeVisitante, pageable
        );
        
        return new ResponseEntity<>(partidasEncontradas, HttpStatus.OK); // 200
    }

    //Busca partida por ID
    @GetMapping("/{id}")
    public ResponseEntity<PartidaDTO> buscarPartidaPorId(@PathVariable Long id) {
        PartidaDTO partidaEncontrada = partidaService.findPartidaById(id);
        return ResponseEntity.ok(partidaEncontrada); // 200 - Partida encontrada
    }

    //Atualiza partida existente
    @PutMapping("/{id}")
    public ResponseEntity<PartidaDTO> updatePartida(@PathVariable Long id, @Valid @RequestBody PartidaDTO partidaDTO) {
        PartidaDTO partidaAtualizada = partidaService.updatePartidaEntity(id, partidaDTO);
        return ResponseEntity.ok(partidaAtualizada); // 200 - Atualizada com sucesso
    }

    //Deleta partida
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarPartida(@PathVariable Long id) {
        partidaService.deletePartidaEntity(id);
        return ResponseEntity.noContent().build(); // 204 - Deletado com sucesso
    }

    // GET /partidas/clube/{clubeId}/goleadas - Buscar goleadas de um clube
    @GetMapping("/clube/{clubeId}/goleadas")
    public ResponseEntity<List<PartidaDTO>> buscarGoleadasPorClube(@PathVariable Long clubeId) {
        List<PartidaDTO> partidas = partidaService.findGoleadasByClube(clubeId);
        return ResponseEntity.ok(partidas);
    }
    
    // GET /partidas/clube/{clubeId}/filtros - Buscar partidas de um clube com filtros de clubeCasa/clubeVisitante
    @GetMapping("/clube/{clubeId}/filtros")
    public ResponseEntity<List<PartidaDTO>> buscarPartidasPorClubeComFiltros(
            @PathVariable Long clubeId,
            @RequestParam(required = false) Boolean clubeCasa,
            @RequestParam(required = false) Boolean clubeVisitante) {
        
        List<PartidaDTO> partidas = partidaService.findPartidasByClubeComFiltros(
            clubeId, clubeCasa, clubeVisitante
        );
        return ResponseEntity.ok(partidas);
    }
    
    // GET /partidas/clube/{clubeId} - Buscar partidas por clube em um período
    @GetMapping("/clube/{clubeId}")
    public ResponseEntity<List<PartidaDTO>> buscarPartidasPorClube(
            @PathVariable Long clubeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim) {
        
        // Se as datas não forem fornecidas, usa valores padrão
        if (dataInicio == null) {
            dataInicio = LocalDateTime.of(1900, 1, 1, 0, 0); // Data antiga
        }
        if (dataFim == null) {
            dataFim = LocalDateTime.of(2100, 12, 31, 23, 59, 59); // Data futura
        }
        
        List<PartidaDTO> partidas = partidaService.buscarPartidasPorClube(clubeId, dataInicio, dataFim);
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
