package model;

public class SolicitacaoDireta {
    private long id;
    private String descricao;
    private int status;
    private String createdAt;

    private long clienteId;
    private String nomeCliente;
    private String telefoneCliente;
    private String cidadeCliente;
    private String bairroCliente;
    private String ruaCliente;

    private long prestadorId;
    private long agendaId;
    private String dataDisponivel;
    private String horarioInicio;
    private String horarioFim;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public long getClienteId() { return clienteId; }
    public void setClienteId(long clienteId) { this.clienteId = clienteId; }

    public String getNomeCliente() { return nomeCliente; }
    public void setNomeCliente(String nomeCliente) { this.nomeCliente = nomeCliente; }

    public String getTelefoneCliente() { return telefoneCliente; }
    public void setTelefoneCliente(String telefoneCliente) { this.telefoneCliente = telefoneCliente; }

    public String getCidadeCliente() { return cidadeCliente; }
    public void setCidadeCliente(String cidadeCliente) { this.cidadeCliente = cidadeCliente; }

    public String getBairroCliente() { return bairroCliente; }
    public void setBairroCliente(String bairroCliente) { this.bairroCliente = bairroCliente; }

    public String getRuaCliente() { return ruaCliente; }
    public void setRuaCliente(String ruaCliente) { this.ruaCliente = ruaCliente; }

    public long getPrestadorId() { return prestadorId; }
    public void setPrestadorId(long prestadorId) { this.prestadorId = prestadorId; }

    public long getAgendaId() { return agendaId; }
    public void setAgendaId(long agendaId) { this.agendaId = agendaId; }

    public String getDataDisponivel() { return dataDisponivel; }
    public void setDataDisponivel(String dataDisponivel) { this.dataDisponivel = dataDisponivel; }

    public String getHorarioInicio() { return horarioInicio; }
    public void setHorarioInicio(String horarioInicio) { this.horarioInicio = horarioInicio; }

    public String getHorarioFim() { return horarioFim; }
    public void setHorarioFim(String horarioFim) { this.horarioFim = horarioFim; }
}