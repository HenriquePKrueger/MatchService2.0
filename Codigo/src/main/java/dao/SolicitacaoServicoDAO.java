package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import model.Categoria;
import model.SolicitacaoImagem;
import model.SolicitacaoServico;

public class SolicitacaoServicoDAO extends DAO {
	private SolicitacaoImagemDAO solicitacaoImagemDAO;

	public SolicitacaoServicoDAO() {
		super();
		this.solicitacaoImagemDAO = new SolicitacaoImagemDAO();
	}
	
	public Long insertSolicitacao(SolicitacaoServico ss) throws SQLException {
		String INSERT_SQL = "INSERT INTO solicitacoes_servicos (descricao, rua, bairro, status, tipo_solicitacao, "
				+ "usuarios_id, categorias_id, lat, long, created_at, cidade, cep) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		Long idGerado = (long) 0;
		
		try(PreparedStatement pstmt = db.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)){
			pstmt.setObject(1, ss.getDescricao());
			pstmt.setObject(2, ss.getRua());
			pstmt.setObject(3, ss.getBairro());
			pstmt.setObject(4, ss.getStatus());
			pstmt.setObject(5, ss.getTipoSolicitacao());
			pstmt.setObject(6, ss.getUsuariosId());
			pstmt.setObject(7, ss.getCategoria().getId());
			pstmt.setObject(8, ss.getLat());
			pstmt.setObject(9, ss.getLng());
			
			String dataString = ss.getCreatedAt();
			LocalDateTime ldt = LocalDateTime.parse(dataString);
			pstmt.setTimestamp(10, Timestamp.valueOf(ldt));
			pstmt.setObject(11, ss.getCidade());
			pstmt.setObject(12, ss.getCep());
			
			int rowsUpdated = pstmt.executeUpdate();

			if (rowsUpdated > 0) {
				try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
					if (generatedKeys.next()) {
						idGerado = generatedKeys.getLong(1);
						ss.setId(idGerado);
					
					}
				}
			}
		}
		
		return idGerado;
	}
	
	public List<SolicitacaoServico> selectSolicitacoes(String coluna, Object valor) throws SQLException{
		List<String> colunasPermitidas = Arrays.asList("id", "status", "usuarios_id");

		if (!colunasPermitidas.contains(coluna.toLowerCase())) {
			throw new IllegalArgumentException("Busca por coluna não permitida: " + coluna);
		}

		String SELECT_SQL = "SELECT solicitacoes_servicos.*, categorias.nome FROM solicitacoes_servicos "
				+ "INNER JOIN categorias ON categorias.id = solicitacoes_servicos.categorias_id "
				+ "WHERE " + coluna + " = ?";
		
		List<SolicitacaoServico> solicitacoes = new ArrayList<>();
		
		try(PreparedStatement pstmt = db.prepareStatement(SELECT_SQL)){
			if(coluna.equalsIgnoreCase("id") && valor instanceof String) {
				valor = Long.parseLong((String) valor);
			}
			
			pstmt.setObject(1, valor);
			
			
			try (ResultSet rs = pstmt.executeQuery()) {
				
				while(rs.next()) {
					SolicitacaoServico s = new SolicitacaoServico();
					Categoria c = new Categoria();
					
					c.setNome(rs.getString("nome"));
					
					s.setId(rs.getLong("id"));
					s.setDescricao(rs.getString("descricao"));
					s.setRua(rs.getString("rua"));
					s.setBairro(rs.getString("bairro"));
					s.setStatus(rs.getInt("status"));
					s.setTipoSolicitacao(rs.getInt("tipo_solicitacao"));
					s.setUsuariosId(rs.getLong("usuarios_id"));
					s.setCreatedAt(rs.getTimestamp("created_at").toString());
					s.setCategoria(c);
					s.setSolicitacaoImagens(getImagensSolicitacao(s.getId()));
					
					solicitacoes.add(s);

					//solicitacoes diretas
					s.setPrestadoresId(rs.getObject("prestadores_id") != null ? rs.getLong("prestadores_id") : null);
					s.setAgendaId(rs.getObject("agenda_id") != null ? rs.getLong("agenda_id") : null);
				}
			}
			
		}
		
		return solicitacoes;
	}

	public List<SolicitacaoServico> listarDisponiveisParaPrestador(Long idUsuario) throws SQLException {
		String SELECT_SQL = "SELECT solicitacoes_servicos.*, categorias.nome "
		        + "FROM solicitacoes_servicos "
		        + "INNER JOIN categorias ON categorias.id = solicitacoes_servicos.categorias_id "
		        + "WHERE solicitacoes_servicos.status = 0 AND solicitacoes_servicos.usuarios_id <> ? "
		        + "ORDER BY solicitacoes_servicos.created_at DESC";
		
		List<SolicitacaoServico> solicitacoes = new ArrayList<>();

		try (PreparedStatement pstmt = db.prepareStatement(SELECT_SQL)) {
			pstmt.setObject(1, idUsuario);

			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					SolicitacaoServico s = new SolicitacaoServico();
					Categoria c = new Categoria();

					c.setNome(rs.getString("nome"));

					s.setId(rs.getLong("id"));
					s.setDescricao(rs.getString("descricao"));
					s.setRua(rs.getString("rua"));
					s.setBairro(rs.getString("bairro"));
					s.setStatus(rs.getInt("status"));
					s.setTipoSolicitacao(rs.getInt("tipo_solicitacao"));
					s.setUsuariosId(rs.getLong("usuarios_id"));
					s.setCreatedAt(rs.getTimestamp("created_at").toString());
					s.setCategoria(c);
					s.setSolicitacaoImagens(getImagensSolicitacao(s.getId()));

					solicitacoes.add(s);
				}
			}
		}

		return solicitacoes;
	}
	
	public SolicitacaoServico getSolicitacao(Long idSolicitacao) throws SQLException {
		String SELECT_SQL = "SELECT solicitacoes_servicos.*, categorias.nome, categorias.id AS categoriaId FROM solicitacoes_servicos "
				+ "INNER JOIN categorias ON categorias.id = solicitacoes_servicos.categorias_id "
				+ "WHERE solicitacoes_servicos.id = ?";
		
		SolicitacaoServico s = new SolicitacaoServico();
		
		try(PreparedStatement pstmt = db.prepareStatement(SELECT_SQL)){
			pstmt.setObject(1, idSolicitacao);
			
			try(ResultSet rs = pstmt.executeQuery()){
				if(rs.next()) {
					Categoria c = new Categoria();
					c.setId(rs.getInt("categoriaId"));
					c.setNome(rs.getString("nome"));
					
					List<SolicitacaoImagem> imgs = getImagensSolicitacao(idSolicitacao);
					
					s.setId(rs.getLong("id"));
					s.setDescricao(rs.getString("descricao"));
					s.setRua(rs.getString("rua"));
					s.setBairro(rs.getString("bairro"));
					s.setCidade(rs.getString("cidade"));
					s.setCep(rs.getString("cep"));
					s.setLat(rs.getDouble("lat"));
					s.setLng(rs.getDouble("long"));
					s.setStatus(rs.getInt("status"));
					s.setTipoSolicitacao(rs.getInt("tipo_solicitacao"));
					s.setUsuariosId(rs.getLong("usuarios_id"));
					s.setCreatedAt(rs.getTimestamp("created_at").toString());
					s.setCategoria(c);
					s.setSolicitacaoImagens(imgs);
				}
			}
		}
		
		return s;
	}
	
	public boolean updateSolicitacao(SolicitacaoServico s) throws SQLException {
		String UPDATE_SQL = "UPDATE solicitacoes_servicos SET descricao = ?, rua = ?, bairro = ?, cep = ?, cidade = ?, lat = ?, long = ?, categorias_id = ?, status = ? WHERE id = ?";
		
		try(PreparedStatement pstmt = db.prepareStatement(UPDATE_SQL)){
			pstmt.setObject(1, s.getDescricao());
			pstmt.setObject(2, s.getRua());
			pstmt.setObject(3, s.getBairro());
			pstmt.setObject(4, s.getCep());
			pstmt.setObject(5, s.getCidade());
			pstmt.setObject(6, s.getLat());
			pstmt.setObject(7, s.getLng());
			
			if (s.getCategoria() != null) {
	            pstmt.setLong(8, s.getCategoria().getId());
	        } else {
	            pstmt.setNull(8, java.sql.Types.INTEGER);
	        }
			
			pstmt.setObject(9, s.getStatus());
			pstmt.setObject(10, s.getId());
			int linhasAfetadas = pstmt.executeUpdate();
			
			if(linhasAfetadas > 0) return true;
		}
		
		return false;
	}
	public boolean deleteSolicitacao(Long idSolicitacao) throws SQLException {
		String DELETE_SQL = "DELETE FROM solicitacoes_servicos WHERE id = ?";
		
		try(PreparedStatement pstmt = db.prepareStatement(DELETE_SQL)){
			pstmt.setObject(1, idSolicitacao);
			
			int linhasAfetadas = pstmt.executeUpdate();
			
			if(linhasAfetadas > 0) return true;
		}
		
		return false;
	}

	public void atualizarStatus(Long idSolicitacao, int status) throws SQLException {
		String UPDATE_SQL = "UPDATE solicitacoes_servicos SET status = ? WHERE id = ?";

		try (PreparedStatement pstmt = db.prepareStatement(UPDATE_SQL)) {
			pstmt.setObject(1, status);
			pstmt.setObject(2, idSolicitacao);
			pstmt.executeUpdate();
		}
	}
	
	
	public List<SolicitacaoImagem> getImagensSolicitacao(Long idSolicitacao) throws SQLException {
		return solicitacaoImagemDAO.listarAprovadasPorSolicitacao(idSolicitacao);
	}



	//métodos solicitação direta
	public boolean criarSolicitacaoDireta(long clienteId, long prestadorId, long agendaId, String descricao) throws SQLException {
    String buscarUsuario = "SELECT rua, bairro, cidade, cep, lat, long FROM usuarios WHERE id = ?";

    String buscarCategoria = "SELECT categorias_id FROM prestadores_categorias WHERE prestadores_id = ? LIMIT 1";

    String buscarHorario = "SELECT id FROM agenda " +
                           "WHERE id = ? AND prestadores_id = ? AND status_horario = 0 " +
                           "FOR UPDATE";

    String inserirSolicitacao = "INSERT INTO solicitacoes_servicos " +
            "(descricao, rua, bairro, status, tipo_solicitacao, usuarios_id, categorias_id, lat, long, created_at, cidade, cep, prestadores_id, agenda_id) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, ?, ?, ?, ?)";

    String ocuparHorario = "UPDATE agenda SET status_horario = 1 WHERE id = ? AND status_horario = 0";

    try {
        db.setAutoCommit(false);

        String rua = "";
        String bairro = "";
        String cidade = "";
        String cep = "";
        double lat = 0;
        double lng = 0;

        try (PreparedStatement ps = db.prepareStatement(buscarUsuario)) {
            ps.setLong(1, clienteId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    rua = rs.getString("rua");
                    bairro = rs.getString("bairro");
                    cidade = rs.getString("cidade");
                    cep = rs.getString("cep");
                    lat = rs.getDouble("lat");
                    lng = rs.getDouble("long");
                } else {
                    db.rollback();
                    return false;
                }
            }
        }

        long categoriaId = -1;

        try (PreparedStatement ps = db.prepareStatement(buscarCategoria)) {
            ps.setLong(1, prestadorId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    categoriaId = rs.getLong("categorias_id");
                } else {
                    db.rollback();
                    return false;
                }
            }
        }

        try (PreparedStatement ps = db.prepareStatement(buscarHorario)) {
            ps.setLong(1, agendaId);
            ps.setLong(2, prestadorId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    db.rollback();
                    return false;
                }
            }
        }

        try (PreparedStatement ps = db.prepareStatement(inserirSolicitacao)) {
            ps.setString(1, descricao);
            ps.setString(2, rua);
            ps.setString(3, bairro);
            ps.setInt(4, 0);
            ps.setInt(5, 0);
            ps.setLong(6, clienteId);
            ps.setLong(7, categoriaId);
            ps.setDouble(8, lat);
            ps.setDouble(9, lng);
            ps.setString(10, cidade);
            ps.setString(11, cep);
            ps.setLong(12, prestadorId);
            ps.setLong(13, agendaId);

            ps.executeUpdate();
        }

        try (PreparedStatement ps = db.prepareStatement(ocuparHorario)) {
            ps.setLong(1, agendaId);

            int linhas = ps.executeUpdate();

            if (linhas == 0) {
                db.rollback();
                return false;
            }
        }

        db.commit();
        return true;

    	} catch (SQLException e) {
        db.rollback();
        throw e;
    	} finally {
        db.setAutoCommit(true);
    	}
	}

	public List<model.SolicitacaoDireta> listarDiretasDoPrestador(long prestadorId) throws SQLException {
    String sql = "SELECT " +
                 "ss.id, ss.descricao, ss.status, ss.created_at, ss.usuarios_id, ss.prestadores_id, ss.agenda_id, " +
                 "u.nome AS nome_cliente, u.telefone AS telefone_cliente, u.cidade AS cidade_cliente, " +
                 "u.bairro AS bairro_cliente, u.rua AS rua_cliente, " +
                 "a.data_disponivel, a.horario_inicio, a.horario_fim " +
                 "FROM solicitacoes_servicos ss " +
                 "INNER JOIN usuarios u ON u.id = ss.usuarios_id " +
                 "INNER JOIN agenda a ON a.id = ss.agenda_id " +
                 "WHERE ss.prestadores_id = ? " +
                 "AND ss.tipo_solicitacao = 0 " +
                 "ORDER BY a.data_disponivel, a.horario_inicio";

    List<model.SolicitacaoDireta> lista = new ArrayList<>();

    try (PreparedStatement ps = db.prepareStatement(sql)) {
        ps.setLong(1, prestadorId);

        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                model.SolicitacaoDireta s = new model.SolicitacaoDireta();

                s.setId(rs.getLong("id"));
                s.setDescricao(rs.getString("descricao"));
                s.setStatus(rs.getInt("status"));
                s.setCreatedAt(rs.getTimestamp("created_at").toString());

                s.setClienteId(rs.getLong("usuarios_id"));
                s.setNomeCliente(rs.getString("nome_cliente"));
                s.setTelefoneCliente(rs.getString("telefone_cliente"));
                s.setCidadeCliente(rs.getString("cidade_cliente"));
                s.setBairroCliente(rs.getString("bairro_cliente"));
                s.setRuaCliente(rs.getString("rua_cliente"));

                s.setPrestadorId(rs.getLong("prestadores_id"));
                s.setAgendaId(rs.getLong("agenda_id"));
                s.setDataDisponivel(rs.getDate("data_disponivel").toString());
                s.setHorarioInicio(rs.getTime("horario_inicio").toString().substring(0, 5));
                s.setHorarioFim(rs.getTime("horario_fim").toString().substring(0, 5));

                lista.add(s);
            }
        }
    }

    return lista;
	}

	public boolean cancelarSolicitacaoDireta(long solicitacaoId, long prestadorId) throws SQLException {
    String buscar = "SELECT agenda_id FROM solicitacoes_servicos " +
                    "WHERE id = ? AND prestadores_id = ? AND tipo_solicitacao = 0 AND status = 0 " +
                    "FOR UPDATE";

    String cancelar = "UPDATE solicitacoes_servicos SET status = 4 WHERE id = ?";

    String liberarAgenda = "UPDATE agenda SET status_horario = 0 WHERE id = ?";

    try {
        db.setAutoCommit(false);

        long agendaId = -1;

        try (PreparedStatement ps = db.prepareStatement(buscar)) {
            ps.setLong(1, solicitacaoId);
            ps.setLong(2, prestadorId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    agendaId = rs.getLong("agenda_id");
                } else {
                    db.rollback();
                    return false;
                }
            }
        }

        try (PreparedStatement ps = db.prepareStatement(cancelar)) {
            ps.setLong(1, solicitacaoId);
            ps.executeUpdate();
        }

        try (PreparedStatement ps = db.prepareStatement(liberarAgenda)) {
            ps.setLong(1, agendaId);
            ps.executeUpdate();
        }

        db.commit();
        return true;

    } catch (SQLException e) {
        db.rollback();
        throw e;
    } finally {
        db.setAutoCommit(true);
    }
	}

	public boolean cancelarSolicitacaoDiretaCliente(long solicitacaoId, long clienteId) throws SQLException {
    String buscar = "SELECT agenda_id FROM solicitacoes_servicos " +
                    "WHERE id = ? AND usuarios_id = ? AND tipo_solicitacao = 0 AND status = 0 " +
                    "FOR UPDATE";

    String cancelar = "UPDATE solicitacoes_servicos SET status = 2 WHERE id = ?";

    String liberarAgenda = "UPDATE agenda SET status_horario = 0 WHERE id = ?";

    try {
        db.setAutoCommit(false);

        long agendaId = -1;

        try (PreparedStatement ps = db.prepareStatement(buscar)) {
            ps.setLong(1, solicitacaoId);
            ps.setLong(2, clienteId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    agendaId = rs.getLong("agenda_id");
                } else {
                    db.rollback();
                    return false;
                }
            }
        }

        try (PreparedStatement ps = db.prepareStatement(cancelar)) {
            ps.setLong(1, solicitacaoId);
            ps.executeUpdate();
        }

        try (PreparedStatement ps = db.prepareStatement(liberarAgenda)) {
            ps.setLong(1, agendaId);
            ps.executeUpdate();
        }

        db.commit();
        return true;

    } catch (SQLException e) {
        db.rollback();
        throw e;
    } finally {
        db.setAutoCommit(true);
    }
}
	
	//Insere a avaliação no BD
	public boolean inserirAvaliacao(Long idSolicitacao, Long idUsuario, long idPrestador, int nota, String comentario) throws SQLException {
	    String INSERT_SQL = "INSERT INTO avaliacoes (solicitacoes_id, usuarios_id, prestador_id, nota, comentario, created_at) VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";
	    
	    try (PreparedStatement pstmt = db.prepareStatement(INSERT_SQL)) {
	        pstmt.setLong(1, idSolicitacao);
	        pstmt.setLong(2, idUsuario);
	        pstmt.setLong(3, idPrestador);
	        pstmt.setInt(4, nota);
	        pstmt.setString(5, comentario);
	        
	        int linhasAfetadas = pstmt.executeUpdate();
	        return linhasAfetadas > 0;
	    }
	}
	
	//Busca no BD o id do prestador
	public Long descobrirPrestadorDaSolicitacao(Long idSolicitacao) throws SQLException {
	    String SELECT_SQL = "SELECT prestadores_id FROM ofertas WHERE solicitacoes_servicos_id = ? AND status IN (1, 3) LIMIT 1";
	    
	    try (PreparedStatement pstmt = db.prepareStatement(SELECT_SQL)) {
	        pstmt.setLong(1, idSolicitacao);
	        
	        try (ResultSet rs = pstmt.executeQuery()) {
	            if (rs.next()) {
	                return rs.getLong(1);
	            }
	        }
	    }
	    return null;
	}

	public boolean excluirSolicitacaoDiretaCanceladaPrestador(long solicitacaoId, long prestadorId) throws SQLException {
	    String sql = "DELETE FROM solicitacoes_servicos " +
	                 "WHERE id = ? " +
	                 "AND prestadores_id = ? " +
	                 "AND tipo_solicitacao = 0 " +
	                 "AND status IN (2, 4)";

	    try (PreparedStatement ps = db.prepareStatement(sql)) {
	        ps.setLong(1, solicitacaoId);
	        ps.setLong(2, prestadorId);

	        return ps.executeUpdate() > 0;
	    }
	}
	
	public boolean concluirSolicitacaoDireta(long solicitacaoId, long prestadorId) throws SQLException {
	    String buscar = "SELECT agenda_id FROM solicitacoes_servicos " +
	                    "WHERE id = ? " +
	                    "AND prestadores_id = ? " +
	                    "AND tipo_solicitacao = 0 " +
	                    "AND status = 0 " +
	                    "FOR UPDATE";

	    String concluirSolicitacao = "UPDATE solicitacoes_servicos " +
	                                 "SET status = 3 " +
	                                 "WHERE id = ?";

	    String concluirAgenda = "UPDATE agenda " +
	                            "SET status_horario = 2 " +
	                            "WHERE id = ?";

	    try {
	        db.setAutoCommit(false);

	        long agendaId = -1;

	        try (PreparedStatement ps = db.prepareStatement(buscar)) {
	            ps.setLong(1, solicitacaoId);
	            ps.setLong(2, prestadorId);

	            try (ResultSet rs = ps.executeQuery()) {
	                if (rs.next()) {
	                    agendaId = rs.getLong("agenda_id");
	                } else {
	                    db.rollback();
	                    return false;
	                }
	            }
	        }

	        try (PreparedStatement ps = db.prepareStatement(concluirSolicitacao)) {
	            ps.setLong(1, solicitacaoId);
	            ps.executeUpdate();
	        }

	        try (PreparedStatement ps = db.prepareStatement(concluirAgenda)) {
	            ps.setLong(1, agendaId);
	            ps.executeUpdate();
	        }

	        db.commit();
	        return true;

	    } catch (SQLException e) {
	        db.rollback();
	        throw e;
	    } finally {
	        db.setAutoCommit(true);
	    }
	}
	
}
