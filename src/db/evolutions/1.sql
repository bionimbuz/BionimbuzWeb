
# --- !Ups

CREATE TABLE tb_user (
    email VARCHAR(50) NOT NULL
);

# --- !Downs

DROP TABLE tb_user;
