<#import "../layout/main.ftl" as layout>

<@layout.padrao title="Minha Agenda - MatchService">
<style>
    .agenda-header {
        background: #0d6efd;
        color: white;
        border-radius: 22px;
        padding: 32px;
        box-shadow: 0 12px 28px rgba(13, 110, 253, .18);
    }

    .agenda-card {
        border: 0;
        border-radius: 20px;
        box-shadow: 0 10px 26px rgba(0,0,0,.08);
    }

    .slot-card {
        border: 1px solid #e8edf3;
        border-radius: 16px;
        padding: 16px;
        background: white;
        transition: .2s;
    }

    .slot-card:hover {
        transform: translateY(-2px);
        box-shadow: 0 8px 22px rgba(0,0,0,.08);
    }

    .status-livre {
        background: #e8fff5;
        color: #087f5b;
    }

    .status-ocupado {
        background: #fff0f0;
        color: #c92a2a;
    }

    .status-pill {
        padding: 6px 10px;
        border-radius: 999px;
        font-size: 13px;
        font-weight: 600;
    }

    .status-concluido {
    background: #e7f5ff;
    color: #1864ab;
}
</style>

<div class="row justify-content-center">
    <div class="col-lg-11">

        <div class="agenda-header mb-4">
            <h1 class="fw-bold mb-2">
                <i class="fa-solid fa-calendar-days me-2"></i>Minha Agenda
            </h1>
            <p class="mb-0">
                Cadastre seus horários disponíveis para que clientes possam escolher um atendimento.
            </p>
        </div>

        <#if erro??>
            <div class="alert alert-danger">${erro}</div>
        </#if>

        <#if sucesso??>
            <div class="alert alert-success">${sucesso}</div>
        </#if>

        <div class="card agenda-card mb-4">
            <div class="card-body p-4">
                <h4 class="fw-bold mb-3">Adicionar horário disponível</h4>

                <form method="post" action="/agenda" class="row g-3">
                    <div class="col-md-4">
                        <label class="form-label">Data</label>
                        <input type="date" name="data" class="form-control" required>
                    </div>

                    <div class="col-md-3">
                        <label class="form-label">Início</label>
                        <input type="time" name="inicio" class="form-control" required>
                    </div>

                    <div class="col-md-3">
                        <label class="form-label">Fim</label>
                        <input type="time" name="fim" class="form-control" required>
                    </div>

                    <div class="col-md-2 d-flex align-items-end">
                        <button class="btn btn-primary w-100">
                            <i class="fa-solid fa-plus me-1"></i>Adicionar
                        </button>
                    </div>
                </form>
            </div>
        </div>

        <div class="card agenda-card">
            <div class="card-body p-4">
                <h4 class="fw-bold mb-4">Horários cadastrados</h4>

                <#if horarios?? && horarios?size gt 0>
                    <div class="row g-3">
                        <#list horarios as h>
                            <div class="col-md-6 col-lg-4">
                                <div class="slot-card">
                                    <div class="d-flex justify-content-between align-items-start mb-3">
                                        <div>
                                            <div class="fw-bold fs-5">${h.dataDisponivel}</div>
                                            <div class="text-muted">
                                                ${h.horarioInicio} até ${h.horarioFim}
                                            </div>
                                        </div>

                                       <#if h.statusHorario == 0>
                                            <span class="status-pill status-livre">Livre</span>
                                        <#elseif h.statusHorario == 1>
                                             <span class="status-pill status-ocupado">Ocupado</span>
                                        <#elseif h.statusHorario == 2>
                                            <span class="status-pill status-concluido">Concluído</span>
                                        </#if>
                                    </div>

                                    <#if h.statusHorario == 0>
    <form method="post" action="/agenda/remover/${h.id}">
        <button class="btn btn-outline-danger btn-sm w-100">
            <i class="fa-solid fa-trash me-1"></i>Remover
        </button>
    </form>
<#elseif h.statusHorario == 1>
    <button class="btn btn-secondary btn-sm w-100" disabled>
        Horário reservado
    </button>
<#elseif h.statusHorario == 2>
    <button class="btn btn-primary btn-sm w-100" disabled>
        Atendimento concluído
    </button>
</#if>
                                </div>
                            </div>
                        </#list>
                    </div>
                <#else>
                    <div class="text-center text-muted py-5">
                        <i class="fa-regular fa-calendar-xmark fa-3x mb-3"></i>
                        <p class="mb-0">Você ainda não cadastrou horários.</p>
                    </div>
                </#if>
            </div>
        </div>
    </div>
</div>
</@layout.padrao>