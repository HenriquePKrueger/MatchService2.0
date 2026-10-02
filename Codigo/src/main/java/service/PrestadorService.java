package service;

import java.util.HashMap;
import java.util.Map;

import dao.PrestadorDAO;
import dao.UsuarioDAO;
import dao.PrestadorDAO.NivelDetalhePrestador;
import model.Prestador;
import model.Usuario;
import spark.Request;
import spark.Response;


public class PrestadorService {
	private PrestadorDAO prestadorDAO; 

	public PrestadorService() {
		this.prestadorDAO = new PrestadorDAO();
	}

	
	public Map<String, Object> obterContatosDoPrestador(Long idPrestador) {
		Map<String, Object> contatos = new HashMap<>();
		
		Prestador p = prestadorDAO.selectPrestador("id", idPrestador, NivelDetalhePrestador.COM_USUARIO);

		contatos.put("prestador", p);
		
		return contatos;
	}
}
