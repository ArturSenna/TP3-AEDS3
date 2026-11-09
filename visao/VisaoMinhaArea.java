package visao;

import controle.ControleAvaliacao;
import controle.ControlePergunta;
import controle.ControleResposta;
import controle.ControleUsuario;
import java.util.Scanner;

// submenu da área pessoal: dados cadastrais, perguntas, respostas e avaliações
public class VisaoMinhaArea {

    private final Scanner          scanner;
    private final ControleUsuario  controleUsuario;
    private final ControlePergunta controlePergunta;
    private final ControleResposta controleResposta;
    private final ControleAvaliacao controleAvaliacao;

    public VisaoMinhaArea(Scanner scanner,
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
            System.out.println("> Início > Minha área");
            System.out.println("Usuário: " + controleUsuario.getUsuarioAtivo().getNome());
            System.out.println("(A) Meus dados");
            System.out.println("(B) Minhas perguntas");
            System.out.println("(C) Minhas respostas");
            System.out.println("(D) Minhas avaliações");
            System.out.println("(R) Retornar ao menu anterior");
            System.out.print("Opção: ");

            String opcao = scanner.nextLine().trim().toUpperCase();

            switch (opcao) {
                case "A" -> new VisaoMeusDados(scanner, controleUsuario).exibir();
                case "B" -> new VisaoPerguntas(scanner, controleUsuario, controlePergunta, controleResposta).exibir();
                case "C" -> new VisaoMinhasRespostas(scanner, controleUsuario, controlePergunta, controleResposta).exibir();
                case "D" -> new VisaoMinhasAvaliacoes(scanner, controleUsuario, controlePergunta, controleResposta, controleAvaliacao).exibir();
                case "R" -> { return; }
                default  -> System.out.println("Opção inválida.");
            }
        }
    }
}
