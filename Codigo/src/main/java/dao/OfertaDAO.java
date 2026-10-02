package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import model.Oferta;

public class OfertaDAO extends DAO {
	public OfertaDAO() {
		super();
	}

	public Long inserirOferta(Oferta oferta) throws SQLException {
		String INSERT_SQL = "INSERT INTO ofertas "
				+ "(descricao, valor, disponibilidade, status, solicitacoes_servicos_id, prestadores_id) "
				+ "VALUES (?, ?, ?, ?, ?, ?)";

		try (PreparedStatement pstmt = db.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
			pstmt.setObject(1, oferta.getDescricao());
			pstmt.setObject(2, oferta.getValor());
			pstmt.setObject(3, oferta.getDisponibilidade());
			pstmt.setInt(4, oferta.getStatus());
			pstmt.setObject(5, oferta.getSolicitacoesServicosId());
			pstmt.setObject(6, oferta.getPrestadoresId());

			pstmt.executeUpdate();

			try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					return generatedKeys.getLong(1);
				}
			}
		}

		return 0L;
	}

	public Oferta selectOferta(Long idOferta) {
		String SELECT_SQL = "SELECT o.*, u.nome AS prestador_nome FROM ofertas o "
				+ "JOIN prestadores p ON p.id = o.prestadores_id "
				+ "JOIN usuarios u ON u.id = p.usuarios_id "
				+ "WHERE o.id = ?";

		try (PreparedStatement pstmt = db.prepareStatement(SELECT_SQL)) {
			pstmt.setObject(1, idOferta);

			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					return montarOferta(rs);
				}
			}
		} catch (SQLException e) {
			System.err.println("Erro na consulta de oferta: " + e.getMessage());
		}

		return null;
	}

	public List<Oferta> listarPorSolicitacao(Long idSolicitacao) {
		String SELECT_SQL = "SELECT o.*, u.nome AS prestador_nome FROM ofertas o "
				+ "JOIN prestadores p ON p.id = o.prestadores_id "
				+ "JOIN usuarios u ON u.id = p.usuarios_id "
				+ "WHERE o.solicitacoes_servicos_id = ? ORDER BY o.created_at DESC";
		List<Oferta> ofertas = new ArrayList<>();

		try (PreparedStatement pstmt = db.prepareStatement(SELECT_SQL)) {
			pstmt.setObject(1, idSolicitacao);

			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					ofertas.add(montarOferta(rs));
				}
			}
		} catch (SQLException e) {
			System.err.println("Erro ao listar ofertas: " + e.getMessage());
		}

		return ofertas;
	}

	public List<Oferta> listarPorPrestador(Long idPrestador) {
		String SELECT_SQL = "SELECT o.*, u_prestador.nome AS prestador_nome, "
				+ "ss.descricao AS solicitacao_descricao, "
				+ "u_cliente.nome AS cliente_nome, u_cliente.telefone AS cliente_telefone, "
				+ "u_cliente.email AS cliente_email "
				+ "FROM ofertas o "
				+ "JOIN prestadores p ON p.id = o.prestadores_id "
				+ "JOIN usuarios u_prestador ON u_prestador.id = p.usuarios_id "
				+ "JOIN solicitacoes_servicos ss ON ss.id = o.solicitacoes_servicos_id "
				+ "JOIN usuarios u_cliente ON u_cliente.id = ss.usuarios_id "
				+ "WHERE o.prestadores_id = ? "
				+ "ORDER BY o.created_at DESC";
		List<Oferta> ofertas = new ArrayList<>();

		try (PreparedStatement pstmt = db.prepareStatement(SELECT_SQL)) {
			pstmt.setObject(1, idPrestador);

			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					ofertas.add(montarOfertaPrestador(rs));
				}
			}
		} catch (SQLException e) {
			System.err.println("Erro ao listar ofertas do prestador: " + e.getMessage());
		}

		return ofertas;
	}

	public boolean prestadorJaOfertou(Long idSolicitacao, Long idPrestador) {
		String SELECT_SQL = "SELECT COUNT(*) AS qtd FROM ofertas WHERE solicitacoes_servicos_id = ? AND prestadores_id = ?";

		try (PreparedStatement pstmt = db.prepareStatement(SELECT_SQL)) {
			pstmt.setObject(1, idSolicitacao);
			pstmt.setObject(2, idPrestador);

			try (ResultSet rs = pstmt.executeQuery()) {
				return rs.next() && rs.getInt("qtd") > 0;
			}
		} catch (SQLException e) {
			System.err.println("Erro ao validar oferta duplicada: " + e.getMessage());
		}

		return false;
	}

	public void atualizarStatusOferta(Long idOferta, int status, String justificativaRecusa) throws SQLException {
		String UPDATE_SQL = "UPDATE ofertas SET status = ?, justificativa_recusa = ? WHERE id = ?";

		try (PreparedStatement pstmt = db.prepareStatement(UPDATE_SQL)) {
			pstmt.setInt(1, status);
			pstmt.setObject(2, justificativaRecusa);
			pstmt.setObject(3, idOferta);
			pstmt.executeUpdate();
		}
	}

	public boolean finalizarOfertaDoPrestador(Long idOferta, Long idPrestador) throws SQLException {
		String UPDATE_SQL = "UPDATE ofertas SET status = 3 "
				+ "WHERE id = ? AND prestadores_id = ? AND status = 1";

		try (PreparedStatement pstmt = db.prepareStatement(UPDATE_SQL)) {
			pstmt.setObject(1, idOferta);
			pstmt.setObject(2, idPrestador);
			return pstmt.executeUpdate() > 0;
		}
	}

	public void recusarOutrasOfertas(Long idSolicitacao, Long idOfertaAceita) throws SQLException {
		String UPDATE_SQL = "UPDATE ofertas SET status = 2, justificativa_recusa = 'Outra oferta foi aceita' "
				+ "WHERE solicitacoes_servicos_id = ? AND id <> ? AND status = 0";

		try (PreparedStatement pstmt = db.prepareStatement(UPDATE_SQL)) {
			pstmt.setObject(1, idSolicitacao);
			pstmt.setObject(2, idOfertaAceita);
			pstmt.executeUpdate();
		}
	}

	public List<Long> obterIdsSolicitacoesOfertadasAtivas(Long prestadorId) throws SQLException {
	    List<Long> ids = new ArrayList<>();
	    String SQL = "SELECT solicitacoes_servicos_id FROM ofertas WHERE prestadores_id = ? AND status <> 2";
	    
	    try (PreparedStatement pstmt = db.prepareStatement(SQL)) {
	        pstmt.setLong(1, prestadorId);
	        try (ResultSet rs = pstmt.executeQuery()) {
	            while (rs.next()) {
	                ids.add(rs.getLong("solicitacoes_servicos_id"));
	            }
	        }
	    }
	    return ids;
	}

	
	public List<Long> obterIdsSolicitacoesRecusadas(Long prestadorId) throws SQLException {
	    List<Long> ids = new ArrayList<>();
	    String SQL = "SELECT solicitacoes_servicos_id FROM ofertas WHERE prestadores_id = ? AND status = 2";
	    
	    try (PreparedStatement pstmt = db.prepareStatement(SQL)) {
	        pstmt.setLong(1, prestadorId);
	        try (ResultSet rs = pstmt.executeQuery()) {
	            while (rs.next()) {
	                ids.add(rs.getLong("solicitacoes_servicos_id"));
	            }
	        }
	    }
	    return ids;
	}
	
	private Oferta montarOferta(ResultSet rs) throws SQLException {
		Oferta oferta = new Oferta();
		oferta.setId(rs.getInt("id"));
		oferta.setDescricao(rs.getString("descricao"));
		oferta.setValor(rs.getDouble("valor"));
		oferta.setDisponibilidade(rs.getString("disponibilidade"));
		oferta.setStatus(rs.getInt("status"));
		oferta.setJustificativaRecusa(rs.getString("justificativa_recusa"));
		Timestamp createdAt = rs.getTimestamp("created_at");
		if (createdAt != null) {
			oferta.setCreatedAt(createdAt.toLocalDateTime().toString());
		}
		oferta.setSolicitacoesServicosId(rs.getInt("solicitacoes_servicos_id"));
		oferta.setPrestadoresId(rs.getInt("prestadores_id"));
		oferta.setPrestadorNome(rs.getString("prestador_nome"));
		return oferta;
	}

	private Oferta montarOfertaPrestador(ResultSet rs) throws SQLException {
		Oferta oferta = montarOferta(rs);
		oferta.setSolicitacaoDescricao(rs.getString("solicitacao_descricao"));
		oferta.setClienteNome(rs.getString("cliente_nome"));
		oferta.setClienteTelefone(rs.getString("cliente_telefone"));
		oferta.setClienteEmail(rs.getString("cliente_email"));
		return oferta;
	}
}
