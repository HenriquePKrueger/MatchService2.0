package model;

public class SolicitacaoImagem {
	private int id;
	private String url;
	private int solicitacoesServicosId;
	private String nomeOriginal;
	private String contentType;
	private long tamanhoBytes;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public int getSolicitacoesServicosId() {
		return solicitacoesServicosId;
	}
	public void setSolicitacoesServicosId(int solicitacoesServicosId) {
		this.solicitacoesServicosId = solicitacoesServicosId;
	}
	public String getNomeOriginal() {
		return nomeOriginal;
	}
	public void setNomeOriginal(String nomeOriginal) {
		this.nomeOriginal = nomeOriginal;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public long getTamanhoBytes() {
		return tamanhoBytes;
	}
	public void setTamanhoBytes(long tamanhoBytes) {
		this.tamanhoBytes = tamanhoBytes;
	}
}
