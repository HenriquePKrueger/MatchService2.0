<#import "/layout/main.ftl" as layout>

<style>
    .search-header {
        background: linear-gradient(rgba(13, 110, 253, 0.9), rgba(13, 110, 253, 0.7)), url('https://images.unsplash.com/photo-1581578731117-e0a8c0cd0d59?ixlib=rb-4.0.3&auto=format&fit=crop&w=1920&q=80');
        background-size: cover;
        background-position: center;
        color: white;
        padding: 60px 0;
        margin-bottom: 2rem;
    }

    .search-card {
        border-radius: 15px;
        border: none;
        box-shadow: 0 10px 30px rgba(0,0,0,0.1);
        margin-top: -100px;
        background: white;
    }

    .prestador-card {
        transition: transform 0.3s, box-shadow 0.3s;
        border-radius: 12px;
        overflow: hidden;
    }

    .prestador-card:hover {
        transform: translateY(-5px);
        box-shadow: 0 10px 20px rgba(0,0,0,0.1) !important;
    }

    .category-badge {
        background-color: #e7f1ff;
        color: #0d6efd;
        font-weight: 600;
        padding: 0.5rem 1rem;
        border-radius: 50px;
        display: inline-block;
        margin-bottom: 1rem;
    }

    .avatar-placeholder {
        width: 60px;
        height: 60px;
        background-color: #0d6efd;
        color: white;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 1.5rem;
        font-weight: bold;
    }
</style>

<@layout.padrao>
    <section class="search-header text-center">
        <div class="container">
            <h1 class="display-5 fw-bold">Encontre o profissional ideal</h1>
            <p class="lead opacity-75">Filtre por serviço, localização ou gênero.</p>
        </div>
    </section>

    <div class="container mb-5">
        <div class="card search-card p-4">
            <form action="/pesquisa" method="GET" class="row g-3 align-items-end">
                <div class="col-md-4">
                    <label class="form-label fw-bold"><i class="fas fa-tools me-2 text-primary"></i>Tipo de Serviço</label>
                    <select name="idCategoria" class="form-select">
                        <option value="">Todas as categorias</option>
                        <#list categorias as cat>
                            <option value="${cat.id}" <#if idCategoria?? && cat.id == idCategoria> selected</#if> >${cat.nome}</option>
                        </#list>
                    </select>
                </div>

                <div class="col-md-3">
                    <label class="form-label fw-bold"><i class="fas fa-map-marker-alt me-2 text-primary"></i>Localização</label>
                    <input type="text" name="cidade" class="form-control" placeholder="Ex: Belo Horizonte" value="${cidadeFiltro!}">
                </div>

                <div class="col-md-3">
                    <label class="form-label fw-bold"><i class="fas fa-venus-mars me-2 text-primary"></i>Gênero</label>
                    <select name="genero" class="form-select">
                        <option value="">Ambos</option>
                        <option value="M" <#if generoFiltro?? && generoFiltro == 'M'> selected </#if>> Masculino</option>
                        <option value="F" <#if generoFiltro?? && generoFiltro == 'F'> selected </#if>>Feminino</option>
                    </select>
                </div>

                <div class="col-md-2">
                    <button type="submit" class="btn btn-primary w-100 fw-bold py-2">
                        <i class="fas fa-search me-2"></i>Buscar
                    </button>
                </div>
            </form>
        </div>
    </div>

    <div class="container my-5">
        <#if buscaFeita??>
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h3 class="fw-bold m-0">Resultados da busca</h3>
                <span class="badge bg-light text-dark p-2 border">${prestadores?size} profissionais encontrados</span>
            </div>

            <#if prestadores?has_content>
                <div class="row g-4">
                    <#list prestadores as p>
                        <div class="col-md-6 col-lg-4">
                            <div class="card prestador-card h-100 border-0 shadow-sm">
                                <div class="card-body p-4">
                                    <div class="d-flex align-items-center mb-3">
                                        <div class="avatar-placeholder me-3">
                                            ${p.usuario.nome?substring(0, 1)?upper_case}
                                        </div>
                                        <div>
                                            <h5 class="card-title fw-bold mb-0">${p.usuario.nome}</h5>
                                            <small class="text-muted"><i class="fas fa-star text-warning me-1"></i> 5.0 (Novo)</small>
                                        </div>
                                    </div>
			
									<#list p.categorias as cat>
	                                    <div class="category-badge">
	                                        <i class="fas fa-briefcase me-2"></i>${cat.nome!"Serviço Geral"}
	                                    </div>									
									</#list>

                                    <p class="card-text text-muted mb-4 text-truncate-2" style="height: 3rem; overflow: hidden;">
                                        ${p.descricao!"Sem descrição disponível para este profissional."}
                                    </p>

                                    <div class="d-grid">
                                        <a href="/prestadores/${p.id}" class="btn btn-outline-primary fw-bold">
                                            Ver Perfil Completo
                                        </a>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </#list>
                </div>
            <#else>
                <div class="text-center py-5">
                    <div class="mb-3 text-muted" style="font-size: 3rem;">
                        <i class="fas fa-search-minus"></i>
                    </div>
                    <h4 class="fw-bold">Nenhum prestador encontrado</h4>
                    <p class="text-muted">Tente mudar os filtros ou buscar em outra cidade.</p>
                    <a href="/pesquisa" class="btn btn-link">Limpar todos os filtros</a>
                </div>
            </#if>
        <#else>
        		<div class="text-center py-5">
                    <div class="mb-3 text-muted" style="font-size: 3rem;">
                        <i class="fa-solid fa-magnifying-glass fa-1x" style="opacity: 0.2;"></i>
                    </div>
                    <p class="text-muted">Use os filtros acima para encontrar o profissional que você precisa.</p>
                </div>
        </#if>
    </div>
</@layout.padrao>