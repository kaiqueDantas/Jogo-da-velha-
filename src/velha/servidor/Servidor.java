package velha.servidor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Servidor implements Runnable {

    private Map<Long, Usuario> usuarios;

    private Map<Usuario, Jogo> jogos;

    public Set<Usuario> getUsuarios() {
        return new HashSet<>(usuarios.values());
    }

    public Map<Long, Usuario> getUsuarioMap() {

        return usuarios;

    }

    public Map<Usuario, Jogo> getJogos() {
        return jogos;
    }

    @Override
    public void run() {
        usuarios = new HashMap<>();
        jogos = new HashMap<>();

        try (ServerSocket server = new ServerSocket(10001)) {
            while (true) {
                try {
                    Socket socket = server.accept();
                    Usuario usuario = new Usuario(this,socket);

                    new Thread(usuario,"Usuário ="+usuario.getId()).start();
                    usuarios.put(usuario.getId(), usuario);

                } catch (IOException ex) {
                    Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
