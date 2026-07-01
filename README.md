# Restaurant Management System — Fase 2

Sistema de gestão de restaurantes desenvolvido como Tech Challenge da Fase 2 do curso de Arquitetura e Desenvolvimento Java (FIAP POS TECH).

---

## Arquitetura

O projeto segue os princípios de **Clean Architecture**, organizado em quatro camadas:

```
src/main/java/com/postech/restaurantmanagement/
├── domain/               # Entidades de negócio e interfaces de repositório
├── application/          # Serviços (casos de uso) e DTOs
├── infrastructure/       # Implementações JPA e configurações de banco
└── presentation/         # Controllers REST e tratamento de exceções
```

Essa separação garante que a lógica de negócio (domínio) não dependa de frameworks ou banco de dados.

---

## Pré-requisitos

| Ferramenta | Versão mínima |
|------------|---------------|
| Java       | 17            |
| Maven      | 3.9           |
| Docker     | 24            |
| Docker Compose | 2.x       |

---

## Configuração

As variáveis de ambiente estão externalizadas no `docker-compose.yml`. Para customizar, copie o arquivo de exemplo e ajuste:

```bash
cp .env.example .env
```

Variáveis disponíveis:

| Variável      | Padrão          | Descrição                    |
|---------------|-----------------|------------------------------|
| `DB_NAME`     | `restaurant_db` | Nome do banco de dados       |
| `DB_USER`     | `postgres`      | Usuário do PostgreSQL        |
| `DB_PASSWORD` | `postgres`      | Senha do PostgreSQL          |
| `DB_PORT`     | `5432`          | Porta exposta do PostgreSQL  |
| `SERVER_PORT` | `8080`          | Porta exposta da aplicação   |

---

## Execução

### Com Docker Compose (recomendado)

```bash
docker-compose up --build
```

A aplicação estará disponível em `http://localhost:8080`.

### Localmente (requer PostgreSQL rodando)

```bash
mvn spring-boot:run
```

Certifique-se de que as variáveis de ambiente `DB_HOST`, `DB_NAME`, `DB_USER` e `DB_PASSWORD` estejam definidas, ou ajuste o `application.yml`.

---

## Endpoints da API

### Tipo de Usuário

| Método   | URL                    | Descrição                    |
|----------|------------------------|------------------------------|
| `POST`   | `/tipo-usuario`        | Cria um novo tipo de usuário |
| `GET`    | `/tipo-usuario`        | Lista todos os tipos         |
| `GET`    | `/tipo-usuario/{id}`   | Busca tipo pelo ID           |
| `PUT`    | `/tipo-usuario/{id}`   | Atualiza tipo pelo ID        |
| `DELETE` | `/tipo-usuario/{id}`   | Remove tipo pelo ID          |

**Body — POST / PUT:**
```json
{
  "nome": "Dono de Restaurante"
}
```

**Resposta de sucesso (201 / 200):**
```json
{
  "id": 1,
  "nome": "Dono de Restaurante"
}
```

---

### Usuário

| Método   | URL               | Descrição                 |
|----------|-------------------|---------------------------|
| `POST`   | `/usuario`        | Cria um novo usuário      |
| `GET`    | `/usuario`        | Lista todos os usuários   |
| `GET`    | `/usuario/{id}`   | Busca usuário pelo ID     |
| `PUT`    | `/usuario/{id}`   | Atualiza usuário pelo ID  |
| `DELETE` | `/usuario/{id}`   | Remove usuário pelo ID    |

**Body — POST / PUT:**
```json
{
  "nome": "João Silva",
  "email": "joao@email.com",
  "senha": "senha123",
  "tipoUsuarioId": 1
}
```

**Resposta de sucesso (201 / 200):**
```json
{
  "id": 1,
  "nome": "João Silva",
  "email": "joao@email.com",
  "tipoUsuarioId": 1
}
```

**Erros comuns:**

| Status | Situação                                      |
|--------|-----------------------------------------------|
| `400`  | Campo obrigatório ausente ou formato inválido |
| `404`  | Recurso não encontrado pelo ID informado      |

---

## Testes

### Executar testes unitários

```bash
mvn clean test
```

### Executar testes com verificação de cobertura (≥ 80%)

```bash
mvn clean verify
```

O relatório de cobertura é gerado em `target/site/jacoco/index.html`.

---

## Estrutura de arquivos

```
restaurant-management-system/
├── src/
│   ├── main/
│   │   ├── java/com/postech/restaurantmanagement/
│   │   │   ├── domain/model/               # TipoUsuario, Usuario
│   │   │   ├── domain/repository/          # Interfaces de repositório
│   │   │   ├── application/dto/            # Request e Response DTOs
│   │   │   ├── application/service/        # TipoUsuarioService, UsuarioService
│   │   │   ├── infrastructure/persistence/ # Entities JPA + implementações
│   │   │   └── presentation/controller/   # REST controllers
│   │   └── resources/
│   │       ├── application.yml             # Configuração principal
│   │       └── application-test.yml        # Configuração de testes (H2)
│   └── test/
│       └── java/.../application/service/  # Testes unitários (20 testes)
├── Dockerfile
├── docker-compose.yml
├── .env.example
├── CHANGELOG.md
└── pom.xml
```

---

## Membros do Time

| Pessoa  | Responsabilidade                                                                 |
|---------|----------------------------------------------------------------------------------|
| Caio    | TipoUsuario CRUD + Docker Compose + Testes unitários                             |
| Igor    | Restaurante CRUD + Testes unitários + Testes de integração                       |
| Armando | Cardápio CRUD + Clean Architecture + Testes unitários + Documentação (README + Postman) |
| Luciano | QA geral + Testes de integração do Cardápio + Code review + Vídeo de Apresentação |
