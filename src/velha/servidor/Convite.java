
package velha.servidor;


public class Convite {
    
    private final String jogador;
    private Boolean aceito;

    public Convite(String jogador) {
        this.jogador = jogador;
    }

    public Boolean getAceito() {
        return aceito;
    }

    public void setAceito(Boolean aceito) {
        this.aceito = aceito;
    }

    public String getJogador() {
        return jogador;
    }
    
    
    
}
