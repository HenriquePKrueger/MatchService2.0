package model;

public class Agenda {
	private int id;
	private int prestadoresId;
	private String dataDisponivel;
	private String horarioInicio;
	private String horarioFim;
	private int statusHorario;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getPrestadoresId() {
		return prestadoresId;
	}
	public void setPrestadoresId(int prestadoresId) {
		this.prestadoresId = prestadoresId;
	}
	public String getDataDisponivel() {
		return dataDisponivel;
	}
	public void setDataDisponivel(String dataDisponivel) {
		this.dataDisponivel = dataDisponivel;
	}
	public String getHorarioInicio() {
		return horarioInicio;
	}
	public void setHorarioInicio(String horarioInicio) {
		this.horarioInicio = horarioInicio;
	}
	public String getHorarioFim() {
		return horarioFim;
	}
	public void setHorarioFim(String horarioFim) {
		this.horarioFim = horarioFim;
	}
	public int getStatusHorario() {
		return statusHorario;
	}
	public void setStatusHorario(int statusHorario) {
		this.statusHorario = statusHorario;
	}
}
