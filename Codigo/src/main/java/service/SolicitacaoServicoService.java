package service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.Part;

import dao.CategoriaDAO;
import dao.OfertaDAO;
import dao.PrestadorDAO;

import dao.PrestadorDAO.NivelDetalhePrestador;

import dao.SolicitacaoImagemDAO;

import dao.SolicitacaoServicoDAO;
import model.Categoria;
import model.CriacaoSolicitacaoResult;
import model.ImageModerationResult;
import model.Prestador;
import model.SolicitacaoImagem;
import model.SolicitacaoServico;
import spark.Request;
import spark.Response;

public class SolicitacaoServicoService {
	private static final String IMAGE_FIELD_NAME = "img";
	private static final String STATUS_APROVADA = "APROVADA";
	private static final String STATUS_REJEITADA = "REJEITADA";
	private static final String STATUS_ERRO_ANALISE = "ERRO_ANALISE";
	private static final String DOCUMENT_BLOCK_MESSAGE = "A imagem enviada parece conter documento, cartão ou informação pessoal sensível. Para sua segurança, envie outra foto relacionada ao serviço.";
	private static final String MODERATION_ERROR_MESSAGE = "Não foi possível validar uma das imagens enviadas. Tente novamente com outra foto.";
	private static final String UNSAFE_IMAGE_MESSAGE = "Uma das imagens enviadas foi recusada por conter conteúdo impróprio. Envie outra foto adequada para continuar.";

	private SolicitacaoServicoDAO solicitacaoServicoDAO;
	private SolicitacaoImagemDAO solicitacaoImagemDAO;
	private CategoriaDAO categoriaDAO;
	private PrestadorDAO prestadorDAO;
	private OfertaDAO ofertaDAO;
	private ImageModerationService imageModerationService;
	private ImageUploadValidator imageUploadValidator;

	public SolicitacaoServicoService() {
		this.solicitacaoServicoDAO = new SolicitacaoServicoDAO();
		this.solicitacaoImagemDAO = new SolicitacaoImagemDAO();
		this.categoriaDAO = new CategoriaDAO();
		this.prestadorDAO = new PrestadorDAO();
		this.ofertaDAO = new OfertaDAO();
		this.imageModerationService = new ImageModerationService();
		this.imageUploadValidator = new ImageUploadValidator();
	}

	public CriacaoSolicitacaoResult criarNovaSolicitacao(Request req, Response res) {
		CriacaoSolicitacaoResult result = new CriacaoSolicitacaoResult();
		Long userId = (Long) req.session().attribute("user_id");
		int SOLICITACAO_INDIRETA = 1;
		int STATUS_ANDAMENTO = 0;

		try {
			String latStr = getParamSafe(req, "lat");
			String lngStr = getParamSafe(req, "long");
			String catStr = getParamSafe(req, "categoria");

			if (userId == null || isBlank(latStr) || isBlank(lngStr) || isBlank(catStr)) {
				result.setSuccess(false);
				result.setMessage("Dados obrigatórios da solicitação não foram informados.");
				return result;
			}

			SolicitacaoServico ss = new SolicitacaoServico();
			Categoria c = new Categoria();
			c.setId(Integer.parseInt(catStr));

			ss.setDescricao(getParamSafe(req, "descricao"));
			ss.setRua(getParamSafe(req, "rua address-search"));
			ss.setBairro(getParamSafe(req, "bairro"));
			ss.setCep(getParamSafe(req, "cep"));
			ss.setCidade(getParamSafe(req, "cidade"));
			ss.setStatus(STATUS_ANDAMENTO);
			ss.setTipoSolicitacao(SOLICITACAO_INDIRETA);
			ss.setUsuariosId(userId);
			ss.setLat(Double.parseDouble(latStr));
			ss.setLng(Double.parseDouble(lngStr));
			ss.setCategoria(c);
			ss.setCreatedAt(LocalDateTime.now().toString());

			List<ProcessedImage> imagens = validarEModerarImagens(req);
			ProcessedImage imagemBloqueada = getImagemBloqueada(imagens);
			if (imagemBloqueada != null) {
				result.setSuccess(false);
				result.setMessage(getMensagemBloqueio(imagemBloqueada));
				return result;
			}

			Long idSolicitacao = solicitacaoServicoDAO.insertSolicitacao(ss);

			salvarERegistrarImagens(idSolicitacao, imagens, result);

			result.setSuccess(true);
			result.setMessage("Solicitação aberta com sucesso! Aguarde para receber ofertas.");
			return result;
		} catch (NumberFormatException e) {
			result.setSuccess(false);
			result.setMessage("Dados numéricos inválidos na solicitação.");
		} catch (SQLException e) {
			System.err.println("Erro na consulta: " + e.getMessage());
			result.setSuccess(false);
			result.setMessage("Erro ao processar solicitação! Tente novamente mais tarde.");
		} catch (ServletException e) {
			System.err.println("Servlet: Erro ao processar arquivo: " + e.getMessage());
			result.setSuccess(false);
			result.setMessage("Erro ao processar imagens enviadas.");
		} catch (IOException e) {
			System.err.println("Erro ao processar arquivo: " + e.getMessage());
			result.setSuccess(false);
			result.setMessage("Erro ao processar imagens enviadas.");
		}

		return result;
	}

	public List<SolicitacaoServico> getSolicitacoes(Request req, Response res) {
		List<SolicitacaoServico> solicitacoes = new ArrayList<>();
		try {
			Long usuarioId = (Long) req.session().attribute("user_id");
			solicitacoes = solicitacaoServicoDAO.selectSolicitacoes("usuarios_id", usuarioId);
		} catch (SQLException e) {
			System.err.println("Erro na consulta: " + e.getMessage());
		}

		return solicitacoes;
	}

	public List<SolicitacaoServico> listarSolicitacoesDisponiveis(Request req, Response res) {
		List<SolicitacaoServico> solicitacoes = new ArrayList<>();

		try {
			Long usuarioId = (Long) req.session().attribute("user_id");

			solicitacoes = solicitacaoServicoDAO.listarDisponiveisParaPrestador(usuarioId);
		} catch (SQLException e) {
			System.err.println("Erro na consulta: " + e.getMessage());
		}

		return solicitacoes;
	}

	public SolicitacaoServico getSolicitacao(Request req, Response res, Long idSolicitacao) {
		SolicitacaoServico s = new SolicitacaoServico();

		try {
			s = solicitacaoServicoDAO.getSolicitacao(idSolicitacao);
		} catch (SQLException e) {
			System.err.println("Erro na consulta: " + e.getMessage());
		}

		return s;
	}

	public Map<String, Object> atualizarSolicitacao(Request req, Response res, Long idSolicitacao) {
		Map<String, Object> response = new HashMap<>();

		try {
			SolicitacaoServico s = solicitacaoServicoDAO.getSolicitacao(idSolicitacao);
			Categoria c = categoriaDAO.getCategoria(Long.parseLong(getParamSafe(req, "categoria")));

			s.setBairro(getParamSafe(req, "bairro"));
			s.setDescricao(getParamSafe(req, "descricao"));
			s.setRua(getParamSafe(req, "rua address-search"));
			s.setCep(getParamSafe(req, "cep"));
			s.setCidade(getParamSafe(req, "cidade"));
			s.setCategoria(c);

			if (solicitacaoServicoDAO.updateSolicitacao(s)) {
				response.put("mensagem", "Solicitação atualizada com sucesso!");
				response.put("statusCode", 200);
			} else {
				response.put("mensagem", "Erro ao atualizar: registro não alterado.");
				response.put("statusCode", 422);
			}

		} catch (SQLException e) {
			System.err.println("Erro na atualização: " + e.getMessage());
			response.put("mensagem", "Erro ao processar solicitação. Tente novamente mais tarde.");
			response.put("statusCode", 500);
		}

		return response;
	}

	//public String deletarSolicitacao(Request req, Response res, Long idSolicitacao) {
	//	String mensagem = "";

		//try {
			//if (solicitacaoServicoDAO.deleteSolicitacao(idSolicitacao)) {
				//mensagem = "Solicitação deletada com sucesso!";
			//}
		//} catch (SQLException e) {
			//mensagem = "Erro ao processar a exclusão. Tente novamente mais tarde";
			//System.err.println("Erro na deleção: " + e.getMessage());
		//}

		//return mensagem;
	//}

	private List<ProcessedImage> validarEModerarImagens(Request req) throws IOException, ServletException {
		List<ProcessedImage> imagens = new ArrayList<>();
		Collection<Part> parts = req.raw().getParts();

		for (Part part : parts) {
			if (!IMAGE_FIELD_NAME.equals(part.getName())) {
				continue;
			}

			if (part.getSize() <= 0 && isBlank(part.getSubmittedFileName())) {
				continue;
			}

			ProcessedImage processedImage = new ProcessedImage(part);
			byte[] bytes = readBytes(part);
			ImageValidationResult validationResult = imageUploadValidator.validate(part, bytes);

			if (!validationResult.isValid()) {
				processedImage.status = STATUS_REJEITADA;
				processedImage.motivoRejeicao = validationResult.getReason();
				imagens.add(processedImage);
				continue;
			}

			try {
				ImageModerationResult moderationResult = imageModerationService.analyze(bytes);

				if (moderationResult.isApproved()) {
					processedImage.status = STATUS_APROVADA;
					processedImage.bytes = bytes;
					processedImage.extensao = validationResult.getExtension();
				} else {
					processedImage.status = STATUS_REJEITADA;
					processedImage.motivoRejeicao = moderationResult.getReason();
				}
			} catch (IOException e) {
				processedImage.status = STATUS_ERRO_ANALISE;
				processedImage.motivoRejeicao = "Não foi possível validar a imagem no momento.";
				System.err.println("Erro na moderação de imagem: " + e.getMessage());
			}

			imagens.add(processedImage);
		}

		return imagens;
	}

	private ProcessedImage getImagemBloqueada(List<ProcessedImage> imagens) {
		for (ProcessedImage imagem : imagens) {
			if (!STATUS_APROVADA.equals(imagem.status)) {
				return imagem;
			}
		}

		return null;
	}

	private String getMensagemBloqueio(ProcessedImage imagem) {
		if (STATUS_ERRO_ANALISE.equals(imagem.status)) {
			return MODERATION_ERROR_MESSAGE;
		}

		String motivo = imagem.motivoRejeicao == null ? "" : imagem.motivoRejeicao.toLowerCase();
		if (motivo.contains("documento") || motivo.contains("informação pessoal sensível")) {
			return DOCUMENT_BLOCK_MESSAGE;
		}
		if (motivo.contains("adulto") || motivo.contains("sugestivo") || motivo.contains("violento")
				|| motivo.contains("impróprio")) {
			return UNSAFE_IMAGE_MESSAGE;
		}

		return MODERATION_ERROR_MESSAGE;
	}

	private void salvarERegistrarImagens(Long idSolicitacao, List<ProcessedImage> imagens, CriacaoSolicitacaoResult result)
			throws IOException, SQLException {
		Path diretorio = Paths.get("storage");
		Files.createDirectories(diretorio);

		for (ProcessedImage processedImage : imagens) {
			SolicitacaoImagem imagem = montarRegistroImagem(idSolicitacao, processedImage);

			if (STATUS_APROVADA.equals(processedImage.status)) {
				String nomeUnico = "foto_" + System.nanoTime() + "." + processedImage.extensao;
				Path caminhoDestino = diretorio.resolve(nomeUnico);
				Files.write(caminhoDestino, processedImage.bytes);
				imagem.setUrl(nomeUnico);
			} else {
				continue;
			}

			solicitacaoImagemDAO.inserir(imagem);
		}
	}

	private SolicitacaoImagem montarRegistroImagem(Long idSolicitacao, ProcessedImage processedImage) {
		SolicitacaoImagem imagem = new SolicitacaoImagem();
		imagem.setSolicitacoesServicosId(idSolicitacao.intValue());
		imagem.setNomeOriginal(processedImage.nomeOriginal);
		imagem.setContentType(processedImage.contentType);
		imagem.setTamanhoBytes(processedImage.tamanhoBytes);

		return imagem;
	}

	private byte[] readBytes(Part part) throws IOException {
		try (InputStream inputStream = part.getInputStream(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
			byte[] buffer = new byte[8192];
			int read;
			while ((read = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, read);
			}
			return outputStream.toByteArray();
		}
	}

	private String getParamSafe(Request req, String param) {
		String value = req.queryParams(param);

		return (value == null) ? "" : value.strip();
	}

	private boolean isBlank(String str) {
		return str == null || str.trim().isEmpty();
	}


	//métodos solicitação direta

	public boolean criarSolicitacaoDireta(Request req, Response res) {
    Long clienteId = (Long) req.session().attribute("user_id");

    if (clienteId == null) {
        res.status(401);
        return false;
    }

    	String prestadorIdStr = getParamSafe(req, "prestador_id");
    	String agendaIdStr = getParamSafe(req, "agenda_id");
    	String descricao = getParamSafe(req, "descricao");

    if (isBlank(prestadorIdStr) || isBlank(agendaIdStr) || isBlank(descricao)) {
        	res.status(400);
        	return false;
    }

    try {
        long prestadorId = Long.parseLong(prestadorIdStr);
        long agendaId = Long.parseLong(agendaIdStr);

        boolean ok = solicitacaoServicoDAO.criarSolicitacaoDireta(clienteId, prestadorId, agendaId, descricao);

        res.status(ok ? 201 : 409);
        return ok;

    } catch (Exception e) {
        System.err.println("Erro ao criar solicitação direta: " + e.getMessage());
        res.status(500);
        return false;
    }
	}

	public List<model.SolicitacaoDireta> listarSolicitacoesDiretasPrestador(Request req, Response res) {
    List<model.SolicitacaoDireta> lista = new ArrayList<>();

    try {
        	Long usuarioId = (Long) req.session().attribute("user_id");

        	Prestador p = prestadorDAO.selectPrestador("usuarios_id", usuarioId, NivelDetalhePrestador.SIMPLES);

     	if (p == null) {
            	return lista;
        }

        lista = solicitacaoServicoDAO.listarDiretasDoPrestador(p.getId());

    } catch (SQLException e) {
        System.err.println("Erro ao listar solicitações diretas: " + e.getMessage());
    }

    return lista;
	}

	public String deletarSolicitacao(Request req, Response res, Long idSolicitacao) {
    String mensagem = "";

    try {
        Long usuarioId = (Long) req.session().attribute("user_id");
        SolicitacaoServico s = solicitacaoServicoDAO.getSolicitacao(idSolicitacao);

        if (s.getTipoSolicitacao() == 0) {
            if (s.getStatus() == 0) {
                boolean ok = solicitacaoServicoDAO.cancelarSolicitacaoDiretaCliente(idSolicitacao, usuarioId);

                return ok
                    ? "Solicitação direta cancelada com sucesso!"
                    : "Não foi possível cancelar essa solicitação.";
            }

            if (s.getStatus() == 2 || s.getStatus() == 4) {
                boolean ok = solicitacaoServicoDAO.deleteSolicitacao(idSolicitacao);

                return ok
                    ? "Solicitação direta removida com sucesso!"
                    : "Não foi possível remover essa solicitação.";
            }
        }

        if (solicitacaoServicoDAO.deleteSolicitacao(idSolicitacao)) {
            mensagem = "Solicitação deletada com sucesso!";
        }

    } catch (SQLException e) {
        mensagem = "Erro ao processar a exclusão. Tente novamente mais tarde";
        System.err.println("Erro na deleção: " + e.getMessage());
    }

    return mensagem;
	}
	
	public boolean cancelarSolicitacaoDireta(Request req, Response res) {
	    try {
	        Long usuarioId = (Long) req.session().attribute("user_id");

	        Prestador p = prestadorDAO.selectPrestador("usuarios_id", usuarioId, NivelDetalhePrestador.SIMPLES);

	        if (p == null) {
	            res.status(403);
	            return false;
	        }

	        long solicitacaoId = Long.parseLong(req.params("id"));

	        boolean ok = solicitacaoServicoDAO.cancelarSolicitacaoDireta(solicitacaoId, p.getId());

	        res.status(ok ? 200 : 404);
	        return ok;

	    } catch (Exception e) {
	        System.err.println("Erro ao cancelar solicitação direta: " + e.getMessage());
	        res.status(500);
	        return false;
	    }
	}
	
	//Método para a avaliação do serviço
	public boolean avaliarServico(Request req, Response res) {
	    try {
	        Long idSolicitacao = Long.parseLong(req.queryParams("solicitacaoId"));
	        int nota = Integer.parseInt(req.queryParams("nota"));
	        String comentario = getParamSafe(req, "comentario");        
	        Long userId = (Long) req.session().attribute("user_id");
	     
	        //System.out.println("Dados recebidos:");
	        //System.out.println("   - idSolicitacao: " + idSolicitacao);
	        //System.out.println("   - userId: " + userId);
	        //System.out.println("   - nota: " + nota);
	        
	        if (userId == null) {
	        	//System.out.println("   ERRO! userId nulo");
	        	return false;
	        }
	        
	        //System.out.println("Buscando o idPrestador");
	        Long idPrestador = solicitacaoServicoDAO.descobrirPrestadorDaSolicitacao(idSolicitacao);
	        //System.out.println("   - Encontrado: " + idPrestador);
	        
	        if (idPrestador == null || idPrestador == 0) {
	        	//System.out.println("   ERRO! avaliação foi cancelada " + idPrestador);
	            return false;
	        }
	        
	        //System.out.println("Enviando o INSERT para o BD");
	        boolean sucesso = solicitacaoServicoDAO.inserirAvaliacao(idSolicitacao, userId, idPrestador, nota, comentario);
	        //System.out.println("   - Resultado do INSERT: " + sucesso);
	        
	        return sucesso;

	    } catch (Exception e) {
	        //ystem.err.println("Erro ao enviar avaliação: " + e.getMessage());
	        return false;
	    }
	}
	

	public boolean excluirSolicitacaoDiretaCanceladaPrestador(Request req, Response res) {
	    try {
	        Long usuarioId = (Long) req.session().attribute("user_id");

	        Prestador p = prestadorDAO.selectPrestador("usuarios_id", usuarioId, PrestadorDAO.NivelDetalhePrestador.SIMPLES);

	        if (p == null) {
	            res.status(403);
	            return false;
	        }

	        long solicitacaoId = Long.parseLong(req.params("id"));

	        boolean ok = solicitacaoServicoDAO.excluirSolicitacaoDiretaCanceladaPrestador(solicitacaoId, p.getId());

	        res.status(ok ? 200 : 404);
	        return ok;

	    } catch (Exception e) {
	        System.err.println("Erro ao excluir solicitação direta cancelada: " + e.getMessage());
	        res.status(500);
	        return false;
	    }
	}
	
	public boolean concluirSolicitacaoDireta(Request req, Response res) {
	    try {
	        Long usuarioId = (Long) req.session().attribute("user_id");

	        Prestador p = prestadorDAO.selectPrestador(
	            "usuarios_id",
	            usuarioId,
	            PrestadorDAO.NivelDetalhePrestador.SIMPLES
	        );

	        if (p == null) {
	            res.status(403);
	            return false;
	        }

	        long solicitacaoId = Long.parseLong(req.params("id"));

	        boolean ok = solicitacaoServicoDAO.concluirSolicitacaoDireta(solicitacaoId, p.getId());

	        res.status(ok ? 200 : 404);
	        return ok;

	    } catch (Exception e) {
	        System.err.println("Erro ao concluir solicitação direta: " + e.getMessage());
	        res.status(500);
	        return false;
	    }
	}
	
	private static class ProcessedImage {
		private final String nomeOriginal;
		private final String contentType;
		private final long tamanhoBytes;
		private String status;
		private String motivoRejeicao;
		private String extensao;
		private byte[] bytes;

		private ProcessedImage(Part part) {
			this.nomeOriginal = part.getSubmittedFileName();
			this.contentType = part.getContentType();
			this.tamanhoBytes = part.getSize();
		}
	}

}
