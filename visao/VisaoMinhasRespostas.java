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

// tela de Minhas Respostas: lista respostas do usuário e permite arquivamento
public class VisaoMinhasRespostas {

    private final Scanner          scanner;
    private final ControleUsuario  controleUsuario;
    private final ControlePergunta controlePergunta;
    private final ControleResposta controleResposta;

    private static final DateTimeFormatter FMT =
        DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm");

    public VisaoMinhasRespostas(Scanner scanner,
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
            try {
                int idUsuarioLogado = controleUsuario.getUsuarioAtivo().getId();
                ArrayList<Resposta> respostas = controleResposta.listarPorUsuario(idUsuarioLogado);

                System.out.println("\n============================");
                System.out.println("      AJUDA AÍ 1.0");
                System.out.println("============================");
                System.out.println("> Início > Minha área > Minhas respostas");

                if (respostas.isEmpty()) {
                    System.out.println("\nVocê ainda não escreveu nenhuma resposta.");
                    System.out.println("\nPressione ENTER para retornar...");
                    scanner.nextLine();
                    return;
                }

                System.out.println("\nMINHAS RESPOSTAS");
                System.out.println("----------------------------");
                for (int i = 0; i < respostas.size(); i++) {
                    Resposta r = respostas.get(i);
                    Pergunta p = controlePergunta.buscarPorId(r.getIdPergunta());
                    String tituloPergunta = (p != null) ? p.getPergunta() : "[Pergunta indisponível]";
                    String dataResp = formatarData(r.getCriacao());

                    System.out.printf("(%d) Pergunta: %s%n", i + 1, tituloPergunta);
                    System.out.printf("    Sua resposta: %s%n", r.getResposta());
                    System.out.printf("    Data: %s | Nota: %d (%d avaliações)%n%n",
                                      dataResp, r.getNota(), r.getAvaliacoes());
                }

                System.out.println("(R) Retornar");
                System.out.println("Ou digite o número da resposta para detalhes.");
                System.out.print("Opção: ");

                String op = scanner.nextLine().trim();
                if (op.equalsIgnoreCase("R")) {
                    return;
                }

                try {
                    int num = Integer.parseInt(op);
                    if (num >= 1 && num <= respostas.size()) {
                        abrirDetalhesMinhaResposta(respostas.get(num - 1));
                    } else {
                        System.out.println("Número inválido.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Opção inválida.");
                }

            } catch (Exception e) {
                System.out.println("Erro ao listar respostas: " + e.getMessage());
                return;
            }
        }
    }

    private void abrirDetalhesMinhaResposta(Resposta r) {
        while (true) {
            try {
                Resposta respAtual = controleResposta.buscarPorId(r.getId());
                if (respAtual == null || !respAtual.isAtiva()) {
                    System.out.println("Resposta não está mais ativa.");
                    return;
                }

                Pergunta p = controlePergunta.buscarPorId(respAtual.getIdPergunta());
                String textoPergunta = (p != null) ? p.getPergunta() : "[Pergunta indisponível]";
                Usuario autorPergunta = (p != null) ? controleUsuario.buscarPorId(p.getIdUsuario()) : null;
                String nomeAutorPergunta = (autorPergunta != null) ? autorPergunta.getNome() : "Desconhecido";
                String dataPergunta = (p != null) ? formatarData(p.getCriacao()) : "";

                String dataResp = formatarData(respAtual.getCriacao());

                System.out.println("\n============================");
                System.out.println("      AJUDA AÍ 1.0");
                System.out.println("============================");
                System.out.println("> Início > Minha área > Minhas respostas > Resposta");
                System.out.println();
                System.out.println("PERGUNTA:");
                System.out.println(textoPergunta);
                if (p != null) {
                    System.out.printf("Criada por %s em %s%n", nomeAutorPergunta, dataPergunta);
                }
                System.out.println();
                System.out.println("SUA RESPOSTA:");
                System.out.println("-------------");
                System.out.println(respAtual.getResposta());
                System.out.printf("Respondido por você em %s%n", dataResp);
                System.out.printf("Nota: %d  (%d avaliações)%n%n",
                                  respAtual.getNota(), respAtual.getAvaliacoes());

                System.out.println("(A) Arquivar resposta");
                System.out.println("(R) Retornar ao menu anterior");
                System.out.print("Opção: ");

                String op = scanner.nextLine().trim().toUpperCase();
                if (op.equals("A")) {
                    System.out.print("Confirma o arquivamento definitivo da resposta? (S/N): ");
                    if (scanner.nextLine().trim().equalsIgnoreCase("S")) {
                        int idUsuarioLogado = controleUsuario.getUsuarioAtivo().getId();
                        int idAutorPergunta = (p != null) ? p.getIdUsuario() : -1;
                        controleResposta.arquivar(respAtual.getId(), idUsuarioLogado, idAutorPergunta);
                        System.out.println("Resposta arquivada com sucesso.");
                        return;
                    }
                } else if (op.equals("R")) {
                    return;
                } else {
                    System.out.println("Opção inválida.");
                }

            } catch (Exception e) {
                System.out.println("Erro nos detalhes da resposta: " + e.getMessage());
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
