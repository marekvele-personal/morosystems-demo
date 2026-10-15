# morosystems-demo

Spring Boot aplikace na CRUD správu uživatelů s PostgreSQL databází a Liquibase migracemi.

## Spuštění lokálně

Požadavky: docker compose nebo podman compose

```docker compose up --build```

nebo

```podman compose up --build```

## Databáze a inicializace

Databáze není perzistentní a vytváří se čistá při startu kontejneru PostgreSQL.

Po spuštění aplikace se databáze inicializuje přes **Liquibase**:

## Porty

Aplikace běží na:

- **8080** – webová aplikace
- **5432** – PostgreSQL databáze

## Přístup k aplikaci

Po startu otevřete aplikaci na: 

`http://localhost:8080`

Swagger UI najdete na adrese:

`http://localhost:8080/swagger-ui/index.html`

Healthcheck je dostupný na:

`http://localhost:8080/actuator/health`
