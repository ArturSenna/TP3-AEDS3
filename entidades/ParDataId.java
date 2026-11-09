package entidades;

import aed3.InterfaceArvoreBMais;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ParDataId implements InterfaceArvoreBMais<ParDataId> {

    private long data;
    private int  id;
    private static final short TAMANHO = 12; // 8 bytes (long) + 4 bytes (int)

    public ParDataId() {
        this(-1L, -1);
    }

    public ParDataId(long data) {
        this(data, -1);
    }

    public ParDataId(long data, int id) {
        this.data = data;
        this.id   = id;
    }

    public long getData() {
        return this.data;
    }

    public int getId() {
        return this.id;
    }

    @Override
    public ParDataId clone() {
        return new ParDataId(this.data, this.id);
    }

    @Override
    public short size() {
        return TAMANHO;
    }

    // Ordenação decrescente de data: datas maiores vêm primeiro na árvore
    @Override
    public int compareTo(ParDataId outro) {
        if (this.data != outro.data) {
            return this.data > outro.data ? -1 : 1;
        }
        if (this.id == -1 || outro.id == -1) {
            return 0;
        }
        return this.id - outro.id;
    }

    @Override
    public String toString() {
        return "(" + this.data + ";" + this.id + ")";
    }

    @Override
    public byte[] serialize() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeLong(this.data);
        dos.writeInt(this.id);
        return baos.toByteArray();
    }

    @Override
    public void deserialize(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);
        this.data = dis.readLong();
        this.id   = dis.readInt();
    }
}
