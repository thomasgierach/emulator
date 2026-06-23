# z/OS Emulator + Auth Microservice Demo

This is an in-progress backend/full-stack portfolio project originally built to explore:
- z/OS-style emulator workflows
- gRPC service-to-service communication
- Gradle multi-module project structure
- Spring Boot backend services
- authentication and authorization patterns
- React frontend integration

The project is still under active development. The current focus is building a secure authentication foundation and connecting the frontend/backend services cleanly.

## Current Status

Implemented or partially implemented:
- Gradle multi-module structure
- Spring Boot service setup
- gRPC/protobuf integration
- PostgreSQL-backed auth service
- React login UI
- CORS configuration
- Docker-based local database setup

In progress:
- JWT-based authentication
- role-based authorization
- Spring Security integration
- Testcontainers integration
- z/OS emulator workflow expansion

## Running Locally

```bash
docker compose up --build
```
## Architecture

- `auth` — authentication service with PostgreSQL persistence and gRPC API
- `home` — backend service that communicates with auth over HTTP/gRPC
- `proto` — shared protobuf definitions
- `frontend` — React login UI
- `docker-compose.yml` — local infrastructure for Postgres, Redis, Kafka, and services