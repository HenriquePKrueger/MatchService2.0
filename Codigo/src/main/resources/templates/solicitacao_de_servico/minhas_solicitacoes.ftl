<#import "/layout/main.ftl" as layout>

<style>
    .request-card {
        background-color: #fff;
        border-radius: 12px;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
        border: 1px solid #eaeaea;
        transition: all 0.3s ease;
        margin-bottom: 20px;
        overflow: hidden;
    }

    .request-card:hover {
        box-shadow: 0 6px 16px rgba(0, 0, 0, 0.08);
        transform: translateY(-2px);
        border-color: #cce5ff;
    }

    .request-card-header {
        background-color: #fcfcfc;
        border-bottom: 1px solid #f0f0f0;
        padding: 16px 20px;
        display: flex;
        justify-content: space-between;
        align-items: center;
    }

    .request-card-body {
        padding: 20px;
    }

    .request-card-footer {
        padding: 16px 20px;
        background-color: #fff;
        border-top: 1px solid #f0f0f0;
        display: flex;
        justify-content: flex-end;
        gap: 10px;
    }

    .status-badge {
        padding: 6px 12px;
        border-radius: 20px;
        font-size: 0.85rem;
        font-weight: 600;
        display: inline-flex;
        align-items: center;
        gap: 6px;
    }

    .status-aberto {
        background-color: #e3f2fd;
        color: #0d6efd;
    }

    .status-andamento {
        background-color: #fff3cd;
        color: #856404;
    }

    .status-concluido {
        background-color: #d1e7dd;
        color: #0f5132;
    }

    .status-cancelado {
        background-color: #f8d7da;
        color: #842029;
    }

    .service-category {
        font-size: 1.1rem;
        font-weight: 700;
        color: #1a1a1a;
        margin-bottom: 4px;
    }

    .service-category i {
        color: #007bff;
        margin-right: 8px;
        width: 20px;
        text-align: center;
    }

    .service-date {
        font-size: 0.85rem;
        color: #6c757d;
    }

    .service-desc {
        color: #495057;
        font-size: 0.95rem;
        line-height: 1.5;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
        overflow: hidden;
        margin-bottom: 15px;
    }

    .service-location {
        font-size: 0.85rem;
        color: #6c757d;
        display: flex;
        align-items: center;
        gap: 6px;
    }
</style>

<#assign meusScripts>
<script>
    $(document).on("click", ".excluir", function() {
        const solicitacaoId = $(this).attr("solicitacao-id");

        Swal.fire({
            title: "Atenção!",
            icon: "warning",
            text: "Deseja cancelar/excluir essa solicitação de serviço?",
            showDenyButton: true,
            confirmButtonText: "Sim",
            denyButtonText: "Não"
        }).then((result) => {
            if (result.isConfirmed) {
                $.ajax({
                    method: "DELETE",
                    dataType: "json",
                    data: {
                        solicitacaoId: solicitacaoId
                    },
                    url: "/minhas-solicitacoes/" + solicitacaoId,
                    success: function(response) {
                        Swal.fire({
                            icon: "success",
                            title: "Sucesso!",
                            text: response.mensagem
                        });

                        location.reload();
                    },
                    error: function(response) {
                        Swal.fire({
                            icon: "error",
                            title: "Erro!",
                            text: response.mensagem
                        });
                    }
                });
            }
        });
    });
    
    $(document).on("click", ".avaliar-btn", function() {
    const solicitacaoId = $(this).attr("data-id");

    Swal.fire({
        title: 'Avaliar Serviço',
        html: `
            <select id="notaAvaliacao" class="swal2-select" style="width: 80%; font-size: 1rem;">
                <option value="5">5 Estrelas - Excelente</option>
                <option value="4">4 Estrelas - Muito Bom</option>
                <option value="3">3 Estrelas - Bom</option>
                <option value="2">2 Estrelas - Ruim</option>
                <option value="1">1 Estrela - Péssimo</option>
            </select>
            <textarea id="comentarioAvaliacao" class="swal2-textarea" placeholder="Como foi o serviço? (Opcional)" style="width: 80%;"></textarea>
        `,
        showCancelButton: true,
        confirmButtonText: 'Enviar',
        cancelButtonText: 'Cancelar',
        preConfirm: () => {
            return {
                nota: document.getElementById('notaAvaliacao').value,
                comentario: document.getElementById('comentarioAvaliacao').value
            }
        }
    }).then((result) => {
        if (result.isConfirmed) {
            $.ajax({
                method: "POST",
                dataType: "json",
                url: "/avaliar-servico",
                data: {
                    solicitacaoId: solicitacaoId,
                    nota: result.value.nota,
                    comentario: result.value.comentario
                },
                success: function(response) {
                    Swal.fire('Sucesso!', response.mensagem, 'success').then(() => location.reload());
                },
                error: function() {
                    Swal.fire('Erro!', 'Não foi possível enviar a avaliação.', 'error');
                }
            });
        }
    });
});
</script>
</#assign>

<@layout.padrao title="MatchService - Minhas Solicitações" scripts=meusScripts>
    <div class="container mt-5 mb-5">
        <div class="page-header">
            <div>
                <h2 class="fw-bold mb-1">Minhas Solicitações</h2>
                <p class="text-muted mb-0">Gerencie os serviços que você solicitou na plataforma.</p>
            </div>

            <a href="/criar-solicitacao" class="btn btn-primary shadow-sm" style="font-weight: 600; border-radius: 8px;">
                <i class="fas fa-plus me-2"></i> Nova Solicitação
            </a>
        </div>

        <div class="row mt-5">
            <#list solicitacoes as solicitacao>

                <div class="col-lg-6">
                    <div class="request-card">
                        <div class="request-card-header">
                            <#if solicitacao.tipoSolicitacao == 0>
                                <#if solicitacao.status == 0>
                                    <span class="status-badge status-aberto">
                                        <i class="fas fa-calendar-check"></i> Agendada
                                    </span>
                                <#elseif solicitacao.status == 2>
                                    <span class="status-badge status-cancelado">
                                        <i class="fas fa-ban"></i> Cancelada por você
                                    </span>
                                <#elseif solicitacao.status == 4>
                                    <span class="status-badge status-cancelado">
                                        <i class="fas fa-ban"></i> Cancelada pelo prestador
                                    </span>
                                <#elseif solicitacao.status == 3>
                                    <span class="status-badge status-concluido">
                                        <i class="fas fa-check-circle"></i> Concluída
                                    </span>
                                </#if>
                            <#else>
                                <#if solicitacao.status == 0>
                                    <span class="status-badge status-aberto">
                                        <i class="fas fa-search"></i> Aguardando Propostas
                                    </span>
                                <#elseif solicitacao.status == 1>
                                    <span class="status-badge status-andamento">
                                        <i class="fas fa-handshake"></i> Serviço em Andamento
                                    </span>
                                <#elseif solicitacao.status == 2>
                                    <span class="status-badge status-cancelado">
                                        <i class="fas fa-ban"></i> Cancelada
                                    </span>
                                <#elseif solicitacao.status == 3>
								   	<div class="d-flex align-items-center gap-2">
								        <span class="status-badge status-concluido">
								            <i class="fas fa-check-circle"></i> Concluída
								        </span>
								        <button class="btn btn-sm btn-outline-primary avaliar-btn" data-id="${solicitacao.id}">
								            <i class="fas fa-star"></i> Avaliar
								        </button>
								    </div>
								</#if>
                            </#if>
                        </div>

                        <div class="request-card-body">
                            <h4 class="service-category">${solicitacao.categoria.nome}</h4>

                            <p class="service-desc">
                                ${solicitacao.descricao}
                            </p>

                            <div class="service-location">
                                <i class="fas fa-map-marker-alt"></i>
                                ${solicitacao.rua} - ${solicitacao.bairro}
                            </div>
                        </div>

                        <div class="request-card-footer">
                                <#if solicitacao.tipoSolicitacao == 1>
                                    <a href="/solicitacoes/${solicitacao.id}/ofertas" class="btn btn-outline-primary">
                                <i class="fas fa-envelope-open-text me-1"></i> Ver ofertas recebidas
                                    </a>
                                </#if>

                            <#if solicitacao.status == 0>
                                <button class="btn btn-outline-danger excluir" solicitacao-id="${solicitacao.id}">
                                <i class="fas fa-trash-alt me-1"></i>
                            <#if solicitacao.tipoSolicitacao == 0>
                                Cancelar
                            <#else>
                                Excluir
                            </#if>
                            </button>

                            <#if solicitacao.tipoSolicitacao == 1>
                                <a href="/minhas-solicitacoes/${solicitacao.id}" class="btn btn-primary">
                                <i class="fas fa-edit me-1"></i> Ver / Editar
                                </a>
                            </#if>

                            <#elseif solicitacao.tipoSolicitacao == 0 && (solicitacao.status == 2 || solicitacao.status == 4)>
                            <button class="btn btn-outline-danger excluir" solicitacao-id="${solicitacao.id}">
                                <i class="fas fa-xmark me-1"></i> Excluir
                            </button>

                            <#else>
                                <button class="btn btn-secondary" disabled>
                                    <i class="fas fa-ban me-1"></i> Indisponível
                                </button>
                            </#if>
                        </div>
                    </div>
                </div>

            </#list>
        </div>
    </div>
</@layout.padrao>