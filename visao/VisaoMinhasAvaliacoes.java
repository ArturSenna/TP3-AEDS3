package visao;

import controle.ControleAvaliacao;
import controle.ControlePergunta;
import controle.ControleResposta;
import controle.ControleUsuario;
import java.util.Scanner;
import entidades.Avaliacao;
import entidades.Pergunta;
import entidades.Resposta;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class VisaoMinhasAvaliacoes {

    private final Scanner          scanner;
    private final ControleUsuario  controleUsuario;
    private final ControlePergunta controlePergunta;
    private final ControleResposta controleResposta;
    private final ControleAvaliacao controleAvaliacao;
    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm");

    public VisaoMinhasAvaliacoes(Scanner scanner,
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
            try {
                int idUsuarioLogado = controleUsuario.getUsuarioAtivo().getId();
                ArrayList<Avaliacao> avaliacoes = controleAvaliacao.listarPorUsuario(idUsuarioLogado);

                System.out.println("\n============================");
                System.out.println("      AJUDA AÍ 1.0");
                System.out.println("============================");
                System.out.println("> Início > Minha área > Minhas avaliações");

                if (avaliacoes.isEmpty()) {
                    System.out.println("\nVocê ainda não avaliou nenhuma resposta.");
                    System.out.println("\nPressione ENTER para retornar...");
                    scanner.nextLine();
                    return;
                }

                System.out.println("\nMINHAS AVALIAÇÕES");
                System.out.println("----------------------------");
                for (int i = 0; i < avaliacoes.size(); i++) {
                    Avaliacao av = avaliacoes.get(i);
                    Resposta r = controleResposta.buscarPorId(av.getIdResposta());
                    String textoResposta = (r != null) ? r.getResposta() : "[Resposta indisponível]";
                    String tipoVoto = av.isAvaliacao() ? "Concordou (+1)" : "Discordou (-1)";

                    System.out.printf("(%d) Resposta: %s%n", i + 1, textoResposta);
                    System.out.printf("    Seu voto: %s", tipoVoto);
                    if (r != null) {
                        System.out.printf(" | Nota atual da resposta: %d (%d avaliações) | Respondida em %s",
                                r.getNota(), r.getAvaliacoes(), formatarData(r.getCriacao()));
                    }
                    System.out.println("\n");
                }

                System.out.println("(R) Retornar");
                System.out.println("Ou digite o número da avaliação para detalhes.");
                System.out.print("Opção: ");

                String op = scanner.nextLine().trim();
                if (op.equalsIgnoreCase("R")) {
                    return;
                }

                try {
                    int num = Integer.parseInt(op);
                    if (num >= 1 && num <= avaliacoes.size()) {
                        abrirDetalhesAvaliacao(avaliacoes.get(num - 1));
                    } else {
                        System.out.println("Número inválido.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Opção inválida.");
                }

            } catch (Exception e) {
                System.out.println("Erro ao listar avaliações: " + e.getMessage());
                return;
            }
        }
    }

    private void abrirDetalhesAvaliacao(Avaliacao av) {
        while (true) {
            try {
                int idUsuarioLogado = controleUsuario.getUsuarioAtivo().getId();

                // recarrega a avaliação para refletir estado atual
                Avaliacao avAtual = controleAvaliacao.listarPorUsuario(idUsuarioLogado).stream()
                        .filter(a -> a.getId() == av.getId())
                        .findFirst().orElse(null);
                if (avAtual == null) {
                    System.out.println("Esta avaliação não existe mais.");
                    return;
                }

                Resposta r = controleResposta.buscarPorId(avAtual.getIdResposta());
                Pergunta p = (r != null) ? controlePergunta.buscarPorId(r.getIdPergunta()) : null;

                String textoPergunta = (p != null) ? p.getPergunta() : "[Pergunta indisponível]";
                String textoResposta = (r != null) ? r.getResposta() : "[Resposta indisponível]";
                String tipoVoto = avAtual.isAvaliacao() ? "Concordou (+1)" : "Discordou (-1)";

                System.out.println("\n============================");
                System.out.println("      AJUDA AÍ 1.0");
                System.out.println("============================");
                System.out.println("> Início > Minha área > Minhas avaliações > Avaliação");
                System.out.println();
                System.out.println("PERGUNTA:");
                System.out.println(textoPergunta);
                System.out.println();
                System.out.println("RESPOSTA AVALIADA:");
                System.out.println("------------------");
                System.out.println(textoResposta);
                if (r != null) {
                    System.out.printf("Nota atual: %d (%d avaliações) | Respondida em %s%n", r.getNota(), r.getAvaliacoes(), formatarData(r.getCriacao()));
                }
                System.out.println();
                System.out.println("SUA AVALIAÇÃO: " + tipoVoto);
                System.out.println();

                System.out.println("(A) Cancelar avaliação");
                System.out.println("(R) Retornar ao menu anterior");
                System.out.print("Opção: ");

                String op = scanner.nextLine().trim().toUpperCase();
                if (op.equals("A")) {
                    System.out.print("Confirma o cancelamento da avaliação? (S/N): ");
                    if (scanner.nextLine().trim().equalsIgnoreCase("S")) {
                        boolean ok = controleAvaliacao.cancelarAvaliacao(avAtual.getId(), idUsuarioLogado);
                        System.out.println(ok
                                ? "Avaliação cancelada e nota da resposta estornada."
                                : "Não foi possível cancelar a avaliação.");
                        return; // volta para a lista
                    }
                } else if (op.equals("R")) {
                    return;
                } else {
                    System.out.println("Opção inválida.");
                }

            } catch (Exception e) {
                System.out.println("Erro nos detalhes da avaliação: " + e.getMessage());
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
