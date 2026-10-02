package service;

import java.util.List;
import model.Categoria;
import dao.CategoriaDAO;


public class CategoriaService {
	private CategoriaDAO categoriaDAO;
	
	public CategoriaService() {
		this.categoriaDAO = new CategoriaDAO();
	}
	public List<Categoria> obterCategorias() {
		return categoriaDAO.getCategorias();
	}
}
