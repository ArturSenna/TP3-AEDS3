package controle;

import entidades.ArquivoAvaliacao;
import entidades.Avaliacao;
import entidades.Resposta;

import java.util.ArrayList;

public class ControleAvaliacao {

    private ArquivoAvaliacao arqAvaliacao;
    private ControleResposta controleResposta;

    public ControleAvaliacao(ControleResposta controleResposta) throws Exception {
        this.arqAvaliacao     = new ArquivoAvaliacao();
        this.controleResposta = controleResposta;
    }

    // valida regras (não auto-voto, não duplicado), cria a avaliação e atualiza nota da resposta (+1/-1)
    public boolean avaliar(int idUsuario, int idResposta, boolean concorda) throws Exception {
        Resposta r = controleResposta.buscarPorId(idResposta);
        if( r == null || !r.isAtiva()) return false;
        if( r.getIdUsuario() == idUsuario) return false;
        if( jaAvaliou(idUsuario, idResposta)) return false;

        Avaliacao a = new Avaliacao(-1, idUsuario, idResposta, concorda);
        arqAvaliacao.create(a);
        controleResposta.atualizarNotaEAvaliacoes(idResposta, (concorda? 1 : -1), 1);
        return true;
    }

    // verifica se o usuário já avaliou esta resposta
    public boolean jaAvaliou(int idUsuario, int idResposta) throws Exception {
        return (arqAvaliacao.buscarPorUsuarioEResposta(idUsuario, idResposta) != null);
    }

    public ArrayList<Avaliacao> listarPorUsuario(int idUsuario) throws Exception {
        return arqAvaliacao.readAllByUsuario(idUsuario);
    }

    // remove avaliação e estorna a pontuação (-1 se era positiva, +1 se era negativa)
    public boolean cancelarAvaliacao(int idAvaliacao, int idUsuarioRequisitante) throws Exception {
        Avaliacao a = arqAvaliacao.read(idAvaliacao);
        if(a == null) return false;
        if(a.getIdUsuario() != idUsuarioRequisitante) return false;

        Resposta r = controleResposta.buscarPorId(a.getIdResposta());
        if(r!=null) controleResposta.atualizarNotaEAvaliacoes(a.getIdResposta(), (a.isAvaliacao()? -1 : 1), -1);
        return arqAvaliacao.delete(idAvaliacao);
    }

    public void close() throws Exception {
        arqAvaliacao.close();
    }
}
