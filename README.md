# 🚗 Sistema de Aluguel de Carros

> Sistema web para apoio à gestão de aluguéis de automóveis: clientes introduzem, modificam, consultam e cancelam pedidos; agentes (empresas e bancos) modificam, avaliam financeiramente e executam contratos — com contratos de crédito concedidos por bancos.

**Disciplina:** Desenvolvimento e Integração de Aplicações Web (DIAW) — Laboratório 02 · Engenharia de Software · PUC Minas
**Professor:** João Paulo Carneiro Aramuni

---

## 📚 Índice
- [Sobre o projeto](#-sobre-o-projeto)
- [Funcionalidades](#-funcionalidades)
- [Tecnologias](#-tecnologias)
- [Arquitetura](#-arquitetura)
- [Modelagem (UML)](#-modelagem-uml)
- [Estrutura de diretórios](#-estrutura-de-diretórios)
- [Instalação e execução](#-instalação-e-execução)
- [API REST](#-api-rest)
- [Regras de negócio](#-regras-de-negócio)
- [Autores](#-autores)

## 📝 Sobre o projeto
O sistema só pode ser utilizado após cadastro prévio e autenticação (usuário e senha). Os pedidos introduzidos pelos clientes são analisados do ponto de vista financeiro pelos agentes e, em caso de parecer positivo, ficam disponíveis para execução do contrato. Dependendo do tipo de contrato, o automóvel é registrado como propriedade do cliente, da empresa ou do banco, e o aluguel pode ser associado a um contrato de crédito concedido por um banco agente.

## ✨ Funcionalidades
- 🔐 Cadastro e login (clientes e agentes) com senha BCrypt e token JWT
- 👤 CRUD de cliente (RG, CPF, nome, endereço, profissão, até 3 rendimentos com entidade empregadora)
- 🚘 CRUD de automóveis (matrícula, ano, marca, modelo, placa) pelos agentes
- 📄 Pedidos: criar, consultar status, modificar e cancelar (cliente)
- 🏦 Avaliação financeira com parecer, modificação e execução de contrato (agente)
- 💳 Contrato de crédito bancário, definição do proprietário do automóvel
- 📱 Interface responsiva

## 🛠 Tecnologias
| Camada | Tecnologia |
|---|---|
| Linguagem | Java — JDK 25 |
| Back-end | Spring Boot 4.0.x (Web MVC, Data JPA, Security, Validation) |
| Build | Apache Maven |
| Front-end | React 19 + Vite 7 + React Router |
| Banco de dados | Supabase (PostgreSQL) |
| Autenticação | Usuário e senha · BCrypt · JWT (jjwt) |

## 🏛 Arquitetura
Arquitetura **MVC** em camadas, dividida nos dois subsistemas do enunciado:
- **Gestão de pedidos e contratos** (`backend/`): `controller` (C) → `service` (regras) → `repository` → `model` (M), com DTOs na fronteira.
- **Construção dinâmica das páginas web** (`frontend/`): a camada de visão (V) em React consome a API REST.

```
Navegador (React) ──HTTP/JSON + JWT──▶ Spring Boot (Controller → Service → Repository) ──JPA──▶ Supabase PostgreSQL
```

## 📐 Modelagem (UML)
Arquivos PlantUML em [`docs/uml`](docs/uml):
- [Diagrama de Casos de Uso](docs/uml/casos-de-uso.puml)
- [Diagrama de Classes](docs/uml/classes.puml)
- [Diagrama de Pacotes (Visão Lógica)](docs/uml/pacotes.puml)
- [Histórias de Usuário](docs/historias-de-usuario.md)

Para gerar as imagens: `java -jar plantuml.jar docs/uml/*.puml` (ou extensão PlantUML do VS Code).

## 📁 Estrutura de diretórios
```
├── backend/                      # API Spring Boot (Maven)
│   └── src/main/java/br/com/aluguelcarros/
│       ├── config/               # Segurança, CORS
│       ├── controller/           # Endpoints REST
│       ├── dto/                  # Objetos de entrada/saída
│       ├── exception/            # Erros e handler global
│       ├── model/                # Entidades JPA
│       ├── repository/           # Spring Data JPA
│       ├── security/             # JWT e UserDetails
│       └── service/              # Regras de negócio
├── frontend/                     # React + Vite
│   └── src/{api,components,context,pages}
├── database/schema.sql           # DDL PostgreSQL
└── docs/                         # UML e histórias de usuário
```

## ⚙️ Instalação e execução

### Pré-requisitos
JDK 25, Maven 3.9+, Node.js 20+ e um projeto no [Supabase](https://supabase.com).

### 1. Banco de dados (Supabase)
1. Crie um projeto no Supabase e copie a connection string em **Project Settings → Database** (Session pooler, porta 5432).
2. (Opcional) Execute `database/schema.sql` no SQL Editor. Caso contrário, o Hibernate cria as tabelas na primeira execução.

### 2. Back-end
```bash
cd backend
export DB_URL="jdbc:postgresql://<host>:5432/postgres?sslmode=require"
export DB_USERNAME="postgres.<project-ref>"
export DB_PASSWORD="<senha>"
export JWT_SECRET="<chave aleatória com 32+ caracteres>"
export CORS_ORIGINS="http://localhost:5173"
mvn spring-boot:run          # API em http://localhost:8080/api
```
Produção: `mvn clean package && java -jar target/aluguel-carros-1.0.0.jar` (recomenda-se `DDL_AUTO=validate` após criar o schema).

### Dados de demonstração
Na primeira execução (banco sem usuários), o back-end cadastra automaticamente dados de exemplo
(classe `config/DataSeeder`): 2 agentes, 3 clientes com rendimentos, 8 automóveis, 7 pedidos em
todos os status (pendente, aprovado, reprovado, cancelado e contratado) e 1 contrato com crédito bancário.
Para desativar, use `export APP_SEED_ENABLED=false`.

Todos os usuários usam a senha **`senha123`**:

| Usuário    | Perfil          | O que aparece                                        |
|------------|-----------------|------------------------------------------------------|
| `joao`     | Cliente         | 2 pedidos (pendente e aprovado com crédito)          |
| `maria`    | Cliente         | 2 pedidos (pendente e contratado)                    |
| `pedro`    | Cliente         | 3 pedidos (pendente, reprovado e cancelado)          |
| `locadora` | Agente (empresa)| Todos os pedidos para avaliar; cadastro de automóveis|
| `banco`    | Agente (banco)  | Todos os pedidos; concessão de contratos de crédito  |

### 3. Front-end
```bash
cd frontend
cp .env.example .env         # VITE_API_URL=http://localhost:8080/api
npm install
npm run dev                  # http://localhost:5173
npm run build                # build de produção em dist/
```

## 🔌 API REST
| Método | Rota | Acesso | Descrição |
|---|---|---|---|
| POST | `/api/auth/login` | público | Login (retorna JWT) |
| POST | `/api/auth/registrar/agente` | público | Cadastro de agente |
| GET | `/api/auth/me` | autenticado | Usuário logado |
| POST | `/api/clientes` | público | Cadastro de cliente |
| GET | `/api/clientes` | agente | Listar clientes |
| GET/PUT/DELETE | `/api/clientes/{id}` | próprio cliente (GET também agente) | Consultar / atualizar / excluir |
| GET | `/api/automoveis` | autenticado | Listar automóveis |
| POST/PUT/DELETE | `/api/automoveis[/{id}]` | agente | Manter automóveis |
| GET | `/api/pedidos` | autenticado | Cliente: seus pedidos · Agente: todos |
| POST | `/api/pedidos` | cliente | Introduzir pedido |
| PUT | `/api/pedidos/{id}` | cliente/agente | Modificar pedido |
| PATCH | `/api/pedidos/{id}/cancelar` | cliente | Cancelar |
| PATCH | `/api/pedidos/{id}/avaliar` | agente | Parecer financeiro |
| POST | `/api/pedidos/{id}/contrato` | agente | Executar contrato (+ crédito) |

## 📏 Regras de negócio
- Fluxo de status: `PENDENTE → APROVADO | REPROVADO`, `APROVADO → CONTRATADO`; cliente cancela em `PENDENTE`/`APROVADO`.
- Cliente modifica somente pedidos `PENDENTE`; agente modifica qualquer pedido não contratado/cancelado.
- Máximo de 3 rendimentos por cliente; CPF, RG, CNPJ, placa e matrícula únicos.
- Pedido com crédito só pode ser contratado por um **banco**, que registra valor, juros e parcelas.
- Proprietário do automóvel: cliente, ou o próprio agente contratante (empresa ou banco).

## 👥 Autor
Michael William Tang
