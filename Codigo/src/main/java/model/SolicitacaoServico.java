package model;

import java.util.ArrayList;
import java.util.List;

public class SolicitacaoServico {
	private Long id;
	private String descricao;
	private String rua;
	private String bairro;
	private String cidade;
	private int status;
	private int tipoSolicitacao;
	private Long usuariosId;
	private double lat;
	private double lng;
	private String created_at;
	private String cep;
	private Categoria categoria;
	private List<SolicitacaoImagem> solicitacaoImagens;
	private Long prestadoresId;
	private Long agendaId;

	public void setPrestadoresId(Long prestadoresId) {
    		this.prestadoresId = prestadoresId;
	}

		public Long getAgendaId() {
    		return agendaId;
	}

		public void setAgendaId(Long agendaId) {
    		this.agendaId = agendaId;
		}

	
	public SolicitacaoServico() {
		setSolicitacaoImagens(new ArrayList<>());
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public String getRua() {
		return rua;
	}
	public void setRua(String rua) {
		this.rua = rua;
	}
	public String getBairro() {
		return bairro;
	}
	public void setBairro(String bairro) {
		this.bairro = bairro;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getTipoSolicitacao() {
		return tipoSolicitacao;
	}
	public void setTipoSolicitacao(int tipoSolicitacao) {
		this.tipoSolicitacao = tipoSolicitacao;
	}
	public Long getUsuariosId() {
		return usuariosId;
	}
	public void setUsuariosId(Long usuariosId) {
		this.usuariosId = usuariosId;
	}
	public double getLat() {
		return this.lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLng() {
		return this.lng;
	}
	public void setLng(double lng) {
		this.lng = lng;
	}
	public void setCreatedAt(String createdAt) {
		this.created_at = createdAt;
	}
	public String getCreatedAt() {
		return this.created_at;
	}
	public void setCep(String cep) {
		this.cep =  cep;
	}
	public String getCep() {
		return this.cep;
	}

	public Categoria getCategoria() {
		return categoria;
	}
	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}
	public List<SolicitacaoImagem> getSolicitacaoImagens() {
		return solicitacaoImagens;
	}
	public void setSolicitacaoImagens(List<SolicitacaoImagem> solicitacaoImagens) {
		this.solicitacaoImagens = solicitacaoImagens;
	}
	public String getCidade() {
		return cidade;
	}
	public void setCidade(String cidade) {
		this.cidade = cidade;
	}
}
