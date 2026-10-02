<#import "/layout/main.ftl" as layout>
<style>
/* Cards do Formulário */
        .form-card {
            background-color: #fff;
            border-radius: 12px;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
            padding: 30px;
            margin-bottom: 24px;
            border: 1px solid #eaeaea;
        }
        
        .section-title {
            font-size: 1.15rem;
            font-weight: 600;
            color: #1a1a1a;
            margin-bottom: 20px;
            display: flex;
            align-items: center;
        }
        
        .section-title i {
            color: #007bff;
            margin-right: 10px;
            font-size: 1.1rem;
        }

        /* Inputs e Selects */
        .form-label {
            font-weight: 500;
            color: #495057;
            font-size: 0.95rem;
        }
        .form-control, .form-select {
            border-radius: 8px;
            padding: 12px 14px;
            border: 1px solid #ced4da;
            background-color: #fff; /* Fundo branco para indicar que é editável */
        }
        .form-control:focus, .form-select:focus {
            border-color: #007bff;
            box-shadow: 0 0 0 0.2rem rgba(0, 123, 255, 0.15);
        }

        /* Campos desativados/somente leitura (caso tenha cidade/estado automáticos pelo CEP) */
        .form-control:read-only {
            background-color: #f8f9fa;
            color: #6c757d;
        }

        /* Galeria de Fotos Anexadas */
        .attached-photos {
            display: flex;
            gap: 15px;
            flex-wrap: wrap;
            margin-top: 10px;
        }
        
        .photo-item-wrapper {
            position: relative;
            width: 120px;
            height: 120px;
            border-radius: 8px;
            overflow: hidden;
            border: 1px solid #e0e0e0;
            box-shadow: 0 2px 4px rgba(0,0,0,0.05);
            transition: transform 0.2s ease;
        }

        .photo-item-wrapper:hover {
            transform: scale(1.05);
            cursor: pointer;
        }

        .photo-item-wrapper img {
            width: 100%;
            height: 100%;
            object-fit: cover;
        }

        /* Botões */
        .btn-primary {
            background-color: #007bff;
            border-color: #007bff;
            font-weight: 600;
            padding: 10px 24px;
            border-radius: 8px;
        }
        .btn-primary:hover {
            background-color: #0056b3;
        }
        .btn-light {
            background-color: #fff;
            border: 1px solid #ced4da;
            font-weight: 500;
            padding: 10px 24px;
            border-radius: 8px;
        }
        
        .badge-status {
            font-size: 0.85rem;
            padding: 6px 12px;
            border-radius: 20px;
            background-color: #e3f2fd;
            color: #0d6efd;
            font-weight: 600;
        }
</style>

<#assign meusScripts>
<script id="search-js" defer src="https://api.mapbox.com/search-js/v1.5.0/web.js"></script>

<script>
	const script = document.getElementById('search-js');
	script.onload = function () {
	  mapboxsearch.config.accessToken = '${mapbox_access_token?js_string}';
	  
	  // Inicializa o Autofill e captura a instância
	  const autofillCollection = mapboxsearch.autofill({ 
	    options: { country: 'br' } 
	  });
	
	  // Adiciona o listener para quando um endereço é recuperado
	  autofillCollection.addEventListener('retrieve', (event) => {
	    if (event.detail && event.detail.features && event.detail.features.length > 0) {
	      const feature = event.detail.features[0];
	      // Mapbox retorna coordinates como [longitude, latitude]
	      const coords = feature.geometry.coordinates;
	      userLng = coords[0];
	      userLat = coords[1];
	      
	      $("#lat").val(userLat);
	      $("#long").val(userLng);
	      
	      console.log("Coordenadas capturadas:", userLat, userLng);
	    }
	  });
	};
	
	$("#salvar").on("click", function(e){
		e.preventDefault();
		const solicitacaoId = $(this).data("solicitacao-id");
		let formData = new FormData(document.querySelector("form"));
		const data = Object.fromEntries(formData);
		
		Swal.fire({
			icon:"info",
			title: "Aviso:",
			text: "Deseja atualizar os dados dessa solicitação?",
			showDenyButton: true,
			showConfirmButton: true,
			confirmButtonText: "Sim",
			denyButtonText: "Não"
		}).then((result) => {
			if(result.isConfirmed){
				$.ajax({
					method: "PUT",
					url:"/minhas-solicitacoes/" + solicitacaoId,
					dataType: "json",
					beforeSend: function(){
						$("#loader").addClass("show");
					},
					data: data,
					success: function(response){
						Swal.fire({
							icon:"success",
							title: "Sucesso!",
							text: response.mensagem,
						});
						
						setTimeout(() => {
							window.location.href = "/minhas-solicitacoes";
						}, 2000);
					},
					error: function(response){
						Swal.fire({
							icon:"error",
							title: "Erro!",
							text: response.mensagem,
						});
						
						setTimeout(() => {
							window.location.href = "/minhas-solicitacoes";
						}, 2000);
					},
					complete: function(data){
						$("#loader").removeClass("show");
					},
				});
			}
			
		});
	});
	
</script>
</#assign>
<@layout.padrao title="MatchService - Editar Solicitação" scripts=meusScripts>
<div id="loader" class="loader-overlay">
	<div class="spinner-grow text-primary" style="width: 3rem; height: 3rem;" role="status">
		<span class="visually-hidden">Loading...</span>
	</div>
</div>
<div class="container mt-5 mb-5">
        <input type="hidden" id="lat" value="${solicitacao.lat!}">
        <input type="hidden" id="long" value="${solicitacao.lng!}">
        <div class="row mb-4 align-items-center">
            <div class="col-lg-8 mx-auto d-flex justify-content-between align-items-center">
                <div>
                    <a href="/minhas-solicitacoes" class="text-decoration-none text-secondary mb-2 d-inline-block">
                        <i class="fas fa-arrow-left me-1"></i> Voltar para a lista
                    </a>
                    <h2 class="fw-bold mb-1 d-flex align-items-center gap-3">
                        Editar Solicitação 
                        <span class="badge-status"><i class="fas fa-search"></i> Aberta</span>
                    </h2>
                    <p class="text-muted mb-0">Atualize os dados para receber orçamentos mais precisos.</p>
                </div>
            </div>
        </div>

        <div class="row">
            <div class="col-lg-8 mx-auto">
                <form action="#">
                    <div class="form-card">
                        <h4 class="section-title"><i class="fas fa-tools"></i> Detalhes do Serviço</h4>
                        
                        <div class="mb-4">
                            <label for="categoria" class="form-label">Categoria da Solicitação</label>
                            <select class="form-select" id="categoria" required name="categoria">
                            <#list categorias as categoria>
                            <option value="${categoria.id}" 
                            <#if categoria.id == solicitacao.categoria.id> selected</#if>
                     		>${categoria.nome}</option>
                            </#list>
                                
                            </select>
                        </div>

                        <div class="mb-3">
                            <label for="descricao" class="form-label">Descrição do Problema</label>
                            <textarea class="form-control" id="descricao" rows="5" required name="descricao">${solicitacao.descricao}</textarea>
                        </div>
                    </div>

                    
                    <div class="form-card">
                        <h4 class="section-title"><i class="fas fa-map-marker-alt"></i> Local do Serviço</h4>
                        <div class="row g-3">
                         <div class="col-md-12">
                                <label for="rua" class="form-label">Rua / Avenida</label>
                                <input type="text" class="form-control" id="rua" name="rua" autocomplete="address-line1" value="${solicitacao.rua!}" required>
                         </div>
                            <div class="col-md-4">
                                <label for="cep" class="form-label">CEP</label>
                                <input type="text" class="form-control" id="cep" name="cep" autocomplete="postal-code" placeholder="00000-000" value="${solicitacao.cep!}" readonly>
                            </div>
                            <div class="col-md-8">
                                <label for="bairro" class="form-label">Bairro</label>
                                <input type="text" class="form-control" autocomplete="address-level3" name="bairro" id="bairro" value="${solicitacao.bairro!}" readonly>
                            </div>
                             <div class="col-md-12">
                                <label for="cidade" class="form-label">Cidade <span class="text-muted fw-normal"></span></label>
                                <input type="text" class="form-control" name="cidade" autocomplete="address-level2" id="cidade" value="${solicitacao.cidade!}" readonly>
                            </div>
                        </div>
                    </div>

                    
                    <div class="form-card">
                        <h4 class="section-title mb-1"><i class="fas fa-images"></i> Fotos Anexadas</h4>
                        <p class="text-muted small mb-3">Estas são as fotos que você enviou aos prestadores para detalhar o problema.</p>
                        
                        <div class="attached-photos">
                    	<#list solicitacao.solicitacaoImagens as img>
                    		<div class="photo-item-wrapper" title="Ver foto em tela cheia">
                                <img src="/${img.url}" alt="Foto da fechadura">
                            </div>
                    	</#list>
                            
                            <!-- Dica de UX: Se o usuário quiser adicionar mais fotos na edição, 
                                 poderíamos ter um botão sutil aqui no futuro. Por enquanto atende o requisito de "exibir". -->
                        </div>
                    </div>

                    <!-- Ações -->
                    <div class="d-flex justify-content-end gap-3 mb-5">
                        <a href="/minhas-solicitacoes" class="btn btn-light shadow-sm">
                            Cancelar
                        </a>
                        <button type="submit" class="btn btn-primary shadow-sm" id="salvar" data-solicitacao-id="${solicitacao.id}">
                            <i class="fas fa-save me-2"></i> Salvar Alterações
                        </button>
                    </div>

                </form>
            </div>
        </div>
    </div>
</@layout.padrao>
