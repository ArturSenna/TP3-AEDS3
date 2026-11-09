package entidades;

// par (pergunta, relevância) devolvido pela busca por palavras-chave;
// a relevância é a soma de TF x IDF dos termos da consulta presentes na pergunta
public class ResultadoBusca {

    private final Pergunta pergunta;
    private final float    relevancia;

    public ResultadoBusca(Pergunta pergunta, float relevancia) {
        this.pergunta   = pergunta;
        this.relevancia = relevancia;
    }

    public Pergunta getPergunta()   { return pergunta; }
    public float    getRelevancia() { return relevancia; }

    @Override
    public String toString() {
        return String.format("(%d; %.3f)", pergunta.getId(), relevancia);
    }
}
