package visao;

import controle.ControleUsuario;
import java.util.Scanner;

public class VisaoLogin {

    private final Scanner        scanner;
    private final ControleUsuario controle;

    public VisaoLogin(Scanner scanner, ControleUsuario controle) {
        this.scanner  = scanner;
        this.controle = controle;
    }

    public boolean exibir() {
        while (true) {
            System.out.println("\n============================");
            System.out.println("      AJUDA AÍ 1.0");
            System.out.println("============================");
            System.out.println("(A) Login");
            System.out.println("(B) Novo usuário (primeiro acesso)");
            System.out.println("(S) Sair");
            System.out.print("Opção: ");

            String opcao = scanner.nextLine().trim().toUpperCase();

            switch (opcao) {
                case "A" -> { if (telaLogin()) return true; }
                case "B" -> telaCadastro();
                case "S" -> { return false; }
                default  -> System.out.println("Opção inválida.");
            }
        }
    }

    private boolean telaLogin() {
        System.out.println("\n--- LOGIN ---");
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Senha: ");
        String senha = scanner.nextLine().trim();

        try {
            if (controle.login(email, senha)) {
                System.out.println("Login realizado com sucesso! Bem-vindo, "
                    + controle.getUsuarioAtivo().getNome() + "!");
                return true;
            } else {
                System.out.println("Email ou senha incorretos.");
                System.out.println("(R) Recuperar senha   (qualquer tecla) Tentar novamente");
                String resp = scanner.nextLine().trim().toUpperCase();
                if (resp.equals("R")) {
                    telaRecuperarSenha();
                }
                return false;
            }
        } catch (Exception e) {
            System.out.println("Erro ao realizar login: " + e.getMessage());
            return false;
        }
    }

    private void telaRecuperarSenha(){
        System.out.println("\n--- RECUPERAÇÃO DE SENHA ---");
        System.out.print("Email:");
        System.out.print("\n> ");
        String email = scanner.nextLine().trim();

        try{
            String pergunta = controle.getPerguntaSecretaPorEmail(email);
            if(pergunta==null){
                System.out.println("Email nao encontrado");
                return;
            }
            System.out.println("Pergunta de recuperação: "+ pergunta);
            System.out.print("\n> ");
            String resposta = scanner.nextLine().trim();

            if(!controle.validarRespostaSecreta(email, resposta)){
                System.out.println("Resposta incorreta.");
                return;
            }
            System.out.println("Nova senha "); System.out.print("\n> ");
            String novaSenha = scanner.nextLine().trim();
            System.out.println("Confirme a nova senha"); System.out.print("\n> ");
            String novaSenhaConfirmacao = scanner.nextLine().trim();

            if(novaSenha.isEmpty() || novaSenhaConfirmacao.isEmpty()){
                System.out.println("Senha vazia, tente novamente.");
                return;
            }
            if(!novaSenha.equals(novaSenhaConfirmacao)){
                System.out.println("As senhas nao sao identicas");
                return;
            }

            boolean status = controle.redefinirSenhaPorEmail(email, novaSenha);
            System.out.println(status ? "Senha redefinida com sucesso" : "Falha ao redefinir a senha.");
        } catch (Exception e) {
            System.out.println("Erro ao redefinir senha: " + e.getMessage());
        }
    }

    private void telaCadastro() {
        System.out.println("\n--- NOVO USUÁRIO ---");
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();

        try {
            if (controle.getUsuarioAtivo() == null) {
                // ArquivoUsuario.create() valida duplicidade; feedback de email virá lá
            }

            System.out.print("Nome completo: ");
            String nome = scanner.nextLine().trim();

            System.out.print("Senha: ");
            String senha = scanner.nextLine().trim();

            System.out.print("Pergunta de recuperação de senha: ");
            String pergunta = scanner.nextLine().trim();

            System.out.print("Resposta (será armazenada sem acentos/maiúsculas): ");
            String resposta = scanner.nextLine().trim();

            controle.cadastrar(nome, email, senha, pergunta, resposta);
            System.out.println("Usuário cadastrado com sucesso! Faça login para continuar.");

        } catch (Exception e) {
            System.out.println("Erro no cadastro: " + e.getMessage());
        }
    }
}
