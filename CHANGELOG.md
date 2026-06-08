# Changelog

Todas as mudanças notáveis neste projeto serão documentadas aqui.

Formato baseado em [Keep a Changelog](https://keepachangelog.com/pt-BR/1.1.0/),
e este projeto adota [Versionamento Semântico](https://semver.org/lang/pt-BR/).

---

## [Unreleased]

## [0.3.0] - 2026-06-08

### Adicionado
- Entidade `TipoUsuario` com campo `nome` e tabela `tipo_usuario` (migration V2)
- CRUD completo de tipos de usuário: `POST /api/v1/tipo-usuario`, `GET /api/v1/tipo-usuario`, `GET /api/v1/tipo-usuario/{id}`, `PUT /api/v1/tipo-usuario/{id}`, `DELETE /api/v1/tipo-usuario/{id}`
- Associação `ManyToOne` entre `User` e `TipoUsuario` via coluna `tipo_usuario_id` (migration V3)
- Campo opcional `tipoUsuarioId` em `UserCreateRequestDTO` e `UserUpdateRequestDTO`
- `TipoUsuarioResponseDTO` aninhado na resposta de `UserResponseDTO`
- Exceções `TipoUsuarioNotFoundException` (404) e `DuplicateTipoUsuarioException` (409)
- Dados iniciais: tipos "Dono de Restaurante" e "Cliente" inseridos via migration V2
- 21 novos testes unitários: `TipoUsuarioServiceImplTest` (12) e `TipoUsuarioControllerTest` (9)

## [0.2.0] - 2026-04-07

### Adicionado
- Versionamento de API: todos os endpoints passam a usar o prefixo `/api/v1`
- Endpoint `GET /api/v1/usuarios?nome=X` para busca de usuários por nome (parcial, case-insensitive)
- Nota no README sobre requisito de e-mail único e ProblemDetail

### Alterado
- `README.md`: tabela de endpoints atualizada com prefixo `/api/v1` e novo endpoint de busca por nome
- `README.md`: seção "rodar localmente" atualizada — removida referência ao H2, instrução agora orienta subir o PostgreSQL via Docker
- `README.md`: lista de funcionalidades expandida com novos requisitos da disciplina
- `application-test.yml`: perfil de teste migrado de H2 in-memory para PostgreSQL (mesmas variáveis de ambiente do `docker-compose.yml`)

### Removido
- Dependência H2 (`com.h2database:h2`) do `pom.xml` — banco em memória não é aceito no projeto

## [0.1.0] - 2026-03-30

### Adicionado
- Estrutura base do projeto Spring Boot 3.2 com Java 17
- Dependências: Spring Web, Data JPA, Validation, Security (BCrypt), Lombok, DevTools
- PostgreSQL como banco de dados principal; H2 para testes
- Flyway configurado para migrações de banco de dados
- Springdoc OpenAPI (Swagger UI em `/swagger-ui.html`)
- `SecurityConfig` com `BCryptPasswordEncoder` e endpoints liberados
- `OpenApiConfig` com metadados da API
- `application.yml` com configuração via variáveis de ambiente
- `application-test.yml` com H2 in-memory para testes unitários
- Dockerfile multi-stage (build Maven + runtime JRE 17 Alpine)
- `docker-compose.yml` com serviços `app` e `postgres` (healthcheck + volume persistente)
- Estrutura de pacotes: `model`, `repository`, `service`, `controller`, `dto`, `exception`, `config`
- Pasta `db/migration/` preparada para scripts Flyway
- `.gitignore` para Maven, IDEs e variáveis de ambiente
- `README.md` com visão geral, arquitetura, pré-requisitos e endpoints

[Unreleased]: https://github.com/postech-adjt-equipe1/restaurant-management-system/compare/v0.3.0...HEAD
[0.3.0]: https://github.com/postech-adjt-equipe1/restaurant-management-system/compare/v0.2.0...v0.3.0
[0.2.0]: https://github.com/postech-adjt-equipe1/restaurant-management-system/compare/v0.1.0...v0.2.0
[0.1.0]: https://github.com/postech-adjt-equipe1/restaurant-management-system/releases/tag/v0.1.0
