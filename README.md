# breakdown-dashboard-svc

The deployable REST API service for the Breakdown expense-splitting platform. This module is the runtime entry point — it exposes HTTP endpoints and delegates all business logic to `breakdown-dashboard`. It is packaged as a **WAR** and deployed to **WildFly 35**.

---

## Module Coordinates

| Property    | Value                            |
|-------------|----------------------------------|
| groupId     | `com.nihith`                     |
| artifactId  | `breakdown-dashboard-svc`        |
| version     | `0.0.1-SNAPSHOT`                 |
| packaging   | `war`                            |
| Java        | 21                               |
| Spring Boot | 3.4.3                            |

---

## Architecture Position

```
breakdown-dashboard-svc        ← (this module) WAR deployed to WildFly 35
        │ depends on
breakdown-dashboard            ← service orchestration layer
        │ depends on
  ┌─────┴─────────────────────┐
breakdown-mongo-adapter    calculation-engine
        │ depends on            │ depends on
        └────────────────────►breakdown-model
```

---

## Package Structure

```
com.nihith.breakdown
├── BreakdownDashboardSvcApplication.java   # Spring Boot application entry point
├── ServletInitializer.java                 # Extends SpringBootServletInitializer for WAR deployment
└── controller/
    └── GroupViewRestController.java        # REST controller exposing all API endpoints
```

---

## API Reference

All endpoints are prefixed with `/group`.

---

### `GET /group/{groupId}/transaction-list`

Retrieve all recorded **expense** transactions for a group.

**Path Parameters**

| Parameter | Type   | Description                       |
|-----------|--------|-----------------------------------|
| `groupId` | String | Unique identifier of the group    |

**Response — `200 OK`**

```json
{
  "data": {
    "groupId": "trip2025",
    "transactionList": [
      {
        "uuid": "e2552fb1-8727-41fd-9ab9-6da937a5125f",
        "transactionName": "Dinner",
        "transactionDescription": "Restaurant bill",
        "transactionType": "EXPENSE",
        "amount": 23.04,
        "paidById": "alice",
        "paidForList": [
          { "paidForId": "bob",   "paidForValue": 10.04 },
          { "paidForId": "carol", "paidForValue": 13.00 }
        ],
        "splitType": "SHARES",
        "timestamp": null,
        "groupId": "trip2025",
        "transactionStatus": "INCOMPLETE"
      }
    ],
    "settlementList": []
  },
  "responseStatus": "SUCCESS",
  "messages": [
    { "messageType": "INFORMATION", "message": "Transaction list fetched successfully" }
  ]
}
```

---

### `GET /group/{groupId}/settlement-list`

Retrieve the current **computed settlement list** for a group. The settlement list is automatically recomputed whenever a new expense is inserted.

**Path Parameters**

| Parameter | Type   | Description                       |
|-----------|--------|-----------------------------------|
| `groupId` | String | Unique identifier of the group    |

**Response — `200 OK`**

```json
{
  "data": {
    "groupId": "trip2025",
    "transactionList": [],
    "settlementList": [
      {
        "uuid": "371a0832-9bc8-4405-a7f9-b69877c59a49",
        "transactionType": "SETTLEMENT",
        "paidById": "bob",
        "paidForList": [
          { "paidForId": "alice", "paidForValue": 26.00 }
        ],
        "groupId": "trip2025",
        "transactionStatus": "INCOMPLETE"
      }
    ]
  },
  "responseStatus": "SUCCESS",
  "messages": [
    { "messageType": "INFORMATION", "message": "Settlement list fetched successfully" }
  ]
}
```

---

### `POST /group/{groupId}/insert-transaction`

Add a new expense transaction to a group. Upon successful insert, the settlement list for the group is automatically recomputed and updated.

**Path Parameters**

| Parameter | Type   | Description                       |
|-----------|--------|-----------------------------------|
| `groupId` | String | Unique identifier of the group    |

**Request Body** — `application/json`

| Field                    | Type            | Required | Description                                              |
|--------------------------|-----------------|----------|----------------------------------------------------------|
| `transactionName`        | String          | Yes      | Name/label of the expense (e.g. "Groceries")             |
| `transactionDescription` | String          | No       | Optional longer description                              |
| `transactionType`        | String (enum)   | No       | `EXPENSE` (default for new transactions)                 |
| `amount`                 | Number          | Yes      | Total expense amount (must be > 0)                       |
| `paidById`               | String          | Yes      | ID of the person who paid                                |
| `paidForList`            | Array           | Yes      | Each participant's share (see below)                     |
| `paidForList[].paidForId`| String          | Yes      | ID of the person/family sharing this expense             |
| `paidForList[].paidForValue` | Number      | Yes      | This participant's share value                           |
| `splitType`              | String (enum)   | No       | `EQUAL`, `SHARES`, `PERCENTAGE`, or `AMOUNT`             |
| `timestamp`              | String (ISO)    | No       | When the expense occurred                                |

> `groupId` is set automatically from the path parameter and should **not** be included in the request body.

**Example Request — Equal split among A, B, C**

```json
POST /group/trip2025/insert-transaction

{
  "transactionName": "Hotel",
  "transactionDescription": "3-night stay",
  "transactionType": "EXPENSE",
  "amount": 6.00,
  "paidById": "alice",
  "paidForList": [
    { "paidForId": "alice", "paidForValue": 2.00 },
    { "paidForId": "bob",   "paidForValue": 2.00 },
    { "paidForId": "carol", "paidForValue": 2.00 }
  ],
  "splitType": "EQUAL"
}
```

**Example Request — Family-level split (B and C are in Family F)**

When B and C belong to Family F, the `paidForList` can reference `familyF` as a single entity. The settlement engine treats it as one debtor, producing a single settlement:

```json
POST /group/trip2025/insert-transaction

{
  "transactionName": "Hotel",
  "transactionType": "EXPENSE",
  "amount": 6.00,
  "paidById": "alice",
  "paidForList": [
    { "paidForId": "alice",   "paidForValue": 2.00 },
    { "paidForId": "familyF", "paidForValue": 4.00 }
  ],
  "splitType": "AMOUNT"
}
```

This results in a single settlement record: `familyF → alice: $4.00` instead of two separate records.

**Response — `200 OK`**

```json
{
  "data": {
    "groupId": "trip2025",
    "transactionList": [ { ... } ],
    "settlementList": [
      {
        "transactionType": "SETTLEMENT",
        "paidById": "familyF",
        "paidForList": [
          { "paidForId": "alice", "paidForValue": 4.00 }
        ],
        "groupId": "trip2025",
        "transactionStatus": "INCOMPLETE"
      }
    ]
  },
  "responseStatus": "SUCCESS",
  "messages": [
    { "messageType": "INFORMATION", "message": "Transaction inserted successfully" }
  ]
}
```

**Response — failure**

```json
{
  "data": null,
  "responseStatus": "FAILURE",
  "messages": [
    { "messageType": "ERROR", "message": "Transaction not inserted" }
  ]
}
```

---

## Validation

Request bodies are validated with Bean Validation (`@Validated`). The following constraints apply:

| Field             | Constraint         |
|-------------------|--------------------|
| `transactionName` | Must not be empty  |
| `amount`          | Must not be null, must be > 0 |
| `paidById`        | Must not be empty  |
| `paidForList`     | Must not be null   |
| `paidForList[].paidForId` | Must not be empty |

---

## Build

```bash
# Build all upstream dependencies first (in order)
cd breakdown-model        ; ./mvnw clean install ; cd ..
cd breakdown-mongo-adapter ; ./mvnw clean install ; cd ..
cd calculation-engine     ; ./mvnw clean install ; cd ..
cd breakdown-dashboard    ; ./mvnw clean install ; cd ..

# Build the WAR
cd breakdown-dashboard-svc
./mvnw clean package
```

The WAR is produced at `target/breakdown-dashboard-svc-0.0.1-SNAPSHOT.war`.

---

## Deployment — WildFly 35

1. Start WildFly:
   ```bash
   wildfly-35.0.1.Final/bin/standalone.bat   # Windows
   wildfly-35.0.1.Final/bin/standalone.sh    # Linux/macOS
   ```

2. Set the required environment variables (or configure them in WildFly's `standalone.xml`):
   - `PREFERRED_DB` — set to `mongo` (or leave default; resolves to MongoDB adapter)
   - `spring.data.mongodb.uri` — MongoDB connection URI
   - `spring.data.mongodb.database` — e.g. `breakdownLoc`

3. Deploy the WAR via the WildFly admin console (`http://localhost:9990`) or by copying it to `wildfly-35.0.1.Final/standalone/deployments/`.

4. The application will be accessible at:
   ```
   http://localhost:8080/breakdown-dashboard-svc/group/{groupId}/...
   ```

---

## Running Tests

```bash
cd breakdown-dashboard-svc
./mvnw test
```

Code coverage reports (JaCoCo) are generated at `target/site/jacoco/index.html`.

---

## Swagger / OpenAPI Documentation

The service uses [springdoc-openapi](https://springdoc.org/) (`2.8.6`) to automatically generate an OpenAPI 3 specification from the controller annotations.

### Accessing the UI and spec

Once the application is running, the following endpoints are available:

| What                      | URL (embedded Tomcat / local)              | URL (WildFly, default context)                                   |
|---------------------------|--------------------------------------------|------------------------------------------------------------------|
| Swagger UI                | `http://localhost:8080/swagger-ui.html`    | `http://localhost:8080/breakdown-dashboard-svc/swagger-ui.html`  |
| OpenAPI JSON spec         | `http://localhost:8080/v3/api-docs`        | `http://localhost:8080/breakdown-dashboard-svc/v3/api-docs`      |
| OpenAPI YAML spec         | `http://localhost:8080/v3/api-docs.yaml`   | `http://localhost:8080/breakdown-dashboard-svc/v3/api-docs.yaml` |

> **Note:** When deployed to WildFly the context root is the WAR filename. The default WAR name is `breakdown-dashboard-svc-0.0.1-SNAPSHOT.war`; rename it to `breakdown-dashboard-svc.war` (or configure the context root in WildFly) to shorten the path.

### Running locally with embedded Tomcat (for Swagger development)

The WAR is packaged with `spring-boot-starter-tomcat` scoped as `provided` for WildFly deployment. To run with the embedded Tomcat locally (so Swagger UI is reachable without WildFly):

1. Temporarily change the `spring-boot-starter-tomcat` scope in `pom.xml` from `provided` to `compile`.
2. Start the application:
   ```bash
   cd breakdown-dashboard-svc
   ./mvnw spring-boot:run
   ```
3. Open `http://localhost:8080/swagger-ui.html` in your browser.
4. Revert the scope change before building the deployment WAR.

### Exporting the spec file

With the application running, export the spec in either format using `curl`:

**JSON**
```bash
curl http://localhost:8080/v3/api-docs -o breakdown-api-spec.json
```

**YAML**
```bash
curl http://localhost:8080/v3/api-docs.yaml -o breakdown-api-spec.yaml
```

### Generating the spec at build time (Maven plugin)

The `springdoc-openapi-maven-plugin` can generate the spec during `mvn verify` without a browser. It requires the app to start during the integration-test phase:

1. Add the plugin to `pom.xml` inside `<build><plugins>`:
   ```xml
   <plugin>
       <groupId>org.springdoc</groupId>
       <artifactId>springdoc-openapi-maven-plugin</artifactId>
       <version>1.4</version>
       <executions>
           <execution>
               <id>generate-openapi-spec</id>
               <goals>
                   <goal>generate</goal>
               </goals>
           </execution>
       </executions>
       <configuration>
           <apiDocsUrl>http://localhost:8080/v3/api-docs</apiDocsUrl>
           <outputFileName>breakdown-api-spec.json</outputFileName>
           <outputDir>${project.build.directory}</outputDir>
       </configuration>
   </plugin>
   <!-- spring-boot-maven-plugin must also be configured to start/stop the app -->
   ```
2. Run:
   ```bash
   ./mvnw verify
   ```
   The generated file will be at `target/breakdown-api-spec.json`.

### Swagger UI features available

- **Try it out** — execute live API calls directly from the browser.
- **Examples** — all three endpoints include pre-filled request/response examples covering equal split, family split, shares split, and percentage split scenarios.
- **Schema explorer** — click on `Transaction`, `ResponseStructure`, etc. to inspect all fields, types, and validation constraints.

---

| Dependency                              | Purpose                                          |
|-----------------------------------------|--------------------------------------------------|
| `com.nihith:breakdown-dashboard:0.0.1`  | Service layer (includes mongo adapter + engine)  |
| `spring-boot-starter-web`              | Spring MVC, embedded Tomcat (provided for WAR)   |
| `spring-boot-starter-tomcat`           | `provided` — WildFly supplies the servlet container |

