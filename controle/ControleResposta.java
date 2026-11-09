package controle;

import entidades.ArquivoResposta;
import entidades.Resposta;
import java.util.ArrayList;

public class ControleResposta {

    private ArquivoResposta arqResposta;

    public ControleResposta() throws Exception {
        arqResposta = new ArquivoResposta();
    }

    public int criar(int idPergunta, int idUsuario, String textoResposta) throws Exception {
        long agora = System.currentTimeMillis();
        Resposta r = new Resposta(-1, idPergunta, idUsuario, agora,
                                  textoResposta, (short) 0, (short) 0, true);
        return arqResposta.create(r);
    }

    public Resposta buscarPorId(int idResposta) throws Exception {
        return arqResposta.read(idResposta);
    }

    public ArrayList<Resposta> listarPorPergunta(int idPergunta) throws Exception {
        return arqResposta.readAllByPergunta(idPergunta);
    }

    public ArrayList<Resposta> listarPorUsuario(int idUsuario) throws Exception {
        return arqResposta.readAllByUsuario(idUsuario);
    }

    // Permite arquivamento pelo autor da resposta ou pelo autor da pergunta
    public boolean arquivar(int idResposta, int idUsuarioRequisitante, int idAutorPergunta) throws Exception {
        Resposta r = arqResposta.read(idResposta);
        if (r == null || !r.isAtiva()) return false;
        if (r.getIdUsuario() != idUsuarioRequisitante && idAutorPergunta != idUsuarioRequisitante) {
            return false;
        }
        return arqResposta.arquivar(idResposta);
    }

    // Atualiza a nota e o contador de avaliações após um voto ou cancelamento de voto
    public boolean atualizarNotaEAvaliacoes(int idResposta, int deltaNota, int deltaAvaliacoes) throws Exception {
        Resposta r = arqResposta.read(idResposta);
        if (r == null) return false;
        r.setNota((short) (r.getNota() + deltaNota));
        r.setAvaliacoes((short) (r.getAvaliacoes() + deltaAvaliacoes));
        return arqResposta.update(r);
    }

    public void close() throws Exception {
        arqResposta.close();
    }
}
