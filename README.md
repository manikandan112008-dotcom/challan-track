# CHALLAN TRACK – Traffic E-Challan Issue and Repeat Offender Tracker

## 1. Project Title

CHALLAN TRACK – TRAFFIC E-CHALLAN ISSUE AND REPEAT OFFENDER TRACKER

## 2. Problem Statement

Traffic officers often issue paper challans that are poorly tracked and hard to reconcile. The same vehicle can accumulate repeat violations over time, but without a digital system it is difficult to identify offenders and escalate fines correctly. This project solves that by using a Spring Boot REST API with a MySQL database to record vehicles, violation types, challans, and payments.

## 3. Objective

The system must:

- Register vehicles
- Register violation types
- Issue digital challans
- Track repeat offences
- Escalate fines for repeated violations within a rolling 12-month period
- Record payment
- Update challan status to PAID only after a valid payment
- Detect unpaid challans older than 30 days
- Provide REST APIs and Swagger documentation

## 4. Features

- Vehicle management CRUD
- Violation type management CRUD
- Challan issuance with automatic fine escalation
- Repeat violation tracking by vehicle and violation type
- Payment validation and status updates
- Unpaid challan reporting older than 30 days
- Global validation and exception handling
- Swagger UI documentation
- CORS configuration for frontend use

## 5. Technologies

- Java 17
- Spring Boot 3.3.x
- Spring Web
- Spring Data JPA
- Spring Validation
- Hibernate
- MySQL 8.x
- Maven
- SpringDoc OpenAPI

## 6. Architecture

This is a monolithic Spring Boot REST API with the following layers:

- Controller Layer: exposes endpoints
- Service Layer: contains business logic
- Repository Layer: queries the database
- Entity Layer: maps to MySQL tables
- DTO Layer: prevents exposing JPA entities directly
- Exception Layer: handles validation and runtime errors

## 7. Folder Structure

```text
challan-track/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── example/
│   │   │           └── challantrack/
│   │   │               ├── ChallanTrackApplication.java
│   │   │               ├── config/
│   │   │               ├── controller/
│   │   │               ├── dto/
│   │   │               ├── entity/
│   │   │               ├── exception/
│   │   │               ├── repository/
│   │   │               └── service/
│   │   └── resources/
│   │       ├── application.properties
│   │       └── data.sql
│   └── test/
├── pom.xml
├── README.md
├── mvnw
├── mvnw.cmd
└── .mvn/
```

## 8. Database Setup

Create a MySQL database called:

```sql
CREATE DATABASE challan_track;
```

The application uses Hibernate to automatically create/update the required tables.

## 9. MySQL Setup

1. Install MySQL 8.x.
2. Start MySQL.
3. Update the database credentials in `src/main/resources/application.properties`.
4. Use the database name `challan_track`.

## 10. Configure application.properties

```properties
spring.application.name=challan-track

spring.datasource.url=jdbc:mysql://localhost:3306/challan_track
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.defer-datasource-initialization=true
spring.sql.init.mode=always

server.port=8080

springdoc.swagger-ui.path=/swagger-ui.html
springdoc.api-docs.path=/v3/api-docs
```

## 11. How to Run in VS Code

Open the project in VS Code.

From the project root, run:

```powershell
mvn spring-boot:run
```

The project-level Maven configuration automatically uses the mirror in `.mvn/settings.xml`. You can also use the Maven wrapper:

```powershell
./mvnw.cmd spring-boot:run
```

If startup reports that port 8080 is already in use, the application is already running. Reuse `http://localhost:8080/swagger-ui/index.html`, or stop the existing Java process before starting another instance.

## 12. Maven Commands

```powershell
mvn clean install
mvn test
mvn spring-boot:run
```

## 13. API List

### Vehicle APIs

- POST `/api/vehicles`
- GET `/api/vehicles`
- GET `/api/vehicles/{id}`
- PUT `/api/vehicles/{id}`
- DELETE `/api/vehicles/{id}`

### Violation Type APIs

- POST `/api/violation-types`
- GET `/api/violation-types`
- GET `/api/violation-types/{id}`
- PUT `/api/violation-types/{id}`
- DELETE `/api/violation-types/{id}`

### Challan APIs

- POST `/api/challans`
- GET `/api/vehicles/{vehicleId}/challans`
- GET `/api/vehicles/{vehicleId}/repeat-violations`
- GET `/api/challans/unpaid/older-than-30-days`

### Payment API

- POST `/api/payments`

## 14. Sample Requests

### Create Vehicle

```json
{
  "vehicleNumber": "TN01AB1234",
  "ownerName": "Ravi Kumar",
  "vehicleType": "Car",
  "ownerPhone": "9876543210"
}
```

### Create Violation Type

```json
{
  "violationCode": "SPEED001",
  "violationName": "Speeding",
  "description": "Overspeeding violation",
  "baseFine": 1000
}
```

### Issue Challan

```json
{
  "vehicleId": 1,
  "violationTypeId": 1,
  "officerName": "Officer Kumar",
  "violationDate": "2026-09-28"
}
```

### Create Payment

```json
{
  "challanId": 1,
  "paymentReference": "PAY123456",
  "amount": 2000,
  "paymentMethod": "UPI",
  "paymentDate": "2026-09-28"
}
```

## 15. Sample Responses

### Challan Response

```json
{
  "id": 1,
  "challanNumber": "CH-20260928-0001",
  "vehicleNumber": "TN01AB1234",
  "violationType": "Speeding",
  "officerName": "Officer Kumar",
  "violationDate": "2026-09-28",
  "originalFine": 1000,
  "applicableFine": 1000,
  "status": "UNPAID"
}
```

## 16. Fine Escalation Explanation

The fine increases according to repeated occurrences of the same violation type for the same vehicle within the last 12 months.

Formula:

```text
applicableFine = baseFine × (previousOccurrences + 1)
```

Examples:

- First speeding: 1000
- Second speeding: 2000
- Third speeding: 3000
- Fourth speeding: 4000

This does not apply across different violation types.

## 17. Payment Workflow

1. User sends payment request.
2. System checks challan exists.
3. System checks if challan is already PAID.
4. System validates amount matches `applicableFine` exactly.
5. System validates payment reference and required fields.
6. Payment is saved.
7. Challan status becomes PAID.

The save payment + status update is enclosed in `@Transactional`.

## 18. 30-Day Unpaid Challan Workflow

The API `/api/challans/unpaid/older-than-30-days` returns challans that are:

- `UNPAID`
- older than 30 days based on violation date

The response includes:

- challan number
- vehicle number
- owner name
- violation type
- applicable fine
- violation date
- number of unpaid days

## 19. Swagger URL

Open:

```text
http://localhost:8080/swagger-ui/index.html
```

## 20. Postman Testing

### Test order

1. Create vehicle
2. Get vehicles
3. Create violation type
4. Get violation types
5. Issue first challan
6. Issue second same violation
7. Issue third same violation
8. Check increased fine
9. Issue different violation
10. Get vehicle challan history
11. Create payment
12. Verify challan becomes PAID
13. Try invalid payment
14. Try payment for already paid challan
15. Test 30-day unpaid API
16. Test invalid vehicle
17. Test invalid violation type
18. Test duplicate vehicle
19. Test validation errors

## 21. Common Errors

- MySQL not running
- Wrong database password
- Database name mismatch
- Invalid request payload
- Payment amount mismatch
- Duplicate vehicle or violation type
- Missing `@Valid` on request body

## 22. Future Enhancements

- Role-based officer login
- Email or SMS notifications
- Dashboard analytics
- Export reports as PDF/Excel
- Fine payment gateway integration
- Vehicle owner history and alerts

## 23. Final Notes

This project is built to be simple, beginner-friendly, modular, and easy to run in VS Code. It is designed for academic use and can be extended into a larger production grade system later.
