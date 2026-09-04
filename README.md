# Asset Allocation

A Spring Boot service for tracking personal finance data, including assets and the transactions used to build an investment portfolio.

The first version uses SQLite for local persistence and Flyway for database migrations. All identifiers are UUIDs; the application does not use auto-incrementing numeric IDs.

## Technology

- Java 25
- Spring Boot 4.0.1
- Maven
- SQLite
- Flyway
- Spring JDBC

## Project structure

This is a Maven multi-module project:

```text
finance-domain/       Domain records and enums
finance-persistence/  Database dependencies and persistence layer foundation
finance-application/  Executable Spring Boot application
```

## Database

The application uses a local SQLite database at `data/asset-allocation.db` by default. The directory must exist before starting the application:

```powershell
New-Item -ItemType Directory -Force data
```

The location can be overridden with the `ASSET_ALLOCATION_DB` environment variable.

Flyway creates the following tables on startup:

### `asset`

Stores financial instruments and their metadata:

- `id` — UUID stored as text
- `name` — human-readable asset name
- `isin` — unique ISIN
- `ticker` — optional exchange ticker
- `currency` — ISO currency code
- `created_at`, `updated_at` — timestamps

### `asset_transaction`

Stores purchases and sales:

- `id` — UUID stored as text
- `asset_id` — UUID reference to `asset`
- `transaction_type` — `BUY` or `SELL`
- `transaction_date` — date of the trade
- `quantity` — number of units
- `price` — price per unit
- `currency` — transaction currency
- `fees` — transaction fees, defaulting to zero
- `notes` — optional notes
- `created_at` — creation timestamp

Quantities and monetary values are represented with `BigDecimal` in Java to avoid floating-point rounding errors.

## Build and test

From the repository root:

```powershell
mvn test
```

The integration test starts the application context with a test SQLite database and verifies that both finance tables are created by Flyway.

## Run the server

Create the database directory and start the executable module:

```powershell
New-Item -ItemType Directory -Force data
mvn -pl finance-application -am package
java -jar finance-application\target\finance-application-0.0.1-SNAPSHOT.jar
```

The server listens on port `8080` by default. The health endpoint is:

```text
GET http://localhost:8080/actuator/health
```

## Current scope

The current implementation establishes the server, database connection, migration, and domain foundation. REST endpoints and transaction-management services will be added next.
