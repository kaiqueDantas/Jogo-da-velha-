package velha.servidor;

import java.util.HashMap;
import java.util.Map;

public class Jogo  {

    public Usuario getJogador1() {
        return jogador1;
    }

    public Usuario getJogador2() {
        return jogador2;
    }

    public boolean isTerminado() {
        return terminado;
    }

    public Usuario getJogadorAtual() {
        return jogadorAtual;
    }

    public Usuario getVencedor() {
        return vencedor;
    }

    public Map<Coordenada, ValorCampo> getCampo() {
        return campo;
    }

    public Map<Usuario, ValorCampo> getJogada() {
        return jogada;
    }

   private Usuario jogador1;
   private Usuario jogador2;
   private boolean terminado = false;
   private Usuario jogadorAtual;
   private Usuario vencedor;

    private Map<Coordenada, ValorCampo> campo = new HashMap<>(9);
   private Map<Usuario, ValorCampo> jogada = new HashMap<>(2);

    public Jogo(Usuario jogador1, Usuario jogador2) {
        this.jogador1 = jogador1;
        this.jogador2 = jogador2;
        jogadorAtual = jogador1;
        jogada.put(jogador1, ValorCampo.O);
        jogada.put(jogador2, ValorCampo.X);
    }
   
   

    public synchronized void jogada (Usuario usuario, Coordenada coordenada) throws JogadaInvalidaException, JogoTerminadoException {
        
        if (usuario == null) {

            throw new IllegalArgumentException("Usuário não existe!!!");

        }

        if (!usuario.equals(jogador1) && !usuario.equals(jogador2)) {

            throw new IllegalArgumentException("Usuário não faz parte do jogo!!!");

        }

        if (!jogadorAtual.equals(usuario)) {

            throw new IllegalArgumentException("A vez do jogador " + jogadorAtual.getNome());
        }

        if (terminado) {

            throw new JogoTerminadoException();

        }

        if (campo.containsKey(coordenada)) {

            throw new JogadaInvalidaException();

        }

        campo.put(coordenada, jogada.get(usuario));

        verificaFimJogo();
        
        if(terminado && !velha()){
        
            vencedor = jogadorAtual;
            return;
            
            
        }

        if (jogadorAtual.equals(jogador1)) {

            jogadorAtual = jogador2;
        } else {

            jogadorAtual = jogador1;
        }

    }

    public void verificaFimJogo() {

        for (int linha = 1; linha <= 3; linha++) {

            if (verificaLinha(linha)) {
                terminado = true;
                return;
            }

        }

        for (int coluna = 1; coluna <= 3; coluna++) {

            if (verificaColuna(coluna)) {
                terminado = true;
                return;
            }

        }
        if (verificaDiagonalDireita()) {
            terminado = true;
            return;

        }
        if (verificaDiagobnalEsquerda()) {
            terminado = true;
            return;

        }

        if (velha()) {

            terminado = true;
            return;

        }
    }

    private boolean verificaLinha(int linha) {

        return camposIguais(campo.get(new Coordenada(linha, 1)),
                campo.get(new Coordenada(linha, 2)),
                campo.get(new Coordenada(linha, 3)));

    }

    private boolean verificaColuna(int coluna) {
        return camposIguais(campo.get(new Coordenada(1, coluna)),
                campo.get(new Coordenada(2, coluna)),
                campo.get(new Coordenada(3, coluna)));
    }

    private boolean verificaDiagonalDireita() {
        return camposIguais(campo.get(new Coordenada(1, 1)),
                campo.get(new Coordenada(2, 2)),
                campo.get(new Coordenada(3, 3)));

    }

    private boolean verificaDiagobnalEsquerda() {
        return camposIguais(campo.get(new Coordenada(1, 3)),
                campo.get(new Coordenada(2, 2)),
                campo.get(new Coordenada(3, 1)));
    }

    private boolean velha() {

        return campo.size() == 9;

    }

    private boolean camposIguais(ValorCampo c1, ValorCampo c2, ValorCampo c3) {

        return c1 == c2 && c1 == c3;

    }

}
