package com.example.testeapifutebol.Controller;

import com.example.testeapifutebol.DTO.ClubeDTO;
import com.example.testeapifutebol.Service.ClubeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

    // POST /clubes - Criar novo clube
    @PostMapping
    public ResponseEntity<ClubeDTO> criarClubeEntity(@RequestBody ClubeDTO clubeDTO) {
        ClubeDTO clubeCriado = clubeService.saveClubeEntity(clubeDTO);
        return new ResponseEntity<>(clubeCriado, HttpStatus.CREATED); // me da o retorno 201
    }

    // GET /clubes - Listar todos os clubes
    @GetMapping
    public ResponseEntity<List<ClubeDTO>> findAllClubeEntity() {
        List<ClubeDTO> clubes = clubeService.findAllClubeEntity();
        return new ResponseEntity<>(clubes, HttpStatus.OK); // me da o retorno 200
    }

    /**
     * ENDPOINT GET AVANÇADO - LISTAR CLUBES COM FILTROS, PAGINAÇÃO E ORDENAÇÃO
     * - Lista clubes com filtros opcionais (nome, estado, situação)
     * - Permite paginação (dividir resultados em páginas)
     * - Permite ordenação (ascendente/descendente por qualquer campo)
     *
     * FILTROS:
     * - GET /clubes/buscar?nome=Flamengo          → Clubes com "Flamengo" no nome
     * - GET /clubes/buscar?estado=RJ              → Clubes do Rio de Janeiro
     * - GET /clubes/buscar?ativo=S                → Apenas clubes ativos
     * - GET /clubes/buscar?datacriacao=2024-01-15 → Clubes criados em 15/01/2024
     * - GET /clubes/buscar?nome=Fla&estado=RJ     → Clubes com "Fla" no nome E do RJ
     * 
     * PAGINAÇÃO:
     * - GET /clubes/buscar?page=1&size=10         → Segunda página, 10 clubes por página
     * - size: quantidade de itens por página
     * 
     * ORDENAÇÃO:
     * - GET /clubes/buscar?sort=nome,asc          → Ordenar por nome A-Z
     * - GET /clubes/buscar?sort=nome,desc         → Ordenar por nome Z-A
     * - GET /clubes/buscar?sort=datacriacao,desc  → Ordenar por data (mais recente primeiro)
     * - sortBy: campo para ordenar (nome, estado, datacriacao)
     * - sortDir: ORDEM (asc ou desc)
     * 
     * COMBINAÇÕES:
     * - GET /clubes/buscar?estado=RJ&ativo=S&datacriacao=2024-01-15&sort=nome,asc&page=0&size=5
     *   → Clubes ativos do RJ criados em 15/01/2024, ordenados por nome A-Z, primeira página com 5 resultados
     *
     * RETORNOS:
     * - 200 OK + lista de clubes (mesmo que vazia)
     * - 404 not foud
     */
    @GetMapping("/buscar") // URL: /clubes/buscar
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
        
        // Cria configuração de paginação e ordenação
        // Pageable é configurações de página, tamanho e ordenação
        // É como dizer: "quero a página 2, com 5 itens, ordenados por nome A-Z"
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        // Busca clubes aplicando filtros, paginação e ordenação
        // Passa todos os filtros e configurações para o Service fazer a busca
        Page<ClubeDTO> clubesEncontrados = clubeService.findClubesComFiltros(nome, estado, ativo, datacriacao, pageable);
        return new ResponseEntity<>(clubesEncontrados, HttpStatus.OK); // 200
    }

    // PUT /clubes/{id} - Atualizar clube existente
    @PutMapping("/{id}")
    public ResponseEntity<ClubeDTO> updateClube(@PathVariable Long id, @RequestBody ClubeDTO clubeDTO) {
        //chama o serviço para atualizar, esse servoço ja trata as exceções 400, 404 e 409
        ClubeDTO clubeAtualizado = clubeService.updateClubeEntity(id, clubeDTO);
        //se chegou aqui, deu tudo certo e retornou 200 ok
        return ResponseEntity.ok(clubeAtualizado);
    }

    /**
     * ENDPOINT GET POR ID - BUSCAR UM CLUBE ESPECÍFICO
     * Busca clubes aplicando filtros, paginação e ordenação
     * Retorna: 200 OK + dados do clube, ou 404 se não encontrar
     */
    @GetMapping("/{id}") // Diz ao Spring: "quando alguém fizer GET /clubes/1, execute este método"
    public ResponseEntity<ClubeDTO> buscarClubePorId(@PathVariable Long id) {
        // @PathVariable pega o {id} da URL e coloca na variável "id"

        // Chama o Service para buscar o clube específico no banco de dados
        ClubeDTO clubeEncontrado = clubeService.findClubeById(id);
        
        // Verifica se encontrou o clube
        if (clubeEncontrado == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 - Clube não encontrado
        } else {
            return new ResponseEntity<>(clubeEncontrado, HttpStatus.OK); // 200 - Clube encontrado
        }
    }

    //DELETE /clubes/{id} - Inativar clube (soft delete)
     //Não remove do banco, apenas muda status de "S" para "N"
    //Retorna: 204 No Content se sucesso, ou 404 se não encontrar
    @DeleteMapping("/{id}") // Diz ao Spring: "quando alguém fizer DELETE /clubes/1, execute este método"
    public ResponseEntity<Void> inativarClube(@PathVariable Long id) {
        // @PathVariable pega o {id} da URL e coloca na variável "id"
        // Exemplo: se URL for /clubes/5, então id = 5

        // Chama o Service para inativar o clube, retorno 404
        boolean inativado = clubeService.inativarClubeEntity(id);
        
        // Verifica se conseguiu inativar o clube
        if (inativado) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 - Inativado com sucesso
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 - Clube não encontrado
        }
    }

}










