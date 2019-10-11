package velha.servidor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Usuario implements Runnable {

    private static long cont = 0;
    Queue<Convite> convites = new LinkedBlockingQueue<>();
    UsuarioStatus status = UsuarioStatus.OFFILINE;
    String nome;
    final Socket socket;
    final ObjectOutputStream out;
    final ObjectInputStream in;
    final Servidor servidor;
    final long id;

    public Usuario(Servidor servidor, Socket socket) throws IOException {
        this.id = cont++;
        this.socket = socket;
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());
        this.servidor = servidor;
    }

    public long getId() {
        return id;

    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public UsuarioStatus getStatus() {
        if (!socket.isConnected()) {
            return UsuarioStatus.OFFILINE;

        }
        Jogo jogo = servidor.getJogos().get(this);
        if (jogo == null || jogo.isTerminado()) {
            return UsuarioStatus.DISPONIVEL;

        }

        return UsuarioStatus.JOGANDO;

    }

    private void realizarJogada() throws IOException {

        Jogo jogo = servidor.getJogos().get(this);

        if (jogo == null) {

            enviarResposta(false, "Você não está em um jogo.");
            return;

        }

        enviarResposta(true, "Você está em um jogo.");
        out.writeBoolean(jogo.isTerminado());

        if (jogo.isTerminado()) {
            servidor.getJogos().remove(this);
            out.writeUTF(jogo.getVencedor().getNome());
            out.flush();
            return;
        }
        boolean atual = equals(jogo.getJogadorAtual());
        out.writeBoolean(atual);
        out.flush();
        if (atual) {
            try {
                jogo.jogada(this, new Coordenada(in.readInt(), in.readInt()));
                enviarResposta(true, "Joga realizada!!!");
            } catch (JogoTerminadoException | JogadaInvalidaException ex) {
                enviarResposta(false, ex.getLocalizedMessage());
            }
        }

    }

    private void listarJogadores() throws IOException {
        Set<Usuario> lista = servidor.getUsuarios().stream().filter(u -> u.getNome() != null).collect(Collectors.toSet());
        out.writeInt(lista.size());
        for (Usuario usuario : lista) {

            out.writeLong(usuario.getId());
            out.writeUTF(usuario.getNome());
            out.writeUTF(usuario.getStatus().name());
        }
        out.flush();

    }

    private void convidarJogador() throws IOException {

        long idJogador = in.readLong();

        Usuario convidado = servidor.getUsuarioMap().get(idJogador);

        if (convidado == null) {
            enviarResposta(false, "Usuário não encontrado!!!");

        } else {
            if (convidado.getStatus().isJogavel()) {
                Convite convite = convidado.conviteRecebido(nome);
                while (convite.getAceito() == null) {
                    synchronized (this) {
                        try {
                            wait(500);
                        } catch (InterruptedException ex) {
                            enviarResposta(false, "Usuário demorou a responder!!!");
                            return;

                        }
                    }
                    if (convidado.getStatus() == UsuarioStatus.OFFILINE) {
                        enviarResposta(false, "Usuário desconectado!!!");
                        return;
                    }
                }

                if (convite.getAceito()) {

                    enviarResposta(true, "Usuário aceitou!!!!!");
                    Jogo jogo = new Jogo(this, convidado);
                    servidor.getJogos().put(this, jogo);
                    servidor.getJogos().put(convidado, jogo);

                } else {
                    enviarResposta(false, "Usuário recusou!!!");

                }

            } else {
                enviarResposta(false, "Usuário indisponível!!!");

            }

        }

    }

    private Convite conviteRecebido(String nomeJogador) {

        Convite convite = new Convite(nomeJogador);
        convites.add(convite);
        return convite;
    }

    private void erro() {

    }

    @Override
    public void run() {
        try {
            nome = in.readUTF().trim();

            comando:
            while (true) {

                switch (in.readUTF()) {

                    case "jogada": {
                        realizarJogada();
                        break;
                    }
                    case "listar": {
                        listarJogadores();
                        break;
                    }
                    case "convidar": {
                        convidarJogador();
                        break;
                    }

                    case "listar jogadas": {
                        listarJogadas();
                        break;
                    }
                    case "sair": {
                        break comando;
                    }

                    default:
                        erro();

                }

                if (convites.isEmpty()) {
                    out.writeUTF("aguardando");
                } else {
                    out.writeUTF("convite recebido");
                    Convite convite = convites.poll();
                    out.writeUTF(convite.getJogador());
                    convite.setAceito(in.readBoolean());
                }
                out.flush();

            }
            socket.close();

        } catch (IOException ex) {
            Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + (int) (this.id ^ (this.id >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Usuario)) {
            return false;
        }
        final Usuario other = (Usuario) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    private void enviarResposta(boolean b, String resp) throws IOException {

        out.writeBoolean(b);
        out.writeUTF(resp);
        out.flush();
    }

    private void listarJogadas() throws IOException {
        Jogo jogo = servidor.getJogos().get(this);

        if (jogo == null) {

            enviarResposta(false, "Você não está em um jogo.");
            return;

        }
        Map<Usuario, ValorCampo> jogadas = jogo.getJogada();
        out.writeInt(jogadas.size());
        for (Map.Entry<Usuario, ValorCampo> jogada : jogadas.entrySet()) {
            out.writeUTF(jogada.getKey().getNome());
            out.writeUTF(jogada.getValue().name());
        }
        Map<Coordenada, ValorCampo> campos = jogo.getCampo();
        out.writeInt(campos.size());
        for (Map.Entry<Coordenada, ValorCampo> campo : campos.entrySet()) {
            out.writeInt(campo.getKey().getLinha());
            out.writeInt(campo.getKey().getColuna());
            out.writeUTF(campo.getValue().name());
        }
        out.flush();
    }

}
