package entidades;

import aed3.InterfaceRegistro;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.text.Normalizer;

public class Usuario implements InterfaceRegistro {

    private int    id;
    private String nome;
    private String email;
    private int    hashSenha;
    private String perguntaSecreta;
    private int    hashRespostaSecreta;

    public Usuario() {
        this(-1, "", "", 0, "", 0);
    }

    public Usuario(int id, String nome, String email,
                   int hashSenha, String perguntaSecreta, int hashRespostaSecreta) {
        this.id                  = id;
        this.nome                = nome;
        this.email               = email;
        this.hashSenha           = hashSenha;
        this.perguntaSecreta     = perguntaSecreta;
        this.hashRespostaSecreta = hashRespostaSecreta;
    }

    @Override public int    getId()    { return id; }
    @Override public void   setId(int id) { this.id = id; }

    public String getNome()               { return nome; }
    public void   setNome(String nome)    { this.nome = nome; }

    public String getEmail()              { return email; }
    public void   setEmail(String email)  { this.email = email; }

    public int  getHashSenha()                 { return hashSenha; }
    public void setHashSenha(int hashSenha)    { this.hashSenha = hashSenha; }

    public String getPerguntaSecreta()                       { return perguntaSecreta; }
    public void   setPerguntaSecreta(String perguntaSecreta) { this.perguntaSecreta = perguntaSecreta; }

    public int  getHashRespostaSecreta()                       { return hashRespostaSecreta; }
    public void setHashRespostaSecreta(int hashRespostaSecreta){ this.hashRespostaSecreta = hashRespostaSecreta; }

    // nunca armazenar a senha em texto puro
    public static int hashSenha(String senha) {
        return Math.abs(senha.hashCode());
    }

    // hash sem acento, minúsculo, sem espaços extras
    public static int hashResposta(String resposta) {
        String normalizada = Normalizer.normalize(resposta, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "")
                .toLowerCase()
                .trim();
        return Math.abs(normalizada.hashCode());
    }

    @Override
    public byte[] serialize() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeInt(id);
        dos.writeUTF(nome);
        dos.writeUTF(email);
        dos.writeInt(hashSenha);
        dos.writeUTF(perguntaSecreta);
        dos.writeInt(hashRespostaSecreta);

        return baos.toByteArray();
    }

    @Override
    public void deserialize(byte[] data) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataInputStream dis = new DataInputStream(bais);

        id                  = dis.readInt();
        nome                = dis.readUTF();
        email               = dis.readUTF();
        hashSenha           = dis.readInt();
        perguntaSecreta     = dis.readUTF();
        hashRespostaSecreta = dis.readInt();
    }

    @Override
    public String toString() {
        return String.format("ID: %d | Nome: %s | Email: %s", id, nome, email);
    }
}
