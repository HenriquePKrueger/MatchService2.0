<#import "/layout/main.ftl" as layout>

<#assign meusScripts>
<script src="/js/ofertas/ofertas.js"></script>
</#assign>
<@layout.padrao title="MatchService - Ofertas Recebidas" scripts=meusScripts>
<style>

/* Estilo customizado para as opções de recusa (Cards Clicáveis) */
        .reason-card {
            border: 1px solid #dee2e6;
            border-radius: 8px;
            padding: 12px 16px;
            margin-bottom: 10px;
            cursor: pointer;
            transition: all 0.2s ease;
            background-color: #fff;
            display: flex;
            align-items: center;
        }

        .reason-card:hover {
            background-color: #f8f9fa;
            border-color: #c6c7c8;
        }

        /* Quando o rádio dentro do card estiver marcado, muda o visual do card */
        .reason-card:has(input:checked) {
            border-color: #dc3545; /* Vermelho padrão de recusa */
            background-color: #fff5f5; /* Fundo levemente avermelhado */
            box-shadow: 0 2px 4px rgba(220, 53, 69, 0.1);
        }

        .reason-card input[type="radio"] {
            margin-right: 12px;
            width: 1.2em;
            height: 1.2em;
            cursor: pointer;
        }
        
        .reason-card input[type="radio"]:checked {
            background-color: #dc3545;
            border-color: #dc3545;
        }

        .reason-card .reason-text {
            font-weight: 500;
            color: #495057;
            margin: 0;
            cursor: pointer;
        }

        .reason-card:has(input:checked) .reason-text {
            color: #dc3545;
            font-weight: 600;
        }

        /* Animação suave para a caixa de texto "Outros" */
        #caixaOutroMotivo {
            transition: all 0.3s ease;
            overflow: hidden;
        }
</style>
<div class="container mt-5 mb-5">
	<div class="d-flex justify-content-between align-items-center mb-4">
		<div>
			<h2 class="fw-bold mb-1">Ofertas Recebidas</h2>
			<p class="text-muted mb-0">Solicitação #${solicitacao.id}: ${solicitacao.descricao}</p>
		</div>
		<a class="btn btn-outline-secondary" href="/minhas-solicitacoes">Voltar</a>
	</div>

	<#if ofertas?size == 0>
		<div class="alert alert-info">Esta solicitação ainda não recebeu ofertas.</div>
	<#else>
		<div class="row g-3">
			<#list ofertas as oferta>
				<div class="col-lg-6">
					<div class="card h-100 shadow-sm border-0">
						<div class="card-body">
							<div class="d-flex justify-content-between align-items-start mb-3">
								<div>
									<a href="/prestadores/${oferta.prestadoresId}" class="fw-bold mb-1">
                                            <h5 class="">${oferta.prestadorNome!"Prestador"}</h5>
                                        </a>
									<p class="text-muted small mb-0">${oferta.createdAt!""}</p>
								</div>
								<#if oferta.status == 0>
									<span class="badge text-bg-warning">Pendente</span>
								<#elseif oferta.status == 1>
									<span class="badge text-bg-success">Aceita</span>
								<#elseif oferta.status == 3>
									<span class="badge text-bg-dark">Concluída</span>
								<#else>
									<span class="badge text-bg-secondary">Recusada</span>
								</#if>
							</div>

							<p class="fs-4 fw-bold mb-2">R$ ${oferta.valor?string["0.00"]}</p>
							<p class="mb-2">${oferta.descricao}</p>
							<p class="text-muted mb-3"><i class="far fa-clock me-1"></i>${oferta.disponibilidade}</p>

							<#if oferta.status == 2 && oferta.justificativaRecusa??>
								<p class="small text-muted mb-0">Motivo: ${oferta.justificativaRecusa}</p>
							</#if>
						</div>

						<div class="card-footer bg-white border-0 d-flex gap-2 justify-content-end">
						<#if oferta.status == 0>
								<!-- <form method="POST" action="/ofertas/${oferta.id}/recusar"> -->
									<input type="hidden" name="justificativa_recusa" value="Oferta recusada pelo cliente">
									<button class="btn btn-outline-danger btn-recusar" data-nome-prestador="${oferta.prestadorNome!"Prestador"}" data-oferta-id="${oferta.id}" data-oferta-valor="${oferta.valor?string["0.00"]}" id="" type="submit">Recusar</button>
								<!-- </form> -->
								<!-- <form method="POST" action="/ofertas/${oferta.id}/aceitar"> -->
									<button class="btn btn-primary btn-aceitar" id="" data-oferta-id="${oferta.id}" type="submit">Aceitar</button>
								<!--</form> -->
						<#elseif oferta.status == 1>
							<button class="btn btn-primary btn-contatos" id="" data-valor="${oferta.valor}" data-prestador-id="${oferta.prestadoresId}" type="submit"><i class="fa-solid fa-address-book"></i> Ver contatos do prestador</button>
						</#if>
						</div>
					</div>
				</div>
			</#list>
		</div>
	</#if>
</div>

<div class="modal fade" id="modalRecusarOferta" tabindex="-1" aria-labelledby="modalRecusarLabel" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content border-0 shadow-lg">
                
                <div class="modal-header border-bottom-0 pb-2 pt-4 px-4">
                    <h5 class="modal-title fw-bold text-dark d-flex align-items-center" id="modalRecusarLabel">
                        <i class="fas fa-times-circle text-danger me-2 fs-4"></i> Recusar Oferta
                    </h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                
                <div class="modal-body px-4 pb-4">
                    <p class="text-muted mb-4">
                        Por favor, conte-nos por que você está recusando a oferta de R$ <b id="valor">150,00</b> do prestador <b id="nome-prestador">Valdeci</b>. Seu feedback ajuda nossos profissionais a melhorarem!
                    </p>

                    <form id="formRecusa">
                        <!-- Opção 1 -->
                        <label class="reason-card">
                            <input class="form-check-input mt-0" type="radio" name="motivoRecusa" value="preco" required>
                            <span class="reason-text">Preço fora do meu orçamento</span>
                        </label>

                        <!-- Opção 2 -->
                        <label class="reason-card">
                            <input class="form-check-input mt-0" type="radio" name="motivoRecusa" value="agenda">
                            <span class="reason-text">A disponibilidade não condiz com o meu horário</span>
                        </label>

                        <!-- Opção 3 -->
                        <label class="reason-card">
                            <input class="form-check-input mt-0" type="radio" name="motivoRecusa" value="concorrente">
                            <span class="reason-text">Já fechei com outro profissional</span>
                        </label>

                        <!-- Opção 4 -->
                        <label class="reason-card mb-2">
                            <input class="form-check-input mt-0" type="radio" name="motivoRecusa" value="outro" id="radioOutro">
                            <span class="reason-text">Outro motivo</span>
                        </label>

                        <!-- Caixa de texto (Aparece dinamicamente se selecionar "Outro motivo") -->
                        <div id="caixaOutroMotivo" style="display: none; opacity: 0; height: 0;">
                            <label for="textoJustificativa" class="form-label text-muted small fw-medium mt-2 mb-1">Poderia detalhar melhor? (Opcional)</label>
                            <textarea class="form-control" id="textoJustificativa" rows="3" placeholder="Digite aqui o motivo da recusa..."></textarea>
                        </div>
                    </form>

                </div>
                
                <div class="modal-footer bg-light border-top-0 px-4 py-3 rounded-bottom">
                    <button type="button" class="btn btn-light border fw-medium" data-bs-dismiss="modal">Manter Oferta</button>
                    <!-- Botão de ação destrutiva em vermelho -->
                    <button type="button" id="confirmar-recusa" class="btn btn-danger fw-bold px-4 shadow-sm"  data-oferta-id="" onclick="confirmarRecusa(this)">
                        Confirmar Recusa
                    </button>
                </div>
                
            </div>
        </div>
    </div>
</@layout.padrao>
