package service;

import java.util.List;
import java.util.stream.Collectors;
import java.sql.SQLException;
import java.util.Arrays;

import at.favre.lib.crypto.bcrypt.BCrypt;
import dao.CategoriaDAO;
import dao.PrestadorDAO;
import dao.UsuarioDAO;
import dao.PrestadorDAO.NivelDetalhePrestador;
import model.Categoria;
import model.Prestador;
import model.Usuario;
import spark.Request;
import spark.Response;
import spark.Session;
import spark.Spark;

public class UsuarioService {
	private UsuarioDAO usuarioDAO;
	private PrestadorDAO prestadorDAO;
	private CategoriaDAO categoriaDAO;

	public UsuarioService() {
		this.usuarioDAO = new UsuarioDAO();
		this.prestadorDAO = new PrestadorDAO();
		this.categoriaDAO = new CategoriaDAO();
	}

	public void criarUsuario(Request req, Response res) {
		String nome = getParamSafe(req, "nome");
		String email = getParamSafe(req, "email");
		String telefone = getParamSafe(req, "telefone");
		String rua = getParamSafe(req, "rua address-search");
		String bairro = getParamSafe(req, "bairro");
		String cidade = getParamSafe(req, "cidade");
		String login = getParamSafe(req, "login");
		String sexo = req.queryParams("sexo");
		String senhaBruta = getParamSafe(req, "senha");
		String cep = getParamSafe(req, "cep");
		String uf = getParamSafe(req, "uf");
		String lat = getParamSafe(req, "lat");
		String lng = getParamSafe(req, "long");
		
		if (isBlank(nome) || isBlank(email) || isBlank(telefone) || isBlank(senhaBruta) || isBlank(login)) {
			encerrarComErro(req, res, "Preencha todos os campos obrigatórios!", "/cadastro");
		}

		if (!login.matches("^[a-zA-Z0-9._-]{3,20}$")) {
			encerrarComErro(req, res, "Login inválido! Use apenas letras, números e (._-)", "/cadastro");
		}

		if (usuarioDAO.campoJaExiste("login", login)) {
			encerrarComErro(req, res, "Este login já está sendo usado.", "/cadastro");
		}

		if (usuarioDAO.campoJaExiste("email", email)) {
			encerrarComErro(req, res, "Este e-mail já está cadastrado.", "/cadastro");
		}

		String senhaHash = BCrypt.withDefaults().hashToString(12, getParamSafe(req, "senha").toCharArray());

		Usuario u = new Usuario();
		u.setUf(uf);
		u.setNome(nome);
		u.setEmail(email);
		u.setTelefone(telefone);
		u.setRua(rua);
		u.setBairro(bairro);
		u.setCidade(cidade);
		u.setSenha(senhaHash);
		u.setLogin(login);
		u.setGenero(sexo);
		u.setCep(cep);
		
		//u.setLat(Double.parseDouble(lat)); //linha 76
		//u.setLng(Double.parseDouble(lng));
		
		//obs: não estava conseguindo cadastrar corretamente, tava dando erro na localização, entaõ adicionei isso:
		if(lat != null && !lat.isEmpty()) {
		    u.setLat(Double.parseDouble(lat));
		}

		if(lng != null && !lng.isEmpty()) {
		    u.setLng(Double.parseDouble(lng));
		}
		
		
		String tipoUsuario = req.queryParams("tipoUsuario");
		u.setTipoUsuario(Byte.parseByte(tipoUsuario));
		
		try {
			usuarioDAO.insertUsuario(u);

			req.session().invalidate();
			criarSessao(u, req);
		} catch (SQLException e) {
			System.err.println("Falha no banco: " + e.getMessage());
			encerrarComErro(req, res, "Erro ao processar seu cadastro. Tente novamente.", "/cadastro");
		}

		String[] categoriasSelecionadas = req.queryParamsValues("id_categoria");

		if ("1".equals(tipoUsuario)) {
			Prestador p = new Prestador();
			p.setUsuario(u);
			prestadorDAO.inserirPrestador(p);

			if (categoriasSelecionadas != null) {
				List<Integer> ids = Arrays.stream(categoriasSelecionadas).map(Integer::parseInt)
						.collect(Collectors.toList());
				usuarioDAO.vincularCategorias(p.getId(), ids);
			}
		}

		res.redirect("/");
	}

	public void autenticarUsuario(Request req, Response res) {
		String login = getParamSafe(req, "login");
		String senha = getParamSafe(req, "senha");
		if (isBlank(login) || isBlank(senha)) {
			encerrarComErro(req, res, "Por favor, informe o login e a senha.", "/login");
		}

		Usuario u = usuarioDAO.selectUsuario("login", login);

		if (u == null || !BCrypt.verifyer().verify(senha.toCharArray(), u.getSenha()).verified) {
			encerrarComErro(req, res, "Usuário ou senha inválidos.", "/login");
		}

		criarSessao(u, req);
		res.redirect("/");
	}

	public Usuario carregarDadosPerfil(Request req, Response res) {
		Long idUsuario = (Long) req.session().attribute("user_id");

		if (idUsuario == null) {
			res.redirect("/login");
			Spark.halt();
			return null;
		}

		Usuario u = usuarioDAO.selectUsuario("id", idUsuario);
		
		if (u.getTipoUsuario() == 1) {
			Prestador p = prestadorDAO.selectPrestador("usuarios_id", idUsuario, NivelDetalhePrestador.SIMPLES);
			p.setCategorias(categoriaDAO.getCategorias());
			p.setUsuario(u);
	
			List<Categoria> todasCategorias = categoriaDAO.getCategorias();
	        List<Integer> idsDoUsuario = categoriaDAO.buscarIdsPorUsuario(p.getId());
	        
			req.attribute("categorias", todasCategorias);
			req.attribute("categoriasDoUsuario", idsDoUsuario);
			
			u.setPrestador(p);
		}

		return u;
	}

	public void carregarEnderecoDoUsuario(Request req, Response res) {
		Long idUsuario = (Long) req.session().attribute("user_id");
		
		Usuario u = usuarioDAO.selectUsuario("id", idUsuario);
		
		req.attribute("rua", u.getRua());
		req.attribute("cep", u.getCep());
		req.attribute("cidade", u.getCidade());
		req.attribute("bairro", u.getBairro());
		req.attribute("lat", String.valueOf(u.getLat()));
		req.attribute("lng", String.valueOf(u.getLng()));
	}
	
	public void editarPerfil(Request req, Response res) {
		Long idUsuario = (Long) req.session().attribute("user_id");

		String nome = getParamSafe(req, "nome");
		String telefone = getParamSafe(req, "telefone");
		String rua = getParamSafe(req, "rua address-search");
		String bairro = getParamSafe(req, "bairro");
		String cidade = getParamSafe(req, "cidade");
		String cep = getParamSafe(req, "cep");
		String uf = getParamSafe(req, "uf");

		if (isBlank(nome) || isBlank(telefone) || isBlank(rua) || isBlank(bairro) || isBlank(cidade) || isBlank(cep)
				|| isBlank(uf)) {
			encerrarComErro(req, res, "Preencha todos os campos obrigatórios!", "/perfil");
		}

		Usuario u = usuarioDAO.selectUsuario("id", idUsuario);
		u.setNome(nome);
		u.setTelefone(telefone);
		u.setRua(rua);
		u.setBairro(bairro);
		u.setCidade(cidade);
		u.setUf(uf);
		u.setCep(cep);
		
		try {
			if (u.getTipoUsuario() == 1) {
				String descricao = getParamSafe(req, "descricao");
				Prestador p = prestadorDAO.selectPrestador("usuarios_id", idUsuario, NivelDetalhePrestador.SIMPLES);
				p.setDescricao(descricao);
				
				String[] categoriasSelecionadas = req.queryParamsValues("categorias[]");
				prestadorDAO.deleteCategoriasPrestador(p.getId());
				
				if (categoriasSelecionadas != null) {
					List<Integer> ids = Arrays.stream(categoriasSelecionadas).map(Integer::parseInt)
							.collect(Collectors.toList());

					usuarioDAO.vincularCategorias(p.getId(), ids);

				}
				prestadorDAO.updatePrestador(p);
			}

			usuarioDAO.updateUsuario(u);
			
			req.session().invalidate();
			criarSessao(u, req);
			encerrarComSucesso(req, res, "Dados atualizados com sucesso!", "/perfil");

		} catch (SQLException e) {
			System.err.println("Falha no banco: " + e.getMessage());
			encerrarComErro(req, res, "Erro ao processar sua edição. Tente novamente.", "/perfil");
		}

	}

	private void criarSessao(Usuario u, Request req) {
		Session session = req.session(true);
		session.attribute("user_login", u.getLogin());
		session.attribute("user_id", u.getId());
		session.attribute("tipo_usuario", u.getTipoUsuario());
	}

	private boolean isBlank(String str) {
		return str == null || str.trim().isEmpty();
	}

	private String getParamSafe(Request req, String param) {
		String value = req.queryParams(param);
		return (value == null) ? "" : value.strip();
	}

	private void encerrarComErro(Request req, Response res, String mensagem, String redirect) {
		req.session().attribute("erro", mensagem);

		req.session().attribute("temp_nome", req.queryParams("nome"));
		req.session().attribute("temp_email", req.queryParams("email"));
		req.session().attribute("temp_login", req.queryParams("login"));
		req.session().attribute("temp_telefone", req.queryParams("telefone"));
		req.session().attribute("temp_rua", req.queryParams("rua"));
		req.session().attribute("temp_bairro", req.queryParams("bairro"));
		req.session().attribute("temp_cidade", req.queryParams("cidade"));

		res.redirect(redirect);
		Spark.halt();
	}

	private void encerrarComSucesso(Request req, Response res, String mensagem, String redirect) {
		req.session().attribute("sucesso", mensagem);

		res.redirect(redirect);
	}
}
