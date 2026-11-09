import controle.ControleAvaliacao;
import controle.ControlePergunta;
import controle.ControleResposta;
import controle.ControleUsuario;
import visao.VisaoLogin;
import visao.VisaoMenuPrincipal;
import java.io.File;
import java.util.Scanner;

// ponto de entrada: inicializa pastas de dados, controles e telas
public class Principal {

    public static void main(String[] args) {

        new File("./dados/usuarios").mkdirs();
        new File("./dados/perguntas").mkdirs();
        new File("./dados/respostas").mkdirs();
        new File("./dados/avaliacoes").mkdirs();

        Scanner scanner = new Scanner(System.in);
        ControleUsuario   controleUsuario   = null;
        ControlePergunta  controlePergunta  = null;
        ControleResposta  controleResposta  = null;
        ControleAvaliacao controleAvaliacao = null;

        try {
            controleUsuario   = new ControleUsuario();
            controlePergunta  = new ControlePergunta();
            controleResposta  = new ControleResposta();
            controleAvaliacao = new ControleAvaliacao(controleResposta);

            VisaoLogin visaoLogin = new VisaoLogin(scanner, controleUsuario);

            while (true) {
                boolean logado = visaoLogin.exibir();

                if (!logado) {
                    System.out.println("Encerrando o sistema. Até mais!");
                    break;
                }

                new VisaoMenuPrincipal(
                    scanner,
                    controleUsuario,
                    controlePergunta,
                    controleResposta,
                    controleAvaliacao
                ).exibir();
            }

        } catch (Exception e) {
            System.err.println("Erro fatal ao iniciar o sistema: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // fecha com segurança os arquivos binários abertos na execução
            try { if (controleUsuario   != null) controleUsuario.close();   } catch (Exception ignored) {}
            try { if (controlePergunta  != null) controlePergunta.close();  } catch (Exception ignored) {}
            try { if (controleResposta  != null) controleResposta.close();  } catch (Exception ignored) {}
            try { if (controleAvaliacao != null) controleAvaliacao.close(); } catch (Exception ignored) {}
            scanner.close();
        }
    }
}
