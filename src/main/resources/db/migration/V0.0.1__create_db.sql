CREATE SEQUENCE SQ_LOGS START 1;

CREATE TABLE public.logs (
	id int8 NOT NULL GENERATED BY DEFAULT AS IDENTITY,
	nome varchar(255) NOT NULL,
    ini timestamp NOT NULL,
    fim timestamp,
    sucesso boolean,
    operacao int NOT NULL,
    contrato varchar(20),
	CONSTRAINT logs_pkey PRIMARY KEY (id)
);