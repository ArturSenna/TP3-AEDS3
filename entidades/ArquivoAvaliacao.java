package entidades;

import aed3.Arquivo;
import aed3.ArvoreBMais;
import aed3.ParIdId;
import java.util.ArrayList;

public class ArquivoAvaliacao extends Arquivo<Avaliacao> {

    // relacionamento 1:N -> (idUsuario, idAvaliacao)
    private ArvoreBMais<ParIdId> relUsuarioAvaliacao;
    // relacionamento 1:N -> (idResposta, idAvaliacao)
    private ArvoreBMais<ParIdId> relRespostaAvaliacao;

    public ArquivoAvaliacao() throws Exception {
        super("avaliacoes", Avaliacao.class.getConstructor());
        relUsuarioAvaliacao = new ArvoreBMais<>(
            ParIdId.class.getConstructor(),
            5,
            "./dados/avaliacoes/relUsuarioAvaliacao.db"
        );
        relRespostaAvaliacao = new ArvoreBMais<>(
            ParIdId.class.getConstructor(),
            5,
            "./dados/avaliacoes/relRespostaAvaliacao.db"
        );
    }

    // grava o registro no arquivo e indexa nas duas Árvores B+
    @Override
    public int create(Avaliacao a) throws Exception {
        int id = super.create(a);
        a.setId(id);
        relUsuarioAvaliacao.create(new ParIdId(a.getIdUsuario(), id));
        relRespostaAvaliacao.create(new ParIdId(a.getIdResposta(), id));
        return id;
    }

    // recupera todas as avaliações do usuário pela Árvore B+
    public ArrayList<Avaliacao> readAllByUsuario(int idUsuario) throws Exception {
        ArrayList<Avaliacao> lista = new ArrayList<>();
        ArrayList<ParIdId> pares = relUsuarioAvaliacao.read(new ParIdId(idUsuario, -1));
        for(ParIdId par : pares){
            Avaliacao a = super.read(par.getId2());
            if (a!=null) lista.add(a);
        }
        return lista;
    }

    // busca se o usuário já avaliou a resposta para evitar voto duplicado
    public Avaliacao buscarPorUsuarioEResposta(int idUsuario, int idResposta) throws Exception {
        ArrayList<ParIdId> pares = relUsuarioAvaliacao.read(new ParIdId(idUsuario, -1));
        for(ParIdId par : pares){
            Avaliacao a = super.read(par.getId2());
            if(a != null && a.getIdResposta() == idResposta){
                return a;
            }
        }
        return null;
    }

    // remove do arquivo e desindexa das duas Árvores B+
    @Override
    public boolean delete(int id) throws Exception {
        Avaliacao a = super.read(id);
        if(a==null) return false;
        relUsuarioAvaliacao.delete(new ParIdId(a.getIdUsuario(), id));
        relRespostaAvaliacao.delete(new ParIdId(a.getIdResposta(), id));
        return super.delete(id);
    }

    @Override
    public void close() throws java.io.IOException {
        super.close();
    }
}
