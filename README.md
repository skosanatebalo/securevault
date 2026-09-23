# SecureVault

A small Java/PostgreSQL application built to demonstrate core application-security practices end-to-end — authentication, password hashing, brute-force protection, and role-based access control — each backed by real automated tests and containerized for one-command setup anywhere.

![CI](https://github.com/skosanatebalo/securevault/actions/workflows/ci.yml/badge.svg)

## Security features

| Feature | How it works |
|---|---|
| **SQL injection prevention** | All database access goes through `PreparedStatement`, never string-concatenated SQL. |
| **Password hashing** | Passwords are hashed with BCrypt (via jBCrypt), which salts automatically and is deliberately slow, resisting brute-force and rainbow-table attacks. |
| **Brute-force protection** | An account locks for 15 minutes after 5 failed login attempts. A locked account is rejected before password verification runs, so no timing or error-message difference leaks whether the account is locked or the password was wrong. |
| **Username enumeration resistance** | A wrong username and a wrong password return an identical result — an attacker can't tell which one was wrong. |
| **Role-based access control** | Three roles (ADMIN, ANALYST, VIEWER) each map to a fixed set of allowed actions, enforced at the point of action — not just displayed in a menu. |

## Quick start (Docker — no local installs needed)

Requires only Docker and Docker Compose.

    git clone https://github.com/skosanatebalo/securevault.git
    cd securevault
    make up
    make run

Log in with the seeded account: username `admin`, password `AdminPass123!`

To stop everything:

    make down

## Local development (without Docker)

Requires Java 21, Maven, and a local PostgreSQL instance.

    psql -U postgres -c "CREATE DATABASE securevault;"
    psql -U postgres -d securevault -f db/init.sql
    mvn test
    mvn exec:java -Dexec.mainClass="za.co.securevault.Main"

`DatabaseManager` reads `DB_URL`, `DB_USER`, and `DB_PASSWORD` from environment variables, falling back to `jdbc:postgresql://localhost:5432/securevault` / `securevault_user` / `mypassword` if unset — so local runs and Docker both work without code changes.

## Permission matrix

| Action | ADMIN | ANALYST | VIEWER |
|---|:---:|:---:|:---:|
| View assets | Yes | Yes | Yes |
| Add asset | Yes | Yes | No |
| Delete asset | Yes | No | No |
| Manage users | Yes | No | No |

Only an ADMIN sees the "create user" prompt after logging in — the CLI check and the underlying authorization check both call the same `AccessControlService`, so there's a single source of truth for what each role can do.

## Architecture

    za.co.securevault
    Main.java                    - CLI entry point
    model/User.java               - data object
    model/Role.java                - ADMIN, ANALYST, VIEWER
    model/Action.java               - VIEW_ASSETS, ADD_ASSET, DELETE_ASSET, MANAGE_USERS
    database/DatabaseManager.java  - owns the JDBC connection
    repository/UserRepository.java - all SQL for the users table
    security/PasswordUtils.java     - BCrypt hash() / verify()
    service/AuthService.java         - login logic
    service/AccessControlService.java - permission map

Each layer has one responsibility: `model` classes are plain data, `database` only knows how to connect, `repository` is the only place SQL is written, `security` is a standalone utility with no app dependencies, and `service` holds the business rules that tie the other layers together. Dependencies point inward — `AuthService` depends on `UserRepository`, never the other way around — which is what makes each piece independently testable.

## Database schema

- **users** - credentials, role, and lockout state (failed_attempts, locked_until)
- **assets** - tracked hosts (hostname, IP, OS, owner)
- **security_events** - events tied to an asset (foreign key, cascade delete)
- **vulnerabilities** - findings tied to an asset (foreign key, cascade delete)

Full definitions live in db/init.sql, which is the single source of truth used by local setup, Docker, and both CI pipelines.

## Testing

    mvn test

14 tests across unit tests (password hashing, access control) and integration tests that run against a real PostgreSQL instance (repository queries, full login flows including the lockout cycle).

## CI/CD

Both pipelines run the full test suite against a fresh, disposable PostgreSQL instance on every push, then build the Docker image on main:

- GitHub Actions - .github/workflows/ci.yml
- GitLab CI - .gitlab-ci.yml

## Project structure

    src/main/java/...     application code
    src/test/java/...     test suite
    db/init.sql            schema + seed data
    Dockerfile               multi-stage build
    docker-compose.yml        app + PostgreSQL
    Makefile                   build/test/up/run/down/clean
    .github/workflows/ci.yml
    .gitlab-ci.yml

## Disclaimer

This is a learning/demo project. The seeded credentials and default database password are intentionally simple for local demo purposes — rotate them before deploying anywhere real.
