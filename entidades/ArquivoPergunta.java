package entidades;

import aed3.Arquivo;
import aed3.ArvoreBMais;
import aed3.ElementoLista;
import aed3.ListaInvertida;
import aed3.ParIdId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

public class ArquivoPergunta extends Arquivo<Pergunta> {

    // relacionamento 1:N -> (idUsuario, idPergunta)
    private ArvoreBMais<ParIdId> relUsuarioPergunta;
    // índice secundário em Árvore B+ ordenado por data de criação decrescente
    private ArvoreBMais<ParDataId> indiceDataPergunta;
    // índice invertido: termo das palavras-chave -> lista de (idPergunta, TF)
    private ListaInvertida indicePalavrasChave;

    public ArquivoPergunta() throws Exception {
        super("perguntas", Pergunta.class.getConstructor());
        relUsuarioPergunta = new ArvoreBMais<>(
            ParIdId.class.getConstructor(),
            5,
            "./dados/perguntas/relUsuarioPergunta.db"
        );
        indiceDataPergunta = new ArvoreBMais<>(
            ParDataId.class.getConstructor(),
            5,
            "./dados/perguntas/indiceDataPergunta.db"
        );
        indicePalavrasChave = new ListaInvertida(
            4,
            "./dados/perguntas/indicePalavrasChave.dicionario.db",
            "./dados/perguntas/indicePalavrasChave.blocos.db"
        );
    }

    @Override
    public int create(Pergunta pergunta) throws Exception {
        int id = super.create(pergunta);
        pergunta.setId(id);
        relUsuarioPergunta.create(new ParIdId(pergunta.getIdUsuario(), id));
        indiceDataPergunta.create(new ParDataId(pergunta.getCriacao(), id));
        // alimenta o índice invertido e registra a nova entidade para o cálculo do IDF
        indexarPalavrasChave(pergunta);
        indicePalavrasChave.incrementaEntidades();
        return id;
    }

    public ArrayList<Pergunta> readAllByUsuario(int idUsuario) throws Exception {
        ArrayList<Pergunta> lista = new ArrayList<>();
        ArrayList<ParIdId> pares = relUsuarioPergunta.read(new ParIdId(idUsuario, -1));
        for (ParIdId par : pares) {
            Pergunta p = super.read(par.getId2());
            if (p != null) lista.add(p);
        }
        return lista;
    }

    // Retorna as perguntas ativas mais recentes com paginação a partir da Árvore B+
    public ArrayList<Pergunta> readMaisRecentes(int pagina, int itensPorPagina) throws Exception {
        ArrayList<ParDataId> pares = indiceDataPergunta.read(null);
        ArrayList<Pergunta> ativas = new ArrayList<>();
        for (ParDataId par : pares) {
            Pergunta p = super.read(par.getId());
            if (p != null && p.isAtiva()) {
                ativas.add(p);
            }
        }

        int inicio = (pagina - 1) * itensPorPagina;
        if (inicio < 0 || inicio >= ativas.size()) {
            return new ArrayList<>();
        }
        int fim = Math.min(inicio + itensPorPagina, ativas.size());
        return new ArrayList<>(ativas.subList(inicio, fim));
    }

    public int totalPerguntasAtivas() throws Exception {
        ArrayList<ParDataId> pares = indiceDataPergunta.read(null);
        int total = 0;
        for (ParDataId par : pares) {
            Pergunta p = super.read(par.getId());
            if (p != null && p.isAtiva()) {
                total++;
            }
        }
        return total;
    }

    // Busca por palavras-chave: recupera a lista invertida de cada termo da consulta,
    // multiplica o TF armazenado pelo IDF do termo e soma os valores por pergunta
    public ArrayList<ResultadoBusca> readByPalavrasChave(String consulta) throws Exception {
        ArrayList<ResultadoBusca> resultados = new ArrayList<>();

        // cada termo distinto da consulta contribui uma única vez
        LinkedHashSet<String> termos = new LinkedHashSet<>(ProcessadorTermos.extrairTermos(consulta));
        int totalEntidades = indicePalavrasChave.numeroEntidades();
        if (termos.isEmpty() || totalEntidades <= 0) return resultados;

        HashMap<Integer, Float> relevancias = new HashMap<>();
        for (String termo : termos) {
            ElementoLista[] lista = indicePalavrasChave.read(termo);
            if (lista.length == 0) continue;

            // IDF = log10(total de entidades / entidades que contêm o termo)
            float idf = (float) Math.log10((double) totalEntidades / lista.length);
            for (ElementoLista e : lista) {
                relevancias.merge(e.getId(), e.getFrequencia() * idf, Float::sum);
            }
        }

        for (Map.Entry<Integer, Float> entrada : relevancias.entrySet()) {
            Pergunta p = super.read(entrada.getKey());
            if (p != null && p.isAtiva()) {
                resultados.add(new ResultadoBusca(p, entrada.getValue()));
            }
        }

        // ordena por relevância decrescente; em caso de empate, a mais recente primeiro
        resultados.sort((r1, r2) -> {
            int cmp = Float.compare(r2.getRelevancia(), r1.getRelevancia());
            if (cmp != 0) return cmp;
            return Long.compare(r2.getPergunta().getCriacao(), r1.getPergunta().getCriacao());
        });
        return resultados;
    }

    // mantém o índice invertido coerente quando as palavras-chave ou o estado (ativa) mudam
    @Override
    public boolean update(Pergunta perguntaAtualizada) throws Exception {
        Pergunta perguntaAntiga = super.read(perguntaAtualizada.getId());
        if (perguntaAntiga == null) return false;

        if (!super.update(perguntaAtualizada)) return false;

        if (perguntaAntiga.isAtiva() && !perguntaAtualizada.isAtiva()) {
            // arquivamento: sai do índice e deixa de contar para o IDF
            desindexarPalavrasChave(perguntaAntiga);
            indicePalavrasChave.decrementaEntidades();
        } else if (!perguntaAntiga.isAtiva() && perguntaAtualizada.isAtiva()) {
            // reativação: volta para o índice
            indexarPalavrasChave(perguntaAtualizada);
            indicePalavrasChave.incrementaEntidades();
        } else if (perguntaAtualizada.isAtiva()
                   && !perguntaAntiga.getPalavrasChave().equals(perguntaAtualizada.getPalavrasChave())) {
            // alteração das palavras-chave: remove os termos antigos e indexa os novos
            desindexarPalavrasChave(perguntaAntiga);
            indexarPalavrasChave(perguntaAtualizada);
        }
        return true;
    }

    // exclusão lógica: apenas muda ativa para false (update() retira a pergunta do índice invertido)
    public boolean arquivar(int idPergunta) throws Exception {
        Pergunta p = super.read(idPergunta);
        if (p == null) return false;
        p.setAtiva(false);
        p.setAlteracao(System.currentTimeMillis());
        return update(p);
    }

    @Override
    public boolean delete(int id) throws Exception {
        Pergunta p = super.read(id);
        if (p == null) return false;
        relUsuarioPergunta.delete(new ParIdId(p.getIdUsuario(), id));
        indiceDataPergunta.delete(new ParDataId(p.getCriacao(), id));
        if (p.isAtiva()) {
            // perguntas arquivadas já saíram do índice invertido
            desindexarPalavrasChave(p);
            indicePalavrasChave.decrementaEntidades();
        }
        return super.delete(id);
    }

    public void deleteAllByUsuario(int idUsuario) throws Exception {
        ArrayList<ParIdId> pares = relUsuarioPergunta.read(new ParIdId(idUsuario, -1));
        for (ParIdId par : pares) {
            Pergunta p = super.read(par.getId2());
            if (p != null) {
                indiceDataPergunta.delete(new ParDataId(p.getCriacao(), p.getId()));
                if (p.isAtiva()) {
                    desindexarPalavrasChave(p);
                    indicePalavrasChave.decrementaEntidades();
                }
            }
            super.delete(par.getId2());
            relUsuarioPergunta.delete(par);
        }
    }

    // insere o par (idPergunta, TF) na lista de cada termo das palavras-chave;
    // TF = ocorrências do termo / total de termos válidos da pergunta
    private void indexarPalavrasChave(Pergunta p) throws Exception {
        ArrayList<String> termos = ProcessadorTermos.extrairTermos(p.getPalavrasChave());
        if (termos.isEmpty()) return;

        float frequencia = 1.0f / termos.size();
        for (String termo : termos) {
            // a lista não aceita o par (termo, id) repetido: se já existe, acumula a frequência
            ElementoLista e = indicePalavrasChave.read(termo, p.getId());
            if (e == null) {
                indicePalavrasChave.create(termo, new ElementoLista(p.getId(), frequencia));
            } else {
                indicePalavrasChave.update(termo, new ElementoLista(p.getId(), frequencia + e.getFrequencia()));
            }
        }
    }

    // remove o id da pergunta da lista de cada termo das palavras-chave
    private void desindexarPalavrasChave(Pergunta p) throws Exception {
        LinkedHashSet<String> termos = new LinkedHashSet<>(ProcessadorTermos.extrairTermos(p.getPalavrasChave()));
        for (String termo : termos) {
            indicePalavrasChave.delete(termo, p.getId());
        }
    }

    @Override
    public void close() throws java.io.IOException {
        super.close();
    }
}
