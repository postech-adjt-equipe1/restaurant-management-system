# Restaurante — Gestão de Usuários

> **POSTECH FIAP — Arquitetura e Desenvolvimento Java — Tech Challenge Fase 1**

Backend em Spring Boot para cadastro e gerenciamento de usuários de um sistema de restaurantes.

---

## Visão Geral

| Item | Detalhe |
|------|---------|
| Linguagem | Java 17 |
| Framework | Spring Boot 3.2 |
| Banco de dados | PostgreSQL 16 |
| Migrations | Flyway |
| Documentação | Swagger / OpenAPI 3 |
| Containerização | Docker + Docker Compose |

### Tipos de Usuário

- **OWNER** — Dono de Restaurante
- **CUSTOMER** — Cliente

### Funcionalidades

- Cadastro de usuário (com criptografia BCrypt e validação de e-mail único)
- Busca por ID e busca por nome (parcial, case-insensitive)
- Atualização de dados e exclusão (CRUD completo)
- Troca de senha (endpoint separado)
- Validação de login
- Versionamento de API (`/api/v1`)
- Tratamento de erros padronizado com ProblemDetail (RFC 7807)

---

## Arquitetura em Camadas

```
br.com.fiap.restaurante
├── config/        # Configurações (Security, OpenAPI)
├── controller/    # Endpoints REST
├── dto/           # Objetos de transferência (Request / Response)
├── exception/     # Exceções customizadas e GlobalExceptionHandler
├── model/         # Entidades JPA (User, Address, enums)
├── repository/    # Interfaces Spring Data JPA
└── service/       # Regras de negócio
```

---

## Pré-requisitos

- Docker >= 24
- Docker Compose >= 2.20
- (Opcional para dev local) Java 17 + Maven 3.9

---

## Como rodar com Docker

```bash
# 1. Clone o repositório
git clone <url-do-repositorio>
cd restaurante-usuarios

# 2. Suba os containers (build + banco de dados)
docker compose up --build

# 3. Acesse a API
# Swagger UI:  http://localhost:8080/swagger-ui.html
# API Docs:    http://localhost:8080/v3/api-docs
```

Para parar:

```bash
docker compose down
```

Para parar e remover o volume do banco:

```bash
docker compose down -v
```

---

## Como rodar localmente (sem Docker)

> **Atenção:** H2 não é aceito. É necessário ter o PostgreSQL rodando localmente ou subir apenas o banco via Docker.

```bash
# Sobe apenas o banco de dados
docker compose up postgres -d

# Em seguida, rode a aplicação
export DB_HOST=localhost DB_PORT=5432 DB_NAME=restaurante_db DB_USER=postgres DB_PASSWORD=postgres
mvn spring-boot:run
```

---

## Endpoints da API

Todos os endpoints utilizam o prefixo `/api/v1`.

| Método | Rota | Descrição |
|--------|------|-----------|
| POST | `/api/v1/usuarios` | Cadastrar novo usuário |
| GET | `/api/v1/usuarios/{id}` | Buscar usuário por ID |
| GET | `/api/v1/usuarios?nome=X` | Buscar usuários por nome (parcial) |
| PUT | `/api/v1/usuarios/{id}` | Atualizar dados do usuário (sem senha) |
| PATCH | `/api/v1/usuarios/{id}/senha` | Trocar senha |
| DELETE | `/api/v1/usuarios/{id}` | Excluir usuário |
| POST | `/api/v1/usuarios/login` | Validar login |

> Documentação interativa completa: **http://localhost:8080/swagger-ui.html**

---

## Campos do Usuário

| Campo | Tipo | Obrigatório |
|-------|------|-------------|
| nome | String | Sim |
| email | String | Sim (único) |
| login | String | Sim (único) |
| senha | String | Sim (BCrypt) |
| tipo | OWNER / CUSTOMER | Sim |
| dataUltimaAlteracao | LocalDateTime | Automático |
| endereco | Address | Sim |

---

## Membros do Time

| Pessoa | Responsabilidade |
|--------|-----------------|
| Caio      | Arquitetura, Docker, Setup |
| Igor      | Entidades JPA, Repositórios |
| Armando   | Services, Regras de Negócio |
| Luciano   | Controllers, DTOs, Swagger |
| Jurineide | Testes, QA, Documentação |
