<#import "/layout/main.ftl" as layout>

<style>
.card {
	border: none;
	border-radius: 12px;
	box-shadow: 0 4px 6px rgba(0, 0, 0, 0.04);
	margin-bottom: 24px;
}

.card-header {
	background-color: #fff;
	border-bottom: 1px solid #f0f0f0;
	padding: 20px 24px;
	border-radius: 12px 12px 0 0 !important;
}

.card-title {
	font-size: 1.25rem;
	font-weight: 600;
	margin: 0;
	color: #1a1a1a;
}

.card-body {
	padding: 24px;
}

.profile-photo-container {
	position: relative;
	width: 150px;
	height: 150px;
	margin: 0 auto;
	border-radius: 50%;
	border: 4px solid #fff;
	box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
	overflow: hidden;
	background-color: #e9ecef;
	display: flex;
	align-items: center;
	justify-content: center;
}

.profile-photo-container img {
	width: 100%;
	height: 100%;
	object-fit: cover;
}

/* Overlay para alterar foto */
.photo-overlay {
	position: absolute;
	bottom: 0;
	left: 0;
	right: 0;
	background: rgba(0, 0, 0, 0.6);
	color: white;
	padding: 8px 0;
	text-align: center;
	font-size: 0.85rem;
	font-weight: 500;
	cursor: pointer;
	transition: all 0.3s ease;
	transform: translateY(100%);
}

.profile-photo-container:hover .photo-overlay {
	transform: translateY(0);
}


#upload-photo {
	display: none;
}

/* Estilo dos Inputs */
.form-label {
	font-weight: 500;
	color: #495057;
	font-size: 0.9rem;
	margin-bottom: 6px;
}

.form-control {
	border-radius: 8px;
	padding: 10px 14px;
	border: 1px solid #ced4da;
}

.form-control:focus {
	border-color: #007bff;
	box-shadow: 0 0 0 0.2rem rgba(0, 123, 255, 0.15);
}

/* Inputs Desativados */
.form-control:disabled, .form-control[readonly] {
	background-color: #f8f9fa;
	color: #6c757d;
	cursor: not-allowed;
	border-color: #e9ecef;
}

/* Botão Salvar */
.btn-primary {
	background-color: #007bff;
	border-color: #007bff;
	padding: 10px 24px;
	font-weight: 600;
	border-radius: 8px;
}

.btn-primary:hover {
	background-color: #0056b3;
	border-color: #0056b3;
}

/* Informação de Segurança */
.security-badge {
	display: inline-flex;
	align-items: center;
	background-color: #e3f2fd;
	color: #0056b3;
	padding: 8px 12px;
	border-radius: 6px;
	font-size: 0.85rem;
	font-weight: 500;
	margin-top: 15px;
}
</style>
<#assign meusScripts>
<script src="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/js/select2.min.js"></script>
<script
	src="https://cdnjs.cloudflare.com/ajax/libs/jquery.mask/1.14.16/jquery.mask.js"
	integrity="sha512-0XDfGxFliYJPFrideYOoxdgNIvrwGTLnmK20xZbCAvPfLGQMzHUsaqZK8ZoH+luXGRxTrS46+Aq400nCnAT0/w=="
	crossorigin="anonymous" referrerpolicy="no-referrer"></script>
	
<script id="search-js" defer src="https://api.mapbox.com/search-js/v1.5.0/web.js"></script>
	
<script>
		$('.select2').select2();
        function previewImage(event) {
            const input = event.target;
            if (input.files && input.files[0]) {
                const reader = new FileReader();
                
                reader.onload = function(e) {
                    document.getElementById('profile-img-preview').src = e.target.result;
                }
                
                reader.readAsDataURL(input.files[0]);
            }
        }
        
        $(document).ready(function(){
        	$('#telefone').mask('(00) 00000-0000');
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
	          console.log("Coordenadas capturadas:", userLat, userLng);
	        }
	      });
	    };
</script>
</#assign>

<@layout.padrao scripts=meusScripts title="Editar perfil - MatchService"> 
<div class="container mt-5 mb-5">
	<#if sucesso??>
		<div class="alert alert-success">${sucesso}</div>
	<#elseif erro??>
		<div class="alert alert-danger">${erro}</div>
	</#if>

        <div class="row">
            <div class="col-lg-4 col-md-5">
                <div class="card text-center">
                    <div class="card-body py-5">
                        
                        <div class="profile-photo-container mb-3" onclick="document.getElementById('upload-photo').click();">
                           
                            <img id="profile-img-preview" src="https://ui-avatars.com/api/?name=${usuario.login}&background=007bff&color=fff&size=150" alt="Sua foto de perfil">
                            
                            <div class="photo-overlay">
                                <i class="fas fa-camera mb-1"></i><br>Alterar foto
                            </div>
                        </div>
                        <input type="file" id="upload-photo" accept="image/png, image/jpeg" onchange="previewImage(event)">

                        <h4 class="fw-bold mb-1">${usuario.login}</h4>
                        <p class="text-muted mb-3"><i class="fas fa-user-tie me-1"></i>
                        	<#if usuario.tipoUsuario == 0>
                        		Contratante
                        	<#else>
                        		Prestador
                        	</#if>
                        </p>
                        
                        <div class="security-badge w-100 justify-content-center">
                            <i class="fas fa-shield-alt me-2"></i> Seus dados estão protegidos
                        </div>
                    </div>
                </div>
            </div>

           
            <div class="col-lg-8 col-md-7">
                <div class="card">
					<div class="card-header d-flex align-items-center">
					    <h5 class="card-title mb-0">Dados Pessoais</h5>
					    
					    <span class="badge bg-light text-secondary border ms-auto">
					        <i class="fas fa-info-circle me-1"></i> Mantenha atualizado
					    </span>
					</div>
					                    
                    <div class="card-body">
                        <form action="/editar-perfil" method="POST">
                            
                            
                            <h6 class="text-primary mb-3 fw-bold"><i class="fas fa-id-card me-2"></i> Informações da Conta</h6>
                            
                            <div class="row g-3 mb-4">
                                <div class="col-12">
                                    <label for="nome" class="form-label">Nome Completo</label>
                                    <input type="text" class="form-control" id="nome" value="${usuario.nome}" name="nome">
                                </div>
                                
                                <div class="col-md-6">
                                    <label for="email" class="form-label">E-mail <span class="text-muted fw-normal">(Não alterável)</span></label>
                                    <div class="input-group">
                                        <span class="input-group-text bg-light text-muted border-end-0"><i class="fas fa-envelope"></i></span>
                                        <input type="email" class="form-control border-start-0" id="email" value="${usuario.email}" disabled readonly>
                                    </div>
                                </div>
                                
                                <div class="col-md-6">
                                    <label for="login" class="form-label">Login/Usuário <span class="text-muted fw-normal">(Não alterável)</span></label>
                                    <div class="input-group">
                                        <span class="input-group-text bg-light text-muted border-end-0"><i class="fas fa-at"></i></span>
                                        <input type="text" class="form-control border-start-0" id="login" value="${usuario.login}" disabled readonly>
                                    </div>
                                </div>

                                <div class="col-md-6">
                                    <label for="whatsapp" class="form-label">Celular / WhatsApp</label>
                                    <div class="input-group">
                                        <span class="input-group-text bg-white"><i class="fab fa-whatsapp text-success"></i></span>
                                        <input type="tel" class="form-control" id="telefone" name="telefone" value="${usuario.telefone!}">
                                    </div>
                                </div>
                            </div>

                            <hr class="border-light my-4">

                           
                            <h6 class="text-primary mb-3 fw-bold"><i class="fas fa-map-marker-alt me-2"></i> Endereço</h6>
                            
                            <div class="row g-3">
                                <div class="col-md-4">
                                    <label for="cep" class="form-label">CEP</label> <input type="text"
									id="cep" class="form-control" autocomplete="postal-code" name="cep" value="${usuario.cep!}">
                                </div>


                                <div class="col-12">
                                    <label for="endereco" class="form-label">Endereço Completo</label>
                                    <input type="text" class="form-control" name="rua" id="endereco" autocomplete="address-line1" value="${usuario.rua!}">
                                </div>

                                <div class="col-md-5">
                                    <label for="bairro" class="form-label">Bairro</label>
                                    <input type="text" class="form-control" readonly autocomplete="address-level3" name="bairro" value="${usuario.bairro!}">
                                </div>

                                <div class="col-md-5">
                                    <label for="cidade" class="form-label">Cidade</label>
                                    <input type="text" class="form-control" readonly autocomplete="address-level2" name="cidade"  value="${usuario.cidade!}">
                                </div>

                                <div class="col-md-2">
                                    <label class="form-label">UF</label> <input type="text"
									id="estado" class="form-control bg-white" readonly
									autocomplete="address-level1" name="uf" value="${usuario.uf!}">
                                </div>
                            </div>

							<#if usuario.tipoUsuario == 1>
								<div class="categoria-prestador mb-4">
								    <label for="categorias" class="form-label fw-bold">Suas Especialidades:</label>
								    <select name="categorias[]" id="categorias" class="form-select select2" multiple="multiple">
								        <#list usuario.prestador.categorias as cat>
								            
								            <option value="${cat.id}" 
								                <#if categoriasDoUsuario?seq_contains(cat.id)>selected</#if>>
								                ${cat.nome}
								            </option>
								        </#list>
								    </select>
								    <small class="text-muted">Você pode selecionar várias ou remover as atuais.</small>
								</div>
								<div class="mb-3">
								  <label for="exampleTextarea" class="form-label">Adicione uma descrição para os clientes notarem o seu serviço</label>
								  <textarea class="form-control" id="descricao" rows="3" name="descricao" value="${usuario.prestador.descricao!}">${usuario.prestador.descricao!}</textarea>
								</div>
                           </#if>
                            <div class="d-flex justify-content-end mt-4 pt-3 border-top">
                                <button type="submit" class="btn btn-primary px-4">
                                    Salvar Alterações
                                </button>
                            </div>

                        </form>
                    </div>
                </div>
            </div>
            
        </div>
    </div>
</@layout.padrao>
