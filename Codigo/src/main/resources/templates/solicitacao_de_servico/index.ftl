<#import "/layout/main.ftl" as layout>
<style>
		.form-card {
            background-color: #fff;
            border-radius: 12px;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
            padding: 30px;
            margin-bottom: 24px;
            border: 1px solid #eaeaea;
        }
        
        .section-title {
            font-size: 1.2rem;
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
            background-color: #fcfcfc;
        }
        .form-control:focus, .form-select:focus {
            border-color: #007bff;
            box-shadow: 0 0 0 0.2rem rgba(0, 123, 255, 0.15);
            background-color: #fff;
        }

        /* Drag and Drop Area para Fotos */
        .upload-drop-zone {
            border: 2px dashed #007bff;
            border-radius: 12px;
            padding: 40px 20px;
            text-align: center;
            background-color: #f8fbff;
            cursor: pointer;
            transition: all 0.3s ease;
        }
        .upload-drop-zone:hover {
            background-color: #e6f2ff;
        }
        .upload-drop-zone i {
            font-size: 3rem;
            color: #007bff;
            margin-bottom: 15px;
        }
        .upload-drop-zone p {
            margin: 0;
            font-size: 1.1rem;
            color: #495057;
        }
        #file-input {
            display: none;
        }

        /* Preview das imagens */
        .image-preview-container {
            display: flex;
            gap: 10px;
            flex-wrap: wrap;
            margin-top: 15px;
        }
        .img-preview {
            width: 80px;
            height: 80px;
            object-fit: cover;
            border-radius: 8px;
            border: 1px solid #ddd;
        }

        /* Botão Enviar */
        .btn-primary {
            background-color: #007bff;
            border-color: #007bff;
            padding: 14px 24px;
            font-size: 1.1rem;
            font-weight: 600;
            border-radius: 8px;
            width: 100%;
            transition: all 0.3s;
        }
        .btn-primary:hover {
            background-color: #0056b3;
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(0, 123, 255, 0.3);
        }

        /* Switch "Usar meu endereço" */
        .form-check-input:checked {
            background-color: #007bff;
            border-color: #007bff;
        }
        .loader-overlay {
		    position: fixed;
		    top: 0;
		    left: 0;
		    width: 100%;
		    height: 100%;
		    background-color: rgba(255, 255, 255, 0.7); 
		    display: flex;
		    justify-content: center;
		    align-items: center;
		    z-index: 9999;
		    visibility: hidden; 
		    opacity: 0;
		    transition: opacity 0.3s ease-in-out;
		}
		
		.loader-overlay.show {
		    visibility: visible;
		    opacity: 1;
		}
</style>
<#assign meusScripts>
<script src="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/js/select2.min.js"></script>
<script id="search-js" defer src="https://api.mapbox.com/search-js/v1.5.0/web.js"></script>

<script>
		$('.select2').select2();

		const elem = document.getElementById("file-input");
		
		elem.addEventListener("change", handleFiles);
		
		
		function handleFiles() {
			  const fileList = this.files;
			  const container =  document.getElementById('preview-container');
			  container.innerHTML = '';
			  
			  for(let i = 0; i < fileList.length; i++){
				let file = fileList[i];
				let reader = new FileReader();
				
				reader.onload = function(e) {
					const img = document.createElement('img');
                    img.src = e.target.result;
                    img.classList.add('img-preview');
                    container.appendChild(img);
                }
                reader.readAsDataURL(file);
			  }
			  
		}
		
		
		
		$("#usarEnderecoPerfil").on("change", function(){
			let isCheck = $(this).prop('checked');
			
			if(isCheck){
				
				$.ajax({
					type: 'GET',
					beforeSend: function(){
						$("#loader").addClass("show");
					},
					url: '/obter-endereco',
					dataType: "json",
					success: function(data){
						$("#rua").attr("value", data.rua);
						$("#bairro").attr("value", data.bairro);
						$("#cidade").attr("value", data.cidade);
						$("#cep").attr("value", data.cep);
						$("#lat").attr("value", data.lat);
						$("#long").attr("value", data.lng);
						
					}, 
					complete: function(data){
						$("#loader").removeClass("show");
					},
					timeout: 30000,
				});
			}
			else{
				$("#rua").attr("value", null);
				$("#bairro").attr("value", null);
				$("#cidade").attr("value", null);
				$("#cep").attr("value", null);
				$("#lat").attr("value", null);
				$("#long").attr("value", null);
			}
		});
        
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
	    
	    $("form").on("submit", function(e){
			e.preventDefault();
			
			Swal.fire({
				title: "Atenção!",
				text: "Deseja criar uma solicitação de serviço?",
				icon: "question",
				showDenyButton: true,
				confirmButtonText: "Sim",
				denyButtonText: `Não`
			}).then((result) => {
				if(result.isConfirmed){
					let formData = new FormData(this);
					$.ajax({
						type: 'POST',
						beforeSend: function(){
							$("#loader").addClass("show");
						},
						url: '/criar-solicitacao',
						dataType: "json",
						data: formData,
						processData: false,
						contentType: false,
						success: function(data){
							const avisos = data.avisos && data.avisos.length ? "\n\n" + data.avisos.join("\n") : "";
							Swal.fire({
								title: "Sucesso!",
								text: data.mensagem + avisos,
								icon: "success",
								showDenyButton: false,
							}).then((result) => {
								if(result.isConfirmed){
									window.location.href = "/minhas-solicitacoes";
								}
							});
							
						},
						error: function(err){
							const response = err.responseJSON || {};
							Swal.fire({
								title: "Erro!",
								text: response.mensagem || "Erro ao processar solicitação! Tente novamente mais tarde.",
								icon: "error"
							});
						},
						complete: function(data){
							$("#loader").removeClass("show");
						},
						timeout: 30000,
					});
				}
			});
			
			
		});
</script>
</#assign>
<@layout.padrao title="MatchService - Nova Solicitação" scripts=meusScripts>
<div class="container mt-5 mb-5">
        
        <div id="loader" class="loader-overlay">
		    <div class="spinner-grow text-primary" style="width: 3rem; height: 3rem;" role="status">
		        <span class="visually-hidden">Loading...</span>
		    </div>
		</div>
		
        <div class="row mb-4">
            <div class="col-lg-8 mx-auto text-center">
                <h2 class="fw-bold">O que você precisa hoje?</h2>
                <p class="text-muted">Descreva o serviço para encontrarmos os melhores profissionais para sua agenda.</p>
            </div>
        </div>

        <div class="row">
          
            <div class="col-lg-8 mx-auto">
                <form action="#" method="POST" enctype="multipart/form-data" id="solicitacao-form">
                    <input type="hidden" id="lat" name="lat" value="">
                    <input type="hidden" id="long" name="long" value="">
                   
                    <div class="form-card">
                        <h4 class="section-title"><i class="fas fa-tools"></i> Detalhes do Serviço</h4>
                        
                        <div class="mb-4">
                            <label for="categoria" class="form-label">Categoria do Serviço</label>
                            <select class="form-select" id="categoria" name="categoria">
                            	<option value="" selected disabled>Selecione o tipo de profissional...</option>
                            	<#list categorias as categoria>
                            		<option value="${categoria.id}">${categoria.nome}</option>
                            	</#list>
                                
                            </select>
                        </div>

                        <div class="mb-3">
                            <label for="descricao" class="form-label">Descrição do Problema</label>
                            <textarea class="form-control" id="descricao" name="descricao" rows="4" placeholder="Ex: Minha chave quebrou dentro do portão de casa e preciso de alguém para retirar e fazer uma cópia nova. Tenho disponibilidade no fim de semana." required></textarea>
                            <div class="form-text">Quanto mais detalhes você fornecer, orçamentos mais precisos você receberá.</div>
                        </div>
                    </div>

                    
                    <div class="form-card">
                        <div class="d-flex justify-content-between align-items-center mb-4">
                            <h4 class="section-title mb-0"><i class="fas fa-map-marker-alt"></i> Onde será o serviço?</h4>
                            
                        
                            <div class="form-check form-switch">
                                <input class="form-check-input" type="checkbox" role="switch" id="usarEnderecoPerfil">
                                <label class="form-check-label text-primary fw-bold" style="cursor:pointer;" for="usarEnderecoPerfil">Usar meu endereço salvo</label>
                            </div>
                        </div>

                        <div class="row g-3">
                        	<div class="col-md-12">
                                <label for="rua" class="form-label">Rua / Avenida</label>
                                <input type="text" class="form-control" id="rua" name="rua" autocomplete="address-line1" placeholder="Ex: Rua das Flores 30" required>
                            </div>
                            
                            <div class="col-md-4">
                                <label for="cep" class="form-label">CEP</label>
                                <input type="text" class="form-control" id="cep" name="cep" autocomplete="postal-code" placeholder="00000-000" required>
                            </div>
                            <div class="col-md-8">
                                <label for="cidade" class="form-label">Cidade</label>
                                <input type="text" class="form-control" name="cidade" autocomplete="address-level2" id="cidade" placeholder="Ex: São Paulo" required>
                            </div>
                       

                     
                            <div class="col-md-12">
                                <label for="bairro" class="form-label">Bairro</label>
                                <input type="text" autocomplete="address-level3" name="bairro" class="form-control" id="bairro" placeholder="Ex: Centro" required>
                            </div>
                        </div>
                    </div>

                   
                    <div class="form-card">
                        <h4 class="section-title"><i class="fas fa-camera"></i> Fotos do Local/Problema <span class="text-muted fw-normal ms-2">(Opcional)</span></h4>
                        <p class="text-muted small mb-3">Adicionar fotos ajuda os prestadores a entenderem a complexidade e darem um orçamento mais justo (o Bernardo adora economizar tempo com orçamentos precisos!).</p>
                        
                        
                        <div class="upload-drop-zone" id="drop-zone" onclick="document.getElementById('file-input').click();">
                            <i class="fas fa-cloud-upload-alt"></i>
                            <p>Arraste suas fotos aqui ou <b>clique para procurar</b></p>
                            <small class="text-muted">Suporta JPG, PNG e WEBP. Máximo de 5MB por foto.</small>
                            <input type="file" id="file-input" multiple accept="image/png, image/jpeg, image/webp" name="img">
                        </div>
                        
                       
                        <div class="image-preview-container" id="preview-container"></div>
                    </div>


                    <div class="text-end mb-5">
                        <button id="btn-publicar" type="submit" class="btn btn-primary btn-lg shadow-sm">
                            <i class="fas fa-paper-plane me-2"></i> Publicar Solicitação
                        </button>
                    </div>

                </form>
            </div>
        </div>
    </div>
</@layout.padrao>
