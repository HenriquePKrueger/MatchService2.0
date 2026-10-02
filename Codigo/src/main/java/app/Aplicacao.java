package app;

import static spark.Spark.*;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.MultipartConfigElement;

import com.google.gson.Gson;

import io.github.cdimascio.dotenv.Dotenv;

import service.PrestadorPerfilService;
import dao.PrestadorDAO;
import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import model.CriacaoSolicitacaoResult;
import model.Usuario;
import model.Avaliacao;
import service.CategoriaService;
import service.AgendaService;
import service.NotificacaoService;
import service.OfertaService;
import service.PrestadorService;
import service.SolicitacaoServicoService;
import service.UsuarioService;
import spark.ModelAndView;
import spark.Spark;
import spark.template.freemarker.FreeMarkerEngine;

public class Aplicacao {
	private static Configuration freeMarkerConfig;
	private static FreeMarkerEngine engine;
	private static final Gson gson = new Gson();
	private static final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
	
	public static void setupFreeMarker() {
		freeMarkerConfig = new Configuration(Configuration.VERSION_2_3_26);
        freeMarkerConfig.setClassForTemplateLoading(Aplicacao.class, "/templates");
		freeMarkerConfig.setDefaultEncoding("UTF-8");
		freeMarkerConfig.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		freeMarkerConfig.setLogTemplateExceptions(false);
		freeMarkerConfig.setSQLDateAndTimeTimeZone(TimeZone.getDefault());
		engine = new FreeMarkerEngine(freeMarkerConfig);
    }
	
	public static void main(String[] args) {
		setupFreeMarker();
		
		port(8080);
		staticFiles.location("/public"); 
		String diretorioStorage = System.getProperty("user.dir") + File.separator + "storage";
	    Spark.staticFiles.externalLocation(diretorioStorage);
	    
		CategoriaService categoriaService = new CategoriaService();
		UsuarioService usuarioService = new UsuarioService();
		SolicitacaoServicoService solicitacaoServicoService = new SolicitacaoServicoService();
		AgendaService agendaService = new AgendaService();
		OfertaService ofertaService = new OfertaService();
		NotificacaoService notificacaoService = new NotificacaoService();
		PrestadorService prestadorService = new PrestadorService();
		
		before("*", (req, res) -> {
		    if (req.session().attribute("user_id") != null) {
		        req.attribute("user_login", req.session().attribute("user_login"));
		        req.attribute("user_id", req.session().attribute("user_id"));
		        req.attribute("tipo_usuario", req.session().attribute("tipo_usuario"));
		    }
		});
		get("/", (req, res) -> {
			Map<String, Object> model = getModel(req);
			
			return render(model, "index.ftl");
		});

		get("/login", (req, res)-> {
			Map<String, Object> model = getModel(req);
			
			model.put("erro", req.session().attribute("erro"));
			req.session().removeAttribute("erro");
			
			return render(model, "form/login.ftl");
		});
		
		post("/login", (req, res)-> {
			usuarioService.autenticarUsuario(req, res);
			return null;
		});
		
		get("/perfil", (req, res)-> {
			Map<String, Object> model = getModel(req);
			Usuario u = usuarioService.carregarDadosPerfil(req, res);
			model.put("usuario", u);
		
		    model.put("categoriasDoUsuario", req.attribute("categoriasDoUsuario"));
		    model.put("descricao", req.attribute("descricao"));
		    
			model.put("erro", req.session().attribute("erro"));
			model.put("sucesso", req.session().attribute("sucesso"));
		
			req.session().removeAttribute("erro");
			req.session().removeAttribute("sucesso");
			
			return render(model, "form/perfil.ftl");
		});
		
		post("/editar-perfil", (req, res) -> {
			usuarioService.editarPerfil(req, res);
			
			return null;
		});
		
		get("/cadastro", (req, res) -> {
			Map<String, Object> model = getModel(req);
			
			model.put("categorias", categoriaService.obterCategorias());
			model.put("erro", req.session().attribute("erro"));

			model.put("temp_nome", req.session().attribute("temp_nome"));
			model.put("temp_email", req.session().attribute("temp_email"));
			model.put("temp_telefone", req.session().attribute("temp_telefone"));
			
			model.put("temp_rua", req.session().attribute("temp_rua"));
			model.put("temp_bairro", req.session().attribute("temp_bairro"));
			model.put("temp_cidade", req.session().attribute("temp_cidade"));
			model.put("temp_login", req.session().attribute("temp_login"));
			
			req.session().removeAttribute("erro");
			req.session().removeAttribute("temp_nome");
			req.session().removeAttribute("temp_email");
			req.session().removeAttribute("temp_telefone");
			
			req.session().removeAttribute("temp_rua");
			req.session().removeAttribute("temp_bairro");
			req.session().removeAttribute("temp_cidade");
			
			req.session().removeAttribute("temp_login");
			
			return render(model, "form/cadastro.ftl");
		});

		post("/cadastro", (req, res) -> {
		    usuarioService.criarUsuario(req, res);
		    return null;
		});
		
		get("/logout", (req, res) -> {
			req.session().invalidate();
		    res.redirect("/");
		    return null;
		});
		
		get("/about", (req, res) -> {
			Map<String, Object> model = getModel(req);
			
			return render(model, "about/index.ftl");
		});
		
		before("/criar-solicitacao", (request, response) -> {
			if(request.session().attribute("user_id") == null) {
				response.redirect("/login"); 
			}
		});
		
		get("/criar-solicitacao", (req, res) -> {
			Map<String, Object> model = getModel(req);
			model.put("categorias", categoriaService.obterCategorias());
			
			return render(model, "solicitacao_de_servico/index.ftl");
		});
		
		post("/criar-solicitacao", (req, res) -> {
			req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));
	
			CriacaoSolicitacaoResult result = solicitacaoServicoService.criarNovaSolicitacao(req, res);
			
			Map<String, Object> stringMap = new HashMap<>();
			boolean isSuccess = result.isSuccess();
			String mensagem = result.getMessage();
			
			res.status(isSuccess ? 201 : 400);
			
			stringMap.put("mensagem", mensagem);
			stringMap.put("avisos", result.getWarnings());
			
			return gson.toJson(stringMap);
		});
		
		get("/obter-endereco", (req, res) -> {
			Map<String, String> stringMap = new HashMap<>();
			usuarioService.carregarEnderecoDoUsuario(req, res);
			stringMap.put("rua", req.attribute("rua"));
			stringMap.put("cep", req.attribute("cep"));
			stringMap.put("bairro", req.attribute("bairro"));
			stringMap.put("cidade", req.attribute("cidade"));
			stringMap.put("lat", req.attribute("lat"));
			stringMap.put("lng", req.attribute("lng"));
			return gson.toJson(stringMap);
		});
	
		//Chama o ftl criado para a pesquisa
		
		get("/pesquisa", (req, res) -> {
			Map<String, Object> model = getModel(req);
			PrestadorDAO prestadorDAO = new PrestadorDAO();
			
			model.put("categorias", categoriaService.obterCategorias());
			
			String idCatStr = req.queryParams("idCategoria");
			String cidade = req.queryParams("cidade");
			String genero = req.queryParams("genero");
			
			
			if (req.queryParams().size() > 0) {
				int idCategoria = (idCatStr != null && !idCatStr.isEmpty()) ? Integer.parseInt(idCatStr) : 0;
				String cidadeFiltro = (cidade != null && !cidade.isEmpty()) ? cidade : null;
				String generoFiltro = (genero != null && !genero.isEmpty()) ? genero : null;
				
				List<model.Prestador> resultados = prestadorDAO.filtrarPrestadores(idCategoria, cidadeFiltro, generoFiltro);
				model.put("idCategoria", idCategoria);
				model.put("cidadeFiltro", cidadeFiltro);
				model.put("generoFiltro", generoFiltro);
				model.put("prestadores", resultados);
				model.put("buscaFeita", true);
			}
			
			return render(model, "pesquisa/index.ftl");
		});
		
		path("/minhas-solicitacoes", () ->{
			before("*", (request, response) -> {
				if(request.session().attribute("user_id") == null) {
					response.redirect("/login"); 
				}
			});
			
			get("", (req, res) -> {
				Map<String, Object> model = getModel(req);
				model.put("solicitacoes", solicitacaoServicoService.getSolicitacoes(req, res));
				
				return render(model, "solicitacao_de_servico/minhas_solicitacoes.ftl");
			});
			
			get("/:id", (req, res) -> {
				Map<String, Object> model = getModel(req);
				Long idSolicitacao =  Long.parseLong(req.params("id"));
				
				model.put("solicitacao",  solicitacaoServicoService.getSolicitacao(req, res, idSolicitacao));
				model.put("categorias", categoriaService.obterCategorias());
				
				return render(model, "solicitacao_de_servico/editar_solicitacao.ftl");
			});
			
			put("/:id", (req, res) -> {
				Long idSolicitacao = Long.parseLong(req.params("id"));
				
				Map<String, Object> response = solicitacaoServicoService.atualizarSolicitacao(req, res, idSolicitacao);
				
				if (response.containsKey("statusCode")) {
		            res.status((int) response.get("statusCode"));

				}
				return gson.toJson(response);
			});
			
			delete("/:id", (req, res) ->{
				Long idSolicitacao = Long.parseLong(req.params("id"));
				
				Map<String, String> stringMap = new HashMap<>();
				
				stringMap.put("mensagem", solicitacaoServicoService.deletarSolicitacao(req, res, idSolicitacao));
				
				return gson.toJson(stringMap);
			});
			
		});

		before("/solicitacoes-disponiveis", (request, response) -> {
			if(request.session().attribute("user_id") == null) {
				response.redirect("/login");
			}
		});

		get("/solicitacoes-disponiveis", (req, res) -> {
			Map<String, Object> model = getModel(req);
			model.put("solicitacoes", solicitacaoServicoService.listarSolicitacoesDisponiveis(req, res));
			model.put("idsOfertados", ofertaService.obterIdsSolicitacoesOfertadasAtivas(req, res));
			model.put("idsRecusados", ofertaService.obterIdsSolicitacoesOfertadasRecusadas(req, res));
			
			return render(model, "solicitacao_de_servico/solicitacoes_disponiveis.ftl");
		});

		get("/solicitacoes-disponiveis/detalhes/:id", (req, res) -> {
			Map<String, Object> model = getModel(req);
			Long idSolicitacao = Long.parseLong(req.params("id"));
			
			return gson.toJson(solicitacaoServicoService.getSolicitacao(req, res, idSolicitacao));
		});
		
		get("/solicitacoes/:id/ofertas", (req, res) -> {
			Map<String, Object> model = getModel(req);
			model.putAll(ofertaService.carregarOfertasDaSolicitacao(req, res));
			return render(model, "solicitacao_de_servico/ofertas.ftl");
		});

		get("/solicitacoes/:id/ofertar", (req, res) -> {
			Map<String, Object> model = getModel(req);
			model.putAll(ofertaService.carregarFormularioOferta(req, res));
			return render(model, "solicitacao_de_servico/ofertar.ftl");
		});

		
		post("/solicitacoes/:id/ofertar", (req, res) -> {
			ofertaService.criarOferta(req, res);
			return null;
		});

		post("/ofertas/:id/aceitar", (req, res) -> {
			ofertaService.aceitarOferta(req, res);
			return null;
		});

		post("/ofertas/:id/recusar", (req, res) -> {
			ofertaService.recusarOferta(req, res);
			return null;
		});


		get("/contatos-prestador/:id", (req, res) -> {
			res.type("application/json");
			Long idPrestador = Long.parseLong(req.params(":id"));
			return gson.toJson(prestadorService.obterContatosDoPrestador(idPrestador));
		});
		

		get("/prestador/ofertas", (req, res) -> {
			Map<String, Object> model = getModel(req);
			model.putAll(ofertaService.carregarOfertasDoPrestador(req, res));
			return render(model, "prestador/ofertas.ftl");
		});

		post("/prestador/ofertas/:id/finalizar", (req, res) -> {
			ofertaService.finalizarServico(req, res);
			return null;
		});
		
		//Avaliação ao prestador
		post("/avaliar-servico", (req, res) -> {
		    res.type("application/json");
		    Map<String, String> resposta = new HashMap<>();
		    
		    boolean sucesso = solicitacaoServicoService.avaliarServico(req, res);
		    
		    if (sucesso) {
		        res.status(201);
		        resposta.put("mensagem", "Sua avaliação foi registrada com sucesso!");
		    } else {
		        res.status(400);
		        resposta.put("mensagem", "Erro ao registrar avaliação.");
		    }
		    
		    return gson.toJson(resposta);
		});

		get("/notificacoes", (req, res) -> {
			res.type("application/json");
			return gson.toJson(notificacaoService.listarNotificacoes(req, res));
		});

		post("/notificacoes/:id/lida", (req, res) -> {
			res.type("application/json");
			return gson.toJson(notificacaoService.marcarComoLida(req, res));
		});
		
		before("/agenda", (req, res) -> {
		    if (req.session().attribute("user_id") == null) {
		        res.redirect("/login");
		        Spark.halt();
		    }

		    Byte tipo = req.session().attribute("tipo_usuario");

		    if (tipo == null || tipo != 1) {
		        res.redirect("/");
		        Spark.halt();
		    }
		});

		get("/agenda", (req, res) -> {
		    Map<String, Object> model = getModel(req);

		    model.put("horarios", agendaService.carregarAgendaDoPrestador(req));
		    model.put("erro", req.session().attribute("erro"));
		    model.put("sucesso", req.session().attribute("sucesso"));

		    req.session().removeAttribute("erro");
		    req.session().removeAttribute("sucesso");

		    return render(model, "agenda/index.ftl");
		});

		post("/agenda", (req, res) -> {
		    agendaService.criarHorario(req, res);
		    return null;
		});

		post("/agenda/remover/:id", (req, res) -> {
		    agendaService.removerHorario(req, res);
		    return null;
		});
		
		//perfil do prestador:
		PrestadorPerfilService prestadorPerfilService = new PrestadorPerfilService();

			before("/prestadores/:id", (req, res) -> {
    			if (req.session().attribute("user_id") == null) {
        			res.redirect("/login");
        			Spark.halt();
    			}
				});

			get("/prestadores/:id", (req, res) -> {
    			Map<String, Object> model = getModel(req);

    			model.put("prestador", prestadorPerfilService.carregarPerfil(req));
    			
    			PrestadorDAO prestadorDAO = new PrestadorDAO();
    		    List<Avaliacao> avaliacoes = prestadorDAO.buscarAvaliacoesDoPrestador(Long.parseLong(req.params("id")));
    		    model.put("avaliacoes", avaliacoes);

    			return render(model, "prestador_perfil/index.ftl");
				});


		post("/solicitacoes-diretas", (req, res) -> {
    	res.type("application/json");

    	Map<String, Object> resposta = new HashMap<>();

    	Byte tipo = req.session().attribute("tipo_usuario");

    	if (req.session().attribute("user_id") == null || tipo == null || tipo != 0) {
        	res.status(403);
        	resposta.put("sucesso", false);
        	resposta.put("mensagem", "Apenas clientes podem solicitar serviços diretamente.");
        	return gson.toJson(resposta);
    	}

    	boolean ok = solicitacaoServicoService.criarSolicitacaoDireta(req, res);

    	resposta.put("sucesso", ok);
    	resposta.put("mensagem", ok
        	? "Solicitação enviada com sucesso!"
       	 	: "Não foi possível enviar a solicitação. O horário pode já ter sido reservado.");

    	return gson.toJson(resposta);
	});

		get("/solicitacoes-diretas", (req, res) -> {
    		if (req.session().attribute("user_id") == null) {
        		res.redirect("/login");
        		Spark.halt();
    		}

    		Byte tipo = req.session().attribute("tipo_usuario");

    		if (tipo == null || tipo != 1) {
        		res.redirect("/");
        		Spark.halt();
    		}

    		Map<String, Object> model = getModel(req);
    		model.put("solicitacoes", solicitacaoServicoService.listarSolicitacoesDiretasPrestador(req, res));

    		return render(model, "solicitacao_de_servico/solicitacoes_diretas.ftl");
		});
		
		post("/solicitacoes-diretas/:id/cancelar", (req, res) -> {
		    solicitacaoServicoService.cancelarSolicitacaoDireta(req, res);
		    res.redirect("/solicitacoes-diretas");
		    return null;
		});

		post("/solicitacoes-diretas/:id/excluir", (req, res) -> {
		    solicitacaoServicoService.excluirSolicitacaoDiretaCanceladaPrestador(req, res);
		    res.redirect("/solicitacoes-diretas");
		    return null;
		});
		
		post("/solicitacoes-diretas/:id/concluir", (req, res) -> {
		    solicitacaoServicoService.concluirSolicitacaoDireta(req, res);
		    res.redirect("/solicitacoes-diretas");
		    return null;
		});
		
		System.out.println("Servidor rodando em http://localhost:8080");
	}
	
	public static String render(Map<String, Object> model, String templatePath) {
		return engine.render(new ModelAndView(model, templatePath));
	}
	
	private static Map<String, Object> getModel(spark.Request req) {
	    Map<String, Object> model = new HashMap<>();
	  
	    model.put("user_login", req.attribute("user_login"));
	    model.put("user_id", req.attribute("user_id"));
	    model.put("tipo_usuario", req.attribute("tipo_usuario"));
	    model.put("mapbox_access_token", dotenv.get("MAPBOX_ACCESS_TOKEN", ""));
	    return model;
	}
}
