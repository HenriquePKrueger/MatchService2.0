<#import "/layout/main.ftl" as layout>
<style>
        /* Hero Section */
        .hero-section {
            background: linear-gradient(rgba(13, 110, 253, 0.9), rgba(13, 110, 253, 0.7)), url('https://images.unsplash.com/photo-1621905251189-08b45d6a269e?ixlib=rb-4.0.3&auto=format&fit=crop&w=1920&q=80');
            background-size: cover;
            background-position: center;
            color: white;
            padding: 100px 0;
            margin-bottom: 3rem;
        }

        .feature-icon {
            width: 80px;
            height: 80px;
            background-color: #e7f1ff;
            color: #0d6efd;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 2rem;
            margin: 0 auto 1.5rem;
            transition: transform 0.3s;
        }

        .card:hover .feature-icon {
            transform: scale(1.1);
            background-color: #0d6efd;
            color: white;
        }

        .story-img {
            border-radius: 20px;
            box-shadow: 0 15px 30px rgba(0,0,0,0.1);
        }

        .testimonial-card {
            border-left: 5px solid #0d6efd;
            background-color: #f8f9fa;
        }
</style>
<@layout.padrao>
<section class="hero-section text-center">
        <div class="container">
            <div class="row justify-content-center">
                <div class="col-lg-8">
                    <h1 class="display-4 fw-bold mb-4">Conectando necessidades à disponibilidade certa.</h1>
                    <p class="lead fs-4 opacity-75">
                        Não é só sobre encontrar um profissional. É sobre encontrar alguém de confiança que cabe na sua agenda e no seu bolso.
                    </p>
                </div>
            </div>
        </div>
    </section>

    <!-- 2. A NOSSA MISSÃO (Baseada nas dores do Felipe e Bernardo) -->
    <div class="container my-5">
        <div class="row align-items-center g-5">
            <div class="col-lg-6">
                <img src="https://images.unsplash.com/photo-1581578731117-e0a8c0cd0d59?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80" 
                     alt="Profissional trabalhando" class="img-fluid story-img">
            </div>
            <div class="col-lg-6">
                <h6 class="text-primary fw-bold text-uppercase ls-md">Nossa História</h6>
                <h2 class="fw-bold mb-4">Por que criamos o MatchService?</h2>
                <p class="text-muted lead">
                    Percebemos que o mercado de serviços tinha um problema crônico: <strong>o desencontro.</strong>
                </p>
                <p>
                    De um lado, pessoas como o <strong>Felipe</strong>, que trabalham duro a semana toda e precisam de um serviço rápido, sem surpresas no preço e sem o medo de colocar um estranho em casa.
                </p>
                <p>
                    Do outro, profissionais competentes como o <strong>Valdeci</strong>, que têm seu emprego fixo mas querem fazer uma renda extra no fim de semana ou à noite, mas sofrem com cancelamentos e orçamentos que não fecham.
                </p>
                <p class="fw-bold mt-4">
                    O MatchService nasceu para resolver essa equação: transparência total, segurança e, acima de tudo, sincronia de agendas.
                </p>
            </div>
        </div>
    </div>

    <!-- 3. NOSSOS PILARES (Soluções para os problemas levantados) -->
    <section class="bg-light py-5 mt-5">
        <div class="container">
            <div class="text-center mb-5">
                <h2 class="fw-bold">Como resolvemos seus problemas</h2>
                <p class="text-muted">O tripé que sustenta nossa plataforma</p>
            </div>

            <div class="row g-4">
                <!-- Pilar 1: Agenda (Bernardo/Valdeci) -->
                <div class="col-md-4">
                    <div class="card h-100 border-0 shadow-sm p-4 text-center">
                        <div class="feature-icon">
                            <i class="far fa-clock"></i>
                        </div>
                        <h4 class="fw-bold">Sincronia de Agenda</h4>
                        <p class="text-muted">
                            Sabemos que sua rotina é híbrida ou corrida. Nosso sistema foca em encontrar o "Match" perfeito entre o tempo livre do prestador e a sua necessidade, inclusive aos fins de semana e feriados.
                        </p>
                    </div>
                </div>

                <!-- Pilar 2: Preço Justo (Felipe/Bernardo) -->
                <div class="col-md-4">
                    <div class="card h-100 border-0 shadow-sm p-4 text-center">
                        <div class="feature-icon">
                            <i class="fas fa-tags"></i>
                        </div>
                        <h4 class="fw-bold">Transparência de Preço</h4>
                        <p class="text-muted">
                            Medo de pagar caro? Acabou. Oferecemos uma estimativa de média de mercado para cada serviço, garantindo que você pague um valor justo e que o prestador seja valorizado corretamente.
                        </p>
                    </div>
                </div>

                <!-- Pilar 3: Confiança (Felipe) -->
                <div class="col-md-4">
                    <div class="card h-100 border-0 shadow-sm p-4 text-center">
                        <div class="feature-icon">
                            <i class="fas fa-shield-alt"></i>
                        </div>
                        <h4 class="fw-bold">Confiança e Avaliação</h4>
                        <p class="text-muted">
                            As famosas "estrelinhas" não são enfeite. Criamos um sistema robusto de reputação para que você saiba quem está colocando dentro de casa, baseado em experiências reais.
                        </p>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <!-- 4. QUEM USA O MATCHSERVICE? (Personas implícitas) -->
    <div class="container my-5 py-5">
        <h2 class="fw-bold text-center mb-5">Feito para quem valoriza tempo e qualidade</h2>
        
        <div class="row g-4">
            <div class="col-md-6">
                <div class="card testimonial-card p-4 h-100 border-0 shadow-sm">
                    <div class="d-flex align-items-center mb-3">
                        <div class="bg-primary text-white rounded-circle d-flex align-items-center justify-content-center me-3" style="width: 50px; height: 50px;">
                            <i class="fas fa-laptop-code"></i>
                        </div>
                        <div>
                            <h5 class="mb-0 fw-bold">Para quem Contrata</h5>
                            <small class="text-muted">Home office, rotina corrida, urgências</small>
                        </div>
                    </div>
                    <p class="fst-italic mb-0">
                        "Eu precisava de alguém que entendesse que eu trabalho de casa e não posso parar para supervisionar obra o dia todo. O MatchService me mostrou quem estava disponível no meu horário de almoço ou fim de semana."
                    </p>
                </div>
            </div>

            <div class="col-md-6">
                <div class="card testimonial-card p-4 h-100 border-0 shadow-sm" style="border-left-color: #198754;">
                    <div class="d-flex align-items-center mb-3">
                        <div class="bg-success text-white rounded-circle d-flex align-items-center justify-content-center me-3" style="width: 50px; height: 50px;">
                            <i class="fas fa-tools"></i>
                        </div>
                        <div>
                            <h5 class="mb-0 fw-bold">Para quem Presta Serviço</h5>
                            <small class="text-muted">Renda extra, carteira assinada, autônomos</small>
                        </div>
                    </div>
                    <p class="fst-italic mb-0">
                        "Tenho meu emprego fixo na imobiliária, mas queria fazer um extra. O site me ajuda a preencher meus sábados sem aquela confusão de ficar negociando preço pelo WhatsApp o dia todo."
                    </p>
                </div>
            </div>
        </div>
    </div>

    <!-- 5. CTA FINAL -->
    <section class="bg-primary text-white text-center py-5">
        <div class="container">
            <h2 class="fw-bold mb-3">Pronto para encontrar o seu Match?</h2>
            <p class="lead mb-4">Junte-se a comunidade que está descomplicando os serviços residenciais.</p>
            <div class="d-flex justify-content-center gap-3">
                <a href="/modulos/listagem_prestadores/index.html" class="btn btn-light btn-lg fw-bold text-primary">Quero Contratar</a>
                <a href="/modulos/completar_perfil/index.html" class="btn btn-outline-light btn-lg fw-bold">Quero Trabalhar</a>
            </div>
        </div>
    </section>

    <div class="modal fade" id="notificacao-modal" data-bs-backdrop="static" data-bs-keyboard="false" tabindex="-1" aria-hidden="true">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content border border-primary border-2 rounded-4 overflow-hidden">
          
          <div class="modal-header border-0 justify-content-center bg-light py-4">
            <div class="text-center w-100">
              <h4 class="modal-title fw-bold mb-2">Pedido de agendamento</h4>
              <p class="mb-0 fs-5 text-muted">
                Olá, <span id="notif-prestador-nome" class="fw-semibold text-dark">[USER]</span>, você tem uma nova oportunidade de trabalho!
              </p>
            </div>
          </div>
    
          <div class="modal-body p-4 d-flex flex-column flex-sm-row justify-content-around gap-3">
            <button id="btn-notif-detalhes" class="btn btn-primary rounded-pill px-4 py-3 fw-semibold flex-grow-1 lh-sm">
              Ver detalhes do agendamento
            </button>
            <button id="btn-notif-contato" class="btn btn-outline-primary rounded-pill px-4 py-3 fw-semibold flex-grow-1 lh-sm">
              Entrar em contato com o cliente
            </button>
          </div>
    
        </div>
      </div>
    </div>
</@layout.padrao>  