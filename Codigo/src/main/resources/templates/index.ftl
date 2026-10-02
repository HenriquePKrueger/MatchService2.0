<#import "/layout/main.ftl" as layout>
<style>
	 /* Hero Section */
        .hero-section {
            padding: 80px 0 100px;
            background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
            position: relative;
        }
        .hero-title {
            font-size: 3.2rem;
            font-weight: 800;
            color: #1a1a1a;
            line-height: 1.2;
            margin-bottom: 20px;
        }
        .hero-title span {
            color: #007bff;
        }
        .hero-subtitle {
            font-size: 1.2rem;
            color: #495057;
            margin-bottom: 40px;
            font-weight: 400;
        }
        
        .hero-image-wrapper img {
            border-radius: 16px;
            box-shadow: 0 20px 40px rgba(0,0,0,0.1);
            width: 100%;
            object-fit: cover;
            height: 400px;
        }

       

        /* Features Section */
        .features-section {
            padding: 80px 0;
            background-color: #fff;
        }
        .feature-card {
            text-align: center;
            padding: 40px 20px;
            border-radius: 16px;
            transition: all 0.3s;
            border: 1px solid transparent;
        }
        .feature-card:hover {
            background-color: #f8fbff;
            border-color: #cce5ff;
            transform: translateY(-5px);
        }
        .feature-icon-wrapper {
            width: 80px;
            height: 80px;
            background-color: #e6f2ff;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            margin: 0 auto 24px;
            color: #007bff;
            font-size: 2rem;
        }

        /* Call to Action - Prestador */
        .cta-provider {
            background: linear-gradient(135deg, #007bff 0%, #0056b3 100%);
            border-radius: 24px;
            padding: 60px 40px;
            color: white;
            position: relative;
            overflow: hidden;
            margin-bottom: -60px; /* Sobrepor o footer */
            z-index: 10;
            box-shadow: 0 20px 40px rgba(0, 123, 255, 0.2);
        }
        
        /* Footer */
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
</style>
<@layout.padrao scripts=meusScripts>
<section class="hero-section">
        <div class="container">
            <div class="row align-items-center">
                <div class="col-lg-6 mb-5 mb-lg-0 pe-lg-5">
                    <span class="badge bg-primary bg-opacity-10 text-primary mb-3 px-3 py-2 rounded-pill fw-bold">Rápido. Seguro. Na sua hora.</span>
                    <h1 class="hero-title">O profissional certo, no momento exato da <span>sua agenda.</span></h1>
                    <p class="hero-subtitle">Conectamos você aos melhores prestadores de serviço da sua região. Solicite um serviço para o final de semana, fora do horário comercial ou para agora mesmo.</p>
                    
                    <#if !user_id??>
    					<p class="mt-3 mb-0 text-muted fw-medium">Já possui uma conta?
    					<a href="/login" class="btn btn-primary fw-bold rounded-pill px-4 py-2 shadow-sm">Faça login aqui <i class="fas fa-arrow-right"></i></a></p>
					<#elseif tipo_usuario == 0>
					    <div class="mt-3">
					        <a href="/pesquisa" class="btn btn-primary fw-bold rounded-pill px-4 py-2 shadow-sm">
					            Contratar agora mesmo <i class="fas fa-arrow-right ms-1"></i>
					        </a>
					    </div>
					<#elseif tipo_usuario == 1>
					    <div class="mt-3">
					        <a href="/solicitacoes-disponiveis" class="btn btn-primary fw-bold rounded-pill px-4 py-2 shadow-sm">
					            Visualizar solicitações disponíveis <i class="fas fa-arrow-right ms-1"></i>
					        </a>
					    </div>			
					</#if>
                    
                    <div class="mt-4 pt-3 d-flex align-items-center gap-3 text-muted small fw-medium">
                        <div class="d-flex">
                            <i class="fas fa-star text-warning"></i>
                            <i class="fas fa-star text-warning"></i>
                            <i class="fas fa-star text-warning"></i>
                            <i class="fas fa-star text-warning"></i>
                            <i class="fas fa-star text-warning"></i>
                        </div>
                        <span>+5.000 serviços realizados com sucesso</span>
                    </div>
                </div>
                <div class="col-lg-6">
                    <div class="hero-image-wrapper">
                        <!-- Imagem de um prestador de serviço sorrindo (Unsplash) -->
                        <img src="https://images.unsplash.com/photo-1581578731548-c64695cc6952?q=80&w=2070&auto=format&fit=crop" alt="Prestador de serviço trabalhando">
                    </div>
                </div>
            </div>
        </div>
    </section>


    <section class="features-section">
        <div class="container">
            <div class="text-center mb-5 pb-2">
                <h2 class="fw-bold text-dark mb-3">Por que escolher o MatchService?</h2>
                <p class="text-muted">Desenhado para resolver suas dores e valorizar o tempo de todos.</p>
            </div>

            <div class="row g-4">
            
                <div class="col-md-4">
                    <div class="feature-card">
                        <div class="feature-icon-wrapper">
                            <i class="far fa-calendar-check"></i>
                        </div>
                        <h4 class="fw-bold mb-3">Sincronia de Agendas</h4>
                        <p class="text-muted mb-0">Não tem tempo comercial? Filtre profissionais que aceitam trabalhar aos finais de semana, feriados ou fora de hora.</p>
                    </div>
                </div>
                
              
                <div class="col-md-4">
                    <div class="feature-card">
                        <div class="feature-icon-wrapper">
                            <i class="fas fa-shield-alt"></i>
                        </div>
                        <h4 class="fw-bold mb-3">Confiança e "Estrelinhas"</h4>
                        <p class="text-muted mb-0">Contrate sem medo. Visualize a média de valores cobrados e veja a avaliação real de clientes anteriores antes de fechar o negócio.</p>
                    </div>
                </div>

               
                <div class="col-md-4">
                    <div class="feature-card">
                        <div class="feature-icon-wrapper">
                            <i class="fas fa-hand-holding-usd"></i>
                        </div>
                        <h4 class="fw-bold mb-3">Bicos sem dor de cabeça</h4>
                        <p class="text-muted mb-0">Trabalha CLT durante a semana? Use a plataforma para pegar serviços extras apenas nos horários que você está disponível.</p>
                    </div>
                </div>
            </div>
        </div>
    </section>


   
 <div class="container mt-3">
        <div class="cta-provider row align-items-center">
            
            <#if !user_id??>
                <div class="col-lg-8 mb-4 mb-lg-0 text-center text-lg-start">
                    <h2 class="fw-bold mb-2">Você ainda não está logado!</h2>
                    <p class="fs-5 mb-0 opacity-75">Faça login na plataforma e garanta que seus serviços serão realizados de forma rápida e como combinado!</p>
                </div>
                <div class="col-lg-4 text-center text-lg-end">
                    <a href="/login" class="btn btn-light btn-lg fw-bold text-primary px-5 rounded-pill shadow">
                        Fazer Login
                    </a>
                </div>
                
            <#elseif tipo_usuario == 0>
                <div class="col-lg-8 mb-4 mb-lg-0 text-center text-lg-start">
                    <h2 class="fw-bold mb-2">Precisa de um especialista?</h2>
                    <p class="fs-5 mb-0 opacity-75">Não perca tempo. Encontre agora mesmo o profissional ideal para resolver o seu problema com segurança.</p>
                </div>
                <div class="col-lg-4 text-center text-lg-end">
                    <a href="/pesquisa" class="btn btn-light btn-lg fw-bold text-primary px-5 rounded-pill shadow">
                        Procurar profissionais
                    </a>
                </div>
                
            <#elseif tipo_usuario == 1>
                <div class="col-lg-8 mb-4 mb-lg-0 text-center text-lg-start">
                    <h2 class="fw-bold mb-2">Pronto para o próximo serviço?</h2>
                    <p class="fs-5 mb-0 opacity-75">Existem dezenas de clientes na plataforma procurando pelas suas habilidades neste exato momento.</p>
                </div>
                <div class="col-lg-4 text-center text-lg-end">
                    <a href="/solicitacoes-disponiveis" class="btn btn-light btn-lg fw-bold text-primary px-5 rounded-pill shadow">
                        Ver painel de serviços
                    </a>
                </div>
            </#if>
            
        </div>
    </div>

</@layout.padrao>