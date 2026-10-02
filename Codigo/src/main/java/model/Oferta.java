package model;

public class Oferta {
	private int id;
	private String descricao;
	private double valor;
	private String disponibilidade;
	private int status;
	private String justificativaRecusa;
	private String createdAt;
	private int solicitacoesServicosId;
	private int prestadoresId;
	private String prestadorNome;
	private String solicitacaoDescricao;
	private String clienteNome;
	private String clienteTelefone;
	private String clienteEmail;
	private String clienteWhatsappUrl;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public double getValor() {
		return valor;
	}
	public void setValor(double valor) {
		this.valor = valor;
	}
	public String getDisponibilidade() {
		return disponibilidade;
	}
	public void setDisponibilidade(String disponibilidade) {
		this.disponibilidade = disponibilidade;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getJustificativaRecusa() {
		return justificativaRecusa;
	}
	public void setJustificativaRecusa(String justificativaRecusa) {
		this.justificativaRecusa = justificativaRecusa;
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
	public String getPrestadorNome() {
		return prestadorNome;
	}
	public void setPrestadorNome(String prestadorNome) {
		this.prestadorNome = prestadorNome;
	}
	public String getSolicitacaoDescricao() {
		return solicitacaoDescricao;
	}
	public void setSolicitacaoDescricao(String solicitacaoDescricao) {
		this.solicitacaoDescricao = solicitacaoDescricao;
	}
	public String getClienteNome() {
		return clienteNome;
	}
	public void setClienteNome(String clienteNome) {
		this.clienteNome = clienteNome;
	}
	public String getClienteTelefone() {
		return clienteTelefone;
	}
	public void setClienteTelefone(String clienteTelefone) {
		this.clienteTelefone = clienteTelefone;
	}
	public String getClienteEmail() {
		return clienteEmail;
	}
	public void setClienteEmail(String clienteEmail) {
		this.clienteEmail = clienteEmail;
	}
	public String getClienteWhatsappUrl() {
		return clienteWhatsappUrl;
	}
	public void setClienteWhatsappUrl(String clienteWhatsappUrl) {
		this.clienteWhatsappUrl = clienteWhatsappUrl;
	}
}
