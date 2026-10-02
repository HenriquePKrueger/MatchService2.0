package model;

public class Notificacao {
	private int id;
	private long usuariosId;
	private String mensagem;
	private boolean lida;
	private String createdAt;
	private int ofertasId;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public long getUsuariosId() {
		return usuariosId;
	}
	public void setUsuariosId(long usuariosId) {
		this.usuariosId = usuariosId;
	}
	public String getMensagem() {
		return mensagem;
	}
	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}
	public boolean isLida() {
		return lida;
	}
	public void setLida(boolean lida) {
		this.lida = lida;
	}
	public String getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}
	public int getOfertasId() {
		return ofertasId;
	}
	public void setOfertasId(int ofertasId) {
		this.ofertasId = ofertasId;
	}
}
