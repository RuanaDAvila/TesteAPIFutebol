package com.example.testeapifutebol.Service;

import com.example.testeapifutebol.DTO.ClubeDTO;
import com.example.testeapifutebol.Entity.ClubeEntity;
import com.example.testeapifutebol.Repository.ClubeRepository;
import jakarta.persistence.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDate;

/**
 * Service - Camada de lógica de negócio
 * Responsável por processar dados entre Controller e Repository
 * Converte DTO ↔ Entity e aplica regras de negócio
 */
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





}