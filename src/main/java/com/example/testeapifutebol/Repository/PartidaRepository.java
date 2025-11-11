package com.example.testeapifutebol.Repository;

import com.example.testeapifutebol.Entity.PartidaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static org.hibernate.sql.ast.Clause.WHERE;


//Repository para operações com a entidade Partida
@Repository
public interface PartidaRepository extends JpaRepository<PartidaEntity, Long> {
    //Busca estatísticas de clubes
    @Query("SELECT c.id, c.nome, " +
           "COUNT(p) as totalJogos, " +
           "SUM(CASE WHEN (p.clubeCasaId = c.id AND p.resultadoCasa > p.resultadoVisitante) OR (p.clubeVisitanteId = c.id AND p.resultadoVisitante > p.resultadoCasa) THEN 1 ELSE 0 END) as vitorias, " +
           "SUM(CASE WHEN p.resultadoCasa = p.resultadoVisitante AND (p.clubeCasaId = c.id OR p.clubeVisitanteId = c.id) THEN 1 ELSE 0 END) as empates, " +
           "SUM(CASE WHEN (p.clubeCasaId = c.id AND p.resultadoCasa < p.resultadoVisitante) OR (p.clubeVisitanteId = c.id AND p.resultadoVisitante < p.resultadoCasa) THEN 1 ELSE 0 END) as derrotas, " +
           "COALESCE(SUM(CASE WHEN p.clubeCasaId = c.id THEN p.resultadoCasa ELSE 0 END), 0) + COALESCE(SUM(CASE WHEN p.clubeVisitanteId = c.id THEN p.resultadoVisitante ELSE 0 END), 0) as golsFeitos, " +
           "COALESCE(SUM(CASE WHEN p.clubeCasaId = c.id THEN p.resultadoVisitante ELSE 0 END), 0) + COALESCE(SUM(CASE WHEN p.clubeVisitanteId = c.id THEN p.resultadoCasa ELSE 0 END), 0) as golsSofridos " +
           "FROM ClubeEntity c " +
           "LEFT JOIN PartidaEntity p ON (p.clubeCasaId = c.id OR p.clubeVisitanteId = c.id) " +
           "GROUP BY c.id, c.nome")
    List<Object[]> findEstatisticasClubes();

    //Busca estatísticas de um clube específico
    @Query("SELECT " +
           "COUNT(p) as totalJogos, " +
           "SUM(CASE WHEN (p.clubeCasaId = :clubeId AND p.resultadoCasa > p.resultadoVisitante) OR (p.clubeVisitanteId = :clubeId AND p.resultadoVisitante > p.resultadoCasa) THEN 1 ELSE 0 END) as vitorias, " +
           "SUM(CASE WHEN p.resultadoCasa = p.resultadoVisitante AND (p.clubeCasaId = :clubeId OR p.clubeVisitanteId = :clubeId) THEN 1 ELSE 0 END) as empates, " +
           "SUM(CASE WHEN (p.clubeCasaId = :clubeId AND p.resultadoCasa < p.resultadoVisitante) OR (p.clubeVisitanteId = :clubeId AND p.resultadoVisitante < p.resultadoCasa) THEN 1 ELSE 0 END) as derrotas, " +
           "COALESCE(SUM(CASE WHEN p.clubeCasaId = :clubeId THEN p.resultadoCasa ELSE 0 END), 0) + COALESCE(SUM(CASE WHEN p.clubeVisitanteId = :clubeId THEN p.resultadoVisitante ELSE 0 END), 0) as golsFeitos, " +
           "COALESCE(SUM(CASE WHEN p.clubeCasaId = :clubeId THEN p.resultadoVisitante ELSE 0 END), 0) + COALESCE(SUM(CASE WHEN p.clubeVisitanteId = :clubeId THEN p.resultadoCasa ELSE 0 END), 0) as golsSofridos " +
           "FROM PartidaEntity p " +
           "WHERE p.clubeCasaId = :clubeId OR p.clubeVisitanteId = :clubeId")
    Object[] findEstatisticasClube(@Param("clubeId") Long clubeId);

    //Busca partidas de um clube específico (mandante ou visitante) dentro de um período
    @Query("SELECT p FROM PartidaEntity p " +
           "WHERE (p.clubeCasaId = :clubeId OR p.clubeVisitanteId = :clubeId) " +
           "AND p.dataHora BETWEEN :dataInicio AND :dataFim")
    List<PartidaEntity> buscarPartidasPorClube(
            @Param("clubeId") Long clubeId,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim
    );

    // Buscar partidas por estádio (busca parcial)
    @Query("SELECT p FROM PartidaEntity p WHERE UPPER(p.estadio) LIKE UPPER(CONCAT('%', :estadio, '%'))")
    List<PartidaEntity> buscarPartidasPorEstadio(@Param("estadio") String estadio);
    // Buscar partidas por data específica
    @Query("SELECT p FROM PartidaEntity p WHERE DATE(p.dataHora) = DATE(:data)")
    List<PartidaEntity> buscarPartidasPorData(@Param("data") LocalDateTime data);

    // Buscar partidas por resultado específico
    @Query("SELECT p FROM PartidaEntity p WHERE p.resultadoCasa = :golsCasa AND p.resultadoVisitante = :golsVisitante")
    List<PartidaEntity> buscarPartidasPorResultado(
            @Param("golsCasa") Integer golsCasa,
            @Param("golsVisitante") Integer golsVisitante
    );

    // Buscar confrontos diretos entre dois clubes
    @Query("SELECT p FROM PartidaEntity p " +
           "WHERE (p.clubeCasaId = :clube1Id AND p.clubeVisitanteId = :clube2Id) " +
           "OR (p.clubeCasaId = :clube2Id AND p.clubeVisitanteId = :clube1Id) " +
           "ORDER BY p.dataHora DESC")
    List<PartidaEntity> findConfrontosDiretos(
        @Param("clube1Id") Long clube1Id,
        @Param("clube2Id") Long clube2Id
    );

    // Buscar partidas por clube (mandante ou visitante)
    @Query("SELECT p FROM PartidaEntity p WHERE p.clubeCasaId = :clubeId OR p.clubeVisitanteId = :clubeId")
    List<PartidaEntity> findByClubeMandanteIdOrClubeVisitanteId(@Param("clubeId") Long clubeId);
    
    // Buscar partidas entre duas datas
    @Query("SELECT p FROM PartidaEntity p WHERE p.dataHora BETWEEN :dataInicio AND :dataFim")
    List<PartidaEntity> buscarPartidasEntreDatas(
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim
    );
    // Busca paginada de partidas com filtros opcionais
    @Query(value = "SELECT p FROM PartidaEntity p WHERE " +
           "(:estadio IS NULL OR UPPER(p.estadio) LIKE UPPER(CONCAT('%', :estadio, '%'))) AND " +
           "(:golsCasa IS NULL OR p.resultadoCasa = :golsCasa) AND " +
           "(:golsVisitante IS NULL OR p.resultadoVisitante = :golsVisitante) AND " +
           "(:dataHora IS NULL OR p.dataHora = :dataHora) AND " +
           "(COALESCE(:apenasGoleadas, false) = false OR " +
           "(p.resultadoCasa - p.resultadoVisitante) >= 3 OR " +
           "(p.resultadoVisitante - p.resultadoCasa) >= 3) AND " +
           "(:clubeId IS NULL OR p.clubeCasaId = :clubeId OR p.clubeVisitanteId = :clubeId) AND " +
           "(COALESCE(:clubeCasa, false) = false OR p.clubeCasaId = :clubeId) AND " +
           "(COALESCE(:clubeVisitante, false) = false OR p.clubeVisitanteId = :clubeId)")
    Page<PartidaEntity> findPartidasComFiltros(
        @Param("estadio") String estadio,
        @Param("golsCasa") Integer golsCasa,
        @Param("golsVisitante") Integer golsVisitante,
        @Param("dataHora") LocalDateTime dataHora,
        @Param("apenasGoleadas") Boolean apenasGoleadas,
        @Param("clubeId") Long clubeId,
        @Param("clubeCasa") Boolean clubeCasa,
        @Param("clubeVisitante") Boolean clubeVisitante,
        Pageable pageable);




    // Verifica se já existe partida no mesmo estádio no mesmo horário (exceto a própria partida)
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN TRUE ELSE FALSE END " +
           "FROM PartidaEntity p " +
           "WHERE p.estadio = :estadio " +
           "AND p.dataHora = :dataHora " +
           "AND (:id IS NULL OR p.id != :id)")
    boolean existsByEstadioAndDataHora(@Param("estadio") String estadio, 
                                     @Param("dataHora") LocalDateTime dataHora,
                                     @Param("id") Long id);
    
    // Buscar partidas por clube com filtro de goleadas
    @Query("SELECT p FROM PartidaEntity p WHERE " +
           "((p.clubeCasaId = :clubeId AND (p.resultadoCasa - p.resultadoVisitante) >= 3) OR " +
           "(p.clubeVisitanteId = :clubeId AND (p.resultadoVisitante - p.resultadoCasa) >= 3))")
    List<PartidaEntity> findGoleadasByClube(@Param("clubeId") Long clubeId);
    
    // Buscar partidas por clube com filtro de clubeCasa/clubeVisitante
    @Query("SELECT p FROM PartidaEntity p WHERE " +
           "(:clubeId IS NULL OR p.clubeCasaId = :clubeId OR p.clubeVisitanteId = :clubeId) AND " +
           "(COALESCE(:clubeCasa, false) = false OR p.clubeCasaId = :clubeId) AND " +
           "(COALESCE(:clubeVisitante, false) = false OR p.clubeVisitanteId = :clubeId)")
    List<PartidaEntity> findPartidasByClubeComFiltros(
            @Param("clubeId") Long clubeId,
            @Param("clubeCasa") Boolean clubeCasa,
            @Param("clubeVisitante") Boolean clubeVisitante
    );

    // MÉTODOS BÁSICOS HERDADOS DO JpaRepository:
    // - save(PartidaEntity) - salvar partida
    // - findAll() - buscar todas as partidas
    // - deleteById(Long) - deletar partida por ID
    // - count() - contar partidas
    // - existsById(Long) - verificar se partida existe

}

