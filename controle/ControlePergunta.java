package controle;

import entidades.ArquivoPergunta;
import entidades.Pergunta;
import entidades.ResultadoBusca;
import java.util.ArrayList;

public class ControlePergunta {

    private ArquivoPergunta arqPergunta;

    public ControlePergunta() throws Exception {
        arqPergunta = new ArquivoPergunta();
    }

    public int criar(int idUsuario, String textoPergunta, String palavrasChave) throws Exception {
        long agora = System.currentTimeMillis();
        Pergunta p = new Pergunta(-1, idUsuario, agora, agora, (short) 0,
                                  textoPergunta, palavrasChave, true);
        return arqPergunta.create(p);
    }

    public Pergunta buscarPorId(int idPergunta) throws Exception {
        return arqPergunta.read(idPergunta);
    }

    public ArrayList<Pergunta> listarPorUsuario(int idUsuario) throws Exception {
        return arqPergunta.readAllByUsuario(idUsuario);
    }

    public ArrayList<Pergunta> listarMaisRecentes(int pagina, int itensPorPagina) throws Exception {
        return arqPergunta.readMaisRecentes(pagina, itensPorPagina);
    }

    public int totalPerguntasAtivas() throws Exception {
        return arqPergunta.totalPerguntasAtivas();
    }

    // busca por palavras-chave no índice invertido, classificada por TF x IDF
    public ArrayList<ResultadoBusca> buscarPorPalavrasChave(String consulta) throws Exception {
        return arqPergunta.readByPalavrasChave(consulta);
    }

    public boolean alterar(int idPergunta, String novoTexto, String novasPalavrasChave) throws Exception {
        Pergunta p = arqPergunta.read(idPergunta);
        if (p == null || !p.isAtiva()) return false;
        p.setPergunta(novoTexto);
        p.setPalavrasChave(novasPalavrasChave);
        p.setAlteracao(System.currentTimeMillis());
        return arqPergunta.update(p);
    }

    public boolean arquivar(int idPergunta, int idUsuarioAtivo) throws Exception {
        Pergunta p = arqPergunta.read(idPergunta);
        // impede que outro usuário arquive a pergunta
        if (p == null || p.getIdUsuario() != idUsuarioAtivo) return false;
        return arqPergunta.arquivar(idPergunta);
    }

    public void close() throws Exception {
        arqPergunta.close();
    }
}
