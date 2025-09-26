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
 * Service - Lógica de negócio para operações de Clube
 * Converte DTO ↔ Entity e coordena com Repository
 */

    // Quando alguém pede algo, o Service:
//1. Recebe o pedido (ClubeDTO)
//2. Converte para formato do banco (Entity)
//3. Pede pro Repository buscar no depósito
//4. Pega o resultado e converte de volta (DTO)
//5. Entrega o resultado final

@Service
public class ClubeService {
    // Injeta o Repository para acessar o banco de dados
    private final ClubeRepository clubeRepository;

    // Construtor para injeção de dependência
    public ClubeService(ClubeRepository clubeRepository) {
        this.clubeRepository = clubeRepository;
    }

    /**
     * Salva um novo clube no banco de dados
     * Recebe: ClubeDTO (dados do Controller/Postman)
     * Retorna: ClubeDTO (dados salvos com ID gerado)
     */
    public ClubeDTO saveClubeEntity(ClubeDTO clubeDTO) {
        // Converte DTO → Entity
        ClubeEntity clubeParaSalvar = new ClubeEntity();
        
        //Copia dados do DTO para a Entity (todos os campos obrigatórios)
        clubeParaSalvar.setNome(clubeDTO.getNome());                                    // String → String
        clubeParaSalvar.setEstado(clubeDTO.getEstado());                                // String → String  
        clubeParaSalvar.setDatacriacao(LocalDate.parse(clubeDTO.getDatacriacao()));    // String → LocalDate
        clubeParaSalvar.setAtivo(clubeDTO.getAtivo());                                  // String → String

        //Salva no banco de dados (Repository faz a persistência)
        ClubeEntity clubeSalvo = clubeRepository.save(clubeParaSalvar);

        //Converte a Entity salva de volta para DTO (para retornar ao Controller)
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
        //Busca todas as Entities no banco
        List<ClubeEntity> clubes = clubeRepository.findAll();
        
        //Converte cada Entity para DTO usando Stream (programação funcional)
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
        //  Busca o clube existente
        ClubeEntity clubeExistente = clubeRepository.findById(id).orElse(null);
        if (clubeExistente == null) return null;

        // Atualiza os dados
        clubeExistente.setNome(clubeDTO.getNome());
        clubeExistente.setEstado(clubeDTO.getEstado());
        clubeExistente.setDatacriacao(LocalDate.parse(clubeDTO.getDatacriacao()));
        clubeExistente.setAtivo(clubeDTO.getAtivo());
        //salva e retorna
        ClubeEntity clubeAtualizado = clubeRepository.save(clubeExistente);

        ClubeDTO resposta = new ClubeDTO();
        resposta.setNome(clubeAtualizado.getNome());
        resposta.setEstado(clubeAtualizado.getEstado());
        resposta.setDatacriacao(clubeAtualizado.getDatacriacao().toString());
        resposta.setAtivo(clubeAtualizado.getAtivo());

        return resposta;
    }

    /**
     * Inativa um clube (soft delete)
     * Muda status de "S" para "N" sem deletar do banco
     */
    public boolean inativarClubeEntity(Long id) {
        //PROCURAR O CLUBE NO BANCO DE DADOS
        // findById(id) = "procure o clube com esse ID"
        // orElse(null) = "se não encontrar, retorne null (vazio)"
        ClubeEntity clubeExistente = clubeRepository.findById(id).orElse(null);
        
        // verifico se O clube existe.
        if (clubeExistente == null) {
            // Se chegou aqui, significa que não encontrou o clube
            return false; // Retorna "false" = "não consegui inativar porque não existe"
        }

        //MARCAR COMO INATIVO (SOFT DELETE)
        // Ao invés de apagar, só mudo o status de "S" (ativo) para "N" (inativo)
        clubeExistente.setAtivo("N");

        //SALVAR A ALTERAÇÃO NO BANCO DE DADOS
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
        // PROCURAR O CLUBE NO BANCO DE DADOS
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

        //CONVERTER ENTITY PARA DTO
        // Encontrou o clube! Agora precisa "traduzir" os dados
        // Entity = formato do banco de dados
        // DTO = formato para enviar ao Postman/front-end
        ClubeDTO clubeParaRetornar = new ClubeDTO();
        clubeParaRetornar.setNome(clubeEncontrado.getNome());                          // String → String
        clubeParaRetornar.setEstado(clubeEncontrado.getEstado());                      // String → String
        clubeParaRetornar.setDatacriacao(clubeEncontrado.getDatacriacao().toString()); // LocalDate → String
        clubeParaRetornar.setAtivo(clubeEncontrado.getAtivo());                        // String → String

        //RETORNAR O CLUBE ENCONTRADO
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
    public Page<ClubeDTO> findClubesComFiltros(String nome, String estado, String ativo, java.time.LocalDate datacriacao, Pageable pageable) {
        // BUSCAR NO BANCO COM FILTROS - AGORA MUITO MAIS SIMPLES!
        // Uma única chamada substitui toda aquela lógica complexa de if/else
        // O @Query do Repository faz toda a mágica dos filtros opcionais
        Page<ClubeEntity> clubesEncontrados = clubeRepository.findClubesComFiltros(nome, estado, ativo, datacriacao, pageable);

        
        // CONVERTER ENTITIES PARA DTOs
        // Converte os resultados para o formato de resposta
        Page<ClubeDTO> clubesParaRetornar = clubesEncontrados.map(clube -> {
            ClubeDTO dto = new ClubeDTO();
            dto.setNome(clube.getNome());
            dto.setEstado(clube.getEstado());
            dto.setDatacriacao(clube.getDatacriacao().toString());
            dto.setAtivo(clube.getAtivo());
            return dto;
        });

        return clubesParaRetornar;
    }

}