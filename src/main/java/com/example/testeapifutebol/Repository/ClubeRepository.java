package com.example.testeapifutebol.Repository;

import com.example.testeapifutebol.DTO.ClubeDTO;
import com.example.testeapifutebol.Entity.ClubeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;



/**
 * Repository para operações com a entidade Clube
 * Extende JpaRepository que já fornece métodos básicos (save, findById, findAll, delete, etc.)
 * Aqui definimos métodos personalizados para consultas específicas
 */

@Repository
public interface ClubeRepository extends JpaRepository<ClubeEntity, Long> {

    /**
     * MÉTODOS DE BUSCA COM FILTROS, PAGINAÇÃO E ORDENAÇÃO
     * 
     * O QUE É findByNome E POR QUE É USADO?
     * 
     * findByNome é um "método mágico" do Spring Data JPA que:
     * 1. VOCÊ SÓ ESCREVE O NOME - O Spring cria automaticamente o código SQL
     * 2. NÃO PRECISA PROGRAMAR - Você não escreve SELECT, WHERE, etc.
     * 3. É COMO UM ASSISTENTE INTELIGENTE - Ele "entende" o que você quer pelo nome
     * 
     * ANALOGIA SIMPLES:
     * - É como pedir para um bibliotecário: "me traga livros do autor João"
     * - Você não precisa explicar onde procurar, como procurar, etc.
     * - O bibliotecário (Spring) entende e faz tudo automaticamente
     * 
     * COMO FUNCIONA A "MÁGICA" DOS NOMES?
     * - O Spring lê palavra por palavra do nome do método
     * - findBy = "procure por"
     * - Nome = "campo nome da tabela"
     * - Containing = "que contenha" (LIKE '%texto%')
     * - IgnoreCase = "ignorando maiúscula/minúscula"
     * - And = "E também" (combina filtros)
     * 
     * EXEMPLOS DE TRADUÇÃO AUTOMÁTICA:
     * - findByNome("Flamengo") = SELECT * FROM clube_entity WHERE nome = 'Flamengo'
     * - findByNomeContaining("Fla") = SELECT * FROM clube_entity WHERE nome LIKE '%Fla%'
     * - findByNomeAndEstado("Fla", "RJ") = SELECT * WHERE nome LIKE '%Fla%' AND estado = 'RJ'
     * - IgnoreCase = adiciona UPPER() para não diferenciar maiúscula/minúscula
     * - Pageable = adiciona ORDER BY e LIMIT automaticamente
     * 
     * POR QUE USAR findByNome AO INVÉS DE SQL MANUAL?
     * 1. MAIS RÁPIDO - Não precisa escrever SQL
     * 2. MENOS ERROS - Spring gera SQL correto automaticamente
     * 3. MAIS LEGÍVEL - Nome do método explica o que faz
     * 4. AUTOMÁTICO - Paginação e ordenação incluídas
     * 5. SEGURO - Protege contra SQL Injection automaticamente
     * 
     * Exemplo prático de uso:
     * - findByNomeContainingIgnoreCase("fla") encontra "Flamengo", "FLAMENGO", "flamengo"
     * - findByEstadoAndAtivo("RJ", "S") encontra clubes ativos do Rio de Janeiro
     */
    
    // BUSCA POR NOME (contendo o texto, ignorando maiúscula/minúscula)
    // Exemplo: findByNomeContainingIgnoreCase("fla") encontra "Flamengo"
    // SQL gerado: SELECT * FROM clube_entity WHERE UPPER(nome) LIKE UPPER('%fla%')
    Page<ClubeEntity> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
    
    // BUSCA POR ESTADO ESPECÍFICO
    // Exemplo: findByEstado("RJ") encontra todos os clubes do Rio de Janeiro
    // SQL gerado: SELECT * FROM clube_entity WHERE estado = 'RJ'
    Page<ClubeEntity> findByEstado(String estado, Pageable pageable);
    
    // BUSCA POR SITUAÇÃO (ativo ou inativo)
    // Exemplo: findByAtivo("S") encontra todos os clubes ativos
    // SQL gerado: SELECT * FROM clube_entity WHERE ativo = 'S'
    Page<ClubeEntity> findByAtivo(String ativo, Pageable pageable);
    
    // BUSCA POR NOME + ESTADO (2 filtros combinados)
    // Exemplo: clubes com "Fla" no nome E que sejam do RJ
    // SQL gerado: SELECT * FROM clube_entity WHERE UPPER(nome) LIKE UPPER('%fla%') AND estado = 'RJ'
    Page<ClubeEntity> findByNomeContainingIgnoreCaseAndEstado(String nome, String estado, Pageable pageable);
    
    // BUSCA POR NOME + SITUAÇÃO (2 filtros combinados)
    // Exemplo: clubes com "Fla" no nome E que estejam ativos
    // SQL gerado: SELECT * FROM clube_entity WHERE UPPER(nome) LIKE UPPER('%fla%') AND ativo = 'S'
    Page<ClubeEntity> findByNomeContainingIgnoreCaseAndAtivo(String nome, String ativo, Pageable pageable);
    
    // BUSCA POR ESTADO + SITUAÇÃO (2 filtros combinados)
    // Exemplo: clubes do RJ E que estejam ativos
    // SQL gerado: SELECT * FROM clube_entity WHERE estado = 'RJ' AND ativo = 'S'
    Page<ClubeEntity> findByEstadoAndAtivo(String estado, String ativo, Pageable pageable);
    
    // BUSCA POR NOME + ESTADO + SITUAÇÃO (3 filtros combinados)
    // Exemplo: clubes com "Fla" no nome E do RJ E ativos
    // SQL gerado: SELECT * FROM clube_entity WHERE UPPER(nome) LIKE UPPER('%fla%') AND estado = 'RJ' AND ativo = 'S'
    Page<ClubeEntity> findByNomeContainingIgnoreCaseAndEstadoAndAtivo(String nome, String estado, String ativo, Pageable pageable);

}
