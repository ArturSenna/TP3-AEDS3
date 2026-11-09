package visao;

import controle.ControlePergunta;
import controle.ControleResposta;
import controle.ControleUsuario;
import entidades.Pergunta;
import entidades.Resposta;
import entidades.Usuario;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

// gestão de perguntas do usuário com listagem de respostas recebidas e moderação
public class VisaoPerguntas {

    private final Scanner          scanner;
    private final ControleUsuario  controleUsuario;
    private final ControlePergunta controlePergunta;
    private final ControleResposta controleResposta;

    private static final DateTimeFormatter FMT =
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter FMT_EXT =
        DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm");

    public VisaoPerguntas(Scanner scanner,
                          ControleUsuario controleUsuario,
                          ControlePergunta controlePergunta,
                          ControleResposta controleResposta) {
        this.scanner          = scanner;
        this.controleUsuario  = controleUsuario;
        this.controlePergunta = controlePergunta;
        this.controleResposta = controleResposta;
    }

    public void exibir() {
        while (true) {
            int idUsuario = controleUsuario.getUsuarioAtivo().getId();
            try {
                ArrayList<Pergunta> perguntas = controlePergunta.listarPorUsuario(idUsuario);

                System.out.println("\n============================");
                System.out.println("      AJUDA AÍ 1.0");
                System.out.println("============================");
                System.out.println("> Início > Minha área > Minhas perguntas");
                System.out.println("(A) Incluir nova pergunta");
                System.out.println("(R) Retornar");
                System.out.println("Ou digite o número da pergunta para gerenciá-la (ver respostas, alterar ou arquivar).");

                System.out.println("\nMINHAS PERGUNTAS");
                System.out.println("----------------------------");

                if (perguntas.isEmpty()) {
                    System.out.println("Você ainda não possui perguntas.");
                } else {
                    for (int i = 0; i < perguntas.size(); i++) {
                        Pergunta p = perguntas.get(i);
                        String data = formatarDataSimples(p.getCriacao());

                        System.out.println("(" + (i + 1) + ")" + (p.isAtiva() ? "" : " ARQUIVADA"));
                        System.out.println(data);
                        System.out.println(p.getPergunta());
                        System.out.println("Palavras chave: " + p.getPalavrasChave());
                        System.out.println();
                    }
                }

                System.out.print("Opção: ");
                String opcao = scanner.nextLine().trim();
                String opcaoUpper = opcao.toUpperCase();

                if (opcaoUpper.equals("R")) {
                    return;
                } else if (opcaoUpper.equals("A")) {
                    incluir();
                } else {
                    try {
                        int num = Integer.parseInt(opcao);
                        if (num >= 1 && num <= perguntas.size()) {
                            gerenciarPergunta(perguntas.get(num - 1));
                        } else {
                            System.out.println("Número de pergunta inválido.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Opção inválida.");
                    }
                }

            } catch (Exception e) {
                System.out.println("Erro ao listar perguntas: " + e.getMessage());
                return;
            }
        }
    }

    private void gerenciarPergunta(Pergunta pRef) {
        while (true) {
            try {
                Pergunta p = controlePergunta.buscarPorId(pRef.getId());
                if (p == null) {
                    System.out.println("Pergunta não encontrada.");
                    return;
                }

                String data = formatarDataSimples(p.getCriacao());
                ArrayList<Resposta> respostas = controleResposta.listarPorPergunta(p.getId());

                System.out.println("\n============================");
                System.out.println("      AJUDA AÍ 1.0");
                System.out.println("============================");
                System.out.println("> Início > Minha área > Minhas perguntas > Pergunta");
                System.out.println();
                System.out.println(data + (p.isAtiva() ? "" : " [ARQUIVADA]"));
                System.out.println(p.getPergunta());
                System.out.println("Palavras chave: " + p.getPalavrasChave());
                System.out.println();

                System.out.println("RESPOSTAS");
                System.out.println("---------");
                if (respostas.isEmpty()) {
                    System.out.println("Nenhuma resposta recebida para esta pergunta ainda.");
                } else {
                    for (int i = 0; i < respostas.size(); i++) {
                        Resposta r = respostas.get(i);
                        Usuario autorResp = controleUsuario.buscarPorId(r.getIdUsuario());
                        String nomeAutorResp = (autorResp != null) ? autorResp.getNome() : "Desconhecido";
                        String dataResp = formatarDataExtensa(r.getCriacao());

                        System.out.printf("(%d) %s%n", i + 1, r.getResposta());
                        System.out.printf("Respondido por %s em %s%n", nomeAutorResp, dataResp);
                        System.out.printf("Nota: %d  (%d avaliações)%n%n", r.getNota(), r.getAvaliacoes());
                    }
                }

                if (p.isAtiva()) {
                    System.out.println("(A) Alterar dados da pergunta");
                    System.out.println("(B) Arquivar pergunta");
                }
                System.out.println("(R) Retornar");
                if (!respostas.isEmpty()) {
                    System.out.println("Ou digite o número da resposta para gerenciá-la (arquivar resposta inadequada).");
                }
                System.out.print("Opção: ");

                String op = scanner.nextLine().trim();
                String opUpper = op.toUpperCase();

                if (opUpper.equals("R")) {
                    return;
                } else if (p.isAtiva() && opUpper.equals("A")) {
                    alterar(p);
                } else if (p.isAtiva() && opUpper.equals("B")) {
                    if (arquivar(p)) return;
                } else {
                    try {
                        int num = Integer.parseInt(op);
                        if (num >= 1 && num <= respostas.size()) {
                            gerenciarRespostaRecebida(p, respostas.get(num - 1));
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

    private void gerenciarRespostaRecebida(Pergunta p, Resposta r) {
        while (true) {
            try {
                Resposta respAtual = controleResposta.buscarPorId(r.getId());
                if (respAtual == null || !respAtual.isAtiva()) {
                    System.out.println("Resposta não está mais ativa.");
                    return;
                }

                Usuario autorResp = controleUsuario.buscarPorId(respAtual.getIdUsuario());
                String nomeAutorResp = (autorResp != null) ? autorResp.getNome() : "Desconhecido";
                String dataResp = formatarDataExtensa(respAtual.getCriacao());

                System.out.println("\n============================");
                System.out.println("      AJUDA AÍ 1.0");
                System.out.println("============================");
                System.out.println("> Início > Minha área > Minhas perguntas > Pergunta > Resposta");
                System.out.println();
                System.out.println("RESPOSTA RECEBIDA");
                System.out.println("-----------------");
                System.out.println(respAtual.getResposta());
                System.out.printf("Respondido por %s em %s%n", nomeAutorResp, dataResp);
                System.out.printf("Nota: %d  (%d avaliações)%n%n", respAtual.getNota(), respAtual.getAvaliacoes());

                System.out.println("(A) Arquivar esta resposta");
                System.out.println("(R) Retornar ao menu anterior");
                System.out.print("Opção: ");

                String op = scanner.nextLine().trim().toUpperCase();
                if (op.equals("A")) {
                    System.out.print("Deseja arquivar esta resposta por considerá-la inadequada? (S/N): ");
                    if (scanner.nextLine().trim().equalsIgnoreCase("S")) {
                        int idUsuarioLogado = controleUsuario.getUsuarioAtivo().getId();
                        controleResposta.arquivar(respAtual.getId(), idUsuarioLogado, p.getIdUsuario());
                        System.out.println("Resposta arquivada com sucesso pelo autor da pergunta.");
                        return;
                    }
                } else if (op.equals("R")) {
                    return;
                } else {
                    System.out.println("Opção inválida.");
                }

            } catch (Exception e) {
                System.out.println("Erro ao gerenciar resposta: " + e.getMessage());
                return;
            }
        }
    }

    private void incluir() {
        System.out.println("\n--- NOVA PERGUNTA ---");
        System.out.print("Pergunta: ");
        String texto = scanner.nextLine().trim();
        System.out.print("Palavras-chave (separe por ;): ");
        String palavras = scanner.nextLine().trim();

        try {
            controlePergunta.criar(
                controleUsuario.getUsuarioAtivo().getId(), texto, palavras);
            System.out.println("Pergunta incluída com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao incluir pergunta: " + e.getMessage());
        }
    }

    private void alterar(Pergunta p) {
        System.out.print("Novo texto (ENTER para manter): ");
        String novoTexto = scanner.nextLine().trim();
        if (novoTexto.isEmpty()) novoTexto = p.getPergunta();

        System.out.print("Novas palavras-chave (ENTER para manter): ");
        String novasPalavras = scanner.nextLine().trim();
        if (novasPalavras.isEmpty()) novasPalavras = p.getPalavrasChave();

        try {
            boolean ok = controlePergunta.alterar(p.getId(), novoTexto, novasPalavras);
            System.out.println(ok ? "Pergunta alterada com sucesso." : "Falha ao alterar.");
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private boolean arquivar(Pergunta p) {
        System.out.print("Confirma arquivamento definitivo da pergunta? (S/N): ");
        if (!scanner.nextLine().trim().equalsIgnoreCase("S")) return false;

        try {
            int idUsuario = controleUsuario.getUsuarioAtivo().getId();
            boolean ok = controlePergunta.arquivar(p.getId(), idUsuario);
            System.out.println(ok ? "Pergunta arquivada." : "Falha ao arquivar.");
            return ok;
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
            return false;
        }
    }

    private String formatarDataSimples(long millis) {
        return LocalDateTime
            .ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault())
            .format(FMT);
    }

    private String formatarDataExtensa(long millis) {
        return LocalDateTime
            .ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault())
            .format(FMT_EXT);
    }
}
