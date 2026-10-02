
<#import "/layout/main.ftl" as layout>

<#assign meusScripts>
<script src="/js/solicitacoes_disponiveis/solicitacaoservico.js"></script>
</#assign>

<@layout.padrao title="MatchService - Serviços Disponíveis" scripts=meusScripts>
	<#if tipo_usuario?? && tipo_usuario == 1>
		<div class="container">
			<div class="d-flex justify-content-between align-items-center mb-4">
				<div>
					<h2 class="fw-bold mb-1">Serviços disponíveis</h2>
					<p class="text-muted mb-0">Escolha uma solicitação de serviço aberta e envie sua oferta. O solicitante poderá recusar ou aceitá-la.</p>
				</div>
			</div>

			<#if solicitacoes?? && solicitacoes?size gt 0>
				<div class="row g-3">
				<#list solicitacoes as solicitacao>
				    <#-- Lógica de cruzamento das listas -->
				    <#assign jaOfertou = idsOfertados?seq_contains(solicitacao.id)>
				    <#assign foiRecusado = idsRecusados?seq_contains(solicitacao.id)>
				
				    <div class="col-lg-6">
	
				        <div class="card h-100 border-0 
				            <#if jaOfertou>border-start border-success border-4 bg-light-subtle shadow-sm
				            <#elseif foiRecusado>border-start border-danger border-4 bg-light-subtle opacity-75 shadow-sm
				            <#else>shadow-sm</#if>">
				            
				            <div class="card-body">
				                <div class="d-flex justify-content-between align-items-start mb-3">
				                    <h5 class="fw-bold mb-0">Solicitação #${solicitacao.id}</h5>
				                    
				                    <#-- Badge Dinâmico -->
				                    <#if jaOfertou>
				                        <span class="badge text-bg-success"><i class="fas fa-check me-1"></i> Oferta Enviada</span>
				                    <#elseif foiRecusado>
				                        <span class="badge text-bg-danger"><i class="fas fa-times me-1"></i> Oferta Recusada</span>
				                    <#else>
				                        <span class="badge text-bg-primary">Aberta</span>
				                    </#if>
				                </div>
				                <p class="mb-3">${solicitacao.descricao}</p>
				                <p class="text-muted small mb-0">
				                    <i class="fas fa-map-marker-alt me-1"></i>${solicitacao.rua!""}<#if solicitacao.bairro?? && solicitacao.bairro != ""> - ${solicitacao.bairro}</#if>
				                </p>
				            </div>
				            
				            <div class="card-footer bg-white border-0 text-end">
				               
				                <#if jaOfertou>
					                <button type="button" class="btn btn-light border btn-ver-detalhes me-2" data-id="${solicitacao.id}">
					                    Ver detalhes
					                </button>
				                    <button class="btn btn-secondary" disabled>
				                        <i class="fas fa-check-double me-1"></i> Aguardando cliente
				                    </button>
				                <#elseif foiRecusado>
				                    <button class="btn btn-outline-danger" disabled>
				                        <i class="fas fa-ban me-1"></i> Oferta encerrada
				                    </button>
				                <#else>
					                <button type="button" class="btn btn-light border btn-ver-detalhes me-2" data-id="${solicitacao.id}">
					                    Ver detalhes
					                </button>
				                    <a class="btn btn-primary" href="/solicitacoes/${solicitacao.id}/ofertar">
				                        <i class="fas fa-paper-plane me-1"></i> Enviar oferta
				                    </a>
				                </#if>
				            </div>
				        </div>
				    </div>
				</#list>
				</div>
			<#else>
				<div class="p-3 mb-2 bg-primary text-white rounded-pill">Nenhuma solicitação disponível no momento.</div>
			</#if>
		</div> 
		
		<div class="modal fade" id="modalDetalhesSolicitacao" tabindex="-1" aria-labelledby="modalDetalhesLabel" aria-hidden="true">
            <div class="modal-dialog modal-lg modal-dialog-centered">
                <div class="modal-content border-0 shadow">
                    
                    <div class="modal-header bg-light border-bottom-0 pb-3">
                        <h5 class="modal-title fw-bold text-dark" id="modalDetalhesLabel">
                            <i class="fas fa-clipboard-list text-primary me-2"></i> Detalhes da Solicitação 
                            <span id="modalId" class="text-secondary fs-6 ms-2 fw-normal"></span>
                        </h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    
                    <div class="modal-body p-4">
                        
						<div id="modalLoading" class="text-center py-5">
                            <div class="spinner-border text-primary" role="status">
                                <span class="visually-hidden">Carregando...</span>
                            </div>
                            <p class="text-muted mt-2">Buscando informações...</p>
                        </div>

                        
                        <div id="modalContent" class="d-none">
                            
                            <div class="row mb-4">
                                <div class="col-12">
                                    <h6 class="fw-bold text-secondary mb-1 text-uppercase" style="font-size: 0.8rem;">Categoria</h6>
                                    <p id="modalCategoria" class="fs-5 fw-bold mb-0 text-dark">Eletricista </p>
                                </div>
                            </div>

                            <div class="mb-4">
                                <h6 class="fw-bold text-secondary mb-2 text-uppercase" style="font-size: 0.8rem;">Descrição do Problema</h6>
                                <div class="bg-light p-3 rounded border border-light">
                                    <p id="modalDescricao" class="mb-0 text-dark" style="white-space: pre-line;">
                                    Preciso instalar um chuveiro 220v no banheiro da suíte. 

A fiação já está passada no teto, falta apenas fazer as conexões elétricas de forma segura e instalar o disjuntor correto no quadro de energia. Tenho disponibilidade para receber o profissional amanhã a partir das 14h.
                                    </p>
                                </div>
                            </div>

                            <div class="mb-4">
                                <h6 class="fw-bold text-secondary mb-2 text-uppercase" style="font-size: 0.8rem;">Local do Serviço</h6>
                                <p class="mb-0 text-dark">
                                    <i class="fas fa-map-marker-alt text-primary me-2"></i>
                                    <span id="modalEnderecoText">Rua das Flores, Centro - São Paulo/SP</span>
                                </p>
                            </div>

                            <div class="mb-3">
                                <h6 class="fw-bold text-secondary mb-3 text-uppercase" style="font-size: 0.8rem;">Fotos Anexadas</h6>
                                <div id="modalFotos" class="d-flex gap-2 flex-wrap">
                                   
                                </div>
                            </div>
                        </div>
                    </div>
                    
                    <div class="modal-footer border-top-0 pt-0 px-4 pb-4">
                        <button type="button" class="btn btn-light border" data-bs-dismiss="modal">Fechar</button>
                    </div>
                    
                </div>
            </div>
        </div>
        
        <div class="modal fade" id="imageViewerModal" tabindex="-1" aria-hidden="true" style="background-color: rgba(0,0,0,0.85);">
        <div class="modal-dialog modal-dialog-centered modal-xl">
            <div class="modal-content bg-transparent border-0">
                <div class="modal-header border-0 pb-0 justify-content-end">
                    <button type="button" class="btn-close btn-close-white p-2" data-bs-dismiss="modal" aria-label="Close" style="opacity: 1;"></button>
                </div>
                <div class="modal-body text-center p-0 mt-2">
                    <img id="fullSizeImage" src="" class="img-fluid rounded shadow" alt="Imagem ampliada" style="max-height: 85vh; object-fit: contain;">
                </div>
            </div>
        </div>
    </div>
	</#if>
</@layout.padrao>	
