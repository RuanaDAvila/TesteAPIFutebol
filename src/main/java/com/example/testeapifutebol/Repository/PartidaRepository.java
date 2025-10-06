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

/**
 * Repository para operações com a entidade Partida
 * Extende JpaRepository que já fornece métodos básicos (save, findById, findAll, delete, etc.)
 * Para partidas, por enquanto só precisamos dos métodos básicos
 */
@Repository
public interface PartidaRepository extends JpaRepository<PartidaEntity, Long> {
    

    

    // Buscar partidas por clube, por estádio, por data, etc.
    /**
     * 1. BUSCAR PARTIDAS POR CLUBE (casa ou visitante)
     */
    @Query("SELECT p FROM PartidaEntity p WHERE p.clubeCasaId = :clubeId OR p.clubeVisitanteId = :clubeId")
    List<PartidaEntity> buscarPartidasPorClube(@Param("clubeId") Long clubeId);

    // Buscar partidas por estádio (busca parcial)
    @Query("SELECT p FROM PartidaEntity p WHERE UPPER(p.estadio) LIKE UPPER(CONCAT('%', :estadio, '%'))")
    List<PartidaEntity> buscarPartidasPorEstadio(@Param("estadio") String estadio);

    // Buscar partidas por data específica
    @Query("SELECT p FROM PartidaEntity p WHERE DATE(p.dataHora) = DATE(:data)")
    List<PartidaEntity> buscarPartidasPorData(@Param("data") LocalDateTime data);

    // Buscar partidas por resultado específico
    @Query("SELECT p FROM PartidaEntity p WHERE p.resultadoCasa = :golsCasa AND p.resultadoVisitante = :golsVisitante")
    List<PartidaEntity> buscarPartidasPorResultado(@Param("golsCasa") Integer golsCasa,
                                                   @Param("golsVisitante") Integer golsVisitante);

    // Buscar partidas entre duas datas
    @Query("SELECT p FROM PartidaEntity p WHERE p.dataHora BETWEEN :dataInicio AND :dataFim")
    List<PartidaEntity> buscarPartidasEntreDatas(@Param("dataInicio") LocalDateTime dataInicio,
                                                 @Param("dataFim") LocalDateTime dataFim);

    @Query(value = "SELECT p FROM PartidaEntity p WHERE " +
           "(:estadio IS NULL OR UPPER(p.estadio) LIKE UPPER(CONCAT('%', :estadio, '%'))) AND " +
           "(:golsCasa IS NULL OR p.resultadoCasa = :golsCasa) AND " +
           "(:golsVisitante IS NULL OR p.resultadoVisitante = :golsVisitante) AND " +
           "(:dataHora IS NULL OR p.dataHora = :dataHora)",
           countQuery = "SELECT COUNT(p) FROM PartidaEntity p WHERE " +
           "(:estadio IS NULL OR UPPER(p.estadio) LIKE UPPER(CONCAT('%', :estadio, '%'))) AND " +
           "(:golsCasa IS NULL OR p.resultadoCasa = :golsCasa) AND " +
           "(:golsVisitante IS NULL OR p.resultadoVisitante = :golsVisitante) AND " +
           "(:dataHora IS NULL OR p.dataHora = :dataHora)")
    Page<PartidaEntity> findPartidasComFiltros(
        @Param("estadio") String estadio,
        @Param("golsCasa") Integer golsCasa,
        @Param("golsVisitante") Integer golsVisitante,
        @Param("dataHora") LocalDateTime dataHora,
        Pageable pageable);

    // MÉTODOS BÁSICOS HERDADOS DO JpaRepository (GRÁTIS!):
    // - save(PartidaEntity) - salvar partida
    // - findAll() - buscar todas as partidas
    // - findById(Long) - buscar partida por ID
    // - deleteById(Long) - deletar partida por ID
    // - count() - contar partidas
    // - existsById(Long) - verificar se partida existe

}

// Buscar partidas c