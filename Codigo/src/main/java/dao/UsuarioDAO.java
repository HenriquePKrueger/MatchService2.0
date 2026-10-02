package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

import model.Usuario;

public class UsuarioDAO extends DAO {

	public UsuarioDAO() {
		super();
	}

	public boolean campoJaExiste(String coluna, String valor) {
		if (!coluna.equals("login") && !coluna.equals("email")) {
			throw new IllegalArgumentException("Coluna inválida");
		}

		String SELECT_SQL = "SELECT COUNT(1) AS qtd FROM usuarios WHERE " + coluna + " = ?";

		try (PreparedStatement pstmt = db.prepareStatement(SELECT_SQL)) {
			pstmt.setObject(1, valor);

			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				return rs.getInt("qtd") > 0;
			}

		} catch (SQLException e) {
			System.err.println("Erro ao validar " + coluna + ": " + e.getMessage());
		}

		return false;
	}

	public void insertUsuario(Usuario usuario) throws SQLException {
		String INSERT_SQL = "INSERT INTO usuarios "
				+ "(email, senha, telefone, uf, cep, rua, bairro, cidade, login, nome, genero, tipo_usuario, lat, long) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = db.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

			pstmt.setObject(1, usuario.getEmail());
			pstmt.setObject(2, usuario.getSenha());
			pstmt.setObject(3, usuario.getTelefone());
			pstmt.setObject(4, usuario.getUf());
			pstmt.setObject(5, usuario.getCep());
			pstmt.setObject(6, usuario.getRua());
			pstmt.setObject(7, usuario.getBairro());
			pstmt.setObject(8, usuario.getCidade());
			pstmt.setObject(9, usuario.getLogin());
			pstmt.setObject(10, usuario.getNome());
			pstmt.setObject(11, usuario.getGenero());
			pstmt.setObject(12, usuario.getTipoUsuario());
			pstmt.setObject(13, usuario.getLat());
			pstmt.setObject(14, usuario.getLng());
			
			int rowsUpdated = pstmt.executeUpdate();

			if (rowsUpdated > 0) {
				try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
					if (generatedKeys.next()) {
						long idGerado = generatedKeys.getLong(1);
						usuario.setId(idGerado);

					}
				}
			}
		}
	}

	public Usuario selectUsuario(String coluna, Object valor) {
		List<String> colunasPermitidas = Arrays.asList("id", "login", "email");

		if (!colunasPermitidas.contains(coluna.toLowerCase())) {
			throw new IllegalArgumentException("Busca por coluna não permitida: " + coluna);
		}

		String SELECT_SQL = "SELECT * FROM usuarios WHERE " + coluna + " = ?";

		try (PreparedStatement pstmt = db.prepareStatement(SELECT_SQL)) {
			if(coluna.equalsIgnoreCase("id") && valor instanceof String) {
				try {
	                valor = Long.parseLong((String) valor);
	            } catch (NumberFormatException e) {
	                System.err.println("ID inválido fornecido: " + valor);
	                return null;
	            }
			}
			
			pstmt.setObject(1, valor);
			try (ResultSet rs = pstmt.executeQuery()) {

				if (rs.next()) {
					Usuario u = new Usuario();

					u.setId(rs.getInt("id"));
					u.setNome(rs.getString("nome"));
					u.setEmail(rs.getString("email"));
					u.setGenero(rs.getString("genero"));
					u.setSenha(rs.getString("senha"));
					u.setTelefone(rs.getString("telefone"));
					u.setUf(rs.getString("uf"));
					u.setCep(rs.getString("cep"));
					u.setRua(rs.getString("rua"));
					u.setBairro(rs.getString("bairro"));
					u.setCidade(rs.getString("cidade"));
					u.setLogin(rs.getString("login"));
					u.setTipoUsuario(Byte.parseByte(rs.getString("tipo_usuario")));
					u.setLat(rs.getDouble("lat"));
					u.setLng(rs.getDouble("long"));
					
					return u;
				}
			}
		} catch (SQLException e) {
			System.err.println("Erro na consulta de usuário por " + coluna + ": " + e.getMessage());
		}

		return null;
	}

	public void updateUsuario(Usuario u) throws SQLException {
		String UPDATE_SQL = "UPDATE usuarios SET nome = ?, telefone = ?, rua = ?, bairro = ?, cidade = ?, cep = ?, uf = ? "
				+ "WHERE id = ?";
		
		try(PreparedStatement pstmt = db.prepareStatement(UPDATE_SQL)){
			pstmt.setObject(1, u.getNome());
			pstmt.setObject(2, u.getTelefone());
			pstmt.setObject(3, u.getRua());
			pstmt.setObject(4, u.getBairro());
			pstmt.setObject(5, u.getCidade());
			pstmt.setObject(6, u.getCep());
			pstmt.setObject(7, u.getUf());
			pstmt.setObject(8, u.getId());
			
			pstmt.executeUpdate();
		}
	}
	
	public void vincularCategorias(long idPrestador, List<Integer> ids) {
		String INSERT_SQL = "INSERT INTO prestadores_categorias (prestadores_id, categorias_id) VALUES (? , ?)";

		try (PreparedStatement pstmt = db.prepareStatement(INSERT_SQL)) {
			for (Integer idCategoria : ids) {
				pstmt.setObject(1, idPrestador);
				pstmt.setObject(2, idCategoria);
				pstmt.addBatch();
			}

			pstmt.executeBatch();
		} catch (SQLException e) {
			System.err.println("Erro na inserção: " + e.getMessage());
		}
	}
}
