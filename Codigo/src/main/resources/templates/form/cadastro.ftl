<#import "/layout/main.ftl" as layout>
<#assign meusScripts>
<script
	src="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/js/select2.min.js"></script>
<script
	src="https://cdnjs.cloudflare.com/ajax/libs/jquery.mask/1.14.16/jquery.mask.js"
	integrity="sha512-0XDfGxFliYJPFrideYOoxdgNIvrwGTLnmK20xZbCAvPfLGQMzHUsaqZK8ZoH+luXGRxTrS46+Aq400nCnAT0/w=="
	crossorigin="anonymous" referrerpolicy="no-referrer"></script> <script
	id="search-js" defer
	src="https://api.mapbox.com/search-js/v1.5.0/web.js"></script> <script>
		$(document).ready(function() {
			$('#telefone').mask('(00) 00000-0000');
			function ajustarVisibilidade() {
				if ($('.tipo-usuario:checked').val() == 0) {
					$('.categoria-prestador').hide(100);
				} else {
					$('.categoria-prestador').show(100);
				}
			}

			ajustarVisibilidade();

			$(document).on('change', '.tipo-usuario', function() {
				ajustarVisibilidade();
			});
			$('.select2').select2();
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
	</script> 
</#assign> 
<@layout.padrao scripts=meusScripts> 
<#if erro??>
    <div class="alert alert-danger">${erro}</div>
</#if>
<div class="register-box container w-100">
	<div class="register-logo">
		<a href="#"><b>Match</b>Service</a>
	</div>

	<div class="card card-outline card-primary shadow-lg">
		<div class="card-body register-card-body">
			<p class="login-box-msg">Crie sua conta para contratar ou prestar
				serviços</p>

			<form action="/cadastro" method="post">
					<input type="hidden" id="lat" name="lat" value="">
                    <input type="hidden" id="long" name="long" value="">
                    
				<div class="mb-3">
					<label for="nome" class="form-label">Nome (Como você quer ser chamado)</label>
					<div class="input-group">
						<span class="input-group-text"><i class="fas fa-user"></i></span>
						<input type="text" class="form-control" id="nome"
							placeholder="Ex: Bernardo Silva" name="nome" value="${temp_nome!''}" maxlength="100">
					</div>
				</div>
				<div class="mb-3">
					<label class="form-label d-block">Sexo:</label>
					<div class="d-flex gap-4">
						<div class="form-check">
							<input class="form-check-input sexo" type="radio"
								name="sexo" id="contratante" value="M" checked> <label
								class="form-check-label" for="contratante">Masculino</label>
						</div>
						<div class="form-check">
							<input class="form-check-input sexo" type="radio"
								name="sexo" id="prestador" value="F"> <label
								class="form-check-label" for="prestador">Feminino</label>
						</div>
					</div>
				</div>
				<div class="mb-3 row">
					<div class="col-md-4">
						<label for="email" class="form-label">Login</label>
						<div class="input-group">
							<span class="input-group-text"><i class="fa fa-user-circle" aria-hidden="true"></i></span>
							<input class="form-control" id="login"
								placeholder="fulano" name="login" value="${temp_login!''}">
						</div>
					</div>
					<div class="col-md-4">
						<label for="email" class="form-label">E-mail</label>
						<div class="input-group">
							<span class="input-group-text"><i class="fas fa-envelope"></i></span>
							<input type="email" class="form-control" id="email"
								placeholder="email@exemplo.com" name="email" value="${temp_email!''}">
						</div>
					</div>
					<div class="col-md-4">
						<label for="email" class="form-label">Senha</label>
						<div class="input-group">
							<span class="input-group-text"><i class="fa fa-key" aria-hidden="true"></i></span>
							<input type="text" class="form-control" id="senha"
								placeholder="" name="senha" value="${temp_senha!''}">
						</div>
					</div>
				</div>

				<div class="row">
					<div class="col-md-6 mb-3">
						<label for="telefone" class="form-label">Telefone</label>
						<div class="input-group">
							<span class="input-group-text"><i class="fas fa-phone"></i></span>
							<input type="tel" class="form-control" id="telefone"
								placeholder="(00) 00000-0000" name="telefone">
						</div>
					</div>

					<div class="col-md-6">
						<label for="cep" class="form-label">CEP</label> <input type="text"
							id="cep" class="form-control" autocomplete="postal-code" name="cep">
					</div>
				</div>
				<div class="row g-3 mt-1">
					<div class="col-12">
						<label for="rua" class="form-label">Endereço Completo</label> <input
							type="text" id="rua" class="form-control"
							placeholder="Comece digitando sua rua..."
							autocomplete="address-line1" name="rua" value="${temp_rua!''}">
					</div>
					<div class="col-md-5">
						<label class="form-label">Bairro</label> <input type="text"
							id="bairro" class="form-control bg-white" readonly
							autocomplete="address-level3" name="bairro" value="${temp_bairro!''}">
					</div>
					<div class="col-md-5">
						<label class="form-label">Cidade</label> <input type="text"
							id="cidade" class="form-control bg-white" readonly
							autocomplete="address-level2" name="cidade" value="${temp_cidade!''}">
					</div>
					<div class="col-md-2">
						<label class="form-label">UF</label> <input type="text"
							id="estado" class="form-control bg-white" readonly
							autocomplete="address-level1" name="uf">
					</div>
				</div>
				<div class="mb-3">
					<label class="form-label d-block">Eu quero:</label>
					<div class="d-flex gap-4">
						<div class="form-check">
							<input class="form-check-input tipo-usuario" type="radio"
								name="tipoUsuario" id="contratante" value="0" checked> <label
								class="form-check-label" for="contratante">Contratar
								Serviços</label>
						</div>
						<div class="form-check">
							<input class="form-check-input tipo-usuario" type="radio"
								name="tipoUsuario" id="prestador" value="1"> <label
								class="form-check-label" for="prestador">Prestar
								Serviços</label>
						</div>
					</div>
				</div>

				<div class="categoria-prestador">
					<label for="categoria">Categorias do Prestador:</label> <select
						name="id_categoria" class="form-select select2" name="categorias[]"
						multiple="multiple">
						<option value="">Selecione uma categoria</option>
						<#list categorias as cat>
						<option value="${cat.id}">${cat.nome}</option>
						</#list>
					</select>

				</div>

				<div class="row mt-4">
					<div class="col-12">
						<button type="submit" class="btn btn-primary btn-block py-2">
							<i class="fas fa-check-circle me-2"></i> Concluir Cadastro
						</button>
					</div>
				</div>
			</form>

			<div class="mt-3 text-center">
				<a href="/login" class="text-secondary small">Já possui uma conta?
					Faça login</a>
			</div>
		</div>
	</div>
</div>

</@layout.padrao> 
