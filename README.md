# find-it
FindIt is a campus lost-and-found web application that helps users report, browse, and claim lost or found items. The application is designed around a simple workflow where reported items can move from open to claimed to returned, while providing a practical full-stack implementation using Angular, Spring Boot, and PostgreSQL.

## Tech Stack
- **Backend:** Java 21, Spring Boot 4, Spring Data JPA (Hibernate)
- **Database:** PostgreSQL
- **Frontend:** Angular (planned)

## Project Structure
```
src/main/java/com/uddharsh/findit
 ├─ FinditApplication.java
 ├─ entity/        JPA entities and enums
 ├─ repository/    Spring Data JPA repositories
 ├─ service/       Business rules and workflow
 ├─ dto/           Request objects
 └─ exception/     NotFound, Conflict, Forbidden exceptions
src/test/java/com/uddharsh/findit
 ├─ entity/        Domain model tests
 └─ service/       Workflow rule tests
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
Enforced in the service layer. Until authentication is added, each action takes the acting user's id.

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

**Errors:** `NotFoundException` (missing user, item or claim), `ForbiddenException` (not the reporter), `ConflictException` (rule violation, such as a duplicate email or an invalid status change).

## Getting Started

### Prerequisites
- Java 21
- PostgreSQL running locally on port 5432

### Database setup
```bash
psql -U postgres -c "CREATE DATABASE findit;"
```
Tables are created automatically by Hibernate on startup.

### Run the app
```bash
export DB_USER=postgres
export DB_PASSWORD=yourpassword
./mvnw spring-boot:run
```

### Run the tests
```bash
./mvnw test
```
Tests run against the local PostgreSQL database and roll back after each test, so no data is left behind.

## Progress
- [x] Spring Boot project setup with PostgreSQL connection
- [x] Domain model: `User`, `Item`, `Claim` entities and relationships
- [x] Repositories and domain model tests
- [x] Service layer with workflow rules and tests
- [ ] REST API controllers
- [ ] Angular frontend

## Future Work
- Authentication (Spring Security)