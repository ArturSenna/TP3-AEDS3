package entidades;

import aed3.Arquivo;
import aed3.ArvoreBMais;
import aed3.ParIdId;
import java.util.ArrayList;

public class ArquivoResposta extends Arquivo<Resposta> {

    // relacionamento 1:N -> (idPergunta, idResposta)
    private ArvoreBMais<ParIdId> relPerguntaResposta;
    // relacionamento 1:N -> (idUsuario, idResposta)
    private ArvoreBMais<ParIdId> relUsuarioResposta;

    public ArquivoResposta() throws Exception {
        super("respostas", Resposta.class.getConstructor());
        relPerguntaResposta = new ArvoreBMais<>(
            ParIdId.class.getConstructor(),
            5,
            "./dados/respostas/relPerguntaResposta.db"
        );
        relUsuarioResposta = new ArvoreBMais<>(
            ParIdId.class.getConstructor(),
            5,
            "./dados/respostas/relUsuarioResposta.db"
        );
    }

    @Override
    public int create(Resposta r) throws Exception {
        int id = super.create(r);
        r.setId(id);
        relPerguntaResposta.create(new ParIdId(r.getIdPergunta(), id));
        relUsuarioResposta.create(new ParIdId(r.getIdUsuario(), id));
        return id;
    }

    // Retorna todas as respostas ativas de uma pergunta ordenadas por nota decrescente
    public ArrayList<Resposta> readAllByPergunta(int idPergunta) throws Exception {
        ArrayList<Resposta> lista = new ArrayList<>();
        ArrayList<ParIdId> pares = relPerguntaResposta.read(new ParIdId(idPergunta, -1));
        for (ParIdId par : pares) {
            Resposta r = super.read(par.getId2());
            if (r != null && r.isAtiva()) {
                lista.add(r);
            }
        }
        // Ordena por nota decrescente; em caso de empate, por data decrescente
        lista.sort((r1, r2) -> {
            int cmpNota = Short.compare(r2.getNota(), r1.getNota());
            if (cmpNota != 0) return cmpNota;
            return Long.compare(r2.getCriacao(), r1.getCriacao());
        });
        return lista;
    }

    // Retorna todas as respostas ativas escritas por um usuário
    public ArrayList<Resposta> readAllByUsuario(int idUsuario) throws Exception {
        ArrayList<Resposta> lista = new ArrayList<>();
        ArrayList<ParIdId> pares = relUsuarioResposta.read(new ParIdId(idUsuario, -1));
        for (ParIdId par : pares) {
            Resposta r = super.read(par.getId2());
            if (r != null && r.isAtiva()) {
                lista.add(r);
            }
        }
        lista.sort((r1, r2) -> Long.compare(r2.getCriacao(), r1.getCriacao()));
        return lista;
    }

    // Arquivamento lógico da resposta
    public boolean arquivar(int idResposta) throws Exception {
        Resposta r = super.read(idResposta);
        if (r == null) return false;
        r.setAtiva(false);
        return super.update(r);
    }

    @Override
    public boolean delete(int id) throws Exception {
        Resposta r = super.read(id);
        if (r == null) return false;
        relPerguntaResposta.delete(new ParIdId(r.getIdPergunta(), id));
        relUsuarioResposta.delete(new ParIdId(r.getIdUsuario(), id));
        return super.delete(id);
    }

    public void deleteAllByPergunta(int idPergunta) throws Exception {
        ArrayList<ParIdId> pares = relPerguntaResposta.read(new ParIdId(idPergunta, -1));
        for (ParIdId par : pares) {
            Resposta r = super.read(par.getId2());
            if (r != null) {
                relUsuarioResposta.delete(new ParIdId(r.getIdUsuario(), r.getId()));
            }
            super.delete(par.getId2());
            relPerguntaResposta.delete(par);
        }
    }

    public void deleteAllByUsuario(int idUsuario) throws Exception {
        ArrayList<ParIdId> pares = relUsuarioResposta.read(new ParIdId(idUsuario, -1));
        for (ParIdId par : pares) {
            Resposta r = super.read(par.getId2());
            if (r != null) {
                relPerguntaResposta.delete(new ParIdId(r.getIdPergunta(), r.getId()));
            }
            super.delete(par.getId2());
            relUsuarioResposta.delete(par);
        }
    }

    @Override
    public void close() throws java.io.IOException {
        super.close();
    }
}
