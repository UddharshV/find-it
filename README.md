# find-it
FindIt is a campus lost-and-found web application that helps users report, browse, and claim lost or found items. The application is designed around a simple workflow where reported items can move from open to claimed to returned, while providing a practical full-stack implementation using Angular, Spring Boot, and PostgreSQL.

## Tech Stack
- **Backend:** Java 21, Spring Boot 4, Spring Data JPA (Hibernate), Jakarta Validation
- **Database:** PostgreSQL
- **Frontend:** Angular (standalone components, signals, Reactive Forms), TypeScript

## Project Structure
```
findit/
 ├─ src/main/java/com/uddharsh/findit     Spring Boot backend
 │   ├─ FinditApplication.java
 │   ├─ config/        CORS configuration
 │   ├─ controller/    REST endpoints
 │   ├─ service/       Business rules and workflow
 │   ├─ repository/    Spring Data JPA repositories
 │   ├─ entity/        JPA entities and enums
 │   ├─ dto/           Request and response objects
 │   └─ exception/     Custom exceptions and global error handler
 ├─ src/test/java/com/uddharsh/findit     Backend tests
 │   ├─ entity/        Domain model tests
 │   └─ service/       Workflow rule tests
 ├─ frontend/src/app                      Angular frontend
 │   ├─ pages/         Item list, item detail, report/edit form
 │   ├─ services/      API calls (items, claims, users)
 │   ├─ models/        TypeScript types mirroring the API
 │   ├─ interceptors/  Adds the X-User-Id header to every request
 │   ├─ pipes/         Display formatting (labels, dates)
 │   └─ utils/         API error handling
 └─ db/                Demo data scripts
```

## Domain Model
```
User 1 ──── * Item     (a user reports many items)
User 1 ──── * Claim    (a user files many claims)
Item 1 ──── * Claim    (an item receives many claims)
```

| Entity | Table | Key fields |
|---|---|---|
| `User` | `users` | name, email (unique, stored lowercase), createdAt |
| `Item` | `items` | title, description, type, status, category, location, eventDate, imageUrl, reportedBy, timestamps, version |
| `Claim` | `claims` | item, claimant, message, status, timestamps, version |

**Enums**
- `ItemType`: LOST, FOUND
- `ItemStatus`: OPEN, CLAIMED, RETURNED
- `ClaimStatus`: PENDING, APPROVED, REJECTED
- `Category`: ELECTRONICS, ID_CARD, KEYS, CLOTHING, BAGS, BOTTLES, BOOKS, OTHER

**Database rules**
- A user can claim the same item only once (unique `item_id, claimant_id`).
- Items are indexed on status, type, category and reporter for fast filtering.
- `@Version` on `Item` and `Claim` prevents conflicting concurrent updates (optimistic locking).

## Workflow Rules
Enforced in the service layer.

| Action | Who | Allowed when |
|---|---|---|
| Report an item | Any user | Item starts as `OPEN` |
| Edit or delete an item | Reporter only | Item is `OPEN` (delete also requires no claims) |
| File a claim | Any user except the reporter | Item is `OPEN`, one claim per user per item |
| View an item's claims | Reporter only | Any time |
| Approve a claim | Reporter only | Claim is `PENDING`, item is `OPEN` |
| Reject a claim | Reporter only | Claim is `PENDING` |
| Mark item returned | Reporter only | Item is `CLAIMED` |

Approving a claim sets the claim to `APPROVED`, the item to `CLAIMED`, and rejects all other pending claims on that item in a single transaction.

## Frontend
| Page | URL | What it does |
|---|---|---|
| Browse | `/` | Paginated grid of items with type and status badges |
| Item detail | `/items/{id}` | Full details. The reporter sees claims with Approve/Reject, Mark as returned, Edit and Delete; other users see a claim form |
| Report an item | `/report` | Form with browser-side validation plus field errors from the API |
| Edit an item | `/items/{id}/edit` | Same form, pre-filled; lost/found cannot be changed |

Until authentication is added, the **Acting as** dropdown in the header picks the current user. An HTTP interceptor sends that user's id as the `X-User-Id` header on every request, and the choice is remembered across page reloads.

## REST API
| Method | Endpoint | Header | Description |
|---|---|---|---|
| GET | `/api/users` | | List users |
| POST | `/api/users` | | Create a user |
| GET | `/api/users/{id}` | | Get a user |
| GET | `/api/items?page=0&size=20&sort=createdAt,desc` | | List items (paginated, newest first by default) |
| POST | `/api/items` | `X-User-Id` | Report an item |
| GET | `/api/items/{id}` | | Get an item |
| PUT | `/api/items/{id}` | `X-User-Id` | Edit an item |
| DELETE | `/api/items/{id}` | `X-User-Id` | Delete an item |
| POST | `/api/items/{id}/return` | `X-User-Id` | Mark an item returned |
| POST | `/api/items/{id}/claims` | `X-User-Id` | File a claim |
| GET | `/api/items/{id}/claims` | `X-User-Id` | List an item's claims |
| POST | `/api/claims/{id}/approve` | `X-User-Id` | Approve a claim |
| POST | `/api/claims/{id}/reject` | `X-User-Id` | Reject a claim |

**Error responses** use the standard `ProblemDetail` JSON format:

| Status | When |
|---|---|
| 400 Bad Request | Validation failed (field errors listed under `errors`), malformed JSON, or invalid enum value |
| 403 Forbidden | The acting user is not the item's reporter |
| 404 Not Found | User, item or claim does not exist |
| 409 Conflict | Rule violation, such as a duplicate email, an invalid status change, or a concurrent update |

CORS allows requests from the Angular dev server at `http://localhost:4200`.

## Getting Started

### Prerequisites
- Java 21
- PostgreSQL running locally on port 5432
- Node.js 20 or newer, and the Angular CLI (`npm install -g @angular/cli`)

### Database setup
```bash
psql -U postgres -c "CREATE DATABASE findit OWNER findit_app;"
psql -U postgres -c "CREATE DATABASE findit_test OWNER findit_app;"
```
Tables are created automatically by Hibernate on startup. `findit` is used when running the app, and `findit_test` when running tests.

### Credentials
Database credentials are never committed. Provide them in either of two ways:

- **Environment variables:**
  ```bash
  export DB_USERNAME=findit_app
  export DB_PASSWORD=yourpassword
  ```
- **A local `application.properties` in the project root** (ignored by git):
  ```properties
  spring.datasource.username=findit_app
  spring.datasource.password=yourpassword
  ```

### Run the backend
```bash
./mvnw spring-boot:run
```
The API is available at `http://localhost:8080`. Start it once before loading demo data, so Hibernate creates the tables.

### Load demo data (optional)
```bash
psql -U findit_app -d findit -f db/seed-data.sql    # resets the database: 5 users, 14 items, 10 claims
psql -U findit_app -d findit -f db/more-items.sql   # adds 10 more items and 6 claims
```
`seed-data.sql` deletes all existing data first, so only run it against a development database.

### Run the frontend
```bash
cd frontend
npm install
ng serve
```
Open `http://localhost:4200`. The backend must be running on port 8080.

### Run the tests
```bash
./mvnw test
```
Tests run against the `findit_test` database and roll back after each test, so no data is left behind.

## Progress
- [x] Spring Boot project setup with PostgreSQL connection
- [x] Domain model: `User`, `Item`, `Claim` entities and relationships
- [x] Repositories and domain model tests
- [x] Service layer with workflow rules and tests
- [x] REST API controllers with validation, pagination, and error handling
- [x] Angular frontend: browse, detail, claims workflow, report/edit form, pagination

## Future Work
- Search and filters on the item list
- A "my claims" view, so claimants can track their claims after leaving an item's page
- Authentication (Spring Security), replacing the `X-User-Id` header
- Deployment