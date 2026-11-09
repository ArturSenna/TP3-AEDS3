package visao;

import controle.ControleAvaliacao;
import controle.ControlePergunta;
import controle.ControleResposta;
import controle.ControleUsuario;
import entidades.Pergunta;
import entidades.ProcessadorTermos;
import entidades.Resposta;
import entidades.ResultadoBusca;
import entidades.Usuario;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Scanner;

// busca pública de perguntas: por data (Árvore B+) e por palavras-chave (índice invertido + TF x IDF)
public class VisaoBuscarPerguntas {

    private final Scanner          scanner;
    private final ControleUsuario  controleUsuario;
    private final ControlePergunta controlePergunta;
    private final ControleResposta controleResposta;
    private final ControleAvaliacao controleAvaliacao;

    private static final DateTimeFormatter FMT =
        DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm");

    private static final int ITENS_POR_PAGINA = 5;

    private static final String CAMINHO_RECENTES = "> Início > Buscar perguntas > Perguntas mais recentes";
    private static final String CAMINHO_BUSCA    = "> Início > Buscar perguntas > Busca por palavras-chave";

    public VisaoBuscarPerguntas(Scanner scanner,
                                ControleUsuario controleUsuario,
                                ControlePergunta controlePergunta,
                                ControleResposta controleResposta,
                                ControleAvaliacao controleAvaliacao) {
        this.scanner           = scanner;
        this.controleUsuario   = controleUsuario;
        this.controlePergunta  = controlePergunta;
        this.controleResposta  = controleResposta;
        this.controleAvaliacao = controleAvaliacao;
    }

    public void exibir() {
        while (true) {
            System.out.println("\n============================");
            System.out.println("      AJUDA AÍ 1.0");
            System.out.println("============================");
            System.out.println("> Início > Buscar perguntas");
            System.out.println("(A) Perguntas mais recentes");
            System.out.println("(B) Busca por palavras-chave");
            System.out.println("(R) Retornar");
            System.out.print("Opção: ");

            String opcao = scanner.nextLine().trim().toUpperCase();

            switch (opcao) {
                case "A" -> exibirPerguntasMaisRecentes();
                case "B" -> exibirBuscaPorPalavrasChave();
                case "R" -> { return; }
                default  -> System.out.println("Opção inválida.");
            }
        }
    }

    private void exibirPerguntasMaisRecentes() {
        int pagina = 1;

        while (true) {
            try {
                int totalPerguntas = controlePergunta.totalPerguntasAtivas();
                int totalPaginas = (int) Math.ceil((double) totalPerguntas / ITENS_POR_PAGINA);
                if (totalPaginas == 0) totalPaginas = 1;

                if (pagina > totalPaginas) pagina = totalPaginas;
                if (pagina < 1) pagina = 1;

                ArrayList<Pergunta> perguntas = controlePergunta.listarMaisRecentes(pagina, ITENS_POR_PAGINA);

                System.out.println("\n============================");
                System.out.println("      AJUDA AÍ 1.0");
                System.out.println("============================");
                System.out.println(CAMINHO_RECENTES);
                System.out.printf("Página %d de %d (Total de perguntas ativas: %d)%n%n",
                                  pagina, totalPaginas, totalPerguntas);

                if (perguntas.isEmpty()) {
                    System.out.println("Nenhuma pergunta encontrada no sistema.");
                } else {
                    for (int i = 0; i < perguntas.size(); i++) {
                        Pergunta p = perguntas.get(i);
                        Usuario autor = controleUsuario.buscarPorId(p.getIdUsuario());
                        String nomeAutor = (autor != null) ? autor.getNome() : "Desconhecido";
                        String data = formatarData(p.getCriacao());

                        System.out.printf("(%d) %s%n", i + 1, p.getPergunta());
                        System.out.printf("    Criada por %s em %s%n%n", nomeAutor, data);
                    }
                }

                System.out.println("(A) Página anterior");
                System.out.println("(B) Próxima página");
                System.out.println("(R) Retornar");
                System.out.println("Ou digite o número da pergunta para abrir detalhes.");
                System.out.print("Opção: ");

                String entrada = scanner.nextLine().trim();
                String opcaoUpper = entrada.toUpperCase();

                if (opcaoUpper.equals("R")) {
                    return;
                } else if (opcaoUpper.equals("A")) {
                    if (pagina > 1) {
                        pagina--;
                    } else {
                        System.out.println("Você já está na primeira página.");
                    }
                } else if (opcaoUpper.equals("B")) {
                    if (pagina < totalPaginas) {
                        pagina++;
                    } else {
                        System.out.println("Você já está na última página.");
                    }
                } else {
                    try {
                        int num = Integer.parseInt(entrada);
                        if (num >= 1 && num <= perguntas.size()) {
                            abrirDetalhesPergunta(perguntas.get(num - 1), CAMINHO_RECENTES);
                        } else {
                            System.out.println("Número de pergunta inválido.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Opção inválida.");
                    }
                }

            } catch (Exception e) {
                System.out.println("Erro ao listar perguntas recentes: " + e.getMessage());
                return;
            }
        }
    }

    // pede os termos ao usuário; os resultados são apresentados em exibirResultadosBusca()
    private void exibirBuscaPorPalavrasChave() {
        while (true) {
            System.out.println("\n============================");
            System.out.println("      AJUDA AÍ 1.0");
            System.out.println("============================");
            System.out.println(CAMINHO_BUSCA);
            System.out.println("Digite uma ou mais palavras (ENTER vazio para retornar).");
            System.out.print("Termos de busca: ");

            String consulta = scanner.nextLine().trim();
            if (consulta.isEmpty()) return;

            // mesmo tratamento aplicado às palavras-chave: minúsculas, sem acentos, sem stop words
            LinkedHashSet<String> termos = new LinkedHashSet<>(ProcessadorTermos.extrairTermos(consulta));
            if (termos.isEmpty()) {
                System.out.println("Nenhum termo válido para busca (apenas palavras vazias ou numerais).");
                continue;
            }

            try {
                if (controlePergunta.buscarPorPalavrasChave(consulta).isEmpty()) {
                    System.out.println("Termos considerados: " + String.join(", ", termos));
                    System.out.println("Nenhuma pergunta encontrada para os termos informados.");
                    continue;
                }
            } catch (Exception e) {
                System.out.println("Erro ao buscar perguntas: " + e.getMessage());
                return;
            }

            // (N) Nova busca volta para este laço; (R) Retornar encerra a tela
            boolean novaBusca = exibirResultadosBusca(consulta, termos);
            if (!novaBusca) return;
        }
    }

    // lista paginada das perguntas encontradas, ordenadas pela relevância TF x IDF
    private boolean exibirResultadosBusca(String consulta, LinkedHashSet<String> termos) {
        int pagina = 1;

        while (true) {
            try {
                ArrayList<ResultadoBusca> resultados = controlePergunta.buscarPorPalavrasChave(consulta);
                int totalEncontradas = resultados.size();
                int totalPaginas = (int) Math.ceil((double) totalEncontradas / ITENS_POR_PAGINA);
                if (totalPaginas == 0) totalPaginas = 1;

                if (pagina > totalPaginas) pagina = totalPaginas;
                if (pagina < 1) pagina = 1;

                int inicio = (pagina - 1) * ITENS_POR_PAGINA;
                int fim = Math.min(inicio + ITENS_POR_PAGINA, totalEncontradas);
                ArrayList<ResultadoBusca> paginaAtual = new ArrayList<>(resultados.subList(inicio, fim));

                System.out.println("\n============================");
                System.out.println("      AJUDA AÍ 1.0");
                System.out.println("============================");
                System.out.println(CAMINHO_BUSCA);
                System.out.println("Busca: \"" + consulta + "\"");
                System.out.println("Termos considerados: " + String.join(", ", termos));
                System.out.printf("Página %d de %d (Total de perguntas encontradas: %d)%n%n",
                                  pagina, totalPaginas, totalEncontradas);

                if (paginaAtual.isEmpty()) {
                    System.out.println("Nenhuma pergunta encontrada para os termos informados.");
                } else {
                    for (int i = 0; i < paginaAtual.size(); i++) {
                        ResultadoBusca r = paginaAtual.get(i);
                        Pergunta p = r.getPergunta();
                        Usuario autor = controleUsuario.buscarPorId(p.getIdUsuario());
                        String nomeAutor = (autor != null) ? autor.getNome() : "Desconhecido";
                        String data = formatarData(p.getCriacao());

                        System.out.printf("(%d) %s%n", i + 1, p.getPergunta());
                        System.out.printf("    Palavras-chave: %s%n", p.getPalavrasChave());
                        System.out.printf("    Relevância (TF x IDF): %.3f%n", r.getRelevancia());
                        System.out.printf("    Criada por %s em %s%n%n", nomeAutor, data);
                    }
                }

                System.out.println("(A) Página anterior");
                System.out.println("(B) Próxima página");
                System.out.println("(N) Nova busca");
                System.out.println("(R) Retornar");
                System.out.println("Ou digite o número da pergunta para abrir detalhes.");
                System.out.print("Opção: ");

                String entrada = scanner.nextLine().trim();
                String opcaoUpper = entrada.toUpperCase();

                if (opcaoUpper.equals("R")) {
                    return false;
                } else if (opcaoUpper.equals("N")) {
                    return true;
                } else if (opcaoUpper.equals("A")) {
                    if (pagina > 1) {
                        pagina--;
                    } else {
                        System.out.println("Você já está na primeira página.");
                    }
                } else if (opcaoUpper.equals("B")) {
                    if (pagina < totalPaginas) {
                        pagina++;
                    } else {
                        System.out.println("Você já está na última página.");
                    }
                } else {
                    try {
                        int num = Integer.parseInt(entrada);
                        if (num >= 1 && num <= paginaAtual.size()) {
                            abrirDetalhesPergunta(paginaAtual.get(num - 1).getPergunta(), CAMINHO_BUSCA);
                        } else {
                            System.out.println("Número de pergunta inválido.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Opção inválida.");
                    }
                }

            } catch (Exception e) {
                System.out.println("Erro ao buscar perguntas: " + e.getMessage());
                return false;
            }
        }
    }

    // caminho = trilha de navegação da tela de origem (mais recentes ou busca por palavras-chave)
    private void abrirDetalhesPergunta(Pergunta pergunta, String caminho) {
        while (true) {
            try {
                // Recarrega pergunta para garantir dados atualizados
                Pergunta p = controlePergunta.buscarPorId(pergunta.getId());
                if (p == null || !p.isAtiva()) {
                    System.out.println("Pergunta não está mais disponível.");
                    return;
                }

                Usuario autor = controleUsuario.buscarPorId(p.getIdUsuario());
                String nomeAutor = (autor != null) ? autor.getNome() : "Desconhecido";
                String data = formatarData(p.getCriacao());

                ArrayList<Resposta> respostas = controleResposta.listarPorPergunta(p.getId());

                System.out.println("\n============================");
                System.out.println("      AJUDA AÍ 1.0");
                System.out.println("============================");
                System.out.println(caminho + " > Pergunta");
                System.out.println();
                System.out.println(p.getPergunta());
                System.out.println("Palavras-chave: " + p.getPalavrasChave());
                System.out.printf("Criada por %s em %s%n%n", nomeAutor, data);

                System.out.println("RESPOSTAS");
                System.out.println("---------");
                if (respostas.isEmpty()) {
                    System.out.println("Ainda não há respostas para esta pergunta.");
                } else {
                    for (int i = 0; i < respostas.size(); i++) {
                        Resposta r = respostas.get(i);
                        Usuario autorResp = controleUsuario.buscarPorId(r.getIdUsuario());
                        String nomeAutorResp = (autorResp != null) ? autorResp.getNome() : "Desconhecido";
                        String dataResp = formatarData(r.getCriacao());

                        System.out.printf("(%d) %s%n", i + 1, r.getResposta());
                        System.out.printf("Respondido por %s em %s%n", nomeAutorResp, dataResp);
                        System.out.printf("Nota: %d  (%d avaliações)%n%n", r.getNota(), r.getAvaliacoes());
                    }
                }

                System.out.println("(A) Responder à pergunta");
                System.out.println("(R) Retornar");
                if (!respostas.isEmpty()) {
                    System.out.println("Ou digite o número da resposta para interagir.");
                }
                System.out.print("Opção: ");

                String entrada = scanner.nextLine().trim();
                String opcaoUpper = entrada.toUpperCase();

                if (opcaoUpper.equals("R")) {
                    return;
                } else if (opcaoUpper.equals("A")) {
                    telaResponder(p, nomeAutor, data, caminho);
                } else {
                    try {
                        int num = Integer.parseInt(entrada);
                        if (num >= 1 && num <= respostas.size()) {
                            abrirDetalhesResposta(p, nomeAutor, data, respostas.get(num - 1), caminho);
                        } else {
                            System.out.println("Número de resposta inválido.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Opção inválida.");
                    }
                }

            } catch (Exception e) {
                System.out.println("Erro ao carregar detalhes da pergunta: " + e.getMessage());
                return;
            }
        }
    }

    private void telaResponder(Pergunta p, String nomeAutor, String data, String caminho) {
        System.out.println("\n============================");
        System.out.println("      AJUDA AÍ 1.0");
        System.out.println("============================");
        System.out.println(caminho + " > Pergunta > Responder");
        System.out.println();
        System.out.println(p.getPergunta());
        System.out.printf("Criada por %s em %s%n%n", nomeAutor, data);
        System.out.print("Resposta: ");
        String texto = scanner.nextLine().trim();

        if (texto.isEmpty()) {
            System.out.println("Resposta não pode ser vazia.");
            return;
        }

        try {
            int idUsuarioLogado = controleUsuario.getUsuarioAtivo().getId();
            controleResposta.criar(p.getId(), idUsuarioLogado, texto);
            System.out.println("Resposta publicada com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao publicar resposta: " + e.getMessage());
        }
    }

    private void abrirDetalhesResposta(Pergunta p, String nomeAutorPergunta,
                                       String dataPergunta, Resposta r, String caminho) {
        while (true) {
            try {
                // Recarrega resposta atualizada do arquivo
                Resposta respAtual = controleResposta.buscarPorId(r.getId());
                if (respAtual == null || !respAtual.isAtiva()) {
                    System.out.println("Resposta não está mais ativa.");
                    return;
                }

                int idUsuarioLogado = controleUsuario.getUsuarioAtivo().getId();
                boolean ehAutorDaResposta = (respAtual.getIdUsuario() == idUsuarioLogado);

                Usuario autorResp = controleUsuario.buscarPorId(respAtual.getIdUsuario());
                String nomeAutorResp = ehAutorDaResposta ? "você" : ((autorResp != null) ? autorResp.getNome() : "Desconhecido");
                String dataResp = formatarData(respAtual.getCriacao());

                System.out.println("\n============================");
                System.out.println("      AJUDA AÍ 1.0");
                System.out.println("============================");
                System.out.println(caminho + " > Pergunta > Resposta");
                System.out.println();
                System.out.println(p.getPergunta());
                System.out.printf("Criada por %s em %s%n%n", nomeAutorPergunta, dataPergunta);

                System.out.println("RESPOSTA");
                System.out.println("--------");
                System.out.println(respAtual.getResposta());
                System.out.printf("Respondido por %s em %s%n", nomeAutorResp, dataResp);
                System.out.printf("Nota: %d  (%d avaliações)%n%n", respAtual.getNota(), respAtual.getAvaliacoes());

                if (ehAutorDaResposta) {
                    System.out.println("(A) Arquivar resposta");
                    System.out.println("(R) Retornar ao menu anterior");
                    System.out.print("Opção: ");
                    String op = scanner.nextLine().trim().toUpperCase();

                    if (op.equals("A")) {
                        System.out.print("Confirma o arquivamento definitivo desta resposta? (S/N): ");
                        if (scanner.nextLine().trim().equalsIgnoreCase("S")) {
                            controleResposta.arquivar(respAtual.getId(), idUsuarioLogado, p.getIdUsuario());
                            System.out.println("Resposta arquivada com sucesso.");
                            return;
                        }
                    } else if (op.equals("R")) {
                        return;
                    } else {
                        System.out.println("Opção inválida.");
                    }
                } else {
                    boolean jaVotou = controleAvaliacao.jaAvaliou(idUsuarioLogado, respAtual.getId());
                    if (jaVotou) {
                        System.out.println("[Você já avaliou esta resposta.]");
                        System.out.println("(R) Retornar ao menu anterior");
                    } else {
                        System.out.println("(A) Concordar com a resposta");
                        System.out.println("(B) Discordar da resposta");
                        System.out.println("(R) Retornar ao menu anterior");
                    }
                    System.out.print("Opção: ");
                    String op = scanner.nextLine().trim().toUpperCase();

                    if (op.equals("R")) {
                        return;
                    } else if (!jaVotou && op.equals("A")) {
                        controleAvaliacao.avaliar(idUsuarioLogado, respAtual.getId(), true);
                        System.out.println("Avaliação positiva registrada com sucesso (+1 ponto)!");
                    } else if (!jaVotou && op.equals("B")) {
                        controleAvaliacao.avaliar(idUsuarioLogado, respAtual.getId(), false);
                        System.out.println("Avaliação negativa registrada com sucesso (-1 ponto)!");
                    } else {
                        System.out.println("Opção inválida.");
                    }
                }

            } catch (Exception e) {
                System.out.println("Erro ao processar resposta: " + e.getMessage());
                return;
            }
        }
    }

    private String formatarData(long millis) {
        return LocalDateTime
            .ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault())
            .format(FMT);
    }
}
