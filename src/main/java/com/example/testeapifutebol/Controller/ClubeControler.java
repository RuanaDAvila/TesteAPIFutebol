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

import java.util.List;

@RestController
@RequestMapping("/clubes")

public class ClubeControler {
    @Autowired
    private ClubeService clubeService;

    @PostMapping
    public ResponseEntity<ClubeDTO> criarClubeEntity(@RequestBody ClubeDTO clubeDTO) {
        ClubeDTO clubeCriado = clubeService.saveClubeEntity(clubeDTO);
        return new ResponseEntity<>(clubeCriado, HttpStatus.CREATED);

    }

    @GetMapping
    public ResponseEntity<List<ClubeDTO>> findAllClubeEntity() {
        List<ClubeDTO> clubes = clubeService.findAllClubeEntity();
        return new ResponseEntity<>(clubes, HttpStatus.OK);
    }

    /**
     * ENDPOINT GET AVANÇADO - LISTAR CLUBES COM FILTROS, PAGINAÇÃO E ORDENAÇÃO
     * 
     * O que faz?
     * - Lista clubes com filtros opcionais (nome, estado, situação)
     * - Permite paginação (dividir resultados em páginas)
     * - Permite ordenação (ascendente/descendente por qualquer campo)
     * - É como uma busca avançada no Google ou Amazon
     * 
     * Como usar? Exemplos de URLs:
     * 
     * FILTROS:
     * - GET /clubes/buscar?nome=Flamengo          → Clubes com "Flamengo" no nome
     * - GET /clubes/buscar?estado=RJ              → Clubes do Rio de Janeiro
     * - GET /clubes/buscar?ativo=S                → Apenas clubes ativos
     * - GET /clubes/buscar?nome=Fla&estado=RJ     → Clubes com "Fla" no nome E do RJ
     * 
     * PAGINAÇÃO:
     * - GET /clubes/buscar?page=0&size=5          → Primeira página, 5 clubes por página
     * - GET /clubes/buscar?page=1&size=10         → Segunda página, 10 clubes por página
     * 
     * ORDENAÇÃO:
     * - GET /clubes/buscar?sort=nome,asc          → Ordenar por nome A-Z
     * - GET /clubes/buscar?sort=nome,desc         → Ordenar por nome Z-A
     * - GET /clubes/buscar?sort=datacriacao,desc  → Ordenar por data (mais recente primeiro)
     * 
     * COMBINAÇÕES:
     * - GET /clubes/buscar?estado=RJ&ativo=S&sort=nome,asc&page=0&size=5
     *   → Clubes ativos do RJ, ordenados por nome A-Z, primeira página com 5 resultados
     * 
     * RETORNOS:
     * - 200 OK + lista de clubes (mesmo que vazia)
     * - Nunca retorna 404, sempre 200 OK (conforme especificação)
     */
    @GetMapping("/buscar") // URL: /clubes/buscar
    public ResponseEntity<Page<ClubeDTO>> buscarClubesComFiltros(
            // PARÂMETROS DE FILTRO (todos opcionais)
            // @RequestParam(required = false) = parâmetro opcional na URL
            @RequestParam(required = false) String nome,     // ?nome=Flamengo
            @RequestParam(required = false) String estado,   // ?estado=RJ  
            @RequestParam(required = false) String ativo,    // ?ativo=S
            
            // PARÂMETROS DE PAGINAÇÃO E ORDENAÇÃO
            // @RequestParam(defaultValue = "0") = se não informar, usa valor padrão
            @RequestParam(defaultValue = "0") int page,      // ?page=0 (primeira página)
            @RequestParam(defaultValue = "10") int size,     // ?size=10 (10 itens por página)
            @RequestParam(defaultValue = "nome") String sortBy,    // ?sort=nome (campo para ordenar)
            @RequestParam(defaultValue = "asc") String sortDir     // ?sortDir=asc (direção: asc ou desc)
    ) {
        // PASSO 1: CRIAR CONFIGURAÇÃO DE ORDENAÇÃO
        // Determina se vai ordenar crescente (A-Z) ou decrescente (Z-A)
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? 
            Sort.Direction.DESC : Sort.Direction.ASC;
        
        // PASSO 2: CRIAR OBJETO DE PAGINAÇÃO E ORDENAÇÃO
        // Pageable = configurações de página, tamanho e ordenação
        // É como dizer: "quero a página 2, com 5 itens, ordenados por nome A-Z"
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        // PASSO 3: CHAMAR O SERVICE PARA BUSCAR COM FILTROS
        // Passa todos os filtros e configurações para o Service fazer a busca
        Page<ClubeDTO> clubesEncontrados = clubeService.findClubesComFiltros(nome, estado, ativo, pageable);
        
        // PASSO 4: RETORNAR RESULTADO
        // SEMPRE retorna 200 OK, mesmo se não encontrar nada (lista vazia)
        // Isso está conforme a especificação: "lista vazia com status 200 OK"
        return new ResponseEntity<>(clubesEncontrados, HttpStatus.OK); // 200
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClubeDTO> updateClube(@PathVariable Long id, @RequestBody ClubeDTO clubeDTO) {
        ClubeDTO clubeAtualizado = clubeService.updateClubeEntity(id, clubeDTO);

        if (clubeAtualizado == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404
        }

        return new ResponseEntity<>(clubeAtualizado, HttpStatus.OK); // 200
    }

    /**
     * ENDPOINT GET POR ID - BUSCAR UM CLUBE ESPECÍFICO
     * 
     * O que faz: Busca e retorna apenas um clube específico pelo seu ID
     * Como usar: GET http://localhost:8080/clubes/1 (onde 1 é o ID do clube que você quer ver)
     * 
     * Diferença do GET normal:
     * - GET /clubes = mostra TODOS os clubes (lista completa)
     * - GET /clubes/1 = mostra APENAS o clube ID 1 (busca específica)
     * 
     * Exemplo prático:
     * - Se você quer ver só os dados do Flamengo (ID 1), usa esta URL
     * - Se você quer ver só os dados do Vasco (ID 3), usa esta URL
     * - É mais rápido quando você já sabe qual clube quer ver
     * 
     * Retornos possíveis:
     * - 200 OK + dados do clube (se encontrar)
     * - 404 NOT FOUND (se o ID não existir)
     */
    @GetMapping("/{id}") // Diz ao Spring: "quando alguém fizer GET /clubes/1, execute este método"
    public ResponseEntity<ClubeDTO> buscarClubePorId(@PathVariable Long id) {
        // @PathVariable pega o {id} da URL e coloca na variável "id"
        // Exemplo: se URL for /clubes/5, então id = 5
        
        // Chama o Service para buscar o clube específico no banco de dados
        ClubeDTO clubeEncontrado = clubeService.findClubeById(id);
        
        // Verifica se encontrou o clube
        if (clubeEncontrado == null) {
            // NÃO ENCONTROU: clube com esse ID não existe
            // É como procurar um telefone que não está na agenda
            // Retorna status 404 NOT_FOUND (significa: "não encontrei esse clube")
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404
        } else {
            // ENCONTROU: clube existe e foi localizado com sucesso
            // Retorna status 200 OK + os dados do clube específico
            // É como encontrar o telefone na agenda e mostrar os dados da pessoa
            return new ResponseEntity<>(clubeEncontrado, HttpStatus.OK); // tem que retornar 200
        }
    }

    /**
     * ENDPOINT DELETE - INATIVAR UM CLUBE (SOFT DELETE)
     * O que faz: Marca um clube como inativo sem apagar do banco de dados
     * Como usar: DELETE http://localhost:8080/clubes/1 (onde 1 é o ID do clube, eu mudo ele de acordo com a posicao que qquero alterar)
     * Exemplo prático:
     * - Antes: clube com ativo = "S" (ativo)
     * - Depois: clube com ativo = "N" (inativo)
     * - Os dados continuam no banco, só muda o status
     */
    @DeleteMapping("/{id}") // Diz ao Spring: "quando alguém fizer DELETE /clubes/1, execute este método"
    public ResponseEntity<Void> inativarClube(@PathVariable Long id) {
        // @PathVariable pega o {id} da URL e coloca na variável "id"
        // Exemplo: se URL for /clubes/5, então id = 5
        
        // Chama o Service para fazer o trabalho pesado (inativar o clube)
        boolean inativado = clubeService.inativarClubeEntity(id);
        
        // Verifica se conseguiu inativar o clube
        if (inativado) {
            // SUCESSO: clube foi inativado
            // Retorna status 204 NO_CONTENT (significa: "operação bem-sucedida, sem conteúdo para mostrar")
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204
        } else {
            // ERRO: clube não foi encontrado (ID não existe)
            // Retorna status 404 NOT_FOUND (significa: "não encontrei esse clube")
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404
        }
    }

}
