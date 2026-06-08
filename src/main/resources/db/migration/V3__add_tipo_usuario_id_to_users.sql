ALTER TABLE users
    ADD COLUMN tipo_usuario_id BIGINT,
    ADD CONSTRAINT fk_users_tipo_usuario
        FOREIGN KEY (tipo_usuario_id) REFERENCES tipo_usuario (id);
