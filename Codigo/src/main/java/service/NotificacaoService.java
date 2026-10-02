package service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dao.NotificacaoDAO;
import model.Notificacao;
import spark.Request;
import spark.Response;
import spark.Spark;

public class NotificacaoService {
	private NotificacaoDAO notificacaoDAO;

	public NotificacaoService() {
		this.notificacaoDAO = new NotificacaoDAO();
	}

	public List<Notificacao> listarNotificacoes(Request req, Response res) {
		Long idUsuario = exigirLogin(req, res);
		return notificacaoDAO.listarPorUsuario(idUsuario);
	}

	public Map<String, Object> marcarComoLida(Request req, Response res) {
		Long idUsuario = exigirLogin(req, res);
		Long idNotificacao = parseLong(req.params(":id"));
		Map<String, Object> retorno = new HashMap<>();

		try {
			boolean atualizada = notificacaoDAO.marcarComoLida(idNotificacao, idUsuario);
			res.status(atualizada ? 200 : 404);
			retorno.put("sucesso", atualizada);
			return retorno;
		} catch (SQLException e) {
			System.err.println("Erro ao marcar notificação: " + e.getMessage());
			res.status(500);
			retorno.put("sucesso", false);
			return retorno;
		}
	}

	private Long exigirLogin(Request req, Response res) {
		Long idUsuario = (Long) req.session().attribute("user_id");
		if (idUsuario == null) {
			res.redirect("/login");
			Spark.halt();
		}
		return idUsuario;
	}

	private Long parseLong(String value) {
		try {
			return Long.parseLong(value);
		} catch (NumberFormatException e) {
			Spark.halt(400, "Identificador inválido.");
			return 0L;
		}
	}
}
