package com.example.testeapifutebol.Service;

import com.example.testeapifutebol.DTO.EstadioDTO;
import com.example.testeapifutebol.Entity.EstadioEntity;
import com.example.testeapifutebol.Excecao.RegraDeExcecao409;
import com.example.testeapifutebol.Excecao.RegraDeInvalidosExcecao400;
import com.example.testeapifutebol.Repository.EstadioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor //Ele cria o construtor (só com os campos obrigatórios) automaticamente

public class EstadioService {
    private final EstadioRepository estadioRepository;


    // Valida os dados do estádio
    private void validarDadosEstadio(EstadioDTO estadioDTO) {
        // Valida se o nome não é nulo ou vazio
        if (estadioDTO.getName() == null || estadioDTO.getName().trim().isEmpty()) {
            throw new RegraDeInvalidosExcecao400("O nome do estádio é obrigatório");
        }
        
        // Remove espaços extras e converte para minúsculas para validação
        String nome = estadioDTO.getName().trim();
        
        // Verifica se contém apenas letras e espaços, para barrar caracteres especiais
        if (!nome.matches("^[a-zA-ZÀ-ÿ\\s]+")) {
            throw new RegraDeInvalidosExcecao400("O nome do estádio deve ter apenas letras e espaços");
        }
        
        // Remove espaços para contar apenas as letras
        String apenasLetras = nome.replaceAll("[^a-zA-ZÀ-ÿ]", "");
        
        // Valida se tem as 3 letras
        if (apenasLetras.length() < 3) {
            throw new RegraDeInvalidosExcecao400("O nome do estádio deve ter ao menos 3 letras");
        }
        
        // Atualiza o nome no DTO com o valor sem espaços extras
        estadioDTO.setName(nome);
    }

    // Cadastra novo estádio no banco de dados
    public EstadioDTO cadastrarEstadio(EstadioDTO estadioDTO) {
        // Valida os dados do estádio
        validarDadosEstadio(estadioDTO);

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
        // Valida os dados do estádio
        validarDadosEstadio(estadioDTO);
        
        // Busca estádio existente no banco
        EstadioEntity estadioCriado = estadioRepository.findById(id).orElse(null);
        if (estadioCriado == null) return null; // Se não existe, retorna null (404)
        
        // Se o nome foi alterado, verifica se já existe outro estádio com o novo nome
        if (!estadioCriado.getNome().equals(estadioDTO.getName()) && 
            estadioRepository.existsByNome(estadioDTO.getName())) {
            throw new RegraDeExcecao409("Já existe um estádio com o nome '" + estadioDTO.getName() + "'");
        }
        
        // Atualiza dados do estádio
        estadioCriado.setNome(estadioDTO.getName().trim()); // DTO.name → Entity.nome
        estadioRepository.save(estadioCriado); // Salva alterações no banco
        return estadioDTO; // Retorna DTO atualizado para o Controller

    }

    // Remove estádio completamente do banco (HARD DELETE)
    public boolean deleteEstadioEntity(Long id) {
        //Buscar o estádio no banco de dados
        EstadioEntity estadioExistente = estadioRepository.findById(id).orElse(null);

        //Verificar se o estádio existe
        if (estadioExistente == null) {
            return false; // Não encontrou - Controller retornará 404
        }
        estadioRepository.delete(estadioExistente); //HARD DELETE,Apaga completamente do banco
        return true;//Retorna sucesso,Controller retornará 204
    }

    // Busca estádio específico por ID no banco de dados
    public EstadioDTO findEstadioById(Long id) {
        // Busca estádio no banco pelo ID
        EstadioEntity estadioCriado = estadioRepository.findById(id).orElse(null);
        if (estadioCriado == null) return null; // Se não existe, retorna null (404)
        // Converte Entity para DTO, para retornar ao Controller
        EstadioDTO estadioDTO = new EstadioDTO();
        estadioDTO.setName(estadioCriado.getNome()); // Entity.nome → DTO.name
        return estadioDTO; // Retorna DTO com dados do estádio (200)
    }

    // Lista todos os estádios com paginação (page, size, sort)
    public Page<EstadioDTO> findAllEstadios(Pageable pageable) {
        // Busca estádios no banco com paginação
        Page<EstadioEntity> estadios = estadioRepository.findAll(pageable);
        // Converte cada Entity para DTO usando metodo auxiliar
        return estadios.map(this::converterEntityParaDTO);
    }

    // Lista estádios com filtros, paginação e ordenação
    public Page<EstadioDTO> findEstadiosComFiltros(String nome, Pageable pageable) {
        Page<EstadioEntity> estadios;
        
        if (nome != null && !nome.trim().isEmpty()) {
            // Busca por nome (busca parcial, case-insensitive)
            estadios = estadioRepository.findByNomeContainingIgnoreCase(nome.trim(), pageable);
        } else {
            // Se não há filtro, busca todos
            estadios = estadioRepository.findAll(pageable);
        }
        
        // Converte cada Entity para DTO
        return estadios.map(this::converterEntityParaDTO);
    }

    //metodo auxiliar para converter EstadioEntity -> EstadioDTO
    private EstadioDTO converterEntityParaDTO(EstadioEntity estadioEntity) {
        EstadioDTO estadioDTO = new EstadioDTO();
        estadioDTO.setName(estadioEntity.getNome()); // Entity.nome → DTO.name
        return estadioDTO;
    }

}
