package service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dao.NotificacaoDAO;
import dao.OfertaDAO;
import dao.PrestadorDAO;
import dao.SolicitacaoServicoDAO;
import dao.PrestadorDAO.NivelDetalhePrestador;
import model.Oferta;
import model.Prestador;
import model.SolicitacaoServico;
import spark.Request;
import spark.Response;
import spark.Spark;

public class OfertaService {
	private static final int TIPO_PRESTADOR = 1;
	private static final int STATUS_PENDENTE = 0;
	private static final int STATUS_ACEITA = 1;
	private static final int STATUS_RECUSADA = 2;
	private static final int STATUS_SOLICITACAO_PRESTADOR_ESCOLHIDO = 1;
	private static final int STATUS_SOLICITACAO_CONCLUIDA = 3;
	
	private OfertaDAO ofertaDAO;
	private PrestadorDAO prestadorDAO;
	private SolicitacaoServicoDAO solicitacaoServicoDAO;
	private NotificacaoDAO notificacaoDAO;

	public OfertaService() {
		this.ofertaDAO = new OfertaDAO();
		this.prestadorDAO = new PrestadorDAO();
		this.solicitacaoServicoDAO = new SolicitacaoServicoDAO();
		this.notificacaoDAO = new NotificacaoDAO();
	}

	public Map<String, Object> carregarOfertasDaSolicitacao(Request req, Response res) {
		Long idUsuario = exigirLogin(req, res);
		Long idSolicitacao = parseLong(req.params(":id"));
		SolicitacaoServico solicitacao = buscarSolicitacaoOuEncerrar(idSolicitacao, res);

		if (!solicitacao.getUsuariosId().equals(idUsuario)) {
			encerrarComStatus(res, 403, "Você não tem permissão para visualizar estas ofertas.");
		}

		Map<String, Object> model = new HashMap<>();
		model.put("solicitacao", solicitacao);
		model.put("ofertas", ofertaDAO.listarPorSolicitacao(idSolicitacao));
		return model;
	}

	public Map<String, Object> carregarFormularioOferta(Request req, Response res) {
		Long idUsuario = exigirLogin(req, res);
		exigirPrestador(req, res);
		Long idSolicitacao = parseLong(req.params(":id"));
		SolicitacaoServico solicitacao = buscarSolicitacaoOuEncerrar(idSolicitacao, res);
		Prestador prestador = buscarPrestadorOuEncerrar(idUsuario, res);

		validarPrestadorPodeOfertar(solicitacao, prestador, idUsuario, res);

		Map<String, Object> model = new HashMap<>();
		model.put("solicitacao", solicitacao);
		model.put("erro", req.session().attribute("erro"));
		model.put("sucesso", "1".equals(req.queryParams("sucesso")));
		req.session().removeAttribute("erro");
		return model;
	}

	public void criarOferta(Request req, Response res) {
		Long idUsuario = exigirLogin(req, res);
		exigirPrestador(req, res);
		Long idSolicitacao = parseLong(req.params(":id"));
		SolicitacaoServico solicitacao = buscarSolicitacaoOuEncerrar(idSolicitacao, res);
		Prestador prestador = buscarPrestadorOuEncerrar(idUsuario, res);

		validarPrestadorPodeOfertar(solicitacao, prestador, idUsuario, res);

		String descricao = getParamSafe(req, "descricao");
		String disponibilidade = getParamSafe(req, "disponibilidade");
		String valorStr = getParamSafe(req, "valor").replace(".", "").replace(",", ".");

		if (isBlank(descricao) || isBlank(disponibilidade) || isBlank(valorStr)) {
			encerrarComErro(req, res, "Preencha valor, descrição e disponibilidade.",
					"/solicitacoes/" + idSolicitacao + "/ofertar");
		}

		try {
			Oferta oferta = new Oferta();
			oferta.setDescricao(descricao);
			oferta.setDisponibilidade(disponibilidade);
			oferta.setValor(Double.parseDouble(valorStr));
			oferta.setStatus(STATUS_PENDENTE);
			oferta.setSolicitacoesServicosId(idSolicitacao.intValue());
			oferta.setPrestadoresId((int) prestador.getId());

			Long idOferta = ofertaDAO.inserirOferta(oferta);
			String mensagem = "Você recebeu uma nova oferta para a solicitação #" + idSolicitacao + ".";
			notificacaoDAO.inserirNotificacao(solicitacao.getUsuariosId(), mensagem, idOferta);

			res.redirect("/solicitacoes-disponiveis");
		} catch (NumberFormatException e) {
			encerrarComErro(req, res, "Informe um valor válido para a oferta.",
					"/solicitacoes/" + idSolicitacao + "/ofertar");
		} catch (SQLException e) {
			System.err.println("Erro ao criar oferta: " + e.getMessage());
			encerrarComErro(req, res, "Não foi possível enviar a oferta. Tente novamente.",
					"/solicitacoes/" + idSolicitacao + "/ofertar");
		}
	}

	public void aceitarOferta(Request req, Response res) {
		alterarStatusOfertaDoCliente(req, res, STATUS_ACEITA, null);
	}

	public void recusarOferta(Request req, Response res) {
		String justificativa = getParamSafe(req, "justificativa_recusa");
		String hasOutroMotivo = getParamSafe(req, "detalhes");
		
		if (isBlank(justificativa)) {
			justificativa = "Oferta recusada pelo cliente";
		}
		
		if(!isBlank(hasOutroMotivo)) {
			justificativa = hasOutroMotivo;
		}
		
		alterarStatusOfertaDoCliente(req, res, STATUS_RECUSADA, justificativa);
	}

	public List<Long> obterIdsSolicitacoesOfertadasAtivas(Request req, Response res){
		List<Long> solicitacoes = new ArrayList<>();
		
		try {
			Long usuarioId = (Long) req.session().attribute("user_id");
			Prestador p = prestadorDAO.selectPrestador("usuarios_id", usuarioId, NivelDetalhePrestador.SIMPLES);
			
			solicitacoes = ofertaDAO.obterIdsSolicitacoesOfertadasAtivas(p.getId());
		} catch (SQLException e) {
			System.err.println("Erro na consulta: " + e.getMessage());
		}

		return solicitacoes;
	}
	
	public List<Long> obterIdsSolicitacoesOfertadasRecusadas(Request req, Response res){
		List<Long> solicitacoes = new ArrayList<>();
		
		try {
			Long usuarioId = (Long) req.session().attribute("user_id");
			Prestador p = prestadorDAO.selectPrestador("usuarios_id", usuarioId, NivelDetalhePrestador.SIMPLES);
			
			solicitacoes = ofertaDAO.obterIdsSolicitacoesRecusadas(p.getId());
		} catch (SQLException e) {
			System.err.println("Erro na consulta: " + e.getMessage());
		}

		return solicitacoes;
	}

	public Map<String, Object> carregarOfertasDoPrestador(Request req, Response res) {
		Long idUsuario = exigirLogin(req, res);
		exigirPrestador(req, res);
		Prestador prestador = buscarPrestadorOuEncerrar(idUsuario, res);

		List<Oferta> ofertas = ofertaDAO.listarPorPrestador(prestador.getId());
		for (Oferta oferta : ofertas) {
			oferta.setClienteWhatsappUrl(montarWhatsappUrl(oferta.getClienteTelefone()));
		}

		Map<String, Object> model = new HashMap<>();
		model.put("ofertas", ofertas);
		model.put("sucesso", req.session().attribute("sucesso"));
		model.put("erro", req.session().attribute("erro"));
		req.session().removeAttribute("sucesso");
		req.session().removeAttribute("erro");
		return model;
	}

	public void finalizarServico(Request req, Response res) {
		Long idUsuario = exigirLogin(req, res);
		exigirPrestador(req, res);
		Prestador prestador = buscarPrestadorOuEncerrar(idUsuario, res);
		Long idOferta = parseLong(req.params(":id"));
		try {
			Oferta o = ofertaDAO.selectOferta(idOferta);
			SolicitacaoServico s = solicitacaoServicoDAO.getSolicitacao((long)o.getSolicitacoesServicosId());
			s.setStatus(STATUS_SOLICITACAO_CONCLUIDA);
			
			boolean solicitacaoFinalizada = solicitacaoServicoDAO.updateSolicitacao(s);
			boolean finalizada = ofertaDAO.finalizarOfertaDoPrestador(idOferta, prestador.getId());
			
			if (finalizada && solicitacaoFinalizada) {
				req.session().attribute("sucesso", "Serviço finalizado com sucesso.");
			} else {
				req.session().attribute("erro", "Não foi possível finalizar esta oferta.");
			}
			res.redirect("/prestador/ofertas");
		} catch (SQLException e) {
			System.err.println("Erro ao finalizar oferta: " + e.getMessage());
			encerrarComStatus(res, 500, "Não foi possível finalizar a oferta.");
		}
	}
	
	private void alterarStatusOfertaDoCliente(Request req, Response res, int status, String justificativa) {
		Long idUsuario = exigirLogin(req, res);
		Long idOferta = parseLong(req.params(":id"));
		Oferta oferta = buscarOfertaOuEncerrar(idOferta, res);
		SolicitacaoServico solicitacao = buscarSolicitacaoOuEncerrar((long) oferta.getSolicitacoesServicosId(), res);

		if (!solicitacao.getUsuariosId().equals(idUsuario)) {
			encerrarComStatus(res, 403, "Você não tem permissão para alterar esta oferta.");
		}

		try {
			ofertaDAO.atualizarStatusOferta(idOferta, status, justificativa);
			if (status == STATUS_ACEITA) {
				ofertaDAO.recusarOutrasOfertas((long) oferta.getSolicitacoesServicosId(), idOferta);
				solicitacaoServicoDAO.atualizarStatus((long) oferta.getSolicitacoesServicosId(),
						STATUS_SOLICITACAO_PRESTADOR_ESCOLHIDO);
			}
			res.redirect("/solicitacoes/" + oferta.getSolicitacoesServicosId() + "/ofertas");
		} catch (SQLException e) {
			System.err.println("Erro ao atualizar oferta: " + e.getMessage());
			encerrarComStatus(res, 500, "Não foi possível atualizar a oferta.");
		}
	}

	private void validarPrestadorPodeOfertar(SolicitacaoServico solicitacao, Prestador prestador, Long idUsuario,
			Response res) {
		if (solicitacao.getUsuariosId().equals(idUsuario)) {
			encerrarComStatus(res, 403, "Você não pode ofertar na própria solicitação.");
		}

		if (ofertaDAO.prestadorJaOfertou(solicitacao.getId(), prestador.getId())) {
			encerrarComStatus(res, 409, "Você já enviou uma oferta para esta solicitação.");
		}
	}

	private void exigirPrestador(Request req, Response res) {
		Byte tipoUsuario = (Byte) req.session().attribute("tipo_usuario");
		if (tipoUsuario == null || tipoUsuario != TIPO_PRESTADOR) {
			encerrarComStatus(res, 403, "Apenas prestadores podem enviar ofertas.");
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

	private SolicitacaoServico buscarSolicitacaoOuEncerrar(Long idSolicitacao, Response res) {
		try {
			SolicitacaoServico solicitacao = solicitacaoServicoDAO.getSolicitacao(idSolicitacao);
			if (solicitacao == null || solicitacao.getId() == null) {
				encerrarComStatus(res, 404, "Solicitação não encontrada.");
			}
			return solicitacao;
		} catch (SQLException e) {
			System.err.println("Erro ao buscar solicitação: " + e.getMessage());
			encerrarComStatus(res, 500, "Erro ao buscar solicitação.");
			return null;
		}
	}

	private Oferta buscarOfertaOuEncerrar(Long idOferta, Response res) {
		Oferta oferta = ofertaDAO.selectOferta(idOferta);
		if (oferta == null) {
			encerrarComStatus(res, 404, "Oferta não encontrada.");
		}
		return oferta;
	}

	private Prestador buscarPrestadorOuEncerrar(Long idUsuario, Response res) {
		Prestador prestador = prestadorDAO.selectPrestador("usuarios_id", idUsuario, NivelDetalhePrestador.SIMPLES);
		if (prestador == null) {
			encerrarComStatus(res, 403, "Cadastro de prestador não encontrado.");
		}
		return prestador;
	}

	private Long parseLong(String value) {
		try {
			return Long.parseLong(value);
		} catch (NumberFormatException e) {
			Spark.halt(400, "Identificador inválido.");
			return 0L;
		}
	}

	private String getParamSafe(Request req, String param) {
		String value = req.queryParams(param);
		return (value == null) ? "" : value.strip();
	}

	private boolean isBlank(String str) {
		return str == null || str.trim().isEmpty();
	}

	private String montarWhatsappUrl(String telefone) {
		if (isBlank(telefone)) {
			return "";
		}

		String numero = telefone.replaceAll("[^0-9]", "");
		if (numero.isEmpty()) {
			return "";
		}

		if (!numero.startsWith("55")) {
			numero = "55" + numero;
		}

		return "https://wa.me/" + numero;
	}

	private void encerrarComErro(Request req, Response res, String mensagem, String redirect) {
		req.session().attribute("erro", mensagem);
		res.redirect(redirect);
		Spark.halt();
	}

	private void encerrarComStatus(Response res, int status, String mensagem) {
		res.status(status);
		Spark.halt(status, mensagem);
	}
}
