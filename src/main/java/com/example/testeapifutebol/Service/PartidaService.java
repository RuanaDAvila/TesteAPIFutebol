package com.example.testeapifutebol.Service;

import com.example.testeapifutebol.DTO.PartidaDTO;
import com.example.testeapifutebol.Entity.ClubeEntity;
import com.example.testeapifutebol.Entity.PartidaEntity;
import com.example.testeapifutebol.Excecao.RegraDeExcecao409;
import com.example.testeapifutebol.Excecao.RegraDeInvalidosExcecao400;
import com.example.testeapifutebol.Excecao.RegraDoNaoEncontradoExcecao404;
import com.example.testeapifutebol.Repository.ClubeRepository;
import com.example.testeapifutebol.Repository.EstadioRepository;
import com.example.testeapifutebol.Repository.PartidaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service - Lógica de negócio para operações de Partida
 * Converte DTO ↔ Entity e coordena com Repository
 */
@Service
public class PartidaService {
    
    // Injeta o Repository para acessar o banco de dados
    private final PartidaRepository partidaRepository;
    //injeta o repository do clube
    private final ClubeRepository clubeRepository;
    //injeta o repository do estadio
    private final EstadioRepository estadioRepository;


    // Construtor para injeção de dependência
    public PartidaService(PartidaRepository partidaRepository, ClubeRepository clubeRepository, EstadioRepository estadioRepository) {
        this.partidaRepository = partidaRepository;
        this.clubeRepository = clubeRepository;
        this.estadioRepository = estadioRepository;
    }

    /**
     * Salva uma nova partida no banco de dados
     * Recebe: PartidaDTO (dados do Controller/Postman)
     * Retorna: PartidaDTO (dados salvos com ID gerado)
     */
    public PartidaDTO savePartidaEntity(PartidaDTO partidaDTO) {
        //validar se 2 clubes são iguais, retorno 400
        if (partidaDTO.getClubeCasaId().equals(partidaDTO.getClubeVisitanteId())) {
            throw new RegraDeInvalidosExcecao400("Os Clubes da casa e visitantes não podem ser iguais");
        }

        //verifica se o clube da casa existe
        if (!clubeRepository.existsById(partidaDTO.getClubeVisitanteId())) {
            throw new RegraDeInvalidosExcecao400("Clube da casa não encontrado");
        }
        //verifica se o clube visitante existe
        if (!clubeRepository.existsById(partidaDTO.getClubeVisitanteId())) {
            throw new RegraDeInvalidosExcecao400("Clube visitante não encontrado");
        }
        //verifica se o estadio existe
        if (!estadioRepository.existsById(Long.valueOf(partidaDTO.getEstadio()))) {
            throw new RegraDeInvalidosExcecao400("Estádio não encontrado");
        }
        //verifica/valida gols negativos, retrna 400.
        if (partidaDTO.getResultadoCasa() < 0 || partidaDTO.getResultadoVisitante() < 0) {
            throw new RegraDeInvalidosExcecao400("O número de Gols não podem ser negativos.");
        }
        //valida se a data/hora da partida é anterior a data atual, retorna 400.
        if (partidaDTO.getDataHora().isBefore(LocalDateTime.now())) {
            throw new RegraDeInvalidosExcecao400("A data da partida não pode ser anterior a data atual.");
        }
        //validação de data da partida e data de criação do clube, retorno 409
        //busca do clube, que ta no entity
        ClubeEntity clubeCasa = clubeRepository.findById(partidaDTO.getClubeCasaId()).orElseThrow(() -> new RegraDoNaoEncontradoExcecao404("Clube da casa não encontrado"));
        ClubeEntity clubeVisitante = clubeRepository.findById(partidaDTO.getClubeVisitanteId()).orElseThrow(() -> new RegraDoNaoEncontradoExcecao404("Clube visitante não encontrado"));
        if (partidaDTO.getDataHora().isBefore(clubeCasa.getDataCriacao().atStartOfDay())) {
            throw new RegraDeExcecao409("A data da partida é anterior à data de criação do clube da casa");
        }
        if (partidaDTO.getDataHora().isBefore(clubeVisitante.getDataCriacao().atStartOfDay())) {
            throw new RegraDeExcecao409("A data da partida é anterior à data de criação do clube visitante");
        }
        //verifica status do clube da casa
        if ("N".equalsIgnoreCase(clubeCasa.getAtivo())) {
            throw new RegraDeExcecao409("Clube da casa está inativo e não pode participar da partida");
        }
        //verifica status do clube visitante
        if ("N".equalsIgnoreCase(clubeVisitante.getAtivo())) {
            throw new RegraDeExcecao409("Clube visitante está inativo e não pode participar da partida");
        }






        // Converte DTO → Entity
        PartidaEntity partidaParaSalvar = new PartidaEntity();


        
        // Copia dados do DTO para a Entity (todos os campos obrigatórios)
        partidaParaSalvar.setClubeCasaId(partidaDTO.getClubeCasaId());
        partidaParaSalvar.setClubeVisitanteId(partidaDTO.getClubeVisitanteId());
        partidaParaSalvar.setResultadoCasa(partidaDTO.getResultadoCasa());
        partidaParaSalvar.setResultadoVisitante(partidaDTO.getResultadoVisitante());
        partidaParaSalvar.setEstadio(partidaDTO.getEstadio());
        partidaParaSalvar.setDataHora(partidaDTO.getDataHora());

        // Salva no banco de dados (Repository faz a persistência)
        PartidaEntity partidaSalva = partidaRepository.save(partidaParaSalvar);

        // Converte a Entity salva de volta para DTO (para retornar ao Controller)
        PartidaDTO DTOResposta = new PartidaDTO();
        DTOResposta.setClubeCasaId(partidaSalva.getClubeCasaId());
        DTOResposta.setClubeVisitanteId(partidaSalva.getClubeVisitanteId());
        DTOResposta.setResultadoCasa(partidaSalva.getResultadoCasa());
        DTOResposta.setResultadoVisitante(partidaSalva.getResultadoVisitante());
        DTOResposta.setEstadio(partidaSalva.getEstadio());
        DTOResposta.setDataHora(partidaSalva.getDataHora());
        
        return DTOResposta; // Retorna o DTO com dados salvos (incluindo ID gerado)
    }

    /**
     * Lista todas as partidas cadastradas no banco
     * Retorna: List<PartidaDTO> (lista de partidas para o Controller)
     */
    public List<PartidaDTO> findAllPartidaEntity() {
        // Busca todas as Entities no banco
        List<PartidaEntity> partidas = partidaRepository.findAll();
        
        // Converte cada Entity para DTO usando Stream (programação funcional)
        return partidas.stream().map(partida -> {
            PartidaDTO dto = new PartidaDTO();
            dto.setClubeCasaId(partida.getClubeCasaId());
            dto.setClubeVisitanteId(partida.getClubeVisitanteId());
            dto.setResultadoCasa(partida.getResultadoCasa());
            dto.setResultadoVisitante(partida.getResultadoVisitante());
            dto.setEstadio(partida.getEstadio());
            dto.setDataHora(partida.getDataHora());
            return dto;
        }).collect(Collectors.toList()); // Coleta tudo numa List<PartidaDTO>
    }

    /**
     * Busca uma partida específica pelo ID
     * Recebe: ID da partida
     * Retorna: PartidaDTO (dados da partida) ou null (se não encontrar)
     */
    public PartidaDTO findPartidaById(Long id) {
        // Procurar a partida no banco de dados
        PartidaEntity partidaEncontrada = partidaRepository.findById(id).orElse(null);
        
        // Verificação: A partida existe?
        if (partidaEncontrada == null) {
            return null; // Retorna null = "não encontrei essa partida"
        }

        // Converter Entity para DTO
        PartidaDTO partidaParaRetornar = new PartidaDTO();
        partidaParaRetornar.setClubeCasaId(partidaEncontrada.getClubeCasaId());
        partidaParaRetornar.setClubeVisitanteId(partidaEncontrada.getClubeVisitanteId());
        partidaParaRetornar.setResultadoCasa(partidaEncontrada.getResultadoCasa());
        partidaParaRetornar.setResultadoVisitante(partidaEncontrada.getResultadoVisitante());
        partidaParaRetornar.setEstadio(partidaEncontrada.getEstadio());
        partidaParaRetornar.setDataHora(partidaEncontrada.getDataHora());

        return partidaParaRetornar; // Retorna o DTO com os dados da partida específica
    }

    /**
     * Deleta uma partida do banco de dados
     * Recebe: ID da partida
     * Retorna: true se deletou, false se não encontrou
     */
    public boolean deletePartidaEntity(Long id) {
        // Procurar a partida no banco de dados
        PartidaEntity partidaExistente = partidaRepository.findById(id).orElse(null);
        
        // Verificar se a partida existe
        if (partidaExistente == null) {
            return false; // Retorna "false" = "não consegui deletar porque não existe"
        }

        // Deletar a partida do banco de dados
        partidaRepository.deleteById(id);
        
        return true; // Retorna "true" = "consegui deletar com sucesso"
    }

    /**
     * Busca partidas de um clube específico (como mandante ou visitante) dentro de um período
     * @param clubeId ID do clube a ser pesquisado
     * @param dataInicio Data de início do período (inclusive)
     * @param dataFim Data de fim do período (inclusive)
     * @return Lista de partidas que atendem aos critérios
     */
    public List<PartidaDTO> buscarPartidasPorClube(Long clubeId, LocalDateTime dataInicio, LocalDateTime dataFim) {
        List<PartidaEntity> partidas = partidaRepository.buscarPartidasPorClube(clubeId, dataInicio, dataFim);
        return converterListaEntityParaDTO(partidas);
    }

    //Buscar partidas por estádio
    public List<PartidaDTO> buscarPartidasPorEstadio(String estadio) {
        List<PartidaEntity> partidas = partidaRepository.buscarPartidasPorEstadio(estadio);
        return converterListaEntityParaDTO(partidas);
    }

    //Buscar partidas por data específica
    public List<PartidaDTO> buscarPartidasPorData(LocalDateTime data) {
        List<PartidaEntity> partidas = partidaRepository.buscarPartidasPorData(data);
        return converterListaEntityParaDTO(partidas);
    }

    //Buscar partidas por resultado específico
    public List<PartidaDTO> buscarPartidasPorResultado(Integer golsCasa, Integer golsVisitante) {
        List<PartidaEntity> partidas = partidaRepository.buscarPartidasPorResultado(golsCasa, golsVisitante);
        return converterListaEntityParaDTO(partidas);
    }

    //Buscar partidas entre duas datas
    public List<PartidaDTO> buscarPartidasEntreDatas(LocalDateTime dataInicio, LocalDateTime dataFim) {
        List<PartidaEntity> partidas = partidaRepository.buscarPartidasEntreDatas(dataInicio, dataFim);
        return converterListaEntityParaDTO(partidas);
    }

    // Atualizar partida existente
    public PartidaDTO updatePartidaEntity(Long id, PartidaDTO partidaDTO) {
        Optional<PartidaEntity> partidaExistente = partidaRepository.findById(id);
        
        if (partidaExistente.isPresent()) {
            PartidaEntity entity = partidaExistente.get();
            entity.setClubeCasaId(partidaDTO.getClubeCasaId());
            entity.setClubeVisitanteId(partidaDTO.getClubeVisitanteId());
            entity.setDataHora(partidaDTO.getDataHora());
            entity.setEstadio(partidaDTO.getEstadio());
            entity.setResultadoCasa(partidaDTO.getResultadoCasa());
            entity.setResultadoVisitante(partidaDTO.getResultadoVisitante());
            
            PartidaEntity partidaAtualizada = partidaRepository.save(entity);
            return converterEntityParaDTO(partidaAtualizada);
        }
        
        return null; // Partida não encontrada
    }

    // Buscar partidas com filtros, paginação e ordenação
    public Page<PartidaDTO> findPartidasComFiltros(String estadio, Integer golsCasa, Integer golsVisitante, LocalDateTime dataHora, Pageable pageable) {
        Page<PartidaEntity> partidasPage = partidaRepository.findPartidasComFiltros(estadio, golsCasa, golsVisitante, dataHora, pageable);
        return partidasPage.map(this::converterEntityParaDTO);
    }

    /**
     * Converte uma lista de PartidaEntity para PartidaDTO
     * Método auxiliar para evitar repetição de código
     */
    private List<PartidaDTO> converterListaEntityParaDTO(List<PartidaEntity> partidas) {
        return partidas.stream().map(partida -> {
            PartidaDTO dto = new PartidaDTO();
            dto.setClubeCasaId(partida.getClubeCasaId());
            dto.setClubeVisitanteId(partida.getClubeVisitanteId());
            dto.setResultadoCasa(partida.getResultadoCasa());
            dto.setResultadoVisitante(partida.getResultadoVisitante());
            dto.setEstadio(partida.getEstadio());
            dto.setDataHora(partida.getDataHora());
            return dto;
        }).collect(Collectors.toList());
    }

    // Converte PartidaEntity para PartidaDTO
    private PartidaDTO converterEntityParaDTO(PartidaEntity entity) {
        PartidaDTO dto = new PartidaDTO();
        dto.setClubeCasaId(entity.getClubeCasaId());
        dto.setClubeVisitanteId(entity.getClubeVisitanteId());
        dto.setResultadoCasa(entity.getResultadoCasa());
        dto.setResultadoVisitante(entity.getResultadoVisitante());
        dto.setEstadio(entity.getEstadio());
        dto.setDataHora(entity.getDataHora());
        return dto;
    }
}
