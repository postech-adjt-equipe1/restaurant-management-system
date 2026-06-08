# Restaurante — Sistema de Gestão

> **POSTECH FIAP — Arquitetura e Desenvolvimento Java — Tech Challenge Fase 2**

Backend em Spring Boot para cadastro e gerenciamento de usuários, tipos de usuário e restaurantes, com autenticação JWT stateless.

---

## Índice

- [Visão Geral](#visão-geral)
- [Tecnologias](#tecnologias)
- [Arquitetura](#arquitetura)
- [Como Rodar](#como-rodar)
  - [Docker (recomendado)](#docker-recomendado)
  - [Local sem Docker](#local-sem-docker)
- [Variáveis de Ambiente](#variáveis-de-ambiente)
- [Endpoints da API](#endpoints-da-api)
  - [Autenticação](#autenticação)
  - [Usuários](#usuários)
  - [Tipos de Usuário](#tipos-de-usuário)
- [Swagger UI](#swagger-ui)
- [Segurança e JWT](#segurança-e-jwt)
- [Modelo de Dados](#modelo-de-dados)
- [Tratamento de Erros](#tratamento-de-erros)
- [Testes](#testes)
- [Collection Postman](#collection-postman)
- [Membros do Time](#membros-do-time)

---

## Visão Geral

| Item | Detalhe |
|------|---------|
| Linguagem | Java 17 |
| Framework | Spring Boot 3.2.5 |
| Banco de dados | PostgreSQL 16 |
| Migrations | Flyway |
| Autenticação | JWT stateless (JJWT 0.12.6) |
| Documentação | Swagger / OpenAPI 3 |
| Containerização | Docker + Docker Compose |
| Testes | JUnit 5 + Mockito + Spring MVC Test |

### Tipos de Usuário (enum interno)

| Tipo | Descrição |
|------|-----------|
| `OWNER` | Proprietário de restaurante |
| `CUSTOMER` | Cliente |

### Funcionalidades

- Cadastro com criptografia BCrypt e validação de unicidade de e-mail e login
- Busca por ID e por nome (parcial, case-insensitive)
- Atualização de dados cadastrais e exclusão (CRUD completo)
- Troca de senha com validação da senha atual
- Login com retorno de token JWT Bearer (endpoint `/api/v1/usuarios/login`)
- **Fase 2:** CRUD de tipos de usuário (`TipoUsuario`) com associação a usuários
- Tratamento de erros padronizado com **ProblemDetail** (RFC 7807)
- Documentação interativa via Swagger UI

---

## Tecnologias

| Dependência | Versão | Uso |
|-------------|--------|-----|
| Spring Boot | 3.2.5 | Framework principal |
| Spring Security | 6.x | Autenticação e autorização |
| Spring Data JPA | 3.x | Acesso ao banco de dados |
| JJWT | 0.12.6 | Geração e validação de tokens JWT |
| Flyway | 9.x | Migrations de banco de dados |
| PostgreSQL Driver | 42.x | Driver JDBC |
| Springdoc OpenAPI | 2.5.0 | Swagger UI |
| Lombok | 1.18.x | Redução de boilerplate |
| JUnit 5 + Mockito | — | Testes unitários |

---

## Arquitetura

```
br.com.fiap.restaurante
├── config/
│   ├── SecurityConfig.java               # Regras de autorização e filtro JWT
│   └── OpenApiConfig.java                # Configuração do Swagger
├── controller/
│   ├── UserController.java               # CRUD + login (com JWT) + troca de senha
│   ├── UserControllerDocs.java           # Interface com anotações Swagger
│   ├── TipoUsuarioController.java        # CRUD de tipos de usuário
│   └── TipoUsuarioControllerDocs.java    # Interface com anotações Swagger
├── dto/
│   ├── LoginResponse                     # Resposta do login (token JWT)
│   ├── UserCreateRequestDTO              # Criação de usuário
│   ├── UserUpdateRequestDTO              # Atualização de usuário
│   ├── UserChangePasswordRequestDTO      # Troca de senha
│   ├── UserLoginRequestDTO               # Credenciais de login
│   ├── UserResponseDTO                   # Resposta completa
│   ├── UserSearchResponseDTO             # Resposta resumida (busca)
│   ├── AddressRequest/ResponseDTO        # Endereço
│   ├── TipoUsuarioRequestDTO             # Criação/atualização de tipo
│   └── TipoUsuarioResponseDTO            # Resposta de tipo de usuário
├── exception/
│   ├── GlobalExceptionHandler.java       # @ControllerAdvice → ProblemDetail
│   ├── DuplicateEmailException           # 409 Conflict
│   ├── DuplicateLoginException           # 409 Conflict
│   ├── DuplicateTipoUsuarioException     # 409 Conflict
│   ├── InvalidCredentialsException       # 401 Unauthorized
│   ├── InvalidPasswordException          # 400 Bad Request
│   ├── UserNotFoundException             # 404 Not Found
│   └── TipoUsuarioNotFoundException      # 404 Not Found
├── model/
│   ├── User.java                         # Entidade JPA (FK tipo_usuario_id)
│   ├── Address.java                      # Embeddable
│   ├── UserType.java                     # Enum: OWNER | CUSTOMER
│   └── TipoUsuario.java                  # Entidade JPA
├── repository/
│   ├── UserRepository.java               # Spring Data JPA
│   └── TipoUsuarioRepository.java        # Spring Data JPA
├── security/
│   ├── JwtService.java                   # Geração e validação de JWT
│   └── JwtAuthenticationFilter.java      # Filtro Bearer token
└── service/
    ├── UserService.java                  # Interface
    ├── UserServiceImpl.java              # Implementação com regras de negócio
    ├── TipoUsuarioService.java           # Interface
    └── TipoUsuarioServiceImpl.java       # Implementação com regras de negócio
```

### Fluxo de autenticação

```
Cliente → POST /api/v1/usuarios/login → UserService.validateLogin() → JwtService.generateToken()
                                                                               ↓
                                                                     { token, tipo, expiresIn }

Cliente → GET /api/v1/usuarios/{id}
          Authorization: Bearer <token>
          → JwtAuthenticationFilter → extrai login e userType → SecurityContext
          → Controller → UserService → Response
```

---

## Como Rodar

### Docker (recomendado)

**Pré-requisitos:** Docker ≥ 24 e Docker Compose ≥ 2.20

```bash
# 1. Clone o repositório
git clone <url-do-repositorio>
cd restaurant-management-system

# 2. Build e suba todos os serviços
docker compose up --build

# 3. Acesse
#    Swagger UI  → http://localhost:8080/swagger-ui.html
#    API Docs    → http://localhost:8080/v3/api-docs
```

Parar os containers:

```bash
docker compose down          # mantém o volume do banco
docker compose down -v       # remove o volume (apaga os dados)
```

### Local sem Docker

> É necessário ter o PostgreSQL disponível. O H2 não é suportado.

```bash
# Sobe apenas o banco via Docker
docker compose up postgres -d

# Rode a aplicação com as variáveis de ambiente
export DB_HOST=localhost DB_PORT=5432 DB_NAME=restaurante_db \
       DB_USER=postgres DB_PASSWORD=postgres
mvn spring-boot:run
```

Para rodar somente os **testes unitários** (sem banco de dados):

```bash
mvn test -Dtest="UserServiceImplTest,JwtServiceTest,UserControllerTest,AuthControllerTest,GlobalExceptionHandlerTest"
```

---

## Variáveis de Ambiente

Todas as variáveis possuem valor padrão para desenvolvimento local.

| Variável | Padrão | Descrição |
|----------|--------|-----------|
| `DB_HOST` | `localhost` | Host do PostgreSQL |
| `DB_PORT` | `5432` | Porta do PostgreSQL |
| `DB_NAME` | `restaurante_db` | Nome do banco |
| `DB_USER` | `postgres` | Usuário do banco |
| `DB_PASSWORD` | `postgres` | Senha do banco |
| `SERVER_PORT` | `8080` | Porta da aplicação |
| `JWT_SECRET` | *(chave hex 64 chars)* | Chave de assinatura HMAC-SHA |
| `JWT_EXPIRATION` | `86400000` | Validade do token em ms (padrão: 24 h) |

> **Produção:** substitua `JWT_SECRET` por uma chave aleatória segura de no mínimo 32 bytes.

---

## Endpoints da API

### Usuários

Prefixo: `/api/v1/usuarios`

| Método | Rota | Auth | Descrição | Status de sucesso |
|--------|------|------|-----------|-------------------|
| `POST` | `/api/v1/usuarios` | Pública | Cadastrar novo usuário | `201 Created` |
| `POST` | `/api/v1/usuarios/login` | Pública | Autenticar e retornar token JWT | `200 OK` |
| `GET` | `/api/v1/usuarios?nome=X` | JWT | Buscar usuários por nome | `200 OK` |
| `GET` | `/api/v1/usuarios/{id}` | JWT | Buscar usuário por ID | `200 OK` |
| `PUT` | `/api/v1/usuarios/{id}` | JWT | Atualizar dados (sem senha) | `200 OK` |
| `PATCH` | `/api/v1/usuarios/{id}/senha` | JWT | Trocar senha | `204 No Content` |
| `DELETE` | `/api/v1/usuarios/{id}` | JWT | Excluir usuário | `204 No Content` |

### Tipos de Usuário

Prefixo: `/api/v1/tipo-usuario`

| Método | Rota | Auth | Descrição | Status de sucesso |
|--------|------|------|-----------|-------------------|
| `POST` | `/api/v1/tipo-usuario` | JWT | Cadastrar tipo de usuário | `201 Created` |
| `GET` | `/api/v1/tipo-usuario` | JWT | Listar todos os tipos | `200 OK` |
| `GET` | `/api/v1/tipo-usuario/{id}` | JWT | Buscar tipo por ID | `200 OK` |
| `PUT` | `/api/v1/tipo-usuario/{id}` | JWT | Atualizar tipo | `200 OK` |
| `DELETE` | `/api/v1/tipo-usuario/{id}` | JWT | Excluir tipo | `204 No Content` |

**Cadastro** `POST /api/v1/tipo-usuario`
```json
{ "nome": "Dono de Restaurante" }
```

**Response `201 Created`:**
```json
{ "id": 1, "nome": "Dono de Restaurante" }
```

**Associar tipo ao usuário** — envie `tipoUsuarioId` ao criar ou atualizar um usuário:
```json
{
  "nome": "João Silva",
  "email": "joao@email.com",
  "login": "joao.silva",
  "senha": "Senha@123",
  "tipo": "OWNER",
  "tipoUsuarioId": 1,
  "endereco": { ... }
}
```

**Login** `POST /api/v1/usuarios/login`
```json
{
  "login": "joao.silva",
  "senha": "Senha@123"
}
```

**Response `200 OK`:**
```json
{
  "token": "eyJhbGciOiJIUzM4NCJ9...",
  "tipo": "Bearer",
  "expiresIn": 86400000,
  "login": "joao.silva",
  "nome": "João Silva",
  "userType": "CUSTOMER"
}
```

Use o token nas requisições protegidas:
```
Authorization: Bearer eyJhbGciOiJIUzM4NCJ9...
```

#### Exemplos

**Cadastro** `POST /api/v1/usuarios`
```json
{
  "nome": "João Silva",
  "email": "joao@email.com",
  "login": "joao.silva",
  "senha": "Senha@123",
  "tipo": "CUSTOMER",
  "endereco": {
    "logradouro": "Rua das Flores",
    "numero": "123",
    "complemento": "Apto 45",
    "bairro": "Centro",
    "cidade": "São Paulo",
    "estado": "SP",
    "cep": "01310-100"
  }
}
```

**Troca de senha** `PATCH /api/v1/usuarios/{id}/senha`
```json
{
  "senhaAtual": "Senha@123",
  "novaSenha": "NovaSenha@789"
}
```

> Documentação interativa completa (com todos os schemas e exemplos): **http://localhost:8080/swagger-ui.html**

---

## Swagger UI

Acesse **http://localhost:8080/swagger-ui.html** com a aplicação rodando para explorar e testar todos os endpoints diretamente pelo navegador.

![Swagger UI](docs/swagger-ui-full.png)

A interface exibe os endpoints do grupo **Usuários** com descrições, schemas de request/response e a possibilidade de executar chamadas diretamente.

---

## Segurança e JWT

- Autenticação **stateless** — sem sessão HTTP, sem cookie
- Cada requisição protegida deve enviar `Authorization: Bearer <token>` no header
- O token carrega os claims: `sub` (login), `nome`, `userType`, `iat`, `exp`
- O filtro `JwtAuthenticationFilter` extrai o `userType` e cria a authority `ROLE_OWNER` ou `ROLE_CUSTOMER` no `SecurityContext`
- Senhas armazenadas com **BCrypt**

**Rotas públicas** (sem token):

| Método | Rota |
|--------|------|
| `POST` | `/api/v1/usuarios` |
| `POST` | `/api/v1/usuarios/login` |
| `GET` | `/swagger-ui/**` |
| `GET` | `/v3/api-docs/**` |

---

## Modelo de Dados

### User

| Campo | Tipo | Regras |
|-------|------|--------|
| `id` | Long | PK, auto-gerado |
| `nome` | String | Obrigatório, máx. 100 chars |
| `email` | String | Obrigatório, único, formato e-mail, máx. 150 chars |
| `login` | String | Obrigatório, único, 3–50 chars |
| `senha` | String | Obrigatório, mín. 8 chars, armazenada com BCrypt |
| `tipo` | `OWNER` \| `CUSTOMER` | Obrigatório |
| `tipoUsuario` | TipoUsuario | Opcional, FK para `tipo_usuario` |
| `dataUltimaAlteracao` | LocalDateTime | Atualizado automaticamente (`@PrePersist`, `@PreUpdate`) |
| `endereco` | Address | Obrigatório (embedded) |

### TipoUsuario

| Campo | Tipo | Regras |
|-------|------|--------|
| `id` | Long | PK, auto-gerado |
| `nome` | String | Obrigatório, único, máx. 100 chars |

### Address (embedded)

| Campo | Tipo | Regras |
|-------|------|--------|
| `logradouro` | String | Obrigatório |
| `numero` | String | Obrigatório |
| `complemento` | String | Opcional |
| `bairro` | String | Obrigatório |
| `cidade` | String | Obrigatório |
| `estado` | String | Obrigatório |
| `cep` | String | Obrigatório, formato `00000-000` |

---

## Tratamento de Erros

Todos os erros seguem o padrão **RFC 7807 — Problem Details** (`ProblemDetail`):

```json
{
  "type": "https://fiap.com.br/erros/404",
  "title": "Not Found",
  "status": 404,
  "detail": "Usuário não encontrado com id: 42"
}
```

Erros de validação incluem o mapa de campos inválidos:

```json
{
  "type": "https://fiap.com.br/erros/400",
  "title": "Erro de validação",
  "status": 400,
  "detail": "Um ou mais campos possuem valores inválidos.",
  "campos": {
    "senha": "Senha deve ter no mínimo 8 caracteres",
    "email": "must be a well-formed email address"
  }
}
```

| Exceção | Status HTTP |
|---------|-------------|
| Campos inválidos (Bean Validation) | `400 Bad Request` |
| Senha atual incorreta | `400 Bad Request` |
| Credenciais inválidas | `401 Unauthorized` |
| Usuário não encontrado | `404 Not Found` |
| Tipo de usuário não encontrado | `404 Not Found` |
| E-mail ou login duplicado | `409 Conflict` |
| Nome de tipo de usuário duplicado | `409 Conflict` |
| Erro interno | `500 Internal Server Error` |

---

## Testes

O projeto possui **93 testes unitários**, organizados em 7 classes:

| Classe | Testes | Cobertura |
|--------|--------|-----------|
| `UserServiceImplTest` | 20 | Toda a lógica de negócio do serviço (criação, busca, atualização, senha, login, deleção) |
| `JwtServiceTest` | 11 | Geração de token, extração de claims, validação (expirado, inválido, login divergente) |
| `JwtAuthenticationFilterTest` | 8 | Filtro JWT — token válido, inválido, ausente e rotas públicas |
| `UserControllerTest` | 24 | Todos os endpoints REST — status HTTP, corpo da resposta, token JWT no login, erros de validação |
| `GlobalExceptionHandlerTest` | 6 | Respostas ProblemDetail para cada tipo de exceção |
| `TipoUsuarioServiceImplTest` | 12 | CRUD do serviço — criação, busca, atualização, deleção e validação de nome duplicado |
| `TipoUsuarioControllerTest` | 12 | Todos os endpoints REST de tipo de usuário — status HTTP, corpo da resposta e erros |

**Rodar os testes unitários** (não requerem banco de dados):

```bash
mvn test -Dtest="UserServiceImplTest,JwtServiceTest,JwtAuthenticationFilterTest,UserControllerTest,GlobalExceptionHandlerTest,TipoUsuarioServiceImplTest,TipoUsuarioControllerTest"
```

**Resultado esperado:**
```
Tests run: 93, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

---

## Collection Postman

A pasta `postman/` contém dois arquivos prontos para importar:

```
postman/
├── restaurant-management-api.postman_collection.json   # 23 requests com testes automáticos
└── restaurant-management-api.postman_environment.json  # Variáveis de ambiente (baseUrl, token, userId)
```

### Como usar

1. No Postman: **Import** → selecione os dois arquivos da pasta `postman/`
2. Selecione o environment **"Restaurant Management - Local"**
3. Execute os grupos na ordem abaixo:

| Grupo | Requests | Observação |
|-------|----------|------------|
| 1. Login | 4 | Salva `jwtToken` automaticamente |
| 2. Cadastro de Usuário | 6 | Salva `userId` automaticamente |
| 3. Busca de Usuário | 4 | Requer `userId` e `jwtToken` |
| 4. Atualização de Dados | 3 | Requer `userId` e `jwtToken` |
| 5. Troca de Senha | 4 | Requer `userId` e `jwtToken` |
| 6. Deleção de Usuário | 2 | Execute por último |

Cada request inclui **testes automáticos** que verificam o status HTTP, a estrutura do corpo e as mensagens de erro — os resultados aparecem na aba **Test Results**.

---

## Membros do Time

| Pessoa | Responsabilidade |
|--------|-----------------|
| Caio | Arquitetura, Docker, Setup |
| Igor | Entidades JPA, Repositórios |
| Armando | Services, Regras de Negócio |
| Luciano | Controllers, DTOs, Swagger |
| Jurineide | Testes, QA, Documentação |