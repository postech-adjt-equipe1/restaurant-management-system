# Roteiro — Vídeo de Apresentação (~5 min)

Segue a estrutura definida no planejamento da equipe: **Docker Compose subindo → endpoints via Swagger UI → testes rodando → organização do código**. Tempos são sugestões — ajuste conforme o ritmo de fala.

---

## 0. Abertura (0:00 – 0:20)

> "Esse é o Restaurant Management System, projeto do Tech Challenge Fase 2 do curso de Arquitetura e Desenvolvimento Java. O sistema gerencia tipos de usuário, usuários, restaurantes e os itens de cardápio de cada restaurante, com uma API REST em Spring Boot."

Mostre rapidamente a tela do terminal e do editor lado a lado (ou o README aberto).

---

## 1. Subindo o ambiente com Docker Compose (0:20 – 1:30)

**Ação na tela:** terminal, na raiz do projeto.

```bash
docker compose up --build
```

Enquanto builda/sobe, narre:

> "O `docker-compose.yml` sobe dois containers: o Postgres e a aplicação Spring Boot. O Postgres tem um healthcheck configurado, e a aplicação só inicia depois que o banco reporta estar saudável — isso evita erro de conexão na subida."

Espere aparecer o log do Spring Boot iniciado (banner + "Started RestaurantManagementApplication"). Mostre também, em outra aba do terminal:

```bash
docker compose ps
```

> "Os dois containers estão de pé — o Postgres como `healthy` e a aplicação rodando na porta 8080."

---

## 2. Endpoints via Swagger UI (1:30 – 3:30)

**Ação na tela:** navegador aberto em `http://localhost:8080/swagger-ui/index.html`.

> "Toda a API está documentada aqui pelo springdoc-openapi. Os endpoints estão agrupados por tag: Tipo de Usuário, Usuário, Restaurante e Cardápio."

Siga a ordem que respeita as dependências entre os módulos, usando o botão **"Try it out"** de cada endpoint:

1. **Tipo de Usuário** — expanda `POST /tipo-usuario`, clique em "Try it out", edite o body para `{"nome": "Dono de Restaurante"}` e clique em "Execute". Mostre o `201` e o `id` retornado na seção "Response body".
2. **Usuário** — `POST /usuario`, criando um usuário e associando o `tipoUsuarioId` retornado no passo anterior.
3. **Restaurante** — `POST /restaurante`, criando um restaurante e associando o `donoId` do usuário criado. Narre:
   > "Aqui a API valida que o dono é um usuário existente e que o tipo dele é 'Dono de Restaurante' — se não for, retorna 404."
4. **Cardápio** — `POST /restaurante/{restauranteId}/cardapio`, preenchendo o `restauranteId` no path e o body do item. Narre:
   > "O item de cardápio sempre pertence a um restaurante existente — se o restaurante não existir, a API retorna 404 aqui também."
5. Mostre rapidamente um `GET /restaurante/{restauranteId}/cardapio` listando o item criado.
6. **Regra de negócio cruzada:** tente um `DELETE /restaurante/{id}` no restaurante que já tem item de cardápio — mostre o `409 Conflict` na resposta. Narre:
   > "O sistema impede remover um restaurante que ainda tem itens de cardápio associados — isso é uma regra de integridade entre os módulos."

Aproveite para apontar, em qualquer um dos endpoints, os schemas de request/response documentados automaticamente (seção "Schema" abaixo do "Try it out") — mostra que os DTOs estão bem anotados.

---

## 3. Testes rodando + cobertura (3:30 – 4:30)

**Ação na tela:** terminal.

```bash
mvn clean verify
```

Enquanto roda, narre:

> "Temos testes unitários para os services de todos os 4 módulos e testes de integração cobrindo os endpoints REST, incluindo os cenários de erro — 400, 404 e 409."

Quando terminar, abra o relatório do JaCoCo:

```bash
open target/site/jacoco/index.html
```

> "O JaCoCo está configurado no `pom.xml` para exigir no mínimo 80% de cobertura de linha na camada de serviço — que é onde está a regra de negócio — e estamos em 100% nessa camada."

Aponte na tela a linha `application.service` do relatório.

---

## 4. Organização do código / Clean Architecture (4:30 – 5:00)

**Ação na tela:** editor de código, árvore de pastas expandida em `src/main/java/.../restaurantmanagement`.

> "O projeto está organizado em Clean Architecture: `domain` com as regras de negócio puras, sem depender de framework; `application` com os services e DTOs; `infrastructure` com JPA e configuração; e `presentation` com os controllers REST e o tratamento global de exceções."

Clique rapidamente em uma classe de cada camada para ilustrar (ex.: `Restaurante.java` em domain, `RestauranteService.java` em application, `RestauranteEntity.java` em infrastructure, `RestauranteController.java` em presentation).

---

## Encerramento (5:00)

> "Isso cobre as funcionalidades da Fase 2: cadastro de tipo de usuário, restaurante e itens de cardápio, com testes, Docker e documentação. Obrigado!"

---

### Checklist antes de gravar

- [ ] Rodar `mvn clean verify` uma vez antes da gravação para confirmar que passa sem erro (evita travar no meio do vídeo)
- [ ] Deixar o navegador já aberto em `http://localhost:8080/swagger-ui/index.html` antes de gravar
- [ ] Deixar o `docker compose down -v` rodado antes de começar, para gravar a subida do zero
- [ ] Conferir áudio e se a tela inteira (terminal + Postman + editor) está visível na gravação
