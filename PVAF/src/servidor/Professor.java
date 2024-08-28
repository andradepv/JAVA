package servidor;

public class Professor extends User {
    private static final long serialVersionUID = 1L;
    private String titulacao;

    public Professor(String login, String password, String titulacao) {
        super(login, password);
        this.titulacao = titulacao;
    }

    public String getTitulacao() {
        return titulacao;
    }

    @Override
    public String toString() {
        return super.toString() + ", Titulação: " + titulacao;
    }
}
