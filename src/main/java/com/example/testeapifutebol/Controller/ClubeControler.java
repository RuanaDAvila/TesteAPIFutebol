package com.example.testeapifutebol.Controller;

import com.example.testeapifutebol.DTO.ClubeDTO;
import com.example.testeapifutebol.DTO.RankingClubeDTO;
import com.example.testeapifutebol.DTO.RetrospectoAdversarioDTO;
import com.example.testeapifutebol.DTO.RetrospectoClubeDTO;
import com.example.testeapifutebol.DTO.ConfrontoDiretoDTO;
import com.example.testeapifutebol.Service.ClubeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.List;

// Controller REST - Gerencia requisições HTTP para operações com clubes
@RestController
@RequestMapping("/clubes")
public class ClubeControler {
    
    @Autowired
    private ClubeService clubeService;

    //Criar novo clube
    @PostMapping
    public ResponseEntity<ClubeDTO> criarClubeEntity(@RequestBody ClubeDTO clubeDTO) {
        ClubeDTO clubeCriado = clubeService.saveClubeEntity(clubeDTO);
        return new ResponseEntity<>(clubeCriado, HttpStatus.CREATED); // me da o retorno 201
    }

    //buscar/Listar todos os clubes
    @GetMapping
    public ResponseEntity<List<ClubeDTO>> findAllClubeEntity() {
        List<ClubeDTO> clubes = clubeService.findAllClubeEntity();
        return new ResponseEntity<>(clubes, HttpStatus.OK); // me da o retorno 200
    }


    @GetMapping("/buscar")
    public ResponseEntity<Page<ClubeDTO>> buscarClubesComFiltros(
            @RequestParam(required = false) String nome,     // ?nome=Flamengo
            @RequestParam(required = false) String estado,   // ?estado=RJ
            @RequestParam(required = false) String ativo,    // ?ativo=S
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) java.time.LocalDate datacriacao, // ?datacriacao=2024-01-15

            // PARÂMETROS DE PAGINAÇÃO E ORDENAÇÃO
            // @RequestParam(defaultValue = "0") = se não informar, usa valor padrão
            @RequestParam(defaultValue = "0") int page,      // ?page=0 (primeira página)
            @RequestParam(defaultValue = "10") int size,     // ?size=10 (10 itens por página)
            @RequestParam(defaultValue = "nome") String sortBy,    // ?sort=nome (campo para ordenar)
            @RequestParam(defaultValue = "asc") String sortDir     // ?sortDir=asc (direção: asc ou desc)
    ) {
        // Configura direção da ordenação (asc ou desc)
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? 
            Sort.Direction.DESC : Sort.Direction.ASC;
        
        // Pageable é configurações de página, tamanho e ordenação
        //ex.:"quero a página 2, com 5 itens, ordenados por nome A-Z"
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        // Busca clubes aplicando filtros, paginação e ordenação
        Page<ClubeDTO> clubesEncontrados = clubeService.findClubesComFiltros(nome, estado, ativo, datacriacao, pageable);
        return new ResponseEntity<>(clubesEncontrados, HttpStatus.OK); // 200
    }
    
    //Busca o retrospecto completo de um clube
    @GetMapping("/{id}/retrospecto")
    public ResponseEntity<RetrospectoClubeDTO> buscarRetrospectoClube(@PathVariable Long id) {
        // Chama o serviço para buscar o retrospecto
        RetrospectoClubeDTO retrospecto = clubeService.buscarRetrospectoClube(id);
        return new ResponseEntity<>(retrospecto, HttpStatus.OK);
    }
    
    //Busca o retrospecto de um clube contra todos os seus adversários
    @GetMapping("/{id}/retrospecto/adversarios")
    public ResponseEntity<List<RetrospectoAdversarioDTO>> buscarRetrospectoContraAdversarios(@PathVariable Long id) {
        List<RetrospectoAdversarioDTO> retrospecto = clubeService.buscarRetrospectoContraAdversarios(id);
        return new ResponseEntity<>(retrospecto, HttpStatus.OK);
    }
    
    // Busca o histórico de confrontos diretos entre dois clubes
    @GetMapping("/{clube1Id}/confronto/{clube2Id}")
    public ResponseEntity<ConfrontoDiretoDTO> buscarConfrontoDireto(
            @PathVariable Long clube1Id,
            @PathVariable Long clube2Id
    ) {
        ConfrontoDiretoDTO confronto = clubeService.buscarConfrontoDireto(clube1Id, clube2Id);
        return new ResponseEntity<>(confronto, HttpStatus.OK);
    }
    
    // Busca o ranking de clubes
    @GetMapping("/ranking")
    public ResponseEntity<List<RankingClubeDTO>> buscarRanking(
            @RequestParam(defaultValue = "pontos") String tipo
    ) {
        List<RankingClubeDTO> ranking = clubeService.buscarRanking(tipo);
        return new ResponseEntity<>(ranking, HttpStatus.OK);
    }

    //Atualizar clube existente, usando o id
    @PutMapping("/{id}")
    public ResponseEntity<ClubeDTO> updateClube(@PathVariable Long id, @RequestBody ClubeDTO clubeDTO) {
        //chama o serviço para atualizar, esse servoço ja trata as exceções 400, 404 e 409
        ClubeDTO clubeAtualizado = clubeService.updateClubeEntity(id, clubeDTO);
        //se chegou aqui, deu tudo certo e retornou 200 ok
        return ResponseEntity.ok(clubeAtualizado);
    }

    //Busca clube por id
    @GetMapping("/{id}") // Diz ao Spring: "quando alguém fizer GET /clubes/1, execute este método"
    public ResponseEntity<ClubeDTO> buscarClubePorId(@PathVariable Long id) {

        //Chama o Service para buscar o clube específico no banco de dados
        ClubeDTO clubeEncontrado = clubeService.findClubeById(id);
        
        //Verifica se encontrou o clube
        if (clubeEncontrado == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 - Clube não encontrado
        } else {
            return new ResponseEntity<>(clubeEncontrado, HttpStatus.OK); // 200 - Clube encontrado
        }
    }

    //DELETE /clubes/{id} - Inativar clube (soft delete)
     //Não remove do banco, apenas muda status de "S" para "N"
    //Retorna: 204 No Content se sucesso, ou 404 se não encontrar
    @DeleteMapping("/{id}") //quando alguém fizer DELETE/clubes/1, executa este método"
    public ResponseEntity<Void> inativarClube(@PathVariable Long id) {

        //Chama o Service para inativar o clube, retorno 404
        boolean inativado = clubeService.inativarClubeEntity(id);
        
        // Verifica se conseguiu inativar o clube
        if (inativado) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 - Inativado com sucesso
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 - Clube não encontrado
        }
    }

}










