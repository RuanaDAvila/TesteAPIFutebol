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
import java.util.stream.Collectors;

//Service - Lógica de negócio para operações de Partida
//Converte DTO ↔ Entity e coordena com Repository

@Service
public class PartidaService {
    
    // Injeta o Repository para acessar o banco de dados
    private final PartidaRepository partidaRepository;
    //injeta o repository do clube
    private final ClubeRepository clubeRepository;
    //injeta o repository do estadio
    private final EstadioRepository estadioRepository;
    private static final int INTERVALO_MINIMO_HORAS = 48;


    // Construtor para injeção de dependência
    public PartidaService(PartidaRepository partidaRepository, ClubeRepository clubeRepository, EstadioRepository estadioRepository) {
        this.partidaRepository = partidaRepository;
        this.clubeRepository = clubeRepository;
        this.estadioRepository = estadioRepository;
    }

    //Salva uma nova partida no banco de dados
    //Recebe: PartidaDTO (dados do Controller/Postman) e Retorna: PartidaDTO (dados salvos com ID gerado)
    public PartidaDTO savePartidaEntity(PartidaDTO partidaDTO) {
        // Validações iniciais
        validarDadosBasicos(partidaDTO);
        
        // Busca os clubes para validações adicionais
        ClubeEntity clubeCasa = clubeRepository.findById(partidaDTO.getClubeCasaId())
            .orElseThrow(() -> new RegraDoNaoEncontradoExcecao404("Clube da casa não encontrado"));
            
        ClubeEntity clubeVisitante = clubeRepository.findById(partidaDTO.getClubeVisitanteId())
            .orElseThrow(() -> new RegraDoNaoEncontradoExcecao404("Clube visitante não encontrado"));
            
        // Validações de negócio
        validarDataPartida(partidaDTO.getDataHora(), clubeCasa, clubeVisitante);
        validarStatusDosClubes(clubeCasa, clubeVisitante);
        validarConflitoDeHorarios(partidaDTO, null);
        
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

    //Lista todas as partidas cadastradas no banco e Retorna: List<PartidaDTO> (lista de partidas para o Controller)
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

    //Busca uma partida específica pelo ID
    public PartidaDTO findPartidaById(Long id) {
        // Busca a partida no banco de dados, lança exceção se não encontrar
        PartidaEntity partidaEncontrada = partidaRepository.findById(id)
            .orElseThrow(() -> new RegraDoNaoEncontradoExcecao404("Partida não encontrada com o ID: " + id));
        
        // Converte Entity para DTO
        PartidaDTO partidaParaRetornar = new PartidaDTO();
        partidaParaRetornar.setClubeCasaId(partidaEncontrada.getClubeCasaId());
        partidaParaRetornar.setClubeVisitanteId(partidaEncontrada.getClubeVisitanteId());
        partidaParaRetornar.setResultadoCasa(partidaEncontrada.getResultadoCasa());
        partidaParaRetornar.setResultadoVisitante(partidaEncontrada.getResultadoVisitante());
        partidaParaRetornar.setEstadio(partidaEncontrada.getEstadio());
        partidaParaRetornar.setDataHora(partidaEncontrada.getDataHora());

        return partidaParaRetornar;
    }


    public void deletePartidaEntity(Long id) {
        // Verifica se a partida existe
        if (!partidaRepository.existsById(id)) {
            throw new RegraDoNaoEncontradoExcecao404("Partida não encontrada com o ID: " + id);
        }
        
        // Deleta a partida do banco de dados
        partidaRepository.deleteById(id);
    }

    //Busca partidas de um clube específico (como mandante ou visitante) dentro de um período
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
        // Verifica se a partida existe
        PartidaEntity partidaExistente = partidaRepository.findById(id)
            .orElseThrow(() -> new RegraDoNaoEncontradoExcecao404("Partida não encontrada com o ID: " + id));
        
        // Validações iniciais
        validarDadosBasicos(partidaDTO);
        
        // Busca os clubes para validações adicionais
        ClubeEntity clubeCasa = clubeRepository.findById(partidaDTO.getClubeCasaId())
            .orElseThrow(() -> new RegraDoNaoEncontradoExcecao404("Clube da casa não encontrado"));
            
        ClubeEntity clubeVisitante = clubeRepository.findById(partidaDTO.getClubeVisitanteId())
            .orElseThrow(() -> new RegraDoNaoEncontradoExcecao404("Clube visitante não encontrado"));
        
        // Validações de negócio
        validarDataPartida(partidaDTO.getDataHora(), clubeCasa, clubeVisitante);
        validarStatusDosClubes(clubeCasa, clubeVisitante);
        validarConflitoDeHorarios(partidaDTO, id); // Passa o ID da partida atual para evitar conflito com ela mesma
        
        // Atualiza os dados da partida
        partidaExistente.setClubeCasaId(partidaDTO.getClubeCasaId());
        partidaExistente.setClubeVisitanteId(partidaDTO.getClubeVisitanteId());
        partidaExistente.setDataHora(partidaDTO.getDataHora());
        partidaExistente.setEstadio(partidaDTO.getEstadio());
        partidaExistente.setResultadoCasa(partidaDTO.getResultadoCasa());
        partidaExistente.setResultadoVisitante(partidaDTO.getResultadoVisitante());
        
        // Salva e retorna a partida atualizada
        PartidaEntity partidaAtualizada = partidaRepository.save(partidaExistente);
        return converterEntityParaDTO(partidaAtualizada);
    }

    // Buscar partidas com filtros, paginação e ordenação
    public Page<PartidaDTO> findPartidasComFiltros(String estadio, Integer golsCasa, Integer golsVisitante, 
          LocalDateTime dataHora, Boolean apenasGoleadas, Long clubeId,
          Boolean clubeCasa, Boolean clubeVisitante, Pageable pageable) {
        // Se clubeCasa ou clubeVisitante for true, clubeId é obrigatório
        if ((Boolean.TRUE.equals(clubeCasa) || Boolean.TRUE.equals(clubeVisitante)) && clubeId == null) {
            throw new RegraDeInvalidosExcecao400("É necessário informar o ID do clube para filtrar por mandante/visitante");
        }
        
        Page<PartidaEntity> partidasPage = partidaRepository.findPartidasComFiltros(
            estadio, golsCasa, golsVisitante, dataHora, 
            apenasGoleadas != null ? apenasGoleadas : false,
            clubeId,
            clubeCasa != null ? clubeCasa : false,
            clubeVisitante != null ? clubeVisitante : false,
            pageable
        );
        return partidasPage.map(this::converterEntityParaDTO);
    }
    
    // Buscar partidas de um clube específico com filtros de clubeCasa/clubeVisitante
    public List<PartidaDTO> findPartidasByClubeComFiltros(Long clubeId, Boolean clubeCasa, Boolean clubeVisitante) {
        if (clubeId == null) {
            throw new RegraDeInvalidosExcecao400("ID do clube é obrigatório");
        }
        
        List<PartidaEntity> partidas = partidaRepository.findPartidasByClubeComFiltros(
            clubeId,
            clubeCasa != null ? clubeCasa : false,
            clubeVisitante != null ? clubeVisitante : false
        );
        return converterListaEntityParaDTO(partidas);
    }
    
    // Buscar partidas com goleadas de um clube específico
    public List<PartidaDTO> findGoleadasByClube(Long clubeId) {
        if (clubeId == null) {
            throw new RegraDeInvalidosExcecao400("ID do clube é obrigatório");
        }
        
        List<PartidaEntity> partidas = partidaRepository.findGoleadasByClube(clubeId);
        return converterListaEntityParaDTO(partidas);
    }

    //Converte uma lista de PartidaEntity para PartidaDTO, metodo auxiliar para evitar repeticao de cod.
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

    //Valida os dados básicos da partida
    //@param partidaDTO DTO com os dados da partida
     //@throws RegraDeInvalidosExcecao400 Se os dados forem inválidos
    private void validarDadosBasicos(PartidaDTO partidaDTO) {
        // Valida campos obrigatórios
        if (partidaDTO.getClubeCasaId() == null) {
            throw new RegraDeInvalidosExcecao400("O ID do clube da casa é obrigatório");
        }
        
        if (partidaDTO.getClubeVisitanteId() == null) {
            throw new RegraDeInvalidosExcecao400("O ID do clube visitante é obrigatório");
        }
        
        if (partidaDTO.getEstadio() == null || partidaDTO.getEstadio().trim().isEmpty()) {
            throw new RegraDeInvalidosExcecao400("O estádio é obrigatório");
        }
        
        if (partidaDTO.getDataHora() == null) {
            throw new RegraDeInvalidosExcecao400("A data e hora da partida são obrigatórias");
        }
        
        // Valida resultados não nulos
        if (partidaDTO.getResultadoCasa() == null || partidaDTO.getResultadoVisitante() == null) {
            throw new RegraDeInvalidosExcecao400("Os resultados da partida são obrigatórios");
        }

        // Valida clubes iguais
        if (partidaDTO.getClubeCasaId().equals(partidaDTO.getClubeVisitanteId())) {
            throw new RegraDeInvalidosExcecao400("Os clubes da casa e visitante não podem ser iguais");
        }

        // Valida existência dos clubes
        if (!clubeRepository.existsById(partidaDTO.getClubeCasaId())) {
            throw new RegraDeInvalidosExcecao400("Clube da casa inexistente");
        }
        
        if (!clubeRepository.existsById(partidaDTO.getClubeVisitanteId())) {
            throw new RegraDeInvalidosExcecao400("Clube visitante inexistente");
        }

        // Valida existência do estádio
        if (!estadioRepository.existsByNome(partidaDTO.getEstadio())) {
            throw new RegraDeInvalidosExcecao400("Estádio inexistente");
        }

        // Valida gols não negativos
        if (partidaDTO.getResultadoCasa() < 0 || partidaDTO.getResultadoVisitante() < 0) {
            throw new RegraDeInvalidosExcecao400("O número de gols não pode ser negativo");
        }
    }

    //Valida a data da partida em relação à data de criação dos clubes
    private void validarDataPartida(LocalDateTime dataHora, ClubeEntity clubeCasa, ClubeEntity clubeVisitante) {
        // Valida se a data é posterior à criação dos clubes
        if (dataHora.toLocalDate().isBefore(clubeCasa.getDataCriacao())) {
            throw new RegraDeExcecao409("A data da partida é anterior à data de criação do clube da casa, (" + clubeCasa.getNome() + ")");
        }
        
        if (dataHora.toLocalDate().isBefore(clubeVisitante.getDataCriacao())) {
            throw new RegraDeExcecao409("A data da partida é anterior à data de criação do clube visitante, (" + clubeVisitante.getNome() + ")");
        }

        // Valida data deve ser futura (após validar criação dos clubes)
        if (dataHora.isBefore(LocalDateTime.now())) {
            throw new RegraDeInvalidosExcecao400("A data da partida deve ser futura");
        }
    }

    //Valida o status dos clubes (devem estar ativos)
    private void validarStatusDosClubes(ClubeEntity clubeCasa, ClubeEntity clubeVisitante) {
        if ("N".equalsIgnoreCase(clubeCasa.getAtivo())) {
            throw new RegraDeExcecao409("O clube da casa, " + clubeCasa.getNome() + ", está inativo");
        }
        
        if ("N".equalsIgnoreCase(clubeVisitante.getAtivo())) {
            throw new RegraDeExcecao409("O clube visitante, " + clubeVisitante.getNome() + ", está inativo");
        }
    }

    //Valida conflitos de horários para a partida
    private void validarConflitoDeHorarios(PartidaDTO partidaDTO, Long idPartidaAtual) {
        // Verifica se já existe partida no mesmo estádio no mesmo dia
        if (partidaRepository.existsByEstadioAndDataHora(partidaDTO.getEstadio(), partidaDTO.getDataHora(), idPartidaAtual)) {
            throw new RegraDeExcecao409("Já existe uma partida marcada para este estádio no mesmo dia");
        }

        // Verifica se algum clube já tem partida próxima (48h)
        LocalDateTime dataInicio = partidaDTO.getDataHora().minusHours(INTERVALO_MINIMO_HORAS);
        LocalDateTime dataFim = partidaDTO.getDataHora().plusHours(INTERVALO_MINIMO_HORAS);
        
        // Verifica para o clube da casa
        List<PartidaEntity> partidasProximasCasa = partidaRepository.buscarPartidasPorClube(
            partidaDTO.getClubeCasaId(), dataInicio, dataFim);
            
        // Remove a partida atual da verificação (caso seja uma atualização)
        partidasProximasCasa.removeIf(p -> p.getId().equals(idPartidaAtual));
        
        if (!partidasProximasCasa.isEmpty()) {
            throw new RegraDeExcecao409("O clube da casa já tem uma partida agendada com menos de 48h desta data");
        }
        
        // Verifica para o clube visitante
        List<PartidaEntity> partidasProximasVisitante = partidaRepository.buscarPartidasPorClube(
            partidaDTO.getClubeVisitanteId(), dataInicio, dataFim);
            
        // Remove a partida atual da verificação (caso seja uma atualização)
        partidasProximasVisitante.removeIf(p -> p.getId().equals(idPartidaAtual));
        
        if (!partidasProximasVisitante.isEmpty()) {
            throw new RegraDeExcecao409("O clube visitante já tem uma partida agendada próxima a esta data");
        }
    }
}
