package Biblioteca;
import java.util.Random;

//Paulo Victor Souto Andrade - 2315080046
//Ana Flavia de Castro Segadilha da Silva - 2315080055

public class Professor extends UsuarioABS {
    private String titulacao;
    private int senha;

    public Professor(String CPF, String nome, String email, String titulacao) {
        super(CPF, nome, email);
        this.titulacao = titulacao;
        this.senha = gerarSenha();
        
    }

    private int gerarSenha() {
        Random random = new Random();
        return random.nextInt(9000) + 1000;
    }

    public void consultarAcervo() {

    }

    public int getSenha() {
        return this.senha;
    }

	public String getTitulacao() {
		return titulacao;
	}

	public void setTitulacao(String titulacao) {
		this.titulacao = titulacao;
	}

	public void setSenha(int senha) {
		this.senha = senha;
	}
	
	@Override
    public String meusDados() {
        return "Professor CPF = " + getCPF() + ", Nome = " + getNome() + ", Email = " + getEmail() + ", Titulação = " + getTitulacao();
    }
	   
}
