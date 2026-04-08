-- V1: Criação da tabela de usuários

CREATE TABLE users (
    id                   BIGSERIAL PRIMARY KEY,
    nome                 VARCHAR(100)  NOT NULL,
    email                VARCHAR(150)  NOT NULL UNIQUE,
    login                VARCHAR(50)   NOT NULL UNIQUE,
    senha                VARCHAR(255)  NOT NULL,
    data_ultima_alteracao TIMESTAMP    NOT NULL,
    tipo                 VARCHAR(20)   NOT NULL,
    logradouro           VARCHAR(200)  NOT NULL,
    numero               VARCHAR(20)   NOT NULL,
    complemento          VARCHAR(100),
    bairro               VARCHAR(100)  NOT NULL,
    cidade               VARCHAR(100)  NOT NULL,
    estado               VARCHAR(2)    NOT NULL,
    cep                  VARCHAR(9)    NOT NULL
);
