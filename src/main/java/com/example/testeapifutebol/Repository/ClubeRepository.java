package com.example.testeapifutebol.Repository;

import com.example.testeapifutebol.DTO.ClubeDTO;
import com.example.testeapifutebol.Entity.ClubeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
     * MÉTODO ÚNICO COM @Query
     * Como funciona:
     * - (:nome IS NULL OR ...) = se nome for null, ignora o filtro
     * - UPPER(c.nome) LIKE UPPER(CONCAT('%', :nome, '%')) = busca case insensitive
     * - (:datacriacao IS NULL OR c.datacriacao = :datacriacao) = filtro por data específica
     * - Pageable funciona automaticamente para paginação e ordenação
     */
    @Query(value = "SELECT c FROM ClubeEntity c WHERE " +
           "(:nome IS NULL OR UPPER(c.nome) LIKE UPPER(CONCAT('%', :nome, '%'))) AND " +
           "(:estado IS NULL OR c.estado = :estado) AND " +
           "(:ativo IS NULL OR c.ativo = :ativo) AND " +
           "(:datacriacao IS NULL OR c.datacriacao = :datacriacao)",
           countQuery = "SELECT COUNT(c) FROM ClubeEntity c WHERE " +
           "(:nome IS NULL OR UPPER(c.nome) LIKE UPPER(CONCAT('%', :nome, '%'))) AND " +
           "(:estado IS NULL OR c.estado = :estado) AND " +
           "(:ativo IS NULL OR c.ativo = :ativo) AND " +
           "(:datacriacao IS NULL OR c.datacriacao = :datacriacao)")
    Page<ClubeEntity> findClubesComFiltros(
        @Param("nome") String nome,
        @Param("estado") String estado, 
        @Param("ativo") String ativo,
        @Param("datacriacao") java.time.LocalDate datacriacao,
        Pageable pageable
    );

    
    // MÉTODOS BÁSICOS HERDADOS DO JpaRepository:
    // - save(ClubeEntity) - salvar clube
    // - findAll() - buscar todos
    // - findById(Long) - buscar por ID
    // - deleteById(Long) - deletar por ID
    // - count() - contar registros

}
