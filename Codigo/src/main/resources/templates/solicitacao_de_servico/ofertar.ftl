<#import "/layout/main.ftl" as layout>

<#assign meusScripts>
<script
	src="https://cdnjs.cloudflare.com/ajax/libs/jquery.mask/1.14.16/jquery.mask.js"
	integrity="sha512-0XDfGxFliYJPFrideYOoxdgNIvrwGTLnmK20xZbCAvPfLGQMzHUsaqZK8ZoH+luXGRxTrS46+Aq400nCnAT0/w=="
	crossorigin="anonymous" referrerpolicy="no-referrer"></script>
	
<script>
$('#valor').mask('#.##0,00', {reverse: true});


</script>

</#assign>

<@layout.padrao title="MatchService - Enviar Oferta" scripts=meusScripts>
<div class="container mt-5 mb-5">
	<div class="row">
		<div class="col-lg-8 mx-auto">
			<div class="mb-4">
				<h2 class="fw-bold mb-1">Enviar Oferta</h2>
				<p class="text-muted mb-0">Solicitação #${solicitacao.id}: ${solicitacao.descricao}</p>
			</div>

			<#if erro??>
				<div class="alert alert-danger">${erro}</div>
			</#if>
			<#if sucesso?? && sucesso>
				<div class="alert alert-success">Oferta enviada com sucesso.</div>
			</#if>

			<div class="card shadow-sm border-0">
				<div class="card-body p-4">
					<form method="POST" action="/solicitacoes/${solicitacao.id}/ofertar">
						<div class="mb-3">
							<label class="form-label fw-semibold" for="valor">Valor</label>
							<input class="form-control" id="valor" name="valor" step="0.01" min="0" required>
						</div>

						<div class="mb-3">
							<label class="form-label fw-semibold" for="disponibilidade">Disponibilidade</label>
							<input class="form-control" id="disponibilidade" name="disponibilidade" type="text" placeholder="Ex: amanhã à tarde ou sábado pela manhã" required>
						</div>

						<div class="mb-4">
							<label class="form-label fw-semibold" for="descricao">Descrição da oferta</label>
							<textarea class="form-control" id="descricao" name="descricao" rows="5" required></textarea>
						</div>

						<div class="d-flex gap-2 justify-content-end">
							<a class="btn btn-outline-secondary" href="/solicitacoes-disponiveis">Cancelar</a>
							
							<button class="btn btn-primary" type="submit">
								<i class="fas fa-paper-plane me-1"></i> Enviar oferta
							</button>
						</div>
					</form>
				</div>
			</div>
		</div>
	</div>
</div>
</@layout.padrao>
