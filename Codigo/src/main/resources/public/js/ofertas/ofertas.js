$(document).ready(function() {
    const radios = document.querySelectorAll('input[name="motivoRecusa"]');
    const caixaOutro = document.getElementById('caixaOutroMotivo');
    const radioOutro = document.getElementById('radioOutro');
    const textoJustificativa = document.getElementById('textoJustificativa');

    if (radioOutro.checked) {
        caixaOutro.style.display = 'block';
        setTimeout(() => {
            caixaOutro.style.opacity = '1';
            caixaOutro.style.height = 'auto';
            textoJustificativa.focus();
        }, 10);
    }

    radios.forEach(radio => {
        radio.addEventListener('change', function() {
            if (radioOutro.checked) {
                caixaOutro.style.display = 'block';
                setTimeout(() => {
                    caixaOutro.style.opacity = '1';
                    caixaOutro.style.height = 'auto';
                    textoJustificativa.focus();
                }, 10);
            } else {
                caixaOutro.style.opacity = '0';
                caixaOutro.style.height = '0';
                setTimeout(() => {
                    caixaOutro.style.display = 'none';
                    textoJustificativa.value = '';
                }, 300);
            }
        });
    });

    $(".btn-recusar").on("click", function(e) {
        e.preventDefault();

        let modalRecusar;
        const ofertaId = $(this).data("oferta-id");
        const ofertaValor = $(this).data("oferta-valor");
        const nomePrestador = $(this).data("nome-prestador");

        if (typeof bootstrap !== 'undefined') {

            if (!modalRecusar) {
                modalRecusar = new bootstrap.Modal(document.getElementById("modalRecusarOferta"));
            }

            modalRecusar.show();

            $("#valor").text(ofertaValor);
            $("#nome-prestador").text(nomePrestador.toUpperCase());
            $("#confirmar-recusa").attr("data-oferta-id", ofertaId);
        }
    });

    $(".btn-aceitar").on("click", function(e) {
        e.preventDefault();
        const ofertaId = $(this).data("oferta-id");

        Swal.fire({
            icon: "info",
            title: "Atenção:",
            text: "Deseja aceitar essa oferta?",
            showDenyButton: true,
            showConfirmButton: true,
            confirmButtonText: "Sim",
            denyButtonText: "Não"
        }).then((result) => {
            if (result.isConfirmed) {
                $.ajax({
                    url: `/ofertas/${ofertaId}/aceitar`,
                    method: 'POST',
                    success: function(data, textStatus, jqXHR) {
                        if (jqXHR.status == 200) {
                            Swal.fire({
                                icon: "success",
                                title: "Sucesso!",
                                text: "Oferta aceita!",
                                showDenyButton: false,
                                showConfirmButton: true,
                                confirmButtonText: "OK!"
                            });

                            setTimeout(function() {
                                window.location.reload();
                            }, 2000);
                        }
                    },
                    error: function(data, textStatus, jqXHR) {
                        if (jqXHR.status == 500) {
                            Swal.fire({
                                icon: "error",
                                title: "Erro!",
                                text: "Tivemos um problema ao processar a oferta. Tente novamente mais tarde",
                                showDenyButton: false,
                                showConfirmButton: true,
                                confirmButtonText: "OK!"
                            });

                            setTimeout(function() {
                                window.location.reload();
                            }, 2000);
                        }
                    }
                });
            }
        });
    });

    $(".btn-contatos").on("click", function(e) {
        e.preventDefault();

        const prestadorId = $(this).data("prestador-id");
		const valor = $(this).data("valor");
		
        $.ajax({
            method: "GET",
            url: "/contatos-prestador/" + prestadorId,
            dataType: "json",
            success: function(response) {
				
				const prest = response.prestador;
                Swal.fire({
                    icon: "success",
                    title: "Serviço confirmado!",
                    confirmButtonText: '<i class="fas fa-thumbs-up"></i> Ótimo!',
                    confirmButtonColor: "#198754",
                    html: `
				                        <div class="text-start">
				                            <p class="lead fs-6">
				                                Você confirmou o serviço com <strong>${prest.usuario.nome.toUpperCase()}</strong>.
				                            </p>

				                            <div class="alert alert-light border container-fluid">
				                                <div class="row mb-2">
				                                    <div class="col-5 text-muted">Valor:</div>
				                                    <div class="col-7 fw-bold text-success">R$ ${valor}</div>
				                                </div>
				                            </div>

				                            <div class="alert alert-success d-flex align-items-center" role="alert">
				                                <i class="fab fa-whatsapp fa-2x me-3"></i>
				                                <div>
				                                    <small class="d-block">Entre em contato agora:</small>
				                                    <strong class="fs-5">${prest.usuario.telefone}</strong>
				                                </div>
				                            </div>

				                            <p class="small text-muted text-center mt-3">
				                                <i class="fas fa-info-circle"></i> O prestador também foi notificado.
				                            </p>
				                        </div>
				                    `,
                })
            },
            error: function(response) {
                console.log("erro");
            }
        });
    });

});

function confirmarRecusa(btn) {
    const ofertaId = btn.getAttribute("data-oferta-id");

    const motivoSelecionado = document.querySelector('input[name="motivoRecusa"]:checked');

    const motivo = motivoSelecionado.value;
    const detalhes = document.getElementById('textoJustificativa').value;

    console.log("Oferta recusada. Motivo:", motivo, "| Detalhes:", detalhes);

    $.ajax({
        url: `/ofertas/${ofertaId}/recusar`,
        method: 'POST',
        data: {
            justificativa_recusa: motivo,
            detalhes: detalhes,
        },
        success: function(data, textStatus, jqXHR) {
            if (jqXHR.status == 200) {
                Swal.fire({
                    icon: "success",
                    title: "Sucesso!",
                    text: "Solicitação recusada!",
                    showDenyButton: false,
                    showConfirmButton: true,
                    confirmButtonText: "OK!"
                });

                setTimeout(function() {
                    window.location.reload();
                }, 2000);
            }
        },
        error: function(data, textStatus, jqXHR) {
            if (jqXHR.status == 500) {
                Swal.fire({
                    icon: "error",
                    title: "Erro!",
                    text: "Tivemos um problema ao processar sua recusa. Tente novamente mais tarde",
                    showDenyButton: false,
                    showConfirmButton: true,
                    confirmButtonText: "OK!"
                });

                setTimeout(function() {
                    window.location.reload();
                }, 2000);
            }
        }
    });

    const modalElement = document.getElementById('modalRecusarOferta');
    const modalInstance = bootstrap.Modal.getInstance(modalElement);
    modalInstance.hide();
}



