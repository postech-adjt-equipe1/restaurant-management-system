# ─── Stage 1: Build ─────────────────────────────────────────────────────────
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copia o pom.xml e baixa as dependências (cache de camada)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copia o código-fonte e compila (skip tests — testes rodam em CI)
COPY src ./src
RUN mvn package -DskipTests -B

# ─── Stage 2: Runtime ───────────────────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Cria usuário não-root por segurança
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

COPY --from=build /app/target/*.jar app.jar

RUN chown appuser:appgroup app.jar

USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
