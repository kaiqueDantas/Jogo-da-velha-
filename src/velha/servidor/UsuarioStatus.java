
package velha.servidor;


public enum UsuarioStatus {
    
   DISPONIVEL(true),
   JOGANDO,
   OFFILINE;
   
   private boolean jogavel;

    private UsuarioStatus() {
        this(false);
    }
    
     private UsuarioStatus(boolean jogavel) {
        this.jogavel = jogavel;
    }

    public boolean isJogavel() {
        return jogavel;
    }
   
    
   
    
}
