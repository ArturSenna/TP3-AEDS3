import controle.ControleAvaliacao;
import controle.ControlePergunta;
import controle.ControleResposta;
import controle.ControleUsuario;

import java.io.File;

// script utilitário para pré-popular o banco para demonstração no vídeo
public class PovoarDados {

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

    public static void main(String[] args) {
        System.out.println("Limpando dados antigos...");
        limparDiretorio(new File("./dados"));

        new File("./dados/usuarios").mkdirs();
        new File("./dados/perguntas").mkdirs();
        new File("./dados/respostas").mkdirs();
        new File("./dados/avaliacoes").mkdirs();

        try {
            ControleUsuario cu = new ControleUsuario();
            ControlePergunta cp = new ControlePergunta();
            ControleResposta cr = new ControleResposta();
            ControleAvaliacao ca = new ControleAvaliacao(cr);

            System.out.println("Cadastrando usuários...");
            int idArthur = cu.cadastrar("Arthur", "arthur@puc.br", "1234", "Cor favorita?", "Azul");
            int idRafael = cu.cadastrar("Rafael", "rafael@puc.br", "1234", "Nome do pet?", "Rex");

            System.out.println("Criando perguntas (as palavras-chave alimentam o índice invertido)...");
            // 10 perguntas: as palavras-chave repetem termos de propósito para demonstrar o TF
            // (ex.: "hash" e "arvore" aparecem 2x) e misturam termos raros e comuns para o IDF
            // (ex.: "arquivo" aparece em 6 perguntas e "lzw" em apenas 1)
            int p1 = cp.criar(idArthur, "Como funciona o Hash Extensivel em AEDS 3?", "hash extensivel;hash;indice;disco");
            Thread.sleep(100);
            int p2 = cp.criar(idRafael, "Qual a diferenca entre Arvore B e Arvore B+?", "arvore b+;arvore;indice");
            Thread.sleep(100);
            int p3 = cp.criar(idArthur, "O que e a lista de registros excluidos (primeiroLivre)?", "crud;exclusao;arquivo;registro");
            Thread.sleep(100);
            int p4 = cp.criar(idRafael, "Como calcular o custo de I/O em arquivos binarios?", "desempenho;disco;entrada e saida;arquivo");
            Thread.sleep(100);
            int p5 = cp.criar(idArthur, "Melhores praticas para serializacao binaria em Java?", "java;serializacao;bytes;arquivo");
            Thread.sleep(100);
            int p6 = cp.criar(idRafael, "Duvida sobre o algoritmo de compressao LZW", "compressao;lzw;dados");
            Thread.sleep(100);
            int p7 = cp.criar(idArthur, "Como montar um indice invertido com TF-IDF?", "indice invertido;tf-idf;busca;indice");
            Thread.sleep(100);
            int p8 = cp.criar(idRafael, "Como abrir um arquivo binario em Java com RandomAccessFile?", "java;arquivo;randomaccessfile;leitura");
            Thread.sleep(100);
            int p9 = cp.criar(idArthur, "Qual a melhor forma de ordenar um arquivo grande que nao cabe em memoria?", "ordenacao externa;arquivo;memoria;intercalacao");
            Thread.sleep(100);
            int p10 = cp.criar(idRafael, "Como funciona a compactacao de um arquivo com Huffman?", "compressao;huffman;arquivo;bits");

            System.out.println("Criando respostas para demonstrar ordenacao por nota...");
            int r1 = cr.criar(p1, idRafael, "O Hash Extensivel usa um diretorio em memoria/disco que aponta para cestos.");
            int r2 = cr.criar(p1, idRafael, "Resposta redundante que pode ser moderada pelo autor.");
            int r3 = cr.criar(p2, idArthur, "A principal diferenca da B+ e que os dados estao apenas nas folhas encadeadas.");
            int r4 = cr.criar(p7, idRafael, "Guarde o TF em uma lista invertida por termo e calcule o IDF na hora da busca.");

            System.out.println("Criando avaliacoes...");
            // Arthur avalia resposta do Rafael positivamente
            ca.avaliar(idArthur, r1, true);

            cu.close();
            cp.close();
            cr.close();
            ca.close();

            System.out.println("\n--- BANCO POVOADO COM SUCESSO! ---");
            System.out.println("Usuarios prontos:");
            System.out.println("1) arthur@puc.br (senha: 1234)");
            System.out.println("2) rafael@puc.br (senha: 1234)");
            System.out.println("Total de perguntas: 10 (indexadas por palavras-chave na lista invertida)");
            System.out.println("Total de respostas: 4 (com votos para demonstrar ordenacao e moderacao)");
            System.out.println("Sugestoes de busca: \"indice em disco\", \"arquivo\", \"compressao\", \"a pergunta sobre o hash\"");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
