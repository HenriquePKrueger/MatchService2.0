ALTER TABLE solicitacoes_imagens
DROP CONSTRAINT IF EXISTS solicitacoes_imagens_status_check;

ALTER TABLE solicitacoes_imagens
DROP COLUMN IF EXISTS status,
DROP COLUMN IF EXISTS motivo_rejeicao,
DROP COLUMN IF EXISTS adult_score,
DROP COLUMN IF EXISTS racy_score,
DROP COLUMN IF EXISTS gore_score,
DROP COLUMN IF EXISTS azure_raw_response,
DROP COLUMN IF EXISTS tags,
DROP COLUMN IF EXISTS descricao_ia;
