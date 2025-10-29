package com.example.testeapifutebol.Repository;


import com.example.testeapifutebol.Entity.ClubeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;



/**
 * Repository → faz as consultas SQL (SELECT, INSERT, UPDATE, DELETE)
 * Extende JpaRepository que já fornece métodos básicos (save, findById, findAll, delete, etc.)
 * Defino métodos personalizados para consultas específicas
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
    @Query(value = "SELECT c FROM ClubeEntity c WHERE 1=1 " +
           //FILTRO NOME: Se null ignora, se não busca parcial (ex: "fla" encontra "Flamengo")
           //UPPER() = converte para maiúscula tanto o nome do banco quanto o parâmetro (busca case-insensitive)
           //CONCAT('%', :nome, '%') = adiciona % antes e depois (ex: "fla" vira "%fla%")
           //LIKE = busca parcial (ex: "%FLA%" encontra "FLAMENGO", "FLUMINENSE")
           "AND (:nome IS NULL OR UPPER(c.nome) LIKE UPPER(CONCAT('%', :nome, '%'))) " +
           //FILTRO ESTADO: Se null ignora, se não busca exato (ex: "RJ")
           "AND (:estado IS NULL OR c.estado = :estado) " +
           //FILTRO ATIVO: Se null ignora, se não busca exato (true/false)
           "AND (:ativo IS NULL OR c.ativo = :ativo) " +
           //FILTRO DATA: Se null ignora, se não busca data exata (ex: "2024-01-01")
           "AND (:datacriacao IS NULL OR c.datacriacao = :datacriacao)",
           //COUNT QUERY: Conta total de registros para paginação funcionar
           countQuery = "SELECT COUNT(c) FROM ClubeEntity c WHERE 1=1 " +
           "AND (:nome IS NULL OR UPPER(c.nome) LIKE UPPER(CONCAT('%', :nome, '%'))) " +
           "AND (:estado IS NULL OR c.estado = :estado) " +
           "AND (:ativo IS NULL OR c.ativo = :ativo) " +
           "AND (:datacriacao IS NULL OR c.datacriacao = :datacriacao)")
    Page<ClubeEntity> findClubesComFiltros(
        @Param("nome") String nome,
        @Param("estado") String estado, 
        @Param("ativo") String ativo,
        @Param("datacriacao") java.time.LocalDate datacriacao,
        Pageable pageable
    );



    //metodo wue o spring data jpa implementa automaticamente, verifica se nome e estado ja existem
    boolean existsByNomeAndEstado(String nome, String estado);

    // Verifica se já existe um clube com o mesmo nome e estado, excluindo o clube com o ID especificado
    boolean existsByNomeAndEstadoAndIdNot(String nome, String estado, Long id);

    



}
