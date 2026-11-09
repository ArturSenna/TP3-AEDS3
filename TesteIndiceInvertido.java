import aed3.ListaInvertida;
import controle.ControlePergunta;
import entidades.ProcessadorTermos;
import entidades.ResultadoBusca;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

// script utilitário que reproduz o exemplo dos 4 livros do enunciado usando as palavras-chave
// das perguntas e confere as listas invertidas e a classificação TF x IDF.
// ATENÇÃO: assim como PovoarDados, apaga a pasta ./dados antes de começar.
public class TesteIndiceInvertido {

    private static int falhas = 0;

    private static void limparDiretorio(File pasta) {
        if (pasta.exists() && pasta.isDirectory()) {
            File[] arquivos = pasta.listFiles();
            if (arquivos != null) {
                for (File f : arquivos) {
                    if (f.isDirectory()) limparDiretorio(f);
                    f.delete();
                }
            }
        }
    }

    private static void verificar(String descricao, boolean condicao) {
        System.out.println((condicao ? "[OK]    " : "[FALHA] ") + descricao);
        if (!condicao) falhas++;
    }

    private static String lista(ListaInvertida li, String termo) throws Exception {
        return Arrays.toString(li.read(termo));
    }

    private static String ranking(ArrayList<ResultadoBusca> resultados) {
        StringBuilder sb = new StringBuilder();
        for (ResultadoBusca r : resultados) {
            sb.append(String.format(Locale.ROOT, "(%d; %.3f) ", r.getPergunta().getId(), r.getRelevancia()));
        }
        return sb.toString().trim();
    }

    public static void main(String[] args) {
        System.out.println("Limpando dados antigos...");
        limparDiretorio(new File("./dados"));
        new File("./dados/perguntas").mkdirs();

        try {
            System.out.println("\n--- PROCESSAMENTO DOS TERMOS ---");
            verificar("\"Introdução à Inteligência Artificial\" -> [introducao, inteligencia, artificial]",
                ProcessadorTermos.extrairTermos("Introdução à Inteligência Artificial")
                    .toString().equals("[introducao, inteligencia, artificial]"));
            verificar("\"Inteligência no Trabalho por Meio da Inteligência Artificial\" -> 5 termos com repetição",
                ProcessadorTermos.extrairTermos("Inteligência no Trabalho por Meio da Inteligência Artificial")
                    .toString().equals("[inteligencia, trabalho, meio, inteligencia, artificial]"));
            verificar("\"hash extensivel;arvore b+;tf-idf\" -> separa por ; espaço e pontuação",
                ProcessadorTermos.extrairTermos("hash extensivel;arvore b+;tf-idf")
                    .toString().equals("[hash, extensivel, arvore, b, tf, idf]"));
            verificar("\"a pergunta 3 sobre o Hash\" -> [hash] (stop words e numerais descartados)",
                ProcessadorTermos.extrairTermos("a pergunta 3 sobre o Hash").toString().equals("[hash]"));

            System.out.println("\n--- CONSTRUÇÃO DAS LISTAS (4 livros do enunciado como palavras-chave) ---");
            ControlePergunta cp = new ControlePergunta();
            int l1 = cp.criar(1, "Livro 1", "Introdução à Inteligência Artificial");
            int l2 = cp.criar(1, "Livro 2", "Inteligência Emocional para Gestores");
            int l3 = cp.criar(1, "Livro 3", "Inteligência no Trabalho por Meio da Inteligência Artificial");
            int l4 = cp.criar(1, "Livro 4", "Introdução à Gestão de Equipes");

            // segunda visão (somente leitura) sobre os mesmos arquivos do índice, para inspecionar as listas
            ListaInvertida li = new ListaInvertida(4,
                "./dados/perguntas/indicePalavrasChave.dicionario.db",
                "./dados/perguntas/indicePalavrasChave.blocos.db");

            verificar("numeroEntidades() == 4", li.numeroEntidades() == 4);
            verificar("introducao: (1; 0.333) (4; 0.333)",   lista(li, "introducao").equals("[(1;0.33333334), (4;0.33333334)]"));
            verificar("artificial: (1; 0.333) (3; 0.2)",     lista(li, "artificial").equals("[(1;0.33333334), (3;0.2)]"));
            verificar("inteligencia: (1; 0.333) (2; 0.333) (3; 0.4)",
                lista(li, "inteligencia").equals("[(1;0.33333334), (2;0.33333334), (3;0.4)]"));
            verificar("emocional: (2; 0.333)", lista(li, "emocional").equals("[(2;0.33333334)]"));
            verificar("gestores: (2; 0.333)",  lista(li, "gestores").equals("[(2;0.33333334)]"));
            verificar("trabalho: (3; 0.2)",    lista(li, "trabalho").equals("[(3;0.2)]"));
            verificar("meio: (3; 0.2)",        lista(li, "meio").equals("[(3;0.2)]"));
            verificar("gestao: (4; 0.333)",    lista(li, "gestao").equals("[(4;0.33333334)]"));
            verificar("equipes: (4; 0.333)",   lista(li, "equipes").equals("[(4;0.33333334)]"));
            verificar("stop word \"a\" não foi indexada", li.read("a").length == 0);
            li.print();

            System.out.println("\n--- BUSCA \"Inteligência Artificial\" (esperado: [1, 3, 2] com 0.142, 0.110 e 0.042) ---");
            ArrayList<ResultadoBusca> r = cp.buscarPorPalavrasChave("Inteligência Artificial");
            System.out.println("Resultado: " + ranking(r));
            verificar("ordem e valores TF x IDF", ranking(r).equals("(1; 0.142) (3; 0.110) (2; 0.042)"));
            verificar("termo repetido na consulta conta uma vez só",
                ranking(cp.buscarPorPalavrasChave("artificial artificial")).equals(ranking(cp.buscarPorPalavrasChave("artificial"))));
            verificar("consulta só com stop words -> nenhum resultado", cp.buscarPorPalavrasChave("a de para").isEmpty());
            verificar("termo inexistente -> nenhum resultado", cp.buscarPorPalavrasChave("xyzabc").isEmpty());

            System.out.println("\n--- ALTERAÇÃO: livro 2 passa a ter palavras-chave \"Gestão de Equipes Ágeis\" ---");
            cp.alterar(l2, "Livro 2", "Gestão de Equipes Ágeis");
            verificar("inteligencia perde o id 2",  lista(li, "inteligencia").equals("[(1;0.33333334), (3;0.4)]"));
            verificar("emocional fica vazia",       li.read("emocional").length == 0);
            verificar("gestao ganha o id 2",        lista(li, "gestao").equals("[(2;0.33333334), (4;0.33333334)]"));
            verificar("ageis: (2; 0.333)",          lista(li, "ageis").equals("[(2;0.33333334)]"));
            verificar("total de entidades continua 4", li.numeroEntidades() == 4);
            cp.alterar(l1, "Livro 1 (texto alterado)", "Introdução à Inteligência Artificial");
            verificar("alterar só o texto não mexe no índice", lista(li, "introducao").equals("[(1;0.33333334), (4;0.33333334)]"));

            System.out.println("\n--- ARQUIVAMENTO: livro 1 é arquivado ---");
            cp.arquivar(l1, 1);
            verificar("total de entidades cai para 3", li.numeroEntidades() == 3);
            verificar("introducao: (4; 0.333)",       lista(li, "introducao").equals("[(4;0.33333334)]"));
            verificar("inteligencia: (3; 0.4)",       lista(li, "inteligencia").equals("[(3;0.4)]"));
            ArrayList<ResultadoBusca> r2 = cp.buscarPorPalavrasChave("inteligencia");
            System.out.println("Busca \"inteligencia\": " + ranking(r2));
            verificar("só o livro 3 com 0.4 x log10(3/1) = 0.191", ranking(r2).equals("(3; 0.191)"));
            verificar("outro usuário não consegue arquivar", !cp.arquivar(l3, 99) && li.numeroEntidades() == 3);

            System.out.println("\n--- PERSISTÊNCIA: fecha e reabre os arquivos ---");
            cp.close();
            ControlePergunta cp2 = new ControlePergunta();
            ArrayList<ResultadoBusca> r3 = cp2.buscarPorPalavrasChave("gestão de equipes");
            System.out.println("Busca \"gestão de equipes\": " + ranking(r3));
            verificar("índice recuperado do disco", ranking(r3).equals("(4; 0.117) (2; 0.117)"));
            cp2.close();

        } catch (Exception e) {
            e.printStackTrace();
            falhas++;
        }

        System.out.println("\nFALHAS: " + falhas + (falhas == 0 ? "  (tudo conforme o enunciado)" : ""));
        System.out.println("Execute PovoarDados para recriar a base de demonstração.");
    }
}
