package Biblioteca;

//Paulo Victor Souto Andrade - 2315080046
//Ana Flavia de Castro Segadilha da Silva - 2315080055

public abstract class UsuarioABS {
	
	private String CPF;
	private String nome;
	private String email;

	public UsuarioABS(String CPF, String nome, String email) {
	    this.CPF = CPF;
	    this.nome = nome;
	    this.email = email;
	}

		public String getCPF() {
			return CPF;
		}

		public void setCPF(String cPF) {
			CPF = cPF;
		}

		public String getNome() {
			return nome;
		}

		public void setNome(String nome) {
			this.nome = nome;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}
		
		public abstract String meusDados();
		
}
