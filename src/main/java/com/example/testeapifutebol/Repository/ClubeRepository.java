package com.example.testeapifutebol.Repository;

import com.example.testeapifutebol.DTO.ClubeDTO;
import com.example.testeapifutebol.Entity.ClubeEntity;
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


}
