package model;

public class Usuario {
	private long id;
	private String email;
	private String senha;
	private String telefone;
	private String uf;
	private String cep;
	private String rua;
	private String bairro;
	private String cidade;
	private String login;
	private String nome;
	private String genero;
	private byte tipoUsuario;
	private double lat;
	private double lng;
	private Prestador prestador;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public byte getTipoUsuario() {
		return this.tipoUsuario;
	}
	public void setTipoUsuario(byte tipoUsuario) {
		this.tipoUsuario = tipoUsuario;
	}
	public String getSenha() {
		return senha;
	}
	public void setSenha(String senha) {
		this.senha = senha;
	}
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getTelefone() {
		return telefone;
	}
	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}
	public String getUf() {
		return uf;
	}
	public void setUf(String uf) {
		this.uf = uf;
	}
	public String getCep() {
		return cep;
	}
	public void setCep(String cep) {
		this.cep = cep;
	}
	public String getRua() {
		return rua;
	}
	public void setRua(String rua) {
		this.rua = rua;
	}
	public String getCidade() {
		return cidade;
	}
	public void setCidade(String cidade) {
		this.cidade = cidade;
	}
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getGenero() {
		return genero;
	}
	public void setGenero(String genero) {
		this.genero = genero;
	}

	public String getBairro() {
		return bairro;
	}
	public void setBairro(String bairro) {
		this.bairro = bairro;
	}
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLng() {
		return lng;
	}
	public void setLng(double lng) {
		this.lng = lng;
	}
	public Prestador getPrestador() {
		return prestador;
	}
	public void setPrestador(Prestador prestador) {
		this.prestador = prestador;
	}
}
