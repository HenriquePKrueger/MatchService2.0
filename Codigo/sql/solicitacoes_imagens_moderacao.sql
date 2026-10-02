CREATE TABLE IF NOT EXISTS solicitacoes_imagens (
	id SERIAL PRIMARY KEY,
	url TEXT,
	solicitacoes_servicos_id INTEGER NOT NULL REFERENCES solicitacoes_servicos(id) ON DELETE CASCADE,
	nome_original VARCHAR(255),
	content_type VARCHAR(100),
	tamanho_bytes BIGINT DEFAULT 0,
	created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE solicitacoes_imagens
ADD COLUMN IF NOT EXISTS nome_original VARCHAR(255);

ALTER TABLE solicitacoes_imagens
ADD COLUMN IF NOT EXISTS content_type VARCHAR(100);

ALTER TABLE solicitacoes_imagens
ADD COLUMN IF NOT EXISTS tamanho_bytes BIGINT DEFAULT 0;

ALTER TABLE solicitacoes_imagens
ADD COLUMN IF NOT EXISTS created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE solicitacoes_imagens
ALTER COLUMN solicitacoes_servicos_id SET NOT NULL;
