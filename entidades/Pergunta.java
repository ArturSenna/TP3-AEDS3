package entidades;

import aed3.InterfaceRegistro;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

public class Pergunta implements InterfaceRegistro {

    private int     id;
    private int     idUsuario;
    private long    criacao;       // milissegundos desde epoch
    private long    alteracao;     // milissegundos desde epoch
    private short   nota;          // soma dos votos (pode ser negativa)
    private String  pergunta;
    private String  palavrasChave; // termos separados por ";"
    private boolean ativa;

    public Pergunta() {
        this(-1, -1, 0L, 0L, (short) 0, "", "", true);
    }

    public Pergunta(int id, int idUsuario, long criacao, long alteracao,
                    short nota, String pergunta, String palavrasChave, boolean ativa) {
        this.id           = id;
        this.idUsuario    = idUsuario;
        this.criacao      = criacao;
        this.alteracao    = alteracao;
        this.nota         = nota;
        this.pergunta     = pergunta;
        this.palavrasChave = palavrasChave;
        this.ativa        = ativa;
    }

    @Override public int  getId()         { return id; }
    @Override public void setId(int id)   { this.id = id; }

    public int    getIdUsuario()                  { return idUsuario; }
    public void   setIdUsuario(int idUsuario)     { this.idUsuario = idUsuario; }

    public long   getCriacao()                    { return criacao; }
    public void   setCriacao(long criacao)        { this.criacao = criacao; }

    public long   getAlteracao()                  { return alteracao; }
    public void   setAlteracao(long alteracao)    { this.alteracao = alteracao; }

    public short  getNota()                       { return nota; }
    public void   setNota(short nota)             { this.nota = nota; }

    public String getPergunta()                   { return pergunta; }
    public void   setPergunta(String pergunta)    { this.pergunta = pergunta; }

    public String getPalavrasChave()                      { return palavrasChave; }
    public void   setPalavrasChave(String palavrasChave)  { this.palavrasChave = palavrasChave; }

    public boolean isAtiva()                      { return ativa; }
    public void    setAtiva(boolean ativa)        { this.ativa = ativa; }

    @Override
    public byte[] serialize() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeInt(id);
        dos.writeInt(idUsuario);
        dos.writeLong(criacao);
        dos.writeLong(alteracao);
        dos.writeShort(nota);
        dos.writeUTF(pergunta);
        dos.writeUTF(palavrasChave);
        dos.writeBoolean(ativa);

        return baos.toByteArray();
    }

    @Override
    public void deserialize(byte[] data) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataInputStream dis = new DataInputStream(bais);

        id            = dis.readInt();
        idUsuario     = dis.readInt();
        criacao       = dis.readLong();
        alteracao     = dis.readLong();
        nota          = dis.readShort();
        pergunta      = dis.readUTF();
        palavrasChave = dis.readUTF();
        ativa         = dis.readBoolean();
    }

    @Override
    public String toString() {
        return String.format(
            "ID: %d | Usuário: %d | Ativa: %b%n%s%nPalavras-chave: %s",
            id, idUsuario, ativa, pergunta, palavrasChave
        );
    }
}
