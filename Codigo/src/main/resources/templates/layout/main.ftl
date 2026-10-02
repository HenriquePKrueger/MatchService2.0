<#macro padrao title="MatchService" scripts="">
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${title}</title>

    <!-- Google Fonts - Inter -->
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;600;700&display=swap" rel="stylesheet">

    <!-- Bootstrap 5.3 -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    
    <!-- Font Awesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    
    <!-- AdminLTE CSS (Opcional, se for usar componentes específicos) -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/admin-lte@3.2/dist/css/adminlte.min.css">
	<link href="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/css/select2.min.css" rel="stylesheet" />
    <style>
        body { 
		    font-family: 'Inter', sans-serif; 
		    display: flex;
		    flex-direction: column;
		    min-height: 100vh;
		    background-color: #f4f6f9;
		}
		
		main {
		    flex: 1 0 auto;
		    display: flex;
		    align-items: center;
		}
		.btn-nav-login {
            background-color: transparent;
            border: 1px solid #fff;
            color: #fff;
            border-radius: 6px;
            font-weight: 600;
        }
        .btn-nav-login:hover {
            background-color: #fff;
            color: #007bff;
        }
		 .footer {
            background-color: #1a1a1a;
            color: #a0a0a0;
            padding: 120px 0 40px; /* Padding extra no topo para o CTA */
        }
        .footer-brand {
            color: #fff;
            font-size: 1.5rem;
            font-weight: 700;
            margin-bottom: 20px;
            display: inline-block;
        }
        .footer-links a {
            color: #a0a0a0;
            text-decoration: none;
            display: block;
            margin-bottom: 10px;
            transition: color 0.2s;
        }
        .footer-links a:hover {
            color: #fff;
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
</head>
<body class="hold-transition">

    <nav class="navbar navbar-expand-lg navbar-dark bg-primary shadow-sm">
        <div class="container">
            <a class="navbar-brand d-flex align-items-center" href="/">
                <img src="/images/logo.png" width="40" alt="Logo"> 
                <span>MatchService</span>
            </a>

            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarMain">
                <span class="navbar-toggler-icon"></span>
            </button>

            <div class="collapse navbar-collapse" id="navbarMain">
                <ul class="navbar-nav ms-auto mb-2 mb-lg-0 align-items-center">
                    <li class="nav-item">
                        <a class="nav-link text-white" href="/pesquisa">Pesquisa Avançada</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link text-white" href="/about">Sobre nós</a>
                    </li>
                    
                    <!-- Dropdown Usuário -->
                    <#if user_login??>
	                    <li class="nav-item dropdown ms-lg-3">
	                        <a class="nav-link dropdown-toggle text-white fw-bold" href="#" role="button" data-bs-toggle="dropdown" aria-expanded="false">
	                            <i class="fas fa-user-circle me-1"></i> Olá, ${user_login}
	                        </a>
	                        
	                        <ul class="dropdown-menu dropdown-menu-end">
		                   		<#if tipo_usuario == 0>
									<li>
    								<a class="dropdown-item" href="/minhas-solicitacoes-diretas">
    								</a>
									</li>
		                        	<li><a class="dropdown-item" href="/criar-solicitacao"><i class="fa-solid fa-plus me-2"></i></i>Criar Solicitação</a></li>
		                        	<li><a class="dropdown-item" href="/minhas-solicitacoes"><i class="fa-solid fa-handshake-angle"></i></i>Minhas solicitações</a></li>
		                        </#if>

								 <#if tipo_usuario?? && tipo_usuario?number == 1>
									<li>
    									<a class="dropdown-item" href="/solicitacoes-diretas">
        								<i class="fa-solid fa-calendar-check me-2"></i>Solicitações diretas
    									</a>
									</li>

								 	<li>
                                     	<a class="dropdown-item" href="/prestador/ofertas">
                                     	<i class="fa-solid fa-dollar-sign me-2"></i>minhas ofertas
                                        </a>
                                    </li>
                                     <li>
                                     	<a class="dropdown-item" href="/solicitacoes-disponiveis">
                                     	<i class="fa-solid fa-list-check me-2"></i>serviços disponíveis
                                        </a>
                                    </li>
                                     <li>
                                     <a class="dropdown-item" href="/agenda">
                                     <i class="fa-solid fa-calendar-days me-2"></i>Minha Agenda
                                        </a>
                                    </li>
                                </#if>

	                            <li><hr class="dropdown-divider"></li>
	                            <li><a class="dropdown-item" href="/perfil"><i class="fas fa-user-edit me-2"></i>Editar Perfil</a></li>
	                            <li><a class="dropdown-item text-danger" href="/logout"><i class="fas fa-sign-out-alt me-2"></i>Sair</a></li>
	                        </ul>
	                    </li>
                    <#else>
	                    <div class="d-flex gap-3 align-items-center mt-3 mt-lg-0">
	                    <a href="/login" class="btn btn-light text-primary fw-bold px-4 shadow-sm rounded-pill">Entrar</a>
	                    <a href="/cadastro" class="btn btn-nav-login px-4 rounded-pill">Cadastrar-se</a>
                </div>
                    </#if>
                   
                </ul>
            </div>
        </div>
    </nav>

    <main class="flex-fill d-flex align-items-center">
	    <div class="container py-5 p-0">
	        <#nested>
	    </div>
	</main>

    <footer class="footer">
        <div class="container">
            <div class="row g-4">
                <div class="col-lg-4 pe-lg-5">
                    <a href="#" class="footer-brand"><img src="/images/logo.png" width="40" alt="Logo"> MatchService</a>
                    <p class="mb-4">Sua plataforma definitiva para encontrar serviços residenciais e corporativos com quem entende do assunto, no seu tempo.</p>
                    <div class="d-flex gap-3">
                        <a href="#" class="text-light fs-5"><i class="fab fa-instagram"></i></a>
                        <a href="#" class="text-light fs-5"><i class="fab fa-facebook"></i></a>
                        <a href="#" class="text-light fs-5"><i class="fab fa-linkedin"></i></a>
                    </div>
                </div>
                
                <div class="col-lg-2 col-md-4">
                    <h5 class="text-white fw-bold mb-3">Plataforma</h5>
                    <div class="footer-links">
                        <a href="#">Como funciona</a>
                        <a href="#">Buscar serviços</a>
                        <a href="#">Para prestadores</a>
                        <a href="#">Preços</a>
                    </div>
                </div>
                
                <div class="col-lg-2 col-md-4">
                    <h5 class="text-white fw-bold mb-3">Suporte</h5>
                    <div class="footer-links">
                        <a href="#">Central de Ajuda</a>
                        <a href="#">Regras de Segurança</a>
                        <a href="#">Fale Conosco</a>
                    </div>
                </div>

                <div class="col-lg-4 col-md-4">
                    <h5 class="text-white fw-bold mb-3">Fique por dentro</h5>
                    <p>Receba dicas de manutenção e promoções na sua caixa de entrada.</p>
                    <div class="input-group mb-3">
                        <input type="text" class="form-control border-0 bg-dark text-white" placeholder="Seu e-mail">
                        <button class="btn btn-primary" type="button">Assinar</button>
                    </div>
                </div>
            </div>
            
            <hr class="border-secondary my-4">
            
            <div class="text-center small">
                &copy; 2026 MatchService. Todos os direitos reservados.
            </div>
        </div>
    </footer>

    <!-- Scripts -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/admin-lte@3.2/dist/js/adminlte.min.js"></script>
    <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
    ${scripts}
</body>
</html>
</#macro>
