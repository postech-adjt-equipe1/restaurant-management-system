# Code Review — Restaurant Management System (Fase 2)

**Responsável pela revisão:** Integrante 4 (QA geral + Code review)
**Escopo:** revisão de nomenclatura, organização de pacotes, boas práticas Spring Boot, cobertura de testes e infraestrutura Docker, conforme os critérios de avaliação da Fase 2 do Tech Challenge.

---

## 1. Organização de pacotes e Clean Architecture

O projeto segue corretamente a separação em 4 camadas:

```
domain/            → modelos de negócio + interfaces de repositório (portas)
application/        → services (casos de uso) + DTOs
infrastructure/      → entities JPA, implementações de repositório, config
presentation/        → controllers REST + exception handler
```

**Pontos positivos:**

- `domain` não depende de nenhuma anotação de framework (nem JPA, nem Spring) — os modelos de negócio (`Restaurante`, `Usuario`, `TipoUsuario`, `ItemCardapio`) são POJOs puros.
- As entidades JPA (`*Entity`) ficam isoladas em `infrastructure/persistence/entity`, com métodos `from()`/`toDomain()` fazendo a conversão — um padrão de mapper simples e consistente, repetido nos 4 módulos.
- As implementações de repositório (`*RepositoryImpl`) isolam o Spring Data JPA atrás das interfaces definidas em `domain/repository`, respeitando a inversão de dependência.
- Anotações de documentação (Swagger/OpenAPI) ficam só na camada `presentation`, não vazam para `domain` nem `application`.

**Nenhum ponto crítico encontrado nessa camada.**

---

## 2. Nomenclatura e consistência

- Nomenclatura em português para o domínio de negócio (`Restaurante`, `TipoUsuario`, `ItemCardapio`) e em inglês para termos técnicos (`Controller`, `Service`, `Repository`, `Entity`) — padrão consistente nos 4 módulos, sem mistura dentro da mesma classe.
- Endpoints REST seguem convenção de substantivo no singular/plural coerente: `/restaurante`, `/restaurante/{id}`, `/restaurante/{restauranteId}/cardapio`, `/cardapio/{id}`.
- Mensagens de validação (`@NotBlank`, `@NotNull`, `@Positive`) estão todas em português e são específicas por campo — boa experiência para quem consome a API.

**Sugestão (não bloqueante):** os services lançam `RuntimeException` genérica para "recurso não encontrado" (ex.: `ItemCardapioService.buscarPorId`, linha com `throw new RuntimeException(...)`). Funciona porque o `GlobalExceptionHandler` trata `RuntimeException` como 404 globalmente, mas exceções customizadas (`RecursoNaoEncontradoException`, por exemplo) tornariam a intenção mais explícita e evitariam capturar acidentalmente uma `RuntimeException` de outra origem como se fosse "não encontrado". Como isso tocaria os 4 módulos igualmente, decidi não alterar agora — é um refactor de escopo maior que pode ser proposto como próximo passo, não como bloqueio da entrega.

---

## 3. Boas práticas Spring Boot

- Injeção de dependência via construtor em todos os controllers e services (sem `@Autowired` em campo) — boa prática consolidada.
- `@RestControllerAdvice` centraliza o tratamento de exceções (`GlobalExceptionHandler`), evitando `try/catch` repetido nos controllers.
- Validação de entrada via Bean Validation (`@Valid` + anotações nos DTOs), delegando ao Spring o `400 Bad Request` — controllers ficam limpos, sem validação manual.
- Uso de `ResponseEntity` com status HTTP explícito (`201 Created`, `204 No Content`, `404 Not Found`) em vez de sempre `200 OK` — está alinhado com convenções REST.
- Configuração de datasource e variáveis externalizadas via `application.yml` com defaults (`${DB_HOST:localhost}`), permitindo rodar tanto local quanto containerizado sem alterar código.

**Nenhum ponto crítico encontrado nessa camada.**

---

## 4. Cobertura de testes

- Testes unitários de serviço cobrem os 4 módulos (`TipoUsuarioServiceTest`, `UsuarioServiceTest`, `RestauranteServiceTest`, `ItemCardapioServiceTest`), que é exatamente o pacote (`application.service.*`) sobre o qual o gate do JaCoCo no `pom.xml` exige ≥80% — medido em **100%** no último relatório.
- Testes de integração cobrem o CRUD completo de Restaurante (`RestauranteControllerIntegrationTest`) e a regra de negócio cruzada restaurante↔cardápio (`RestauranteDeleteGuardIntegrationTest`, que valida o `409 Conflict` ao tentar remover um restaurante com itens de cardápio).
- **Gap identificado e corrigido nesta revisão:** não havia teste de integração do controller de Cardápio. Adicionado `ItemCardapioControllerIntegrationTest`, cobrindo CRUD completo, validação de campos, e o caso de restaurante inexistente (404) — fechando a integração entre os módulos Restaurante e Cardápio do ponto de vista do controller, não só do service.

**Ação recomendada:** rodar `mvn clean verify` após incluir o teste novo e confirmar no relatório do JaCoCo (`target/site/jacoco/index.html`) que o pacote `presentation.controller` subiu de cobertura em relação ao relatório anterior (57%).

---

## 5. Infraestrutura Docker

- `docker-compose.yml` configura Postgres com `healthcheck` (`pg_isready`) e a aplicação só sobe com `depends_on: condition: service_healthy` — evita a race condition clássica de a aplicação tentar conectar antes do banco estar pronto.
- Variáveis de ambiente do banco totalmente externalizadas, com defaults sensatos.
- **Corrigido nesta revisão:** a imagem de runtime do `Dockerfile` (`eclipse-temurin:17-jre-alpine`) falhava ao resolver o manifest multi-arquitetura em Mac com Apple Silicon. Trocada para `eclipse-temurin:17-jre-jammy`, que tem suporte multi-arch mais estável — build e execução confirmados localmente.
- Removido o atributo `version:` do `docker-compose.yml` (obsoleto no Compose v2, gerava warning).

---

## 6. Resumo

| Categoria                                     | Status                                            |
| --------------------------------------------- | ------------------------------------------------- |
| Organização de pacotes / Clean Architecture   | ✅ Sem pendências                                 |
| Nomenclatura                                  | ✅ Sem pendências (1 sugestão não bloqueante)     |
| Boas práticas Spring Boot                     | ✅ Sem pendências                                 |
| Cobertura de testes (service, gate do JaCoCo) | ✅ 100% no `application.service`                  |
| Testes de integração (controller)             | ✅ Gap do módulo Cardápio corrigido nesta revisão |
| Docker Compose                                | ✅ Corrigido (imagem base + warning)              |
| Documentação (README)                         | ✅ Revisada nesta rodada                          |

**Conclusão:** projeto atende aos critérios de qualidade de código e Clean Architecture da Fase 2. O único item pendente antes da entrega final é a gravação e publicação do vídeo de apresentação, com o link atualizado no README.
