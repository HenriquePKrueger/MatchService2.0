package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import model.Notificacao;

public class NotificacaoDAO extends DAO {
	public NotificacaoDAO() {
		super();
	}

	public void inserirNotificacao(Long usuariosId, String mensagem, Long ofertasId) throws SQLException {
		String INSERT_SQL = "INSERT INTO notificacoes (usuarios_id, mensagem, lida, created_at, ofertas_id) "
				+ "VALUES (?, ?, false, ?, ?)";

		try (PreparedStatement pstmt = db.prepareStatement(INSERT_SQL)) {
			pstmt.setObject(1, usuariosId);
			pstmt.setObject(2, mensagem);
			pstmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
			pstmt.setObject(4, ofertasId);
			pstmt.executeUpdate();
		}
	}

	public List<Notificacao> listarPorUsuario(Long usuariosId) {
		String SELECT_SQL = "SELECT * FROM notificacoes WHERE usuarios_id = ? ORDER BY created_at DESC";
		List<Notificacao> notificacoes = new ArrayList<>();

		try (PreparedStatement pstmt = db.prepareStatement(SELECT_SQL)) {
			pstmt.setObject(1, usuariosId);

			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					Notificacao notificacao = new Notificacao();
					notificacao.setId(rs.getInt("id"));
					notificacao.setUsuariosId(rs.getLong("usuarios_id"));
					notificacao.setMensagem(rs.getString("mensagem"));
					notificacao.setLida(rs.getBoolean("lida"));
					Timestamp createdAt = rs.getTimestamp("created_at");
					if (createdAt != null) {
						notificacao.setCreatedAt(createdAt.toLocalDateTime().toString());
					}
					notificacao.setOfertasId(rs.getInt("ofertas_id"));
					notificacoes.add(notificacao);
				}
			}
		} catch (SQLException e) {
			System.err.println("Erro ao listar notificações: " + e.getMessage());
		}

		return notificacoes;
	}

	public boolean marcarComoLida(Long idNotificacao, Long usuariosId) throws SQLException {
		String UPDATE_SQL = "UPDATE notificacoes SET lida = true WHERE id = ? AND usuarios_id = ?";

		try (PreparedStatement pstmt = db.prepareStatement(UPDATE_SQL)) {
			pstmt.setObject(1, idNotificacao);
			pstmt.setObject(2, usuariosId);
			return pstmt.executeUpdate() > 0;
		}
	}
}
