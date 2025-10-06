package com.example.testeapifutebol.Service;

import com.example.testeapifutebol.DTO.ClubeDTO;
import com.example.testeapifutebol.Entity.ClubeEntity;
import com.example.testeapifutebol.Excecao.ClubeExistenteExcecao;
import com.example.testeapifutebol.Excecao.DadosInvalidosExcecao;
import com.example.testeapifutebol.Repository.ClubeRepository;
import jakarta.persistence.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

/**
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

    private static final Set<String> UFS_BR = Set.of
            ("AC", "AL", "AP", "AM", "BA", "CE", "DF", "ES", "GO", "MA", "MT",
            "MS", "MG", "PA", "PB", "PR", "PE", "PI", "RJ", "RN", "RS", "RO",
             "RR", "SC", "SP", "SE", "TO");

    // Injeta o Repository para acessar o banco de dados
    private final ClubeRepository clubeRepository;
    // Construtor para injeção de dependência
    public ClubeService(ClubeRepository clubeRepository) {
        this.clubeRepository = clubeRepository;
    }

    public ClubeEntity salvarClube(ClubeEntity clube) {
        //chama o metodo de verificacao no repositorio
        if (clubeRepository.existsByNomeAndEstado(clube.getNome(), clube.getEstado())) {
            //lanç a exceção customizada se o clube for duplicado
            String mensagem = String.format("Já existe um clube com o nome '%s' no estado '%s'",
                    clube.getNome(), clube.getEstado());
            throw new ClubeExistenteExcecao(mensagem);//aí consegue usar a exceção existente.
        }
        return clubeRepository.save(clube);
    }

    /**
     * Salva um novo clube no banco de dados
     * Recebe: ClubeDTO (dados do Controller/Postman)
     * Retorna: ClubeDTO (dados salvos com ID gerado)
     */
    public ClubeDTO saveClubeEntity(ClubeDTO clubeDTO){
            // Validar nome
            if (clubeDTO.getNome() == null || clubeDTO.getNome().trim().isEmpty()) {
                throw new DadosInvalidosExcecao("Nome do clube é obrigatório");
            }// Validar tamanho mínimo do nome
            if (clubeDTO.getNome().trim().length() < 2) {
                throw new DadosInvalidosExcecao("Nome do clube deve ter 2 letras");
            }
            // Validar estado
            if (clubeDTO.getEstado() == null || clubeDTO.getEstado().trim().isEmpty()) {
                throw new DadosInvalidosExcecao("Estado é obrigatório");
            }
            // Validar tamanho do estado
            if (clubeDTO.getEstado().trim().length() != 2) {
                throw new DadosInvalidosExcecao("Estado deve ter exatamente 2 letras");
            }
            // Validar se estado existe no Brasil
            if (!UFS_BR.contains(clubeDTO.getEstado().trim().toUpperCase())) {
                throw new DadosInvalidosExcecao("Estado inválido");
            }
            // Validar data de criação
            if (clubeDTO.getDatacriacao() == null) {
                throw new DadosInvalidosExcecao("Data de criação é obrigatória");
            }
            // Validar se data não é no futuro
            LocalDate dataInformada = LocalDate.parse(clubeDTO.getDatacriacao());
            if (dataInformada.isAfter(LocalDate.now())) {
                throw new DadosInvalidosExcecao("Data de criação não pode ser no futuro");
            }
            // Validar ativo
            if (clubeDTO.getAtivo() == null) {
                throw new DadosInvalidosExcecao("Ativo é obrigatório");
            }
            
            // Validar se já existe clube com mesmo nome e estado
            if (clubeRepository.existsByNomeAndEstado(clubeDTO.getNome(), clubeDTO.getEstado())) {
                throw new ClubeExistenteExcecao("Já existe um clube com o nome '" + clubeDTO.getNome() + "' no estado '" + clubeDTO.getEstado() + "'");
            }


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
        DTOResposta.setId(clubeSalvo.getId());                              // Long → Long
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
            dto.setId(clube.getId());                              // Long → Long
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
        if (clubeDTO.getNome() == null || clubeDTO.getNome().trim().isEmpty()) {
            throw new DadosInvalidosExcecao("Nome do clube é obrigatório");
        }// Validar tamanho mínimo do nome
        if (clubeDTO.getNome().trim().length() < 2) {
            throw new DadosInvalidosExcecao("Nome do clube deve ter 2 letras");
        }
        // Validar estado
        if (clubeDTO.getEstado() == null || clubeDTO.getEstado().trim().isEmpty()) {
            throw new DadosInvalidosExcecao("Estado é obrigatório");
        }
        // Validar tamanho do estado
        if (clubeDTO.getEstado().trim().length() != 2) {
            throw new DadosInvalidosExcecao("Estado deve ter exatamente 2 letras");
        }
        // Validar se estado existe no Brasil
        if (!UFS_BR.contains(clubeDTO.getEstado().trim().toUpperCase())) {
            throw new DadosInvalidosExcecao("Estado inválido");
        }
        // Validar data de criação
        if (clubeDTO.getDatacriacao() == null) {
            throw new DadosInvalidosExcecao("Data de criação é obrigatória");
        }
        // Validar se data não é no futuro
        LocalDate dataInformada = LocalDate.parse(clubeDTO.getDatacriacao());
        if (dataInformada.isAfter(LocalDate.now())) {
            throw new DadosInvalidosExcecao("Data de criação não pode ser no futuro");
        }
        // Validar ativo
        if (clubeDTO.getAtivo() == null) {
            throw new DadosInvalidosExcecao("Ativo é obrigatório");
        }

        // Atualiza os dados
        clubeExistente.setNome(clubeDTO.getNome());
        clubeExistente.setEstado(clubeDTO.getEstado());
        clubeExistente.setDatacriacao(LocalDate.parse(clubeDTO.getDatacriacao()));
        clubeExistente.setAtivo(clubeDTO.getAtivo());
        //salva e retorna
        ClubeEntity clubeAtualizado = clubeRepository.save(clubeExistente);

        ClubeDTO resposta = new ClubeDTO();
        resposta.setId(clubeAtualizado.getId());
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
     * Retorna: ClubeDTO (dados do clube) ou null (se não encontrar)
     */
    public ClubeDTO findClubeById(Long id) {
        // PROCURAR O CLUBE NO BANCO DE DADOS
        // findById(id) = "procure exatamente o clube com esse ID"
        // orElse(null) = "se não encontrar, retorne null (vazio)"
        ClubeEntity clubeEncontrado = clubeRepository.findById(id).orElse(null);
        
        // VERIFICA se clube existe
        if (clubeEncontrado == null) {
            // Se chegou aqui, significa que não encontrou o clube
            return null; // Retorna null = "não encontrei esse clube"
        }

        //CONVERTER ENTITY PARA DTO
        // Encontrou o clube! Agora precisa "traduzir" os dados
        // Entity = formato do banco de dados
        // DTO = formato para enviar ao Postman/front-end
        ClubeDTO clubeParaRetornar = new ClubeDTO();
        clubeParaRetornar.setId(clubeEncontrado.getId());                              // Long → Long
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
     * Retorna: Page<ClubeDTO> (página com lista de clubes + informações de paginação)
     */
    public Page<ClubeDTO> findClubesComFiltros(String nome, String estado, String ativo, java.time.LocalDate datacriacao, Pageable pageable) {
        // busca no banco com filtros.
        Page<ClubeEntity> clubesEncontrados = clubeRepository.findClubesComFiltros(nome, estado, ativo, datacriacao, pageable);
        // CONVERTER ENTITIES PARA DTOs
        // Converte os resultados para o formato de resposta
        Page<ClubeDTO> clubesParaRetornar = clubesEncontrados.map(clube -> {
            ClubeDTO dto = new ClubeDTO();
            dto.setId(clube.getId());
            dto.setNome(clube.getNome());
            dto.setEstado(clube.getEstado());
            dto.setDatacriacao(clube.getDatacriacao().toString());
            dto.setAtivo(clube.getAtivo());
            return dto;
        });

        return clubesParaRetornar;
    }


}