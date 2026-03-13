# 01 - Structure

Modules:
- `api`: controllers and DTOs
- `core`: domain model, ports, use case
- `infra`: JPA entity and repository adapters
- `app`: Spring Boot bootstrap and bean configuration

Dependency direction: `api -> core <- infra`, and `app` wires all modules.

