package controle;

import entidades.ArquivoUsuario;
import entidades.Usuario;

public class ControleUsuario {

    private ArquivoUsuario arqUsuario;

    // usuário atualmente logado na sessão
    private Usuario usuarioAtivo;

    public ControleUsuario() throws Exception {
        arqUsuario = new ArquivoUsuario();
    }

    public boolean login(String email, String senha) throws Exception {
        Usuario usuario = arqUsuario.readByEmail(email);
        if (usuario != null && usuario.getHashSenha() == Usuario.hashSenha(senha)) {
            this.usuarioAtivo = usuario;
            return true;
        }
        return false;
    }

    public void logout() {
        this.usuarioAtivo = null;
    }

    public Usuario getUsuarioAtivo() {
        return usuarioAtivo;
    }

    public Usuario buscarPorId(int id) throws Exception {
        return arqUsuario.read(id);
    }

    public int cadastrar(String nome, String email, String senha,
                         String perguntaSecreta, String respostaSecreta) throws Exception {
        Usuario novo = new Usuario(
            -1,
            nome,
            email,
            Usuario.hashSenha(senha),
            perguntaSecreta,
            Usuario.hashResposta(respostaSecreta)
        );
        return arqUsuario.create(novo); // lança Exception se email já existir
    }

    public boolean validarRespostaSecreta(String email, String resposta) throws Exception {
        Usuario usuario = arqUsuario.readByEmail(email);
        if (usuario == null) return false;
        return usuario.getHashRespostaSecreta() == Usuario.hashResposta(resposta);
    }

    public boolean alterarNome(String novoNome) throws Exception {
        usuarioAtivo.setNome(novoNome);
        boolean ok = arqUsuario.update(usuarioAtivo);
        if (!ok) usuarioAtivo = arqUsuario.read(usuarioAtivo.getId()); // reverte em memória
        return ok;
    }

    // ArquivoUsuario.update() já cuida de reindexar o Hash se o email mudar
    public boolean alterarEmail(String novoEmail) throws Exception {
        usuarioAtivo.setEmail(novoEmail);
        boolean ok = arqUsuario.update(usuarioAtivo);
        if (!ok) usuarioAtivo = arqUsuario.read(usuarioAtivo.getId());
        return ok;
    }

    public boolean alterarSenha(String novaSenha) throws Exception {
        usuarioAtivo.setHashSenha(Usuario.hashSenha(novaSenha));
        boolean ok = arqUsuario.update(usuarioAtivo);
        if (!ok) usuarioAtivo = arqUsuario.read(usuarioAtivo.getId());
        return ok;
    }

    public boolean alterarPerguntaSecreta(String novaPergunta, String novaResposta) throws Exception {
        usuarioAtivo.setPerguntaSecreta(novaPergunta);
        usuarioAtivo.setHashRespostaSecreta(Usuario.hashResposta(novaResposta));
        boolean ok = arqUsuario.update(usuarioAtivo);
        if (!ok) usuarioAtivo = arqUsuario.read(usuarioAtivo.getId());
        return ok;
    }

    public String getPerguntaSecretaPorEmail(String email) throws Exception{
        Usuario usuario = arqUsuario.readByEmail(email);
        if(usuario == null) return null;
        return usuario.getPerguntaSecreta();
    }

    public boolean redefinirSenhaPorEmail(String email, String novaSenha) throws Exception{
        Usuario usuario = arqUsuario.readByEmail(email);
        if(usuario == null) return false;
        usuario.setHashSenha(Usuario.hashSenha(novaSenha));
        return arqUsuario.update(usuario);
    }

    public void close() throws Exception {
        arqUsuario.close();
    }
}
