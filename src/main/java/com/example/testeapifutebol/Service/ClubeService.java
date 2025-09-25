package com.example.testeapifutebol.Service;

import com.example.testeapifutebol.DTO.ClubeDTO;
import com.example.testeapifutebol.Entity.ClubeEntity;
import com.example.testeapifutebol.Repository.ClubeRepository;
import jakarta.persistence.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

/**
 * Service - Camada de lógica de negócio
 * Responsável por processar dados entre Controller e Repository
 * Converte DTO ↔ Entity e aplica regras de negócio
 */

    // Quando alguém pede algo, o Service:
//1. Recebe o pedido (ClubeDTO)
//2. Converte para formato do banco (Entity)
//3. Pede pro Repository buscar no depósito
//4. Pega o resultado e converte de volta (DTO)
//5. Entrega o resultado final
    //Um exemplo Cliente: "Quero buscar clubes do RJ"
//Service pensa:
//1. "Vou pedir pro Repository: findByEstado('RJ')"
//2. "Repository me deu uma lista de Entities"
//3. "Vou converter cada Entity para DTO"
//4. "Pronto! Aqui está sua lista formatada para envio"

@Service
public class ClubeService {
    // Injeta o Repository para acessar o banco de dados
    private final ClubeRepository clubeRepository;

    // Construtor - Spring injeta automaticamente o Repository
    public ClubeService(ClubeRepository clubeRepository) {
        this.clubeRepository = clubeRepository;
    }

    /**
     * Salva um novo clube no banco de dados
     * Recebe: ClubeDTO (dados do Controller/Postman)
     * Retorna: ClubeDTO (dados salvos com ID gerado)
     */
    public ClubeDTO saveClubeEntity(ClubeDTO clubeDTO) {
        // 1. Cria uma nova Entity vazia
        ClubeEntity clubeParaSalvar = new ClubeEntity();
        
        // 2. Copia dados do DTO para a Entity (todos os campos obrigatórios)
        clubeParaSalvar.setNome(clubeDTO.getNome());                                    // String → String
        clubeParaSalvar.setEstado(clubeDTO.getEstado());                                // String → String  
        clubeParaSalvar.setDatacriacao(LocalDate.parse(clubeDTO.getDatacriacao()));    // String → LocalDate
        clubeParaSalvar.setAtivo(clubeDTO.getAtivo());                                  // String → String

        // 3. Salva no banco de dados (Repository faz a persistência)
        ClubeEntity clubeSalvo = clubeRepository.save(clubeParaSalvar);

        // 4. Converte a Entity salva de volta para DTO (para retornar ao Controller)
        ClubeDTO DTOResposta = new ClubeDTO();
        DTOResposta.setNome(clubeSalvo.getNome());                          // String → String
        DTOResposta.setEstado(clubeSalvo.getEstado());                      // String → String
        DTOResposta.setDatacriacao(clubeSalvo.getDatacriacao().toString()); // LocalDate → String
        DTOResposta.setAtivo(clubeSalvo.getAtivo());                        // String → String
        
        return DTOResposta; // Retorna o DTO com dados salvos (incluindo ID gerado)
    }

    /**
     * Lista todos os clubes cadastrados no banco
     * Retorna: List<ClubeDTO> (lista de clubes para o Controller)
     */
    public List<ClubeDTO> findAllClubeEntity() {
        // 1. Busca todas as Entities no banco
        List<ClubeEntity> clubes = clubeRepository.findAll();
        
        // 2. Converte cada Entity para DTO usando Stream (programação funcional)
        return clubes.stream().map(clube -> {
            ClubeDTO dto = new ClubeDTO();
            dto.setNome(clube.getNome());                          // String → String
            dto.setEstado(clube.getEstado());                      // String → String  
            dto.setDatacriacao(clube.getDatacriacao().toString()); // LocalDate → String
            dto.setAtivo(clube.getAtivo());                        // String → String
            return dto;
        }).collect(Collectors.toList()); // Coleta tudo numa List<ClubeDTO>
    }

    /**
     * Atualiza um clube existente no banco de dados
     * Recebe: ID do clube + ClubeDTO com novos dados
     * Retorna: ClubeDTO atualizado ou null se não encontrar
     */
    public ClubeDTO updateClubeEntity(Long id, ClubeDTO clubeDTO) {
        // 1. Busca o clube existente
        ClubeEntity clubeExistente = clubeRepository.findById(id).orElse(null);
        if (clubeExistente == null) return null; // Não encontrou

        // 2. Atualiza os dados
        clubeExistente.setNome(clubeDTO.getNome());
        clubeExistente.setEstado(clubeDTO.getEstado());
        clubeExistente.setDatacriacao(LocalDate.parse(clubeDTO.getDatacriacao()));
        clubeExistente.setAtivo(clubeDTO.getAtivo());

        // 3. Salva e retorna
        ClubeEntity clubeAtualizado = clubeRepository.save(clubeExistente);

        ClubeDTO resposta = new ClubeDTO();
        resposta.setNome(clubeAtualizado.getNome());
        resposta.setEstado(clubeAtualizado.getEstado());
        resposta.setDatacriacao(clubeAtualizado.getDatacriacao().toString());
        resposta.setAtivo(clubeAtualizado.getAtivo());

        return resposta;
    }

    /**
     * MÉTODO PARA INATIVAR UM CLUBE (SOFT DELETE)
     * O que é SOFT DELETE?
     * - É como "esconder" um item ao invés de apagar completamente
     * - Imagine uma lixeira do computador: o arquivo não é deletado, só fica "inativo"
     * - No nosso caso: mudamos o campo "ativo" de "S" para "N"
     * 
     * Por que devo usar SOFT DELETE?
     * - Mantém histórico dos dados
     * - Permite "reativar" o clube depois se necessário
     * - Evita perder informações importantes
     * 
     * Recebe: ID do clube (exemplo: 1, 2, 3...)
     * Retorna: true (conseguiu inativar) ou false (clube não existe)
     */
    public boolean inativarClubeEntity(Long id) {
        // PASSO 1: PROCURAR O CLUBE NO BANCO DE DADOS
        // É como procurar uma pessoa na agenda pelo número do telefone
        // findById(id) = "procure o clube com esse ID"
        // orElse(null) = "se não encontrar, retorne null (vazio)"
        ClubeEntity clubeExistente = clubeRepository.findById(id).orElse(null);
        
        // verifico se O clube existe.
        if (clubeExistente == null) {
            // Se chegou aqui, significa que não encontrou o clube
            return false; // Retorna "false" = "não consegui inativar porque não existe"
        }

        // PASSO 2: MARCAR COMO INATIVO (SOFT DELETE)
        // Aqui é onde acontece a "mágica" do SOFT DELETE
        // Ao invés de apagar, só mudamos o status de "S" (ativo) para "N" (inativo)
        // É como marcar uma tarefa como "concluída" sem apagar ela da lista
        clubeExistente.setAtivo("N");

        // PASSO 3: SALVAR A ALTERAÇÃO NO BANCO DE DADOS
        // É como apertar "Ctrl+S" para salvar um documento
        // O Spring pega o objeto modificado e atualiza no MySQL
        clubeRepository.save(clubeExistente);
        // SUCESSO! Clube foi inativado
        return true; // Retorna "true" = "consegui inativar com sucesso"
    }

    /**
     * MÉTODO PARA BUSCAR UM CLUBE ESPECÍFICO PELO ID
     * O que faz?
     * - Procura um clube específico no banco de dados usando o ID
     * - É como procurar uma pessoa específica na agenda pelo número do telefone
     * - Diferente do "listar todos", este busca apenas 1 clube
     * Exemplo prático:
     * - Se você quer ver só o Flamengo (ID 1), usa este método
     * - Se você quer ver só o Vasco (ID 3), usa este método
     * - É mais rápido que buscar todos e depois filtrar
     * Recebe: ID do clube (exemplo: 1, 2, 3...)
     * Retorna: ClubeDTO (dados do clube) ou null (se não encontrar)
     */
    public ClubeDTO findClubeById(Long id) {
        // PASSO 1: PROCURAR O CLUBE NO BANCO DE DADOS
        // É como procurar um contato específico na agenda pelo número
        // findById(id) = "procure exatamente o clube com esse ID"
        // orElse(null) = "se não encontrar, retorne null (vazio)"
        ClubeEntity clubeEncontrado = clubeRepository.findById(id).orElse(null);
        
        // VERIFICAÇÃO: O clube existe?
        if (clubeEncontrado == null) {
            // Se chegou aqui, significa que não encontrou o clube
            // É como procurar o telefone 999999 na agenda e não achar ninguém
            return null; // Retorna null = "não encontrei esse clube"
        }

        // PASSO 2: CONVERTER ENTITY PARA DTO
        // Encontrou o clube! Agora precisa "traduzir" os dados
        // Entity = formato do banco de dados
        // DTO = formato para enviar ao Postman/front-end
        ClubeDTO clubeParaRetornar = new ClubeDTO();
        clubeParaRetornar.setNome(clubeEncontrado.getNome());                          // String → String
        clubeParaRetornar.setEstado(clubeEncontrado.getEstado());                      // String → String
        clubeParaRetornar.setDatacriacao(clubeEncontrado.getDatacriacao().toString()); // LocalDate → String
        clubeParaRetornar.setAtivo(clubeEncontrado.getAtivo());                        // String → String

        // PASSO 3: RETORNAR O CLUBE ENCONTRADO
        return clubeParaRetornar; // Retorna o DTO com os dados do clube específico
    }

    /**
     * MÉTODO PARA LISTAR CLUBES COM FILTROS, PAGINAÇÃO E ORDENAÇÃO
     * 
     * O que faz?
     * - Lista clubes com filtros opcionais (nome, estado, situação)
     * - Permite paginação (dividir resultados em páginas)
     * - Permite ordenação (ascendente/descendente por qualquer campo)
     * - É como uma busca avançada no Google: você pode filtrar, paginar e ordenar
     * 
     * Exemplos práticos:
     * - Buscar clubes do RJ: filtro estado = "RJ"
     * - Buscar clubes ativos: filtro ativo = "S"
     * - Buscar clubes com "Fla" no nome: filtro nome contém "Fla"
     * - Ver página 2 com 5 clubes por página: page=1, size=5
     * - Ordenar por nome A-Z: sort por nome ascendente
     * 
     * Parâmetros:
     * - nome: filtrar clubes que contenham este texto no nome (opcional)
     * - estado: filtrar clubes deste estado específico (opcional)
     * - ativo: filtrar clubes ativos ("S") ou inativos ("N") (opcional)
     * - pageable: configurações de paginação e ordenação
     * 
     * Retorna: Page<ClubeDTO> (página com lista de clubes + informações de paginação)
     */
    public Page<ClubeDTO> findClubesComFiltros(String nome, String estado, String ativo, Pageable pageable) {
        // PASSO 1: BUSCAR NO BANCO COM FILTROS
        // Aqui vamos usar um método especial do Repository que aceita filtros
        // É como fazer uma consulta SQL com WHERE, ORDER BY e LIMIT
        Page<ClubeEntity> clubesEncontrados;
        
        // LÓGICA DE FILTROS: Aplicamos os filtros que foram informados
        if (nome != null && estado != null && ativo != null) {
            // TODOS OS 3 FILTROS: nome + estado + ativo
            // É como SQL: WHERE nome LIKE '%texto%' AND estado = 'RJ' AND ativo = 'S'
            clubesEncontrados = clubeRepository.findByNomeContainingIgnoreCaseAndEstadoAndAtivo(nome, estado, ativo, pageable);
        } else if (nome != null && estado != null) {
            // 2 FILTROS: nome + estado
            // É como SQL: WHERE nome LIKE '%texto%' AND estado = 'RJ'
            clubesEncontrados = clubeRepository.findByNomeContainingIgnoreCaseAndEstado(nome, estado, pageable);
        } else if (nome != null && ativo != null) {
            // 2 FILTROS: nome + ativo
            // É como SQL: WHERE nome LIKE '%texto%' AND ativo = 'S'
            clubesEncontrados = clubeRepository.findByNomeContainingIgnoreCaseAndAtivo(nome, ativo, pageable);
        } else if (estado != null && ativo != null) {
            // 2 FILTROS: estado + ativo
            // É como SQL: WHERE estado = 'RJ' AND ativo = 'S'
            clubesEncontrados = clubeRepository.findByEstadoAndAtivo(estado, ativo, pageable);
        } else if (nome != null) {
            // 1 FILTRO: apenas nome
            // É como SQL: WHERE nome LIKE '%texto%'
            clubesEncontrados = clubeRepository.findByNomeContainingIgnoreCase(nome, pageable);
        } else if (estado != null) {
            // 1 FILTRO: apenas estado
            // É como SQL: WHERE estado = 'RJ'
            clubesEncontrados = clubeRepository.findByEstado(estado, pageable);
        } else if (ativo != null) {
            // 1 FILTRO: apenas ativo
            // É como SQL: WHERE ativo = 'S'
            clubesEncontrados = clubeRepository.findByAtivo(ativo, pageable);
        } else {
            // SEM FILTROS: busca todos (igual ao método original, mas com paginação)
            // É como SQL: SELECT * FROM clube_entity ORDER BY ... LIMIT ...
            clubesEncontrados = clubeRepository.findAll(pageable);
        }

        // PASSO 2: CONVERTER ENTITIES PARA DTOs
        // Pegamos cada clube encontrado e convertemos para o formato de resposta
        // É como "traduzir" os dados do banco para o formato que o Postman entende
        Page<ClubeDTO> clubesParaRetornar = clubesEncontrados.map(clube -> {
            ClubeDTO dto = new ClubeDTO();
            dto.setNome(clube.getNome());                          // String → String
            dto.setEstado(clube.getEstado());                      // String → String
            dto.setDatacriacao(clube.getDatacriacao().toString()); // LocalDate → String
            dto.setAtivo(clube.getAtivo());                        // String → String
            return dto;
        });

        // PASSO 3: RETORNAR PÁGINA COM CLUBES
        // Retorna não só a lista de clubes, mas também informações de paginação:
        // - Lista dos clubes da página atual
        // - Número total de clubes encontrados
        // - Número total de páginas
        // - Página atual, tamanho da página, etc.
        return clubesParaRetornar;
    }

}