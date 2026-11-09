package entidades;

import aed3.Arquivo;
import aed3.HashExtensivel;

public class ArquivoUsuario extends Arquivo<Usuario> {

    // índice indireto: email → idUsuario
    HashExtensivel<ParEmailId> indiceEmail;

    public ArquivoUsuario() throws Exception {
        super("usuarios", Usuario.class.getConstructor());
        indiceEmail = new HashExtensivel<>(
            ParEmailId.class.getConstructor(),
            4,
            "./dados/usuarios/indiceEmail.diretorio.db",
            "./dados/usuarios/indiceEmail.cestos.db"
        );
    }

    @Override
    public int create(Usuario usuario) throws Exception {
        if (readByEmail(usuario.getEmail()) != null) {
            throw new Exception("Email já cadastrado: " + usuario.getEmail());
        }
        int id = super.create(usuario);
        indiceEmail.create(new ParEmailId(usuario.getEmail(), id));
        return id;
    }

    public Usuario readByEmail(String email) throws Exception {
        ParEmailId par = indiceEmail.read(ParEmailId.hash(email));
        if (par != null) {
            return super.read(par.getId());
        }
        return null;
    }

    @Override
    public boolean update(Usuario usuarioAtualizado) throws Exception {
        Usuario usuarioAntigo = super.read(usuarioAtualizado.getId());
        if (usuarioAntigo == null) return false;

        if (!usuarioAntigo.getEmail().equals(usuarioAtualizado.getEmail())) {
            if (readByEmail(usuarioAtualizado.getEmail()) != null) {
                throw new Exception("Email já em uso: " + usuarioAtualizado.getEmail());
            }
            // remove índice antigo e cria o novo
            indiceEmail.delete(ParEmailId.hash(usuarioAntigo.getEmail()));
            indiceEmail.create(new ParEmailId(usuarioAtualizado.getEmail(), usuarioAtualizado.getId()));
        }

        return super.update(usuarioAtualizado);
    }

    @Override
    public boolean delete(int id) throws Exception {
        Usuario usuario = super.read(id);
        if (usuario == null) return false;

        indiceEmail.delete(ParEmailId.hash(usuario.getEmail()));


        return super.delete(id);
    }

    @Override
    public void close() throws java.io.IOException {
        try { indiceEmail.close(); } catch (Exception e) { throw new java.io.IOException(e); }
        super.close();
    }
}
