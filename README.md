# QUARKUS-LOG-ANNOTATION

Exemplo de projeto feito em Quarkus para demonstrar a implementacao de uma anotacao para gravacao de logs de requisicoes

## Dependências
- Quarkus 2.7.5
- JDK 11
- PostgreSQL 12.2
- Docker 4.5.1

## Passos para execução do projeto

> **Passo 1: Execute o docker**
```bash
  docker compose up
```

> **Passo 2: Habilite o flyway.** 
Abra o arquivo **application.properties** e altere a seguinte propriedade:
```bash
  myapp.flyway.migrate = true
```

> **Passo 3: Execute o projeto.** 
```bash
  mvnw quarkus:dev
```

> **Passo 4: Faça uma chamada à API**
```bash
  http://localhost:8080/fruits
```