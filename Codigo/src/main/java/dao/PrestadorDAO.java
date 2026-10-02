package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import model.Prestador;
import model.Usuario;
import model.Avaliacao;

public class PrestadorDAO extends DAO {
	public PrestadorDAO() {
		super();
	}

	public enum NivelDetalhePrestador {
	    SIMPLES,     
	    COM_USUARIO 
	}
	
	public void inserirPrestador(Prestador p) {
		String INSERT_SQL = "INSERT INTO prestadores (descricao, usuarios_id) VALUES (?, ?)";

		try (PreparedStatement pstmt = db.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

			pstmt.setObject(1, p.getDescricao());
			pstmt.setObject(2, p.getUsuario().getId());

			int rowsUpdated = pstmt.executeUpdate();
			System.out.println(rowsUpdated + " linha(s) afetadas");

			if (rowsUpdated > 0) {
				try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
					if (generatedKeys.next()) {
						long idGerado = generatedKeys.getLong(1);
						p.setId(idGerado);

					}
				}
			}
		} catch (SQLException e) {
			System.err.println("Erro na inserção: " + e.getMessage());
		}
	}

	public Prestador selectPrestador(String coluna, Object valor, NivelDetalhePrestador nivelDetalhe) {
		List<String> colunasPermitidas = Arrays.asList("id", "descricao", "usuarios_id");

		if (!colunasPermitidas.contains(coluna.toLowerCase())) {
			throw new IllegalArgumentException("Busca por coluna não permitida: " + coluna);
		}

		StringBuilder SELECT_SQL = new StringBuilder("SELECT prestadores.*");
		
		if(nivelDetalhe == NivelDetalhePrestador.COM_USUARIO) {
			SELECT_SQL.append(", usuarios.id as usuario_id, usuarios.telefone, usuarios.nome ");
			SELECT_SQL.append("FROM prestadores ");
			SELECT_SQL.append("JOIN usuarios ON usuarios.id = prestadores.usuarios_id ");
		} else {
			SELECT_SQL.append("FROM prestadores ");
		}

		SELECT_SQL.append(" WHERE prestadores.").append(coluna).append(" = ?");
		
		try (PreparedStatement pstmt = db.prepareStatement(SELECT_SQL.toString())) {
			if (coluna.equalsIgnoreCase("usuarios_id") && valor instanceof String) {
				try {
					valor = Long.parseLong((String) valor);
				} catch (NumberFormatException e) {
					System.err.println("ID inválido fornecido: " + valor);
					return null;
				}
			}

			pstmt.setObject(1, valor);
			//System.out.println(SELECT_SQL.toString());
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					Prestador p = new Prestador();
					
					p.setDescricao(rs.getString("descricao"));
					p.setId(rs.getInt("id"));
					
					if(nivelDetalhe == NivelDetalhePrestador.COM_USUARIO) {
						Usuario u = new Usuario();
						u.setTelefone(rs.getString("telefone"));
						u.setId(rs.getInt("usuario_id"));
						u.setNome(rs.getString("nome"));
						p.setUsuario(u);
					}
					
					return p;
				}
			} 
		}catch (SQLException e) {
			System.err.println("(PrestadorDAO) Erro na consulta de usuário por " + coluna + ": " + e.getMessage());
		}
		
		return null;
	}
	
	public void updatePrestador(Prestador p) throws SQLException {
		String UPDATE_SQL = "UPDATE prestadores SET descricao = ? WHERE id = ?";
		
		try(PreparedStatement pstmt = db.prepareStatement(UPDATE_SQL)){
			pstmt.setObject(1, p.getDescricao());
			pstmt.setObject(2, p.getId());
			pstmt.executeUpdate();
		}
	}
	public void deleteCategoriasPrestador(Long idPrestador) throws SQLException {
		String DELETE_SQL = "DELETE FROM prestadores_categorias WHERE prestadores_id = ?";
		
		try (PreparedStatement pstmt = db.prepareStatement(DELETE_SQL)){
			pstmt.setObject(1, idPrestador);
			
			pstmt.executeUpdate();
		}
	}
	
	public List<Prestador> filtrarPrestadores(int idCategoria, String cidade, String genero) {
	    List<Prestador> lista = new ArrayList<>();
	    UsuarioDAO usuarioDAO = new UsuarioDAO();
	    CategoriaDAO categoriaDAO = new CategoriaDAO();
	    
	    StringBuilder sql = new StringBuilder();
	    sql.append("SELECT prestadores.id, prestadores.descricao, prestadores.usuarios_id, usuarios.nome AS nome_usuario ");
	    sql.append("FROM prestadores ");
	    sql.append("JOIN usuarios ON prestadores.usuarios_id = usuarios.id ");
	    sql.append("LEFT JOIN prestadores_categorias ON prestadores_categorias.prestadores_id = prestadores.id ");
	    sql.append("LEFT JOIN categorias ON prestadores_categorias.categorias_id = categorias.id ");
	    sql.append("WHERE 1=1 ");
	    
	    if (idCategoria > 0) sql.append("AND categorias.id = ? ");
	    if (cidade != null && !cidade.isEmpty()) sql.append("AND usuarios.cidade ILIKE ? ");
	    if (genero != null && !genero.isEmpty()) sql.append("AND usuarios.genero = ? ");
	    
	    sql.append("GROUP BY prestadores.id, usuarios.nome");
	    
	    System.out.println(sql.toString());
	    try (PreparedStatement pstmt = db.prepareStatement(sql.toString())) {
	        int i = 1;
	        if (idCategoria > 0) pstmt.setInt(i++, idCategoria);
	        if (cidade != null && !cidade.isEmpty()) pstmt.setString(i++, "%" + cidade + "%");
	        if (genero != null && !genero.isEmpty()) pstmt.setString(i++, genero);

	        try (ResultSet rs = pstmt.executeQuery()) {
	            while (rs.next()) {
	                Prestador p = new Prestador();
	                
	                p.setId(rs.getLong("id"));
	                p.setDescricao(rs.getString("descricao"));
	                p.setUsuario(usuarioDAO.selectUsuario("id", rs.getLong("usuarios_id")));
	                p.setCategorias(categoriaDAO.getCategoriasPrestador(rs.getLong("id")));
	                
	                lista.add(p);
	            }
	        }
	    } catch (SQLException e) {
	        System.err.println("Erro ao filtrar prestadores: " + e.getMessage());
	    }
	    return lista;
	}
	
	public List<Avaliacao> buscarAvaliacoesDoPrestador(Long idPrestador) {
        List<Avaliacao> listaAvaliacoes = new ArrayList<>();
        
        String SELECT_SQL = "SELECT u.nome AS nome_cliente, a.nota, a.comentario " +
                            "FROM avaliacoes a " +
                            "JOIN usuarios u ON a.usuarios_id = u.id " +
                            "WHERE a.prestador_id = ? " +
                            "ORDER BY a.created_at DESC";
                            
        try (PreparedStatement pstmt = db.prepareStatement(SELECT_SQL)) {
            pstmt.setLong(1, idPrestador);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String nome = rs.getString("nome_cliente");
                    int nota = rs.getInt("nota");
                    String comentario = rs.getString("comentario");
                    
                    listaAvaliacoes.add(new Avaliacao(nome, nota, comentario));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar avaliações do prestador: " + e.getMessage());
        }
        
        return listaAvaliacoes;
    }
	
}
