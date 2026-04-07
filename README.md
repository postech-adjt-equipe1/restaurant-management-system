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

- Cadastro de usuário (com criptografia BCrypt)
- Busca, atualização e exclusão (CRUD completo)
- Troca de senha
- Validação de login

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

```bash
# Requer PostgreSQL rodando localmente ou use o perfil H2
mvn spring-boot:run -Dspring-boot.run.profiles=test
```

Com PostgreSQL local, exporte as variáveis de ambiente antes:

```bash
export DB_HOST=localhost DB_PORT=5432 DB_NAME=restaurante_db DB_USER=postgres DB_PASSWORD=postgres
mvn spring-boot:run
```

---

## Endpoints da API

| Método | Rota | Descrição |
|--------|------|-----------|
| POST | `/usuarios` | Cadastrar novo usuário |
| GET | `/usuarios/{id}` | Buscar usuário por ID |
| PUT | `/usuarios/{id}` | Atualizar dados do usuário |
| DELETE | `/usuarios/{id}` | Excluir usuário |
| POST | `/usuarios/login` | Validar login |
| PATCH | `/usuarios/{id}/senha` | Trocar senha |

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
