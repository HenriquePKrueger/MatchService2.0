package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import model.SolicitacaoImagem;

public class SolicitacaoImagemDAO extends DAO {
	public List<SolicitacaoImagem> listarAprovadasPorSolicitacao(Long idSolicitacao) throws SQLException {
		String SELECT_SQL = "SELECT * FROM solicitacoes_imagens "
				+ "WHERE solicitacoes_servicos_id = ? AND url IS NOT NULL "
				+ "ORDER BY id";
		List<SolicitacaoImagem> imagens = new ArrayList<>();

		try (PreparedStatement pstmt = db.prepareStatement(SELECT_SQL)) {
			pstmt.setObject(1, idSolicitacao);

			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					imagens.add(mapear(rs));
				}
			}
		}

		return imagens;
	}

	public void inserir(SolicitacaoImagem imagem) throws SQLException {
		String INSERT_SQL = "INSERT INTO solicitacoes_imagens "
				+ "(url, solicitacoes_servicos_id, nome_original, content_type, tamanho_bytes) "
				+ "VALUES (?, ?, ?, ?, ?)";

		try (PreparedStatement pstmt = db.prepareStatement(INSERT_SQL)) {
			setNullableString(pstmt, 1, imagem.getUrl());
			pstmt.setObject(2, imagem.getSolicitacoesServicosId());
			setNullableString(pstmt, 3, imagem.getNomeOriginal());
			setNullableString(pstmt, 4, imagem.getContentType());
			pstmt.setLong(5, imagem.getTamanhoBytes());
			pstmt.executeUpdate();
		}
	}

	private SolicitacaoImagem mapear(ResultSet rs) throws SQLException {
		SolicitacaoImagem imagem = new SolicitacaoImagem();
		imagem.setId(rs.getInt("id"));
		imagem.setUrl(rs.getString("url"));
		imagem.setSolicitacoesServicosId(rs.getInt("solicitacoes_servicos_id"));
		imagem.setNomeOriginal(rs.getString("nome_original"));
		imagem.setContentType(rs.getString("content_type"));
		imagem.setTamanhoBytes(rs.getLong("tamanho_bytes"));
		return imagem;
	}

	private void setNullableString(PreparedStatement pstmt, int index, String value) throws SQLException {
		if (value == null) {
			pstmt.setNull(index, Types.VARCHAR);
		} else {
			pstmt.setString(index, value);
		}
	}
}
