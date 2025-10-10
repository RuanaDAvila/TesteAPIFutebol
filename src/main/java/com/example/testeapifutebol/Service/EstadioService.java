package com.example.testeapifutebol.Service;

import com.example.testeapifutebol.DTO.EstadioDTO;
import com.example.testeapifutebol.Entity.EstadioEntity;
import com.example.testeapifutebol.Excecao.RegraDeExcecao409;
import com.example.testeapifutebol.Repository.EstadioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor

public class EstadioService {
    private final EstadioRepository estadioRepository;


    // Cadastra novo estádio no banco de dados
    public EstadioDTO cadastrarEstadio(EstadioDTO estadioDTO) {
        System.out.println("Recebido: " + estadioDTO.getName());

        //Verificar se estádio já existe (FAZ PARTE DAS MINHAS EXCECOES)
        if (estadioRepository.existsByNome(estadioDTO.getName())) {
            throw new RegraDeExcecao409("Estádio '" + estadioDTO.getName() + "' já existe no sistema");
        }
        // Converte DTO → Entity para salvar no banco
        EstadioEntity estadioEntity = new EstadioEntity();
        estadioEntity.setNome(estadioDTO.getName()); // DTO.name → Entity.nome
        // Salva no banco e gera ID automaticamente
        EstadioEntity salvo = estadioRepository.save(estadioEntity);
        System.out.println("ID gerado: " + salvo.getId());
        return estadioDTO; // Retorna DTO para o Controller
    }

    // Atualiza estádio existente no banco de dados
    public EstadioDTO updateEstadioEntity(Long id, EstadioDTO estadioDTO) {
        // Busca estádio existente no banco
        EstadioEntity estadioCriado = estadioRepository.findById(id).orElse(null);
        if (estadioCriado == null) return null; // Se não existe, retorna null (404)
        // Atualiza dados do estádio
        estadioCriado.setNome(estadioDTO.getName()); // DTO.name → Entity.nome
        estadioRepository.save(estadioCriado); // Salva alterações no banco
        return estadioDTO; // Retorna DTO atualizado para o Controller

    }

    // Remove estádio completamente do banco (HARD DELETE)
    public boolean deleteEstadioEntity(Long id) {
        // 1. Buscar o estádio no banco de dados
        EstadioEntity estadioExistente = estadioRepository.findById(id).orElse(null);

        // 2. Verificar se o estádio existe
        if (estadioExistente == null) {
            return false; // Não encontrou - Controller retornará 404
        }

        // 3. HARD DELETE - Apagar completamente do banco
        estadioRepository.delete(estadioExistente);

        // 4. Retorna sucesso - Controller retornará 204
        return true;
    }

    // Busca estádio específico por ID no banco de dados
    public EstadioDTO findEstadioById(Long id) {
        // Busca estádio no banco pelo ID
        EstadioEntity estadioCriado = estadioRepository.findById(id).orElse(null);
        if (estadioCriado == null) return null; // Se não existe, retorna null (404)
        // Converte Entity → DTO para retornar ao Controller
        EstadioDTO estadioDTO = new EstadioDTO();
        estadioDTO.setName(estadioCriado.getNome()); // Entity.nome → DTO.name
        return estadioDTO; // Retorna DTO com dados do estádio (200)
    }

    // Lista todos os estádios com paginação (page, size, sort)
    public Page<EstadioDTO> findAllEstadios(Pageable pageable) {
        // Busca estádios no banco com paginação
        Page<EstadioEntity> estadios = estadioRepository.findAll(pageable);
        // Converte cada Entity → DTO usando método auxiliar
        return estadios.map(this::converterEntityParaDTO);
    }

    // Método auxiliar: converte EstadioEntity → EstadioDTO (reutilizável)
    private EstadioDTO converterEntityParaDTO(EstadioEntity estadioEntity) {
        EstadioDTO estadioDTO = new EstadioDTO();
        estadioDTO.setName(estadioEntity.getNome()); // Entity.nome → DTO.name
        return estadioDTO;
    }

}
