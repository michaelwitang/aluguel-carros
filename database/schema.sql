-- Esquema PostgreSQL (Supabase). Executar no SQL Editor do Supabase
-- ou deixar o Hibernate criar automaticamente (DDL_AUTO=update).
CREATE TABLE IF NOT EXISTS usuarios (
  id BIGSERIAL PRIMARY KEY,
  username VARCHAR(60) NOT NULL UNIQUE,
  senha VARCHAR(255) NOT NULL,
  nome VARCHAR(120) NOT NULL,
  perfil VARCHAR(20) NOT NULL CHECK (perfil IN ('CLIENTE','AGENTE'))
);
CREATE TABLE IF NOT EXISTS clientes (
  id BIGINT PRIMARY KEY REFERENCES usuarios(id) ON DELETE CASCADE,
  rg VARCHAR(20) NOT NULL UNIQUE,
  cpf VARCHAR(14) NOT NULL UNIQUE,
  endereco VARCHAR(255) NOT NULL,
  profissao VARCHAR(80) NOT NULL
);
CREATE TABLE IF NOT EXISTS rendimentos (
  id BIGSERIAL PRIMARY KEY,
  cliente_id BIGINT NOT NULL REFERENCES clientes(id) ON DELETE CASCADE,
  entidade_empregadora VARCHAR(120) NOT NULL,
  valor NUMERIC(12,2) NOT NULL CHECK (valor >= 0)
);
CREATE TABLE IF NOT EXISTS agentes (
  id BIGINT PRIMARY KEY REFERENCES usuarios(id) ON DELETE CASCADE,
  cnpj VARCHAR(18) NOT NULL UNIQUE,
  tipo VARCHAR(20) NOT NULL CHECK (tipo IN ('EMPRESA','BANCO'))
);
CREATE TABLE IF NOT EXISTS automoveis (
  id BIGSERIAL PRIMARY KEY,
  matricula VARCHAR(30) NOT NULL UNIQUE,
  ano INTEGER NOT NULL,
  marca VARCHAR(60) NOT NULL,
  modelo VARCHAR(60) NOT NULL,
  placa VARCHAR(10) NOT NULL UNIQUE,
  tipo_proprietario VARCHAR(20) NOT NULL DEFAULT 'EMPRESA' CHECK (tipo_proprietario IN ('CLIENTE','EMPRESA','BANCO')),
  proprietario_id BIGINT REFERENCES usuarios(id)
);
CREATE TABLE IF NOT EXISTS pedidos (
  id BIGSERIAL PRIMARY KEY,
  cliente_id BIGINT NOT NULL REFERENCES clientes(id),
  automovel_id BIGINT NOT NULL REFERENCES automoveis(id),
  data_inicio DATE NOT NULL,
  data_fim DATE NOT NULL,
  valor_mensal NUMERIC(12,2) NOT NULL,
  solicita_credito BOOLEAN NOT NULL DEFAULT FALSE,
  status VARCHAR(20) NOT NULL CHECK (status IN ('PENDENTE','APROVADO','REPROVADO','CANCELADO','CONTRATADO')),
  agente_avaliador_id BIGINT REFERENCES agentes(id),
  parecer VARCHAR(500),
  criado_em TIMESTAMP NOT NULL DEFAULT now(),
  atualizado_em TIMESTAMP NOT NULL DEFAULT now(),
  CHECK (data_fim > data_inicio)
);
CREATE TABLE IF NOT EXISTS contratos (
  id BIGSERIAL PRIMARY KEY,
  pedido_id BIGINT NOT NULL UNIQUE REFERENCES pedidos(id),
  agente_id BIGINT NOT NULL REFERENCES agentes(id),
  tipo_proprietario VARCHAR(20) NOT NULL,
  data_assinatura TIMESTAMP NOT NULL DEFAULT now()
);
CREATE TABLE IF NOT EXISTS contratos_credito (
  id BIGSERIAL PRIMARY KEY,
  contrato_id BIGINT NOT NULL UNIQUE REFERENCES contratos(id),
  banco_id BIGINT NOT NULL REFERENCES agentes(id),
  valor NUMERIC(12,2) NOT NULL,
  taxa_juros_mensal NUMERIC(5,2) NOT NULL,
  parcelas INTEGER NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_pedidos_cliente ON pedidos(cliente_id);
CREATE INDEX IF NOT EXISTS idx_pedidos_status ON pedidos(status);
