package model;

import java.util.List;

public class PrestadorPerfil {
    private long prestadorId;
    private long usuarioId;
    private String nome;
    private String telefone;
    private String cidade;
    private String bairro;
    private String rua;
    private String descricao;
    private String categorias;
    private List<Agenda> horariosLivres;

    public long getPrestadorId() { return prestadorId; }
    public void setPrestadorId(long prestadorId) { this.prestadorId = prestadorId; }

    public long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(long usuarioId) { this.usuarioId = usuarioId; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }

    public String getBairro() { return bairro; }
    public void setBairro(String bairro) { this.bairro = bairro; }

    public String getRua() { return rua; }
    public void setRua(String rua) { this.rua = rua; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getCategorias() { return categorias; }
    public void setCategorias(String categorias) { this.categorias = categorias; }

    public List<Agenda> getHorariosLivres() { return horariosLivres; }
    public void setHorariosLivres(List<Agenda> horariosLivres) { this.horariosLivres = horariosLivres; }
}