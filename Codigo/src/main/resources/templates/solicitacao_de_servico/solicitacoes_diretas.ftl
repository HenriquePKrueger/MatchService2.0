<#import "../layout/main.ftl" as layout>

<@layout.padrao title="Solicitações Diretas - MatchService">
<style>
    .page-header {
        background: linear-gradient(135deg, #0d6efd, #0a58ca);
        color: white;
        border-radius: 22px;
        padding: 32px;
        box-shadow: 0 12px 28px rgba(13,110,253,.18);
    }

    .sol-card {
        border: 0;
        border-radius: 18px;
        box-shadow: 0 10px 24px rgba(0,0,0,.08);
    }

    .status-pill {
        border-radius: 999px;
        padding: 6px 12px;
        font-size: 13px;
        font-weight: 700;
    }

    .status-andamento {
        background: #e7f1ff;
        color: #0d6efd;
    }

    .status-cancelada {
        background: #fff0f0;
        color: #c92a2a;
    }
</style>

<div class="row justify-content-center">
    <div class="col-lg-11">
        <div class="page-header mb-4">
            <h1 class="fw-bold mb-2">
                <i class="fa-solid fa-calendar-check me-2"></i>
                Solicitações Diretas
            </h1>
            <p class="mb-0">
                Solicitações feitas diretamente pelos clientes no seu perfil.
            </p>
        </div>

        <#if solicitacoes?? && solicitacoes?size gt 0>
            <div class="row g-4">
                <#list solicitacoes as s>
                    <div class="col-md-6 col-lg-4">
                        <div class="card sol-card h-100">
                            <div class="card-body p-4">
                                <div class="d-flex justify-content-between align-items-start mb-3">
                                    <h5 class="fw-bold mb-0">${s.nomeCliente}</h5>

                                    <#if s.status == 0>
                                        <span class="status-pill status-andamento">Em andamento</span>
                                        <#elseif s.status == 2>
                                            <span class="status-pill status-cancelada">Cancelada pelo cliente</span>
                                        <#elseif s.status == 4>
                                                 <span class="status-pill status-cancelada">Cancelada por você</span>
                                        <#elseif s.status == 3>
                                            <span class="status-pill status-andamento">Concluída</span>
                                        </#if>
                                </div>

                                <p class="text-muted mb-3">
                                    ${s.descricao}
                                </p>

                                <p class="mb-2">
                                    <strong>Data:</strong>
                                    ${s.dataDisponivel?date("yyyy-MM-dd")?string("dd/MM/yyyy")}
                                </p>

                                <p class="mb-2">
                                    <strong>Horário:</strong>
                                    ${s.horarioInicio} - ${s.horarioFim}
                                </p>

                                <p class="mb-2">
                                    <strong>Telefone:</strong>
                                    ${s.telefoneCliente!""}
                                </p>

                                <p class="mb-3">
                                    <strong>Endereço:</strong>
                                    ${s.ruaCliente!""}, ${s.bairroCliente!""}, ${s.cidadeCliente!""}
                                </p>

                                <#if s.status == 0>

                                    <form method="post" action="/solicitacoes-diretas/${s.id}/concluir" class="mb-2">
                                        <button class="btn btn-success w-100">
                                         <i class="fa-solid fa-check me-1"></i>
                                            Concluir atendimento
                                        </button>
                                    </form>

                                    <form method="post" action="/solicitacoes-diretas/${s.id}/cancelar">
                                        <button class="btn btn-outline-danger w-100">
                                        <i class="fa-solid fa-ban me-1"></i>
                                        
                                            Cancelar solicitação
                                        </button>
                                    </form>

                                        <#elseif s.status == 2 || s.status == 4>

                                    <form method="post" action="/solicitacoes-diretas/${s.id}/excluir">
                                        <button class="btn btn-outline-danger w-100">
                                        <i class="fa-solid fa-xmark me-1"></i>
                                            Excluir da lista
                                        </button>
                                    </form>

                                    <#elseif s.status == 3>

                                        <button class="btn btn-secondary w-100" disabled>
                                        <i class="fa-solid fa-check-circle me-1"></i>
                                            Atendimento concluído
                                        </button>

                                    </#if>
                            </div>
                        </div>
                    </div>
                </#list>
            </div>
        <#else>
            <div class="card sol-card">
                <div class="card-body text-center py-5">
                    <i class="fa-regular fa-calendar-xmark fa-3x text-muted mb-3"></i>
                    <h4 class="fw-bold">Nenhuma solicitação direta</h4>
                    <p class="text-muted mb-0">
                       // Quando um cliente agendar diretamente pelo seu perfil, aparecerá aqui.
                    </p>
                </div>
            </div>
        </#if>
    </div>
</div>
</@layout.padrao>