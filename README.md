# Employee Management System (REST API Backend)

A Java / Spring Boot backend for managing employees and departments, built with clean layered
architecture (Controller -> Service -> Repository), Spring Data JPA, MySQL, validation and
centralised exception handling. One reporting query uses plain JDBC.

## Tech stack
- Java 17+
- Spring Boot 3.2.5 (Web, Data JPA, Validation)
- Spring Data JPA (Hibernate) and JDBC
- MySQL 8
- Maven
- Postman (API testing)
- JUnit 5 + Mockito (unit tests)

## Features
- Full CRUD for **Departments** and **Employees** (one department has many employees)
- Input validation (`@NotBlank`, `@Email`, `@Positive`, `@NotNull`) with field-level error messages
- Global exception handling: 400 (validation), 404 (not found), 409 (duplicate / conflict), 500
- Pagination, sorting and filtering by department: `GET /api/employees?departmentId=1&page=0&size=5&sort=name,asc`
- Plain-JDBC report: `GET /api/reports/department-summary` (employee count and average salary per department)
- DTOs so database entities are never exposed directly

## Prerequisites
1. JDK 17 or newer (`java -version`)
2. Maven 3.8+ (`mvn -version`), or open the folder in IntelliJ IDEA / Eclipse / STS
3. MySQL 8 running locally on port 3306

## Setup and run
1. Open `src/main/resources/application.properties` and set your MySQL username and password
   (`spring.datasource.username` / `spring.datasource.password`).
2. You do not need to create the database or tables; `employee_db` and the tables are created automatically on first run.
3. From the project folder run:

```
mvn clean install
mvn spring-boot:run
```

4. The API starts at `http://localhost:8080`.

## Test with Postman
Import `postman/Employee-Management-System.postman_collection.json`, then run the requests
in order (1 to 17). The collection includes success cases and error cases (400, 404, 409).

## API summary

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/departments | Create a department |
| GET | /api/departments | List departments |
| GET | /api/departments/{id} | Get one department |
| PUT | /api/departments/{id} | Update a department |
| DELETE | /api/departments/{id} | Delete (blocked if it still has employees) |
| POST | /api/employees | Create an employee |
| GET | /api/employees | List employees (paged, optional `departmentId`) |
| GET | /api/employees/{id} | Get one employee |
| PUT | /api/employees/{id} | Update an employee |
| DELETE | /api/employees/{id} | Delete an employee |
| GET | /api/reports/department-summary | JDBC summary report |

Example request body for `POST /api/employees`:

```json
{
  "name": "Ravi Kumar",
  "email": "ravi.kumar@example.com",
  "salary": 55000.00,
  "departmentId": 1
}
```

## Run the unit tests
```
mvn test
```
The tests use Mockito, so they do not need MySQL.

## Project structure
```
src/main/java/com/dinakar/ems
  controller/   REST endpoints
  service/      business logic and transactions
  repository/   Spring Data JPA repositories
  entity/       JPA entities (Department, Employee)
  dto/          request/response objects with validation
  exception/    custom exceptions and global handler
  report/       plain-JDBC reporting DAO
```

## Troubleshooting
- **Access denied for user**: the MySQL username or password in `application.properties` is wrong.
- **Communications link failure**: MySQL is not running or is not on port 3306.
- **Port 8080 already in use**: add `server.port=8081` to `application.properties`.
- **Unsupported class file version**: your JDK is older than 17; install JDK 17 or newer.
