CREATE TABLE IF NOT EXISTS ofertas (
	id SERIAL PRIMARY KEY,
	descricao TEXT NOT NULL,
	valor NUMERIC(10, 2) NOT NULL,
	disponibilidade VARCHAR(255),
	status INTEGER NOT NULL DEFAULT 0,
	justificativa_recusa TEXT,
	created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	solicitacoes_servicos_id INTEGER NOT NULL REFERENCES solicitacoes_servicos(id),
	prestadores_id INTEGER NOT NULL REFERENCES prestadores(id)
);

ALTER TABLE ofertas
ADD COLUMN IF NOT EXISTS disponibilidade VARCHAR(255);

ALTER TABLE ofertas DROP CONSTRAINT IF EXISTS ofertas_status_check;

ALTER TABLE ofertas
ADD CONSTRAINT ofertas_status_check
CHECK (status IN (0, 1, 2, 3));

DELETE FROM ofertas o
USING ofertas duplicada
WHERE o.solicitacoes_servicos_id = duplicada.solicitacoes_servicos_id
	AND o.prestadores_id = duplicada.prestadores_id
	AND o.id > duplicada.id;

ALTER TABLE ofertas DROP CONSTRAINT IF EXISTS ofertas_prestador_solicitacao_unique;

ALTER TABLE ofertas
ADD CONSTRAINT ofertas_prestador_solicitacao_unique
UNIQUE (solicitacoes_servicos_id, prestadores_id);

CREATE TABLE IF NOT EXISTS notificacoes (
	id SERIAL PRIMARY KEY,
	usuarios_id INTEGER NOT NULL REFERENCES usuarios(id),
	mensagem TEXT NOT NULL,
	lida BOOLEAN NOT NULL DEFAULT false,
	created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	ofertas_id INTEGER REFERENCES ofertas(id)
);
