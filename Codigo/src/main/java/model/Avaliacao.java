package model;

public class Avaliacao {
	private String nomeCliente;
	private int nota;
	private String comentario;

	public Avaliacao(String nomeCliente, int nota, String comentario) {
		this.nomeCliente = nomeCliente;
		this.nota = nota;
		this.comentario = comentario;
	}

	public String getNomeCliente() {
		return nomeCliente;
	}

	public void setNomeCliente(String nomeCliente) {
		this.nomeCliente = nomeCliente;
	}

	public int getNota() {
		return nota;
	}

	public void setNota(int nota) {
		this.nota = nota;
	}

	public String getComentario() {
		return comentario;
	}

	public void setComentario(String comentario) {
		this.comentario = comentario;
	}
}
