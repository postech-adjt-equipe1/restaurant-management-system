# Changelog

Todas as mudanças relevantes deste projeto serão documentadas aqui.

Formato baseado em [Keep a Changelog](https://keepachangelog.com/pt-BR/1.0.0/).

---

## [1.0.0] - 2026-06-18

### Adicionado
- Entidade `TipoUsuario` com campo `nome` (ex: "Dono de Restaurante", "Cliente")
- CRUD completo para `/tipo-usuario` (POST, GET, GET/{id}, PUT/{id}, DELETE/{id})
- Entidade `Usuario` com campo `tipoUsuarioId` associando ao `TipoUsuario`
- CRUD completo para `/usuario` (POST, GET, GET/{id}, PUT/{id}, DELETE/{id})
- Validação: ao criar/atualizar usuário, verifica se o `tipoUsuarioId` existe
- Tratamento global de exceções (`GlobalExceptionHandler`) com respostas padronizadas
- Validação de campos obrigatórios via Bean Validation (`@NotBlank`, `@Email`, `@NotNull`)
- `docker-compose.yml` subindo aplicação Java + PostgreSQL com variáveis de ambiente externalizadas
- `Dockerfile` com build multi-stage (Maven → JRE Alpine)
- `.env.example` com modelo das variáveis de ambiente necessárias
- 20 testes unitários cobrindo todos os casos de `TipoUsuarioService` e `UsuarioService`
- Cobertura de linha ≥ 80% verificada pelo JaCoCo (`mvn verify`)
- Estrutura em camadas Clean Architecture: `domain`, `application`, `infrastructure`, `presentation`
