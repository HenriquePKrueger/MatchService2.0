package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.Categoria;

public class CategoriaDAO extends DAO{
	public CategoriaDAO() {
		super();
	}
	public List<Categoria> getCategorias(){
		String SELECT_SQL = "SELECT * FROM categorias";
		List<Categoria> cats = new ArrayList<>();
		try (Statement st = db.createStatement(); ResultSet rs = st.executeQuery(SELECT_SQL)) {

			while (rs.next()) {

				Byte id= rs.getByte("id");
				String nome = rs.getString("nome");

				Categoria c = new Categoria();
				c.setId(id);
				c.setNome(nome);
				cats.add(c);
			}

		} catch (SQLException e) {
			System.err.println("Erro na consulta: " + e.getMessage());
		}
		
		return cats;
	}
	
	public Categoria getCategoria(Long idCategoria) throws SQLException {
		String SELECT_SQL = "SELECT * FROM categorias WHERE id = ?";
		Categoria c = new Categoria();
		
		try (PreparedStatement st = db.prepareStatement(SELECT_SQL)) {
			st.setObject(1, idCategoria);
			try(ResultSet rs = st.executeQuery()){
				if(rs.next()) {
					c.setId(rs.getInt("id"));
					c.setNome(rs.getString("nome"));
				}
				
			}
		}
		
		return c;
	}
	
	public List<Categoria> getCategoriasPrestador(long idPrestador){
		StringBuilder SELECT_SQL = new StringBuilder();
		SELECT_SQL.append("SELECT categorias.*, prestadores_categorias.categorias_id ");
		SELECT_SQL.append("FROM prestadores_categorias ");
		SELECT_SQL.append("INNER JOIN categorias ON categorias.id = prestadores_categorias.categorias_id ");
		SELECT_SQL.append("WHERE prestadores_id = ?");
	
	    List<Categoria> cats = new ArrayList<>();
	    
	    try (PreparedStatement pstmt = db.prepareStatement(SELECT_SQL.toString())) {
	        pstmt.setLong(1, idPrestador);
	        
	        try (ResultSet rs = pstmt.executeQuery()) { 
	            while(rs.next()) {
	            	Categoria c = new Categoria();
	            	c.setId(rs.getInt("id"));
	            	c.setNome(rs.getString("nome"));
	                cats.add(c);
	            }
	        }
	    } catch (SQLException e) {
	        System.err.println("Erro na consulta de categorias do prestador: " + e.getMessage());
	    }
	    return cats;
	}
	
	public List<Integer> buscarIdsPorUsuario(long idPrestador) {
	    String SELECT_SQL = "SELECT categorias_id FROM prestadores_categorias WHERE prestadores_id = ?";
	    List<Integer> cats = new ArrayList<>();
	    
	    try (PreparedStatement pstmt = db.prepareStatement(SELECT_SQL)) {
	        pstmt.setLong(1, idPrestador);
	        
	        try (ResultSet rs = pstmt.executeQuery()) { 
	            while(rs.next()) {
	                cats.add(rs.getInt("categorias_id"));
	            }
	        }
	    } catch (SQLException e) {
	        System.err.println("Erro na consulta de categorias: " + e.getMessage());
	    }
	    return cats;
	}
}
