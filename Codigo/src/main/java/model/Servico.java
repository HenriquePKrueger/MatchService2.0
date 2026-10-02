package model;

public class Servico {
	private int id;
	private int status;
	private String createdAt;
	private int solicitacoesServicosId;
	private int prestadoresId;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}
	public int getSolicitacoesServicosId() {
		return solicitacoesServicosId;
	}
	public void setSolicitacoesServicosId(int solicitacoesServicosId) {
		this.solicitacoesServicosId = solicitacoesServicosId;
	}
	public int getPrestadoresId() {
		return prestadoresId;
	}
	public void setPrestadoresId(int prestadoresId) {
		this.prestadoresId = prestadoresId;
	}
}
