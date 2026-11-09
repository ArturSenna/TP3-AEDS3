package entidades;

import aed3.InterfaceRegistro;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

// entidade de resposta que conecta Usuario e Pergunta (relacionamento N:N)
public class Resposta implements InterfaceRegistro {

    private int     id;
    private int     idPergunta;
    private int     idUsuario;
    private long    criacao;
    private String  resposta;
    private short   nota;
    private short   avaliacoes;
    private boolean ativa;

    public Resposta() {
        this(-1, -1, -1, 0L, "", (short) 0, (short) 0, true);
    }

    public Resposta(int id, int idPergunta, int idUsuario, long criacao,
                    String resposta, short nota, short avaliacoes, boolean ativa) {
        this.id         = id;
        this.idPergunta = idPergunta;
        this.idUsuario  = idUsuario;
        this.criacao    = criacao;
        this.resposta   = resposta;
        this.nota       = nota;
        this.avaliacoes = avaliacoes;
        this.ativa      = ativa;
    }

    @Override public int  getId()       { return id; }
    @Override public void setId(int id) { this.id = id; }

    public int    getIdPergunta()               { return idPergunta; }
    public void   setIdPergunta(int idPergunta) { this.idPergunta = idPergunta; }

    public int    getIdUsuario()                { return idUsuario; }
    public void   setIdUsuario(int idUsuario)   { this.idUsuario = idUsuario; }

    public long   getCriacao()                  { return criacao; }
    public void   setCriacao(long criacao)      { this.criacao = criacao; }

    public String getResposta()                 { return resposta; }
    public void   setResposta(String resposta)  { this.resposta = resposta; }

    public short  getNota()                     { return nota; }
    public void   setNota(short nota)           { this.nota = nota; }

    public short  getAvaliacoes()                   { return avaliacoes; }
    public void   setAvaliacoes(short avaliacoes)   { this.avaliacoes = avaliacoes; }

    public boolean isAtiva()                    { return ativa; }
    public void    setAtiva(boolean ativa)      { this.ativa = ativa; }

    // serialização binária para o CRUD genérico
    @Override
    public byte[] serialize() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeInt(id);
        dos.writeInt(idPergunta);
        dos.writeInt(idUsuario);
        dos.writeLong(criacao);
        dos.writeUTF(resposta);
        dos.writeShort(nota);
        dos.writeShort(avaliacoes);
        dos.writeBoolean(ativa);

        return baos.toByteArray();
    }

    // desserialização a partir do fluxo de bytes lidos do disco
    @Override
    public void deserialize(byte[] data) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataInputStream dis = new DataInputStream(bais);

        id         = dis.readInt();
        idPergunta = dis.readInt();
        idUsuario  = dis.readInt();
        criacao    = dis.readLong();
        resposta   = dis.readUTF();
        nota       = dis.readShort();
        avaliacoes = dis.readShort();
        ativa      = dis.readBoolean();
    }

    @Override
    public String toString() {
        return String.format(
            "ID: %d | Pergunta: %d | Autor: %d | Nota: %d (%d avaliações) | Ativa: %b%n%s",
            id, idPergunta, idUsuario, nota, avaliacoes, ativa, resposta
        );
    }
}
