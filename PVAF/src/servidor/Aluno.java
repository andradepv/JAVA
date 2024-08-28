package servidor;

public class Aluno extends User {
    private static final long serialVersionUID = 1L;
    private int anoIngresso;

    public Aluno(String login, String password, int anoIngresso) {
        super(login, password);
        this.anoIngresso = anoIngresso;
    }

    public int getAnoIngresso() {
        return anoIngresso;
    }

    @Override
    public String toString() {
        return super.toString() + ", Ano de Ingresso: " + anoIngresso;
    }
}

