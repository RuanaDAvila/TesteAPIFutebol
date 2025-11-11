package com.example.testeapifutebol.Service;

import com.example.testeapifutebol.DTO.ClubeDTO;
import com.example.testeapifutebol.Entity.ClubeEntity;
import com.example.testeapifutebol.Excecao.RegraDeExcecao409;
import com.example.testeapifutebol.Excecao.RegraDeInvalidosExcecao400;
import com.example.testeapifutebol.Excecao.RegraDoNaoEncontradoExcecao404;
import com.example.testeapifutebol.Repository.ClubeRepository;
import com.example.testeapifutebol.Repository.PartidaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.example.testeapifutebol.DTO.RankingClubeDTO;
import com.example.testeapifutebol.DTO.RetrospectoClubeDTO;
import com.example.testeapifutebol.Entity.PartidaEntity;
import java.util.Optional;
import java.util.HashMap;
import java.util.Map;
import java.util.Comparator;
import java.util.ArrayList;
import com.example.testeapifutebol.DTO.RetrospectoAdversarioDTO;
import com.example.testeapifutebol.DTO.ConfrontoDiretoDTO;



 //Converte DTO ↔ Entity e coordena com Repository
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
    private final PartidaRepository partidaRepository;

    @Autowired
    public ClubeService(ClubeRepository clubeRepository, PartidaRepository partidaRepository) {
        this.clubeRepository = clubeRepository;
        this.partidaRepository = partidaRepository;
    }
    // Salva um clube no banco de dados
    public ClubeEntity salvarClube(ClubeEntity clube) {
        // Validar nome nulo
        if (clube.getNome() == null || clube.getNome().trim().isEmpty()) {
            throw new RegraDeInvalidosExcecao400("Nome é obrigatório");
        }

        // Validar tamanho mínimo do nome
        if (clube.getNome().trim().length() < 2) {
            throw new RegraDeInvalidosExcecao400("Nome deve ter pelo menos 2 caracteres");
        }

        // Validar estado
        if (clube.getEstado() == null || clube.getEstado().trim().isEmpty()) {
            throw new RegraDeInvalidosExcecao400("Estado é obrigatório");
        }

        // Validar se estado existe no Brasil
        if (!UFS_BR.contains(clube.getEstado().trim().toUpperCase())) {
            throw new RegraDeInvalidosExcecao400("Estado inválido: " + clube.getEstado());
        }

        // Validar data de criação
        if (clube.getDataCriacao() == null) {
            throw new RegraDeInvalidosExcecao400("Data de criação é obrigatória");
        }

        // Validar se data não é no futuro
        if (clube.getDataCriacao().isAfter(LocalDate.now())) {
            throw new RegraDeInvalidosExcecao400("Data de criação não pode ser futura");
        }

        // Depois das validações, verificar duplicidade
        if (clubeRepository.existsByNomeAndEstado(clube.getNome(), clube.getEstado())) {
        }
        //chama o metodo de verificacao no repository
        if (clubeRepository.existsByNomeAndEstado(clube.getNome(), clube.getEstado())) {

            //lança exceção customizada se o clube for duplicado
            String mensagem = String.format("Já existe um clube com o nome '%s' no estado '%s'",
                    clube.getNome(), clube.getEstado());
            throw new RegraDeExcecao409(mensagem);//aí consegue usar a exceção existente.
        }
        return clubeRepository.save(clube);
    }


    //Salva um novo clube no banco de dados, Recebe: ClubeDTO (dados do Controller/Postman) e Retorna: ClubeDTO (dados salvos com ID gerado)
    public ClubeDTO saveClubeEntity(ClubeDTO clubeDTO){
            // Validar nome
            if (clubeDTO.getNome() == null || clubeDTO.getNome().trim().isEmpty()) {
                throw new RegraDeInvalidosExcecao400("Nome do clube é obrigatório");
            }// Validar tamanho mínimo do nome
            if (clubeDTO.getNome().trim().length() < 2) {
                throw new RegraDeInvalidosExcecao400("Nome do clube deve ter 2 letras");
            }
            // Validar estado
            if (clubeDTO.getEstado() == null || clubeDTO.getEstado().trim().isEmpty()) {
                throw new RegraDeInvalidosExcecao400("Estado é obrigatório");
            }
            // Validar tamanho do estado
            if (clubeDTO.getEstado().trim().length() != 2) {
                throw new RegraDeInvalidosExcecao400("Estado deve ter exatamente 2 letras");
            }
            // Validar se estado existe no Brasil
            if (!UFS_BR.contains(clubeDTO.getEstado().trim().toUpperCase())) {
                throw new RegraDeInvalidosExcecao400("Estado inválido");
            }
            // Validar data de criação
            if (clubeDTO.getDatacriacao() == null) {
                throw new RegraDeInvalidosExcecao400("Data de criação é obrigatória");
            }
            // Validar se data não é no futuro
            LocalDate dataInformada = LocalDate.parse(clubeDTO.getDatacriacao());
            if (dataInformada.isAfter(LocalDate.now())) {
                throw new RegraDeInvalidosExcecao400("Data de criação não pode ser no futuro");
            }
            // Validar ativo
            if (clubeDTO.getAtivo() == null) {
                throw new RegraDeInvalidosExcecao400("Ativo é obrigatório");
            }
            // Validar se já existe clube com mesmo nome e estado
            if (clubeRepository.existsByNomeAndEstado(clubeDTO.getNome(), clubeDTO.getEstado())) {
                throw new RegraDeExcecao409("Já existe um clube com o nome '" + clubeDTO.getNome() + "' no estado '" + clubeDTO.getEstado() + "'");
            }

            //validar edicao da data do clube posterior a uma data de partida ja registrada "data invalida"
            //if (depois que eu criar a logica das partidas registradas venho e coloco aqui) {
              //  throw new RegraDeExcecao409("A data da criação do clube nao pode ser após a data da última partida  registrada");
           // }



        //Converte DTO para Entity
        ClubeEntity clubeParaSalvar = new ClubeEntity();
        //Copia dados do DTO para a Entity (todos os campos obrigatórios)
        clubeParaSalvar.setNome(clubeDTO.getNome());                                    // String → String
        clubeParaSalvar.setEstado(clubeDTO.getEstado());                                // String → String
        clubeParaSalvar.setDataCriacao(LocalDate.parse(clubeDTO.getDatacriacao()));    // String → LocalDate
        clubeParaSalvar.setAtivo(clubeDTO.getAtivo());                                  // String → String

        //Salva no banco de dados (Repository faz a persistência)
        ClubeEntity clubeSalvo = clubeRepository.save(clubeParaSalvar);
        //Converte a Entity salva de volta para DTO (para retornar ao Controller)
        ClubeDTO DTOResposta = new ClubeDTO();
        DTOResposta.setId(clubeSalvo.getId());                              // Long → Long
        DTOResposta.setNome(clubeSalvo.getNome());                          // String → String
        DTOResposta.setEstado(clubeSalvo.getEstado());                      // String → String
        DTOResposta.setDatacriacao(clubeSalvo.getDataCriacao().toString()); // LocalDate → String
        DTOResposta.setAtivo(clubeSalvo.getAtivo());                        // String → String

        return DTOResposta; // Retorna o DTO com dados salvos (incluindo ID gerado)


    }



    //Lista todos os clubes cadastrados no banco
     //Retorna: List<ClubeDTO> (lista de clubes para o Controller)
    public List<ClubeDTO> findAllClubeEntity() {
        //Busca todas as Entities no banco
        List<ClubeEntity> clubes = clubeRepository.findAll();
        
        //Converte cada Entity para DTO usando Stream (programação funcional)
        return clubes.stream().map(clube -> {
            ClubeDTO dto = new ClubeDTO();
            dto.setId(clube.getId());                              // Long → Long
            dto.setNome(clube.getNome());                          // String → String
            dto.setEstado(clube.getEstado());                      // String → String  
            dto.setDatacriacao(clube.getDataCriacao().toString()); // LocalDate → String
            dto.setAtivo(clube.getAtivo());                        // String → String
            return dto;
        }).collect(Collectors.toList()); // Coleta tudo numa List<ClubeDTO>
    }

    //Atualiza um clube existente no banco de dados, Recebe: ID do clube + ClubeDTO com novos dados
    public ClubeDTO updateClubeEntity(Long id, ClubeDTO clubeDTO) {
        // Busca o clube existente
        ClubeEntity clubeExistente = clubeRepository.findById(id)
                .orElseThrow(() -> new RegraDoNaoEncontradoExcecao404("Clube com ID "+id+" não foi encontrado"));

        // Validações iniciais
        if (clubeDTO.getNome() == null || clubeDTO.getNome().trim().isEmpty()) {
            throw new RegraDeInvalidosExcecao400("Nome do clube é obrigatório");
        }

        if (clubeDTO.getNome().trim().length() < 2) {
            throw new RegraDeInvalidosExcecao400("Nome do clube deve ter no mínimo 2 letras");
        }

        if (clubeDTO.getEstado() == null || clubeDTO.getEstado().trim().isEmpty()) {
            throw new RegraDeInvalidosExcecao400("Estado é obrigatório");
        }

        if (clubeDTO.getEstado().trim().length() != 2) {
            throw new RegraDeInvalidosExcecao400("Estado deve ter exatamente 2 letras");
        }

        if (!UFS_BR.contains(clubeDTO.getEstado().trim().toUpperCase())) {
            throw new RegraDeInvalidosExcecao400("Estado inválido");
        }

        if (clubeDTO.getDatacriacao() == null) {
            throw new RegraDeInvalidosExcecao400("Data de criação é obrigatória");
        }

        LocalDate dataInformada = LocalDate.parse(clubeDTO.getDatacriacao());
        if (dataInformada.isAfter(LocalDate.now())) {
            throw new RegraDeInvalidosExcecao400("Data de criação não pode ser no futuro");
        }

        if (clubeDTO.getAtivo() == null) {
            throw new RegraDeInvalidosExcecao400("Ativo é obrigatório");
        }

        // Verifica se já existe outro clube com o mesmo nome e estado (exceto o próprio clube)
        if (clubeRepository.existsByNomeAndEstadoAndIdNot(
                clubeDTO.getNome(),
                clubeDTO.getEstado(),
                id)) {
            throw new RegraDeExcecao409("Já existe um clube com o nome '" +
                    clubeDTO.getNome() + "' no estado '" + clubeDTO.getEstado() + "'");
        }

        // Atualiza os dados
        clubeExistente.setNome(clubeDTO.getNome());
        clubeExistente.setEstado(clubeDTO.getEstado());
        clubeExistente.setDataCriacao(LocalDate.parse(clubeDTO.getDatacriacao()));
        clubeExistente.setAtivo(clubeDTO.getAtivo());

        // Salva as alterações
        ClubeEntity clubeAtualizado = clubeRepository.save(clubeExistente);

        // Converte para DTO e retorna
        ClubeDTO resposta = new ClubeDTO();
        resposta.setId(clubeAtualizado.getId());
        resposta.setNome(clubeAtualizado.getNome());
        resposta.setEstado(clubeAtualizado.getEstado());
        resposta.setDatacriacao(clubeAtualizado.getDataCriacao().toString());
        resposta.setAtivo(clubeAtualizado.getAtivo());

        return resposta;
    }


     //Inativa um clube (soft delete), Muda status de "S" para "N" sem deletar do banco
    public boolean inativarClubeEntity(Long id) {
        ClubeEntity clubeExistente = clubeRepository.findById(id).orElseThrow(() -> new RegraDoNaoEncontradoExcecao404("Clube com ID "+id+" não foi encontrado"));

        //MARCAR COMO INATIVO (SOFT DELETE),Ao invés de apagar, só mudo o status de "S" (ativo) para "N" (inativo)
        clubeExistente.setAtivo("N");

        //SALVAR A ALTERAÇÃO NO BANCO DE DADOS, O Spring pega o objeto modificado e atualiza no MySQL
        clubeRepository.save(clubeExistente);
        return true; // Retorna "true" = "consegui inativar com sucesso"
    }



     //Procura um clube específico no banco de dados usando o ID
    public ClubeDTO findClubeById(Long id) {
        // Procura O CLUBE NO BANCO DE DADOS E lança 404 se ñ existir
        ClubeEntity clubeEncontrado = clubeRepository.findById(id).orElseThrow(() -> new RegraDoNaoEncontradoExcecao404("Clube com ID "+id+" não foi encontrado"));

        //CONVERTER ENTITY PARA DTO, Entity = formato do banco de dados e DTO = formato para enviar ao Postman/front-end
        ClubeDTO clubeParaRetornar = new ClubeDTO();
        clubeParaRetornar.setId(clubeEncontrado.getId());                              // Long → Long
        clubeParaRetornar.setNome(clubeEncontrado.getNome());                          // String → String
        clubeParaRetornar.setEstado(clubeEncontrado.getEstado());                      // String → String
        clubeParaRetornar.setDatacriacao(clubeEncontrado.getDataCriacao().toString()); // LocalDate → String
        clubeParaRetornar.setAtivo(clubeEncontrado.getAtivo());                        // String → String

        //RETORNAR O CLUBE ENCONTRADO
        return clubeParaRetornar; // Retorna o DTO com os dados do clube específico
    }

    /**
     * MÉTODO PARA LISTAR CLUBES COM FILTROS, PAGINAÇÃO E ORDENAÇÃO
     * - Lista clubes com filtros opcionais (nome, estado, situação)
     * - Permite paginação (dividir resultados em páginas)
     * - Permite ordenação (ascendente/descendente por qualquer campo)
     */
    public Page<ClubeDTO> findClubesComFiltros(String nome, String estado, String ativo, java.time.LocalDate datacriacao, Pageable pageable) {
        //TRATAMENTO DA STRING VAZIA COMO NULL
        if (nome != null && nome.trim().isEmpty()) {
            nome = null;
        }
        if (estado != null && estado.trim().isEmpty()) {
            estado = null;
        }
        if (ativo != null && ativo.trim().isEmpty()) {
            ativo = null;
        }
        // busca no banco com filtros.
        Page<ClubeEntity> clubesEncontrados = clubeRepository.findClubesComFiltros(nome, estado, ativo, datacriacao, pageable);
        // CONVERTER ENTITIES PARA DTOs, Converte os resultados para o formato de resposta
        Page<ClubeDTO> clubesParaRetornar = clubesEncontrados.map(clube -> {
            ClubeDTO dto = new ClubeDTO();
            dto.setId(clube.getId());
            dto.setNome(clube.getNome());
            dto.setEstado(clube.getEstado());
            dto.setDatacriacao(clube.getDataCriacao().toString());
            dto.setAtivo(clube.getAtivo());
            return dto;
        });
        return clubesParaRetornar;
    }

    /**
     * Busca o retrospecto completo de um clube
     * @param clubeId ID do clube
     * @return DTO com as estatísticas do retrospecto
     * @throws RegraDoNaoEncontradoExcecao404 se o clube não for encontrado
     */
    public RetrospectoClubeDTO buscarRetrospectoClube(Long clubeId) {
        // Verifica se o clube existe
        ClubeEntity clube = clubeRepository.findById(clubeId)
            .orElseThrow(() -> new RegraDoNaoEncontradoExcecao404("Clube não encontrado com o ID: " + clubeId));
        
        // Busca todas as partidas do clube (como mandante ou visitante)
        List<PartidaEntity> partidas = partidaRepository.findByClubeMandanteIdOrClubeVisitanteId(clubeId);
        
        // Cria o DTO de retorno
        RetrospectoClubeDTO retrospecto = new RetrospectoClubeDTO();
        retrospecto.setClubeId(clube.getId());
        retrospecto.setClubeNome(clube.getNome());
        
        // Calcula as estatísticas
        for (PartidaEntity partida : partidas) {
            boolean isMandante = partida.getClubeCasaId().equals(clubeId);
            int golsFeitos = isMandante ? partida.getResultadoCasa() : partida.getResultadoVisitante();
            int golsSofridos = isMandante ? partida.getResultadoVisitante() : partida.getResultadoCasa();
            
            retrospecto.setGolsFeitos(retrospecto.getGolsFeitos() + golsFeitos);
            retrospecto.setGolsSofridos(retrospecto.getGolsSofridos() + golsSofridos);
            
            if (golsFeitos > golsSofridos) {
                retrospecto.setVitorias(retrospecto.getVitorias() + 1);
            } else if (golsFeitos == golsSofridos) {
                retrospecto.setEmpates(retrospecto.getEmpates() + 1);
            } else {
                retrospecto.setDerrotas(retrospecto.getDerrotas() + 1);
            }
            
            retrospecto.setTotalJogos(retrospecto.getTotalJogos() + 1);
        }
        
        retrospecto.setSaldoGols(retrospecto.getGolsFeitos() - retrospecto.getGolsSofridos());
        
        return retrospecto;
    }

    /**
     * Busca o retrospecto de um clube contra todos os seus adversários
     * @param clubeId ID do clube para buscar o retrospecto
     * @return Lista de DTOs com as estatísticas contra cada adversário
     * @throws RegraDoNaoEncontradoExcecao404 se o clube não for encontrado
     */
    public List<RetrospectoAdversarioDTO> buscarRetrospectoContraAdversarios(Long clubeId) {
        // Verifica se o clube existe
        ClubeEntity clube = clubeRepository.findById(clubeId)
            .orElseThrow(() -> new RegraDoNaoEncontradoExcecao404("Clube não encontrado com o ID: " + clubeId));
        
        // Busca todas as partidas do clube
        List<PartidaEntity> partidas = partidaRepository.findByClubeMandanteIdOrClubeVisitanteId(clubeId);
        
        // Mapa para armazenar o retrospecto contra cada adversário
        Map<Long, RetrospectoAdversarioDTO> retrospectoPorAdversario = new HashMap<>();
        
        // Processa cada partida
        for (PartidaEntity partida : partidas) {
            // Determina o adversário
            Long adversarioId = partida.getClubeCasaId().equals(clubeId) ? 
                               partida.getClubeVisitanteId() : partida.getClubeCasaId();
            
            // Obtém ou cria o retrospecto para este adversário
            RetrospectoAdversarioDTO retrospecto = retrospectoPorAdversario.computeIfAbsent(
                adversarioId, 
                id -> {
                    RetrospectoAdversarioDTO novo = new RetrospectoAdversarioDTO();
                    novo.setAdversarioId(id);
                    // Busca o nome do adversário
                    String nomeAdversario = clubeRepository.findById(id)
                        .map(ClubeEntity::getNome)
                        .orElse("Clube Desconhecido");
                    novo.setAdversarioNome(nomeAdversario);
                    return novo;
                }
            );
            
            // Atualiza as estatísticas
            boolean isMandante = partida.getClubeCasaId().equals(clubeId);
            int golsFeitos = isMandante ? partida.getResultadoCasa() : partida.getResultadoVisitante();
            int golsSofridos = isMandante ? partida.getResultadoVisitante() : partida.getResultadoCasa();
            
            retrospecto.setTotalJogos(retrospecto.getTotalJogos() + 1);
            retrospecto.setGolsFeitos(retrospecto.getGolsFeitos() + golsFeitos);
            retrospecto.setGolsSofridos(retrospecto.getGolsSofridos() + golsSofridos);
            retrospecto.setSaldoGols(retrospecto.getGolsFeitos() - retrospecto.getGolsSofridos());
            
            if (golsFeitos > golsSofridos) {
                retrospecto.setVitorias(retrospecto.getVitorias() + 1);
            } else if (golsFeitos == golsSofridos) {
                retrospecto.setEmpates(retrospecto.getEmpates() + 1);
            } else {
                retrospecto.setDerrotas(retrospecto.getDerrotas() + 1);
            }
        }
        
        // Retorna a lista de retrospectos ordenada pelo nome do adversário
        return new ArrayList<>(retrospectoPorAdversario.values()).stream()
            .sorted(Comparator.comparing(RetrospectoAdversarioDTO::getAdversarioNome))
            .collect(Collectors.toList());
    }
    
     //Busca o histórico de confrontos diretos entre dois clubes
     //@param clube1Id ID do primeiro clube
     //@param clube2Id ID do segundo clube
     //@return DTO com estatísticas e lista de partidas
     //@throws RegraDoNaoEncontradoExcecao404 se algum dos clubes não for encontrado
    public ConfrontoDiretoDTO buscarConfrontoDireto(Long clube1Id, Long clube2Id) {
        // Verifica se os clubes existem
        ClubeEntity clube1 = clubeRepository.findById(clube1Id)
            .orElseThrow(() -> new RegraDoNaoEncontradoExcecao404("Clube não encontrado com o ID: " + clube1Id));
        
        ClubeEntity clube2 = clubeRepository.findById(clube2Id)
            .orElseThrow(() -> new RegraDoNaoEncontradoExcecao404("Clube não encontrado com o ID: " + clube2Id));

        // Busca todas as partidas entre os dois clubes
        List<PartidaEntity> partidas = partidaRepository.findConfrontosDiretos(clube1Id, clube2Id);

        // Cria o DTO de resposta
        ConfrontoDiretoDTO confronto = new ConfrontoDiretoDTO();
        confronto.setClube1Id(clube1.getId());
        confronto.setClube1Nome(clube1.getNome());
        confronto.setClube2Id(clube2.getId());
        confronto.setClube2Nome(clube2.getNome());
        confronto.setPartidas(partidas);

        // Calcula as estatísticas
        for (PartidaEntity partida : partidas) {
            boolean clube1Mandante = partida.getClubeCasaId().equals(clube1Id);
            
            int golsClube1 = clube1Mandante ? partida.getResultadoCasa() : partida.getResultadoVisitante();
            int golsClube2 = clube1Mandante ? partida.getResultadoVisitante() : partida.getResultadoCasa();
            
            confronto.setGolsClube1(confronto.getGolsClube1() + golsClube1);
            confronto.setGolsClube2(confronto.getGolsClube2() + golsClube2);
            
            if (golsClube1 > golsClube2) {
                confronto.setVitoriasClube1(confronto.getVitoriasClube1() + 1);
            } else if (golsClube2 > golsClube1) {
                confronto.setVitoriasClube2(confronto.getVitoriasClube2() + 1);
            } else {
                confronto.setEmpates(confronto.getEmpates() + 1);
            }
        }
        
        confronto.setTotalJogos(partidas.size());
        
        return confronto;
    }
    
     //Busca o ranking de clubes de acordo com o critério especificado
     //@param tipo Tipo de ranking: 'pontos', 'gols', 'vitorias' ou 'jogos'
     //@return Lista de DTOs com as estatísticas dos clubes ordenadas
    public List<RankingClubeDTO> buscarRanking(String tipo) {
        List<Object[]> estatisticas = partidaRepository.findEstatisticasClubes();
        List<RankingClubeDTO> ranking = new ArrayList<>();
        
        // Converter os resultados da consulta para DTOs
        for (Object[] estatistica : estatisticas) {
            Long clubeId = (Long) estatistica[0];
            String clubeNome = (String) estatistica[1];
            Long totalJogos = (Long) estatistica[2];
            Long vitorias = (Long) estatistica[3];
            Long empates = (Long) estatistica[4];
            Long derrotas = (Long) estatistica[5];
            Long golsFeitos = estatistica[6] != null ? ((Number) estatistica[6]).longValue() : 0L;
            Long golsSofridos = estatistica[7] != null ? ((Number) estatistica[7]).longValue() : 0L;
            
            int pontos = (vitorias.intValue() * 3) + empates.intValue();
            int saldoGols = golsFeitos.intValue() - golsSofridos.intValue();
            
            RankingClubeDTO dto = new RankingClubeDTO();
            dto.setId(clubeId);
            dto.setNome(clubeNome);
            dto.setJogos(totalJogos.intValue());
            dto.setVitorias(vitorias.intValue());
            dto.setEmpates(empates.intValue());
            dto.setDerrotas(derrotas.intValue());
            dto.setGolsFeitos(golsFeitos.intValue());
            dto.setGolsSofridos(golsSofridos.intValue());
            dto.setSaldoGols(saldoGols);
            dto.setPontos(pontos);
            
            // Apenas adiciona se tiver jogos
            if (totalJogos > 0) {
                ranking.add(dto);
            }
        }
        
        // Ordenar de acordo com o tipo de ranking, basicamente vai organizar de acordo com pontos, gols, vitorias e gols
        switch (tipo.toLowerCase()) {
            case "pontos":
                ranking.sort((a, b) -> {
                    if (a.getPontos() != b.getPontos()) {
                        return Integer.compare(b.getPontos(), a.getPontos());
                    } else if (a.getVitorias() != b.getVitorias()) {
                        return Integer.compare(b.getVitorias(), a.getVitorias());
                    } else if (a.getSaldoGols() != b.getSaldoGols()) {
                        return Integer.compare(b.getSaldoGols(), a.getSaldoGols());
                    } else if (a.getGolsFeitos() != b.getGolsFeitos()) {
                        return Integer.compare(b.getGolsFeitos(), a.getGolsFeitos());
                    } else {
                        return a.getNome().compareTo(b.getNome());
                    }
                });
                break;
                
            case "gols":
                ranking.sort((a, b) -> {
                    if (a.getGolsFeitos() != b.getGolsFeitos()) {
                        return Integer.compare(b.getGolsFeitos(), a.getGolsFeitos());
                    }
                    return a.getNome().compareTo(b.getNome());
                });
                break;
                
            case "vitorias":
                ranking.sort((a, b) -> {
                    if (a.getVitorias() != b.getVitorias()) {
                        return Integer.compare(b.getVitorias(), a.getVitorias());
                    } else if (a.getPontos() != b.getPontos()) {
                        return Integer.compare(b.getPontos(), a.getPontos());
                    } else if (a.getSaldoGols() != b.getSaldoGols()) {
                        return Integer.compare(b.getSaldoGols(), a.getSaldoGols());
                    } else {
                        return a.getNome().compareTo(b.getNome());
                    }
                });
                break;
                
            case "jogos":
                ranking.sort((a, b) -> {
                    if (a.getJogos() != b.getJogos()) {
                        return Integer.compare(b.getJogos(), a.getJogos());
                    } else {
                        return a.getNome().compareTo(b.getNome());
                    }
                });
                break;
                
            default:
                throw new IllegalArgumentException("Tipo de ranking inválido. Use: pontos, gols, vitorias ou jogos");
        }
        
        // Atribuir posições
        for (int i = 0; i < ranking.size(); i++) {
            ranking.get(i).setPosicao(i + 1);
        }
        
        return ranking;
    }
}