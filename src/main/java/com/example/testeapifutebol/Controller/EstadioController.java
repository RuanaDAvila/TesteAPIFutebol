package com.example.testeapifutebol.Controller;

import com.example.testeapifutebol.DTO.EstadioDTO;
import com.example.testeapifutebol.Service.EstadioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;




@RestController
@RequestMapping("/estadios")
public class EstadioController {
    @Autowired
    private EstadioService estadioService ;

    // POST /estadios - Criar novo estádio
    @PostMapping
    public ResponseEntity<EstadioDTO> criarEstadio(@RequestBody EstadioDTO estadioDTO) {
        // Chama Service para cadastrar estádio no banco
        EstadioDTO estadioCriado = estadioService.cadastrarEstadio(estadioDTO);
        return new ResponseEntity<>(estadioCriado, HttpStatus.CREATED); // 201 - Criado com sucesso
    }
    // PUT /estadios/{id} - Atualizar estádio existente
    @PutMapping("/{id}")
    public ResponseEntity<EstadioDTO> updateEstadio(@PathVariable Long id, @RequestBody EstadioDTO estadioDTO) {
        // Chama Service para atualizar estádio no banco
        EstadioDTO estadioAtualizado = estadioService.updateEstadioEntity(id, estadioDTO);

        if (estadioAtualizado == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 - Estádio não encontrado
        }

        return new ResponseEntity<>(estadioAtualizado, HttpStatus.OK); // 200 - Atualizado com sucesso
    }
    // DELETE /estadios/{id} - Remover estádio (hard delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarEstadio(@PathVariable Long id) {
        // Chama Service para deletar estádio do banco
        boolean deletado = estadioService.deleteEstadioEntity(id);

        if (deletado) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 - Deletado com sucesso
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 - Estádio não encontrado
        }
    }

    // GET /estadios/{id} - Buscar estádio específico por ID
    @GetMapping("/{id}")
    public ResponseEntity<EstadioDTO> buscarEstadioPorId(@PathVariable Long id) {
        // Chama Service para buscar estádio no banco
        EstadioDTO estadioEncontrado = estadioService.findEstadioById(id);

        if (estadioEncontrado == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 - Estádio não encontrado
        }
        return new ResponseEntity<>(estadioEncontrado, HttpStatus.OK); // 200 - Encontrado com sucesso
    }

    // GET /estadios - Listar todos os estádios com paginação
    @GetMapping
    public ResponseEntity<Page<EstadioDTO>> listarEstadios(
            // PARÂMETROS DE PAGINAÇÃO E ORDENAÇÃO
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nome") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        // Configura direção da ordenação (asc ou desc)
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? 
            Sort.Direction.DESC : Sort.Direction.ASC;
        
        // Cria configuração de paginação e ordenação
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        // Busca estádios aplicando paginação e ordenação
        Page<EstadioDTO> estadiosEncontrados = estadioService.findAllEstadios(pageable);
        
        return new ResponseEntity<>(estadiosEncontrados, HttpStatus.OK); // 200
    }

}
