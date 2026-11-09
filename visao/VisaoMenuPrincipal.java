package visao;

import controle.ControleAvaliacao;
import controle.ControlePergunta;
import controle.ControleResposta;
import controle.ControleUsuario;
import java.util.Scanner;

// menu principal: navegação entre Minha Área e Busca de perguntas
public class VisaoMenuPrincipal {

    private final Scanner          scanner;
    private final ControleUsuario  controleUsuario;
    private final ControlePergunta controlePergunta;
    private final ControleResposta controleResposta;
    private final ControleAvaliacao controleAvaliacao;

    public VisaoMenuPrincipal(Scanner scanner,
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
            System.out.println("> Início");
            System.out.println("(A) Minha área");
            System.out.println("(B) Buscar perguntas");
            System.out.println("(S) Sair");
            System.out.print("Opção: ");

            String opcao = scanner.nextLine().trim().toUpperCase();

            switch (opcao) {
                case "A" -> new VisaoMinhaArea(
                    scanner, controleUsuario, controlePergunta, controleResposta, controleAvaliacao
                ).exibir();
                case "B" -> new VisaoBuscarPerguntas(
                    scanner, controleUsuario, controlePergunta, controleResposta, controleAvaliacao
                ).exibir();
                case "S" -> { controleUsuario.logout(); return; }
                default  -> System.out.println("Opção inválida.");
            }
        }
    }
}
