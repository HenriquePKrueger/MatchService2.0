<#import "/layout/main.ftl" as layout>
<#assign meusScripts>
<script>
    document.getElementById('btnMostrarSenha').addEventListener('click', function() {
        const senhaInput = document.getElementById('senha');
        const icon = this.querySelector('i');

        if (senhaInput.type === 'password') {
            senhaInput.type = 'text';
            icon.classList.remove('fa-eye');
            icon.classList.add('fa-eye-slash');
        } else {
            senhaInput.type = 'password';
            icon.classList.remove('fa-eye-slash');
            icon.classList.add('fa-eye');
        }
    });
</script>
</#assign>

<@layout.padrao scripts=meusScripts title="Login - MatchService">
<div class="row justify-content-center">
    <div class="col-12 col-sm-10 col-md-8 col-lg-5 col-xl-4">
        
        <#if erro??>
            <div class="alert alert-danger shadow-sm border-0 mb-4">
                <i class="fas fa-exclamation-circle me-2"></i> ${erro}
            </div>
        </#if>

        <div class="login-logo mb-4 text-center">
            <a href="/" class="text-decoration-none">
                <b>Match</b>Service
            </a>
        </div>

        <div class="card card-outline card-primary shadow-lg">
            <div class="card-body login-card-body p-4 p-md-5">
                <p class="login-box-msg text-muted mb-4">Faça login para acessar o sistema</p>

                <form action="/login" method="post">
                    
                    <div class="mb-3">
                        <label for="login" class="form-label fw-semibold">Login</label>
                        <div class="input-group">
                            <span class="input-group-text bg-light border-end-0"><i class="fas fa-user text-muted"></i></span>
                            <input class="form-control border-start-0 ps-0" id="login" name="login" 
                                   placeholder="Seu login cadastrado" autofocus required>
                        </div>
                    </div>

                    <div class="mb-4">
                        <label for="senha" class="form-label fw-semibold">Senha</label>
                        <div class="input-group">
                            <span class="input-group-text bg-light border-end-0"><i class="fas fa-lock text-muted"></i></span>
                            <input type="password" class="form-control border-start-0 border-end-0 ps-0" 
                                   id="senha" name="senha" placeholder="Sua senha" required>
                            <button class="btn btn-light border border-start-0 text-muted toggle-password" 
                                    type="button" id="btnMostrarSenha">
                                <i class="fas fa-eye"></i>
                            </button>
                        </div>
                    </div>

                    <div class="row">
                        <div class="col-12">
                            <button type="submit" class="btn btn-primary btn-block w-100 py-3 fw-bold shadow-sm">
                                <i class="fas fa-sign-in-alt me-2"></i> Entrar
                            </button>
                        </div>
                    </div>
                </form>

                <div class="text-center mt-4">
                    <p class="mb-0 text-muted small">Não tem uma conta? <a href="/cadastro" class="text-primary fw-bold">Cadastre-se</a></p>
                </div>
            </div>
        </div>
    </div>
</div>
</@layout.padrao>