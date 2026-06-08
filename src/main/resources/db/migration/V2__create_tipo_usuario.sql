CREATE TABLE tipo_usuario (
    id   BIGSERIAL    PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE
);

INSERT INTO tipo_usuario (nome) VALUES ('Dono de Restaurante');
INSERT INTO tipo_usuario (nome) VALUES ('Cliente');
