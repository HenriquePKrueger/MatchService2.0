document.addEventListener('DOMContentLoaded', function() {

    let detalhesModal;

    // Seleciona todos os botões de "Ver detalhes"
    const botoesDetalhes = document.querySelectorAll('.btn-ver-detalhes');

    botoesDetalhes.forEach(botao => {
        botao.addEventListener('click', function() {
            const solicitacaoId = this.getAttribute('data-id');

            // Inicialização segura do Modal do Bootstrap
            if (typeof bootstrap !== 'undefined') {
                if (!detalhesModal) {
                    detalhesModal = new bootstrap.Modal(document.getElementById('modalDetalhesSolicitacao'));
                }
                detalhesModal.show();
            } else {
                console.error("Erro: O Bootstrap não foi carregado.");
                return;
            }

            // Reseta o modal: Mostra o loading, esconde o conteúdo
            document.getElementById('modalLoading').classList.remove('d-none');
            document.getElementById('modalContent').classList.add('d-none');
            document.getElementById('modalId').innerText = "#" + solicitacaoId;

            $.ajax({
                method: 'GET',
                url: '/solicitacoes-disponiveis/detalhes/' + solicitacaoId,
                dataType: 'json',
                success: function(response) {
                    const local = `${response.rua} - ${response.bairro}`;
                    $("#modalEnderecoText").text(local);
                    $("#modalDescricao").text(response.descricao);
                    $("#modalCategoria").text(response.categoria.nome);

                    const imagensObj = response.solicitacaoImagens;
					$("#modalFotos").empty();
                    imagensObj.forEach(({ url }) => {
                        const img = `<img src="/${url}" onclick="ampliarImagem(this.src)" class="img-thumbnail rounded" style="width: 100px; height: 100px; object-fit: cover; cursor: pointer;" alt="Foto">`;
                        $("#modalFotos").append(img);
                    });

                    //console.log(response);
                    document.getElementById('modalLoading').classList.add('d-none');
                    document.getElementById('modalContent').classList.remove('d-none');
                },
                error: function(response) {
                    console.error('Erro:', error);
                    document.getElementById('modalLoading').classList.add('d-none');
                    document.getElementById('modalContent').innerHTML = `
                        <div class="alert alert-danger">
                            <i class="fas fa-exclamation-triangle me-2"></i> Não foi possível carregar os detalhes desta solicitação.
                        </div>
                    `;
                    document.getElementById('modalContent').classList.remove('d-none');
                },
            });

        });
    });
});

function ampliarImagem(src) {

    document.getElementById('fullSizeImage').src = src;

    const imageModal = new bootstrap.Modal(document.getElementById('imageViewerModal'));
    imageModal.show();
}
