package visao;

import controle.ControleUsuario;
import java.util.Scanner;

public class VisaoMeusDados {

    private final Scanner        scanner;
    private final ControleUsuario controle;

    public VisaoMeusDados(Scanner scanner, ControleUsuario controle) {
        this.scanner  = scanner;
        this.controle = controle;
    }

    public void exibir() {
        while (true) {
            System.out.println("\n============================");
            System.out.println("      AJUDA AÍ 1.0");
            System.out.println("============================");
            System.out.println("> Início > Minha área > Meus dados");
            System.out.println("(A) Alterar nome");
            System.out.println("(B) Alterar email");
            System.out.println("(C) Alterar senha");
            System.out.println("(D) Alterar pergunta e resposta de recuperação");
            System.out.println("(R) Retornar");
            System.out.print("Opção: ");

            String opcao = scanner.nextLine().trim().toUpperCase();

            switch (opcao) {
                case "A" -> alterarNome();
                case "B" -> alterarEmail();
                case "C" -> alterarSenha();
                case "D" -> alterarPerguntaSecreta();
                case "R" -> { return; }
                default  -> System.out.println("Opção inválida.");
            }
        }
    }

    private void alterarNome() {
        // implementar
        System.out.print("Novo nome: ");
        String novoNome = scanner.nextLine().trim();
        try {
            controle.alterarNome(novoNome);
            System.out.println("Nome alterado com sucesso.");
        } catch (UnsupportedOperationException e) {
            System.out.println("[Em desenvolvimento] " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void alterarEmail() {
        // implementar
        System.out.print("Novo email: ");
        String novoEmail = scanner.nextLine().trim();
        try {
            controle.alterarEmail(novoEmail);
            System.out.println("Email alterado com sucesso.");
        } catch (UnsupportedOperationException e) {
            System.out.println("[Em desenvolvimento] " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void alterarSenha() {
        // implementar
        System.out.print("Nova senha: ");
        String novaSenha = scanner.nextLine().trim();
        try {
            controle.alterarSenha(novaSenha);
            System.out.println("Senha alterada com sucesso.");
        } catch (UnsupportedOperationException e) {
            System.out.println("[Em desenvolvimento] " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void alterarPerguntaSecreta() {
        // implementar
        System.out.print("Nova pergunta de recuperação: ");
        String pergunta = scanner.nextLine().trim();
        System.out.print("Nova resposta: ");
        String resposta = scanner.nextLine().trim();
        try {
            controle.alterarPerguntaSecreta(pergunta, resposta);
            System.out.println("Pergunta e resposta atualizadas.");
        } catch (UnsupportedOperationException e) {
            System.out.println("[Em desenvolvimento] " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }
}
