package Biblioteca;
import java.util.Random;

//Paulo Victor Souto Andrade - 2315080046
//Ana Flavia de Castro Segadilha da Silva - 2315080055

public class Funcionario extends UsuarioABS {
    private Cargo cargo;
    private double salario;
    private int senha;

    public Funcionario(String CPF, String nome, String email, Cargo cargo, float salario) {
        super(CPF, nome, email);
        this.cargo = cargo;
        this.salario = salario;
        this.senha = gerarSenhaf();
    }

    private int gerarSenhaf() {
        Random random = new Random();
        return random.nextInt(9000) + 1000;
    }

    public void cadastrarUsuario(UsuarioABS usuario) {

    }

    public void cadastrarLivro(Livro livro) {

    }

    public Cargo getCargo() {
		return cargo;
	}

	public void setCargo(Cargo cargo) {
		this.cargo = cargo;
	}

	public double getSalario() {
		return salario;
	}

	public void setSalario(double salario) {
		this.salario = salario;
	}

	public int getSenha() {
		return senha;
	}

	public void setSenha(int senha) {
		this.senha = senha;
	}
    @Override
	public String meusDados() {
	    return "Funcionario CPF = " + getCPF() + ", Nome = " + getNome() + ", Email = " + getEmail() + ", Cargo = " + cargo.getNome() +
	            ", Salário=" + salario;
	}
    
}
