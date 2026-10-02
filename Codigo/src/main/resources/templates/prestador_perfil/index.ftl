<#import "../layout/main.ftl" as layout>

<@layout.padrao title="Perfil do Prestador - MatchService">
<style>
    .perfil-card {
        border: 0;
        border-radius: 22px;
        box-shadow: 0 10px 26px rgba(0,0,0,.08);
        overflow: hidden;
    }

    .perfil-header {
        background: linear-gradient(135deg, #0d6efd, #0a58ca);
        color: white;
        padding: 32px;
    }

    .avatar-prestador {
        width: 88px;
        height: 88px;
        border-radius: 50%;
        background: white;
        color: #0d6efd;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 36px;
        font-weight: 800;
    }

    .agenda-box {
        border-radius: 22px;
        overflow: hidden;
        box-shadow: 0 10px 26px rgba(0,0,0,.08);
        border: 0;
    }

    .agenda-title {
        background: #0d6efd;
        color: white;
        padding: 22px 26px;
        font-weight: 800;
        font-size: 22px;
    }

    .horario-btn {
        border-radius: 999px;
        padding: 9px 18px;
        font-weight: 700;
        margin: 6px;
    }

    .data-box {
        border-bottom: 1px solid #e9ecef;
        padding: 18px 24px;
    }

    .categoria-pill {
        background: #e7f1ff;
        color: #0d6efd;
        padding: 8px 14px;
        border-radius: 999px;
        font-weight: 700;
        font-size: 14px;
    }

    .step-dot {
        width: 26px;
        height: 26px;
        border-radius: 50%;
        background: #0d6efd;
        color: white;
        display: inline-flex;
        align-items: center;
        justify-content: center;
        font-size: 13px;
        margin-right: 8px;
    }
</style>

<#if prestador??>
<div class="row g-4">
        <div class="col-lg-7">
            
            <div class="card perfil-card">
                <div class="perfil-header">
                    <div class="d-flex align-items-center gap-4">
                        <div class="avatar-prestador">
                            ${prestador.nome?substring(0,1)?upper_case}
                        </div>

                        <div>
                            <h2 class="fw-bold mb-2">${prestador.nome}</h2>
                            <p class="mb-1">
                                <i class="fa-solid fa-location-dot me-2"></i>
                                ${prestador.bairro!""} - ${prestador.cidade!""}
                            </p>
                            <p class="mb-0">
                                <i class="fa-solid fa-screwdriver-wrench me-2"></i>
                                ${prestador.categorias}
                            </p>
                        </div>
                    </div>
                </div>

                <div class="card-body p-4">
                    <h4 class="fw-bold mb-3">Sobre o prestador</h4>

                    <p class="text-muted">
                        <#if prestador.descricao?? && prestador.descricao?length gt 0>
                            ${prestador.descricao}
                        <#else>
                            Este prestador ainda não adicionou uma descrição ao perfil.
                        </#if>
                    </p>

                    <hr>

                    <h5 class="fw-bold mb-3">Informações</h5>

                    <p class="mb-2">
                        <strong>Telefone:</strong> ${prestador.telefone!""}
                    </p>

                    <p class="mb-2">
                        <strong>Endereço:</strong> ${prestador.rua!""}, ${prestador.bairro!""}, ${prestador.cidade!""}
                    </p>

                    <div class="mt-3">
                        <span class="categoria-pill">${prestador.categorias}</span>
                    </div>
                </div>
            </div>

            <div class="card perfil-card mt-4 mb-4">
                <div class="card-body p-4">
                    <h4 class="fw-bold mb-4"><i class="fa-solid fa-star text-warning me-2"></i>Últimas Avaliações</h4>

                    <#if avaliacoes?? && avaliacoes?size gt 0>
                        <div class="d-flex flex-column gap-3">
                            <#list avaliacoes as avaliacao>
                                <div class="p-3 bg-light rounded border border-light">
                                    <div class="d-flex justify-content-between align-items-center mb-2">
                                        <h6 class="fw-bold mb-0">
                                            <i class="fa-solid fa-circle-user text-primary me-2"></i>${avaliacao.nomeCliente!"Cliente"}
                                        </h6>
                                        <span class="text-warning small">
                                            <#list 1..5 as i>
                                                <#if i <= avaliacao.nota>
                                                    <i class="fa-solid fa-star"></i>
                                                <#else>
                                                    <i class="fa-regular fa-star"></i>
                                                </#if>
                                            </#list>
                                        </span>
                                    </div>
                                    <p class="mb-0 text-muted small">${avaliacao.comentario!"Nenhum comentário adicionado."}</p>
                                </div>
                            </#list>
                        </div>
                    <#else>
                        <div class="alert alert-light border mb-0 text-center">
                            <p class="text-muted mb-0">Este prestador ainda não possui avaliações.</p>
                        </div>
                    </#if>
                </div>
            </div>

        </div> <div class="col-lg-5">
            <div class="card agenda-box">
                <div class="agenda-title">
                    <i class="fa-solid fa-calendar-check me-2"></i>
                    Agendar atendimento
                </div>

                <div class="card-body p-0">
                    <form id="formSolicitacaoDireta">
                        <input type="hidden" name="prestador_id" value="${prestador.prestadorId}">
                        <input type="hidden" name="agenda_id" id="agendaIdSelecionado">

                        <div class="data-box">
                            <p class="fw-bold mb-2">
                                <span class="step-dot">1</span>
                                Prestador selecionado
                            </p>
                            <p class="text-muted mb-0">${prestador.nome}</p>
                        </div>

                        <div class="data-box">
                            <p class="fw-bold mb-2">
                                <span class="step-dot">2</span>
                                Descreva o problemática
                            </p>

                            <textarea 
                                class="form-control" 
                                name="descricao"
                                id="descricaoProblema"
                                rows="4" 
                                placeholder="Ex: Preciso consertar um vazamento na cozinha..."
                                required></textarea>
                        </div>

                        <div class="data-box">
                            <p class="fw-bold mb-3">
                                <span class="step-dot">3</span>
                                Horários disponíveis
                            </p>

                            <#if prestador.horariosLivres?? && prestador.horariosLivres?size gt 0>
                                <#list prestador.horariosLivres as h>
                                    <button 
                                        type="button"
                                        class="btn btn-outline-primary horario-btn"
                                        data-agenda-id="${h.id}">
                                        ${h.dataDisponivel} · ${h.horarioInicio} - ${h.horarioFim}
                                    </button>
                                </#list>
                            <#else>
                                <div class="alert alert-light border mb-0">
                                    Este prestador não possui horários livres no momento.
                                </div>
                            </#if>
                        </div>

                        <div class="p-4">
                            <button type="submit" class="btn btn-primary w-100 btn-lg" id="btnSolicitar" disabled>
                                Solicitar serviço
                            </button>
                            <small class="text-muted d-block mt-2 text-center">
                                Selecione um horário e descreva o problema para enviar a solicitação.
                            </small>
                        </div>
                    </form>
                </div>
            </div>
        </div>
	</div>
<#else>
    <div class="alert alert-danger">
        Prestador não encontrado.
    </div>
</#if>

<script>
let horarioSelecionado = null;

document.querySelectorAll(".horario-btn").forEach(btn => {
    btn.addEventListener("click", () => {
        document.querySelectorAll(".horario-btn").forEach(b => {
            b.classList.remove("btn-primary");
            b.classList.add("btn-outline-primary");
        });

        btn.classList.remove("btn-outline-primary");
        btn.classList.add("btn-primary");

        horarioSelecionado = btn.dataset.agendaId;
        document.getElementById("agendaIdSelecionado").value = horarioSelecionado;
        document.getElementById("btnSolicitar").disabled = false;
    });
});

const form = document.getElementById("formSolicitacaoDireta");

if (form) {
    form.addEventListener("submit", async (e) => {
        e.preventDefault();

        const descricao = document.getElementById("descricaoProblema").value.trim();

        if (!descricao || !horarioSelecionado) {
            Swal.fire("Atenção", "Descreva o problema e selecione um horário.", "warning");
            return;
        }

        const confirmacao = await Swal.fire({
            title: "Confirmar solicitação?",
            text: "O horário será reservado na agenda do prestador.",
            icon: "question",
            showCancelButton: true,
            confirmButtonText: "Confirmar",
            cancelButtonText: "Cancelar"
        });

        if (!confirmacao.isConfirmed) return;

        const formData = new URLSearchParams(new FormData(form));

        const response = await fetch("/solicitacoes-diretas", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            },
            body: formData
        });

        let json;

        try {
            json = await response.json();
        } catch (e) {
            Swal.fire("Erro", "O servidor não retornou uma resposta JSON válida.", "error");
            return;
        }

        if (json.sucesso) {
            await Swal.fire("Sucesso!", json.mensagem, "success");
            window.location.reload();
        } else {
            Swal.fire("Erro", json.mensagem, "error");
        }
    });
}
</script>
</@layout.padrao>