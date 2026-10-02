# Código do Projeto

## Moderação de imagens

Ao criar uma solicitação de serviço, as imagens enviadas são validadas localmente e analisadas pela API Azure Computer Vision Analyze Image v3.2 antes de serem salvas em `storage`.

Variáveis necessárias no `.env`:

```env
AZURE_COMPUTER_VISION_ENDPOINT=https://seu-recurso.cognitiveservices.azure.com
AZURE_COMPUTER_VISION_KEY=sua-chave
IMAGE_MODERATION_ENABLED=true
IMAGE_MODERATION_ADULT_THRESHOLD=0.70
IMAGE_MODERATION_RACY_THRESHOLD=0.70
IMAGE_MODERATION_GORE_THRESHOLD=0.70
IMAGE_MAX_SIZE_MB=5
```

Execute também o script `sql/solicitacoes_imagens_moderacao.sql` para criar/atualizar a tabela de imagens. Apenas imagens aprovadas são salvas em `storage` e registradas em `solicitacoes_imagens`; imagens rejeitadas ou com erro de análise bloqueiam a criação da solicitação e não geram registro de imagem.

### Testes rápidos

Há um smoke test sem JUnit em `src/test/java/service/ImageModerationSmokeTest.java`, cobrindo:

- imagem PNG válida;
- MIME type inválido;
- imagem acima de `IMAGE_MAX_SIZE_MB`;
- resposta da Azure marcando conteúdo adulto;
- falha da API para garantir comportamento fail-closed.

Para validação manual completa, com PostgreSQL e Azure configurados:

- criar solicitação com imagem aprovada e confirmar registro da imagem e arquivo em `storage`;
- criar solicitação com imagem rejeitada e confirmar que a solicitação não é criada;
- simular indisponibilidade da Azure e confirmar que a solicitação não é criada;
- abrir cards/detalhes da solicitação e confirmar que as imagens registradas aparecem normalmente.
