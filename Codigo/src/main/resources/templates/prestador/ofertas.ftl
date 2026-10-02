<#import "/layout/main.ftl" as layout>

<@layout.padrao title="MatchService - Ver ofertas">
	<div class="container mt-5 mb-5">
		<div class="d-flex justify-content-between align-items-center mb-4">
			<div>
				<h2 class="fw-bold mb-1">Minhas ofertas</h2>
				<p class="text-muted mb-0">Acompanhe o status das ofertas que você enviou aos clientes.</p>
			</div>
			<a class="btn btn-outline-secondary" href="/solicitacoes-disponiveis">Ver ofertas disponíveis</a>
		</div>

		<#if sucesso??>
			<div class="alert alert-success">${sucesso}</div>
		</#if>

		<#if erro??>
			<div class="alert alert-danger">${erro}</div>
		</#if>

		<#if ofertas?size == 0>
			<div class="alert alert-info">Você ainda não enviou nenhuma oferta.</div>
		<#else>
			<div class="row g-3">
				<#list ofertas as oferta>
					<div class="col-lg-6">
						<div class="card h-100 shadow-sm border-0">
							<div class="card-body">
								<div class="d-flex justify-content-between align-items-start gap-3 mb-3">
									<div>
										<h5 class="fw-bold mb-1">Solicitação #${oferta.solicitacoesServicosId}</h5>
										<p class="text-muted small mb-0">${oferta.createdAt!""}</p>
										<p class="small text-muted mb-0">
											<i class="fas fa-user me-1"></i>Cliente: ${oferta.clienteNome!"Não informado"}
										</p>
									</div>

									<#if oferta.status == 0>
										<span class="badge text-bg-warning">Pendente</span>
									<#elseif oferta.status == 1>
										<span class="badge text-bg-success">Aceita</span>
									<#elseif oferta.status == 2>
										<span class="badge text-bg-secondary">Recusada</span>
									<#elseif oferta.status == 3>
										<span class="badge text-bg-dark">Finalizada</span>
									<#else>
										<span class="badge text-bg-light text-dark">Status ${oferta.status}</span>
									</#if>
								</div>

								<p class="mb-3">${oferta.solicitacaoDescricao!"Solicitação sem descrição."}</p>

								<div class="bg-light rounded p-3 mb-3">
									<h6 class="fw-bold mb-2">Sua oferta</h6>
									<p class="fs-4 fw-bold mb-2">R$ ${oferta.valor?string["0.00"]}</p>
									<p class="mb-2">${oferta.descricao}</p>
									<p class="text-muted mb-0"><i class="far fa-clock me-1"></i>${oferta.disponibilidade!"Disponibilidade não informada."}</p>
								</div>

								<#if oferta.status == 2>
									<div class="alert alert-danger mt-3 mb-0">
										<strong>Motivo da recusa:</strong>
										${oferta.justificativaRecusa!"Motivo da recusa não informado."}
									</div>
								</#if>
							</div>

							<#if oferta.status == 1>
								<div class="card-footer bg-white border-0 d-flex flex-wrap gap-2 justify-content-end">
									<button
										type="button"
										class="btn btn-outline-primary"
										data-bs-toggle="modal"
										data-bs-target="#contatoCliente${oferta.id}">
										<i class="fas fa-address-book me-1"></i>Entrar em contato com cliente
									</button>

									<form method="POST" action="/prestador/ofertas/${oferta.id}/finalizar">
										<button type="submit" class="btn btn-success">
											<i class="fas fa-check me-1"></i>Finalizar serviço
										</button>
									</form>
								</div>
							</#if>
						</div>
					</div>

					<#if oferta.status == 1>
						<div class="modal fade" id="contatoCliente${oferta.id}" tabindex="-1" aria-labelledby="contatoClienteLabel${oferta.id}" aria-hidden="true">
							<div class="modal-dialog modal-dialog-centered">
								<div class="modal-content border-0 shadow">
									<div class="modal-header">
										<h5 class="modal-title fw-bold" id="contatoClienteLabel${oferta.id}">
											<i class="fas fa-user text-primary me-2"></i>Contato do cliente
										</h5>
										<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Fechar"></button>
									</div>
									<div class="modal-body">
										<p class="mb-2"><strong>Nome:</strong> ${oferta.clienteNome!"Não informado"}</p>
										<p class="mb-2"><strong>Telefone:</strong> ${oferta.clienteTelefone!"Telefone não informado."}</p>
										<p class="mb-0"><strong>Email:</strong> ${oferta.clienteEmail!"Email não informado."}</p>
									</div>
									<div class="modal-footer">
										<button type="button" class="btn btn-light border" data-bs-dismiss="modal">Fechar</button>
										<#if oferta.clienteWhatsappUrl?? && oferta.clienteWhatsappUrl != "">
											<a class="btn btn-success" href="${oferta.clienteWhatsappUrl}" target="_blank" rel="noopener noreferrer">
												<i class="fab fa-whatsapp me-1"></i>Abrir WhatsApp
											</a>
										<#else>
											<button type="button" class="btn btn-success" disabled>
												<i class="fab fa-whatsapp me-1"></i>Telefone não informado
											</button>
										</#if>
									</div>
								</div>
							</div>
						</div>
					</#if>
				</#list>
			</div>
		</#if>
	</div>
</@layout.padrao>
