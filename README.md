# Restaurant Management System — Fase 2

Sistema de gestão de restaurantes desenvolvido como Tech Challenge da Fase 2 do curso de Arquitetura e Desenvolvimento Java (FIAP POS TECH).

## Descrição do projeto

Um grupo de restaurantes se uniu para construir um sistema único e compartilhado de gestão, evitando o alto custo de sistemas individuais. A Fase 2 expande o sistema com:

- **Tipo de Usuário** — distingue usuários "Dono de Restaurante" e "Cliente".
- **Usuário** — cadastro de usuários, associados a um tipo de usuário.
- **Restaurante** — cadastro de restaurantes, vinculado a um usuário dono.
- **Cardápio** — itens vendidos por um restaurante (nome, descrição, preço, disponibilidade só no local e caminho da foto).

Cada módulo expõe um CRUD REST completo, construído com Spring Boot sobre Clean Architecture, com testes automatizados e infraestrutura Docker para execução integrada.

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

| Ferramenta     | Versão mínima |
| -------------- | ------------- |
| Java           | 17            |
| Maven          | 3.9           |
| Docker         | 24            |
| Docker Compose | 2.x           |

---

## Configuração

As variáveis de ambiente estão externalizadas no `docker-compose.yml`. Para customizar, copie o arquivo de exemplo e ajuste:

```bash
cp .env.example .env
```

Variáveis disponíveis:

| Variável      | Padrão          | Descrição                   |
| ------------- | --------------- | --------------------------- |
| `DB_NAME`     | `restaurant_db` | Nome do banco de dados      |
| `DB_USER`     | `postgres`      | Usuário do PostgreSQL       |
| `DB_PASSWORD` | `postgres`      | Senha do PostgreSQL         |
| `DB_PORT`     | `5432`          | Porta exposta do PostgreSQL |
| `SERVER_PORT` | `8080`          | Porta exposta da aplicação  |

---

## Execução

### Com Docker Compose (recomendado)

```bash
docker compose up --build
```

A aplicação estará disponível em `http://localhost:8080`.

### Localmente (requer PostgreSQL rodando)

```bash
mvn spring-boot:run
```

Certifique-se de que as variáveis de ambiente `DB_HOST`, `DB_NAME`, `DB_USER` e `DB_PASSWORD` estejam definidas, ou ajuste o `application.yml`.

---

## Documentação interativa (Swagger / OpenAPI)

Com a aplicação em execução, a documentação interativa de todos os endpoints fica disponível em:

| Recurso      | URL                                   |
| ------------ | ------------------------------------- |
| Swagger UI   | http://localhost:8080/swagger-ui.html |
| OpenAPI JSON | http://localhost:8080/v3/api-docs     |

A configuração do Swagger fica isolada em [`infrastructure/config/OpenApiConfig.java`](src/main/java/com/postech/restaurantmanagement/infrastructure/config/OpenApiConfig.java) e em anotações (`@Tag`, `@Operation`, `@ApiResponse`) nos controllers da camada `presentation`. Nenhuma anotação de documentação é usada em `domain` ou `application`, para manter o núcleo de negócio livre de dependências de framework.

---

## Endpoints da API

> A tabela abaixo resume os endpoints. Para o payload completo de cada requisição, use o Swagger UI acima ou a [collection do Postman](postman/Restaurant_Management_System.postman_collection.json).

### Tipo de Usuário

| Método   | URL                  | Descrição                    |
| -------- | -------------------- | ---------------------------- |
| `POST`   | `/tipo-usuario`      | Cria um novo tipo de usuário |
| `GET`    | `/tipo-usuario`      | Lista todos os tipos         |
| `GET`    | `/tipo-usuario/{id}` | Busca tipo pelo ID           |
| `PUT`    | `/tipo-usuario/{id}` | Atualiza tipo pelo ID        |
| `DELETE` | `/tipo-usuario/{id}` | Remove tipo pelo ID          |

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

| Método   | URL             | Descrição                |
| -------- | --------------- | ------------------------ |
| `POST`   | `/usuario`      | Cria um novo usuário     |
| `GET`    | `/usuario`      | Lista todos os usuários  |
| `GET`    | `/usuario/{id}` | Busca usuário pelo ID    |
| `PUT`    | `/usuario/{id}` | Atualiza usuário pelo ID |
| `DELETE` | `/usuario/{id}` | Remove usuário pelo ID   |

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

---

### Restaurante

| Método   | URL                 | Descrição                    |
| -------- | ------------------- | ---------------------------- |
| `POST`   | `/restaurante`      | Cria um novo restaurante     |
| `GET`    | `/restaurante`      | Lista todos os restaurantes  |
| `GET`    | `/restaurante/{id}` | Busca restaurante pelo ID    |
| `PUT`    | `/restaurante/{id}` | Atualiza restaurante pelo ID |
| `DELETE` | `/restaurante/{id}` | Remove restaurante pelo ID   |

**Body — POST / PUT:**

```json
{
  "nome": "Cantina da Nonna",
  "endereco": "Rua das Flores, 123",
  "tipoCozinha": "Italiana",
  "horarioFuncionamento": "11h às 23h",
  "donoId": 1
}
```

**Resposta de sucesso (201 / 200):**

```json
{
  "id": 1,
  "nome": "Cantina da Nonna",
  "endereco": "Rua das Flores, 123",
  "tipoCozinha": "Italiana",
  "horarioFuncionamento": "11h às 23h",
  "donoId": 1
}
```

**Regras de negócio:**

- O `donoId` deve ser um usuário existente cujo tipo de usuário seja **"Dono de Restaurante"**; caso contrário a API responde `404`.
- Um restaurante que ainda possui itens de cardápio não pode ser removido — a API responde `409 Conflict`. Remova os itens primeiro.

---

### Cardápio

| Método   | URL                                     | Descrição                                   |
| -------- | --------------------------------------- | ------------------------------------------- |
| `POST`   | `/restaurante/{restauranteId}/cardapio` | Cria um item de cardápio para o restaurante |
| `GET`    | `/restaurante/{restauranteId}/cardapio` | Lista os itens de cardápio do restaurante   |
| `GET`    | `/cardapio/{itemId}`                    | Busca um item de cardápio pelo ID           |
| `PUT`    | `/cardapio/{itemId}`                    | Atualiza um item de cardápio pelo ID        |
| `DELETE` | `/cardapio/{itemId}`                    | Remove um item de cardápio pelo ID          |

**Body — POST / PUT:**

```json
{
  "nome": "Feijoada Completa",
  "descricao": "Feijoada com arroz, couve e farofa",
  "preco": 45.9,
  "apenasLocal": false,
  "caminhoFoto": "/fotos/feijoada.jpg"
}
```

**Resposta de sucesso (201 / 200):**

```json
{
  "id": 1,
  "nome": "Feijoada Completa",
  "descricao": "Feijoada com arroz, couve e farofa",
  "preco": 45.9,
  "apenasLocal": false,
  "caminhoFoto": "/fotos/feijoada.jpg",
  "restauranteId": 1
}
```

**Regra de negócio:** um item de cardápio sempre pertence a um restaurante existente — `restauranteId` é validado em toda operação de criação/listagem, e a API responde `404` caso o restaurante informado não exista. O campo `caminhoFoto` é opcional (a foto em si não é armazenada, apenas o caminho).

---

### Erros comuns (todos os módulos)

| Status | Situação                                                                                                            |
| ------ | ------------------------------------------------------------------------------------------------------------------- |
| `400`  | Campo obrigatório ausente ou formato inválido (corpo da requisição)                                                 |
| `404`  | Recurso não encontrado pelo ID informado (ou referência a um recurso pai inexistente, ex.: restaurante do cardápio) |
| `409`  | Conflito de estado — ex.: tentar remover um restaurante que ainda possui itens de cardápio                          |

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

## Collection do Postman

A collection com todos os endpoints (incluindo casos de sucesso e de erro) está em
[`postman/Restaurant_Management_System.postman_collection.json`](postman/Restaurant_Management_System.postman_collection.json).
Importe o arquivo no Postman e ajuste a variável de coleção `baseUrl` (padrão: `http://localhost:8080`) se necessário.

---

## Estrutura de arquivos

```
restaurant-management-system/
├── src/
│   ├── main/
│   │   ├── java/com/postech/restaurantmanagement/
│   │   │   ├── domain/model/                    # TipoUsuario, Usuario, Restaurante, ItemCardapio
│   │   │   ├── domain/repository/               # Interfaces de repositório (portas do domínio)
│   │   │   ├── application/dto/                 # Request e Response DTOs
│   │   │   ├── application/service/              # Casos de uso: TipoUsuarioService, UsuarioService,
│   │   │   │                                     # RestauranteService, ItemCardapioService
│   │   │   ├── infrastructure/persistence/       # Entities JPA + implementações dos repositórios
│   │   │   ├── infrastructure/config/            # OpenApiConfig (documentação Swagger, isolada do domínio)
│   │   │   └── presentation/controller/          # REST controllers + GlobalExceptionHandler
│   │   └── resources/
│   │       ├── application.yml                   # Configuração principal
│   │       └── application-test.yml              # Configuração de testes (H2)
│   └── test/
│       └── java/.../application/service/         # Testes unitários
│       └── java/.../presentation/controller/     # Testes de integração
├── postman/                                       # Collection do Postman
├── Dockerfile
├── docker-compose.yml
├── .env.example
├── CHANGELOG.md
└── pom.xml
```

---

## Vídeo de apresentação

🎥 _Link a ser incluído pelo responsável pelo vídeo (ver seção "Membros do Time")._

---

## Membros do Time

| Pessoa  | Responsabilidade                                                                        |
| ------- | --------------------------------------------------------------------------------------- |
| Caio    | TipoUsuario CRUD + Docker Compose + Testes unitários                                    |
| Igor    | Restaurante CRUD + Testes unitários + Testes de integração                              |
| Armando | Cardápio CRUD + Clean Architecture + Testes unitários + Documentação (README + Postman) |
| Luciano | QA geral + Testes de integração do Cardápio + Code review + Vídeo de Apresentação       |
