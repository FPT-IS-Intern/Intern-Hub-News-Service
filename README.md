# Intern Hub News Service

Base News-Service skeleton aligned with the multi-module structure (`api`, `core`, `infra`, `app`).

## Current Flow

- `POST /news` create news
- `GET /news` list news
- `GET /news/{id}` get detail
- `PUT /news/{id}` update news
- `DELETE /news/{id}` delete news
- `GET /news/health` health check

## Modules

- `core`: domain model, repository port, use case
- `infra`: JPA entity/repository implementation
- `api`: REST controllers and request/response DTOs
- `app`: Spring Boot entrypoint and bean wiring

## Database

Liquibase base migration creates `news` table:
- `app/src/main/resources/db/changelog/v1.0/001-create-news-table.yml`

## Quick Run

```powershell
./gradlew clean test
./gradlew :app:bootRun
```

