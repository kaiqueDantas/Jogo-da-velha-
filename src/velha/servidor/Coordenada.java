package velha.servidor;

public class Coordenada {

    private final short linha;
    private final short coluna;

    public Coordenada(int linha, int coluna) {

        if (linha < 1 || linha > 3) {

            throw new IllegalArgumentException("A linha deve ser entre 1 e 3!!!");

        }

        if (coluna < 1 || coluna > 3) {

            throw new IllegalArgumentException("A coluna deve ser entre 1 e 3!!!");

        }

        this.linha = (short) linha;
        this.coluna = (short) coluna;
    }

    public short getLinha() {
        return linha;
    }

    public short getColuna() {
        return coluna;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + this.linha;
        hash = 67 * hash + this.coluna;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Coordenada)) {
            return false;
        }
        final Coordenada other = (Coordenada) obj;
        if (this.linha != other.linha) {
            return false;
        }
        if (this.coluna != other.coluna) {
            return false;
        }
        return true;
    }

}
