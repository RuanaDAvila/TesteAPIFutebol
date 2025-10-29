package com.example.testeapifutebol.Repository;

import com.example.testeapifutebol.Entity.EstadioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EstadioRepository extends JpaRepository<EstadioEntity, Long> {

    // Método customizado: busca estádios por nome (busca parcial, case insensitive)
    @Query("SELECT e FROM EstadioEntity e WHERE UPPER(e.nome) LIKE UPPER(CONCAT('%', :nome, '%'))")
    List<EstadioEntity> buscarEstadiosPorNome(String nome);

    //metodo para ver se o estadio ja possui com o mesmo nome
    boolean existsByNome(String nome);
}
