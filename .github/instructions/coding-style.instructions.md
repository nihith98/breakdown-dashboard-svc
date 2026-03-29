---
description: "Use when writing or modifying any code in breakdown-dashboard-svc: REST controllers, Swagger/OpenAPI interfaces, OpenAPI config, Spring Boot entry point, or controller tests. Covers controller structure, Swagger annotation patterns, DI conventions, response types, and test setup."
applyTo: "breakdown-dashboard-svc/src/**/*.java"
---

# breakdown-dashboard-svc Coding Style

## Module Role

This is the **REST/web layer** — the only deployable WAR. It owns:
- `@RestController` classes under `controller/`
- Swagger contract interfaces under `doc/`
- OpenAPI configuration under `config/`
- Spring Boot entry point (`BreakdownDashboardSvcApplication`) and WildFly initializer

It depends on `breakdown-dashboard` for service logic and `breakdown-model` for shared types.

---

## Package Structure

```
com.nihith.breakdown
├── BreakdownDashboardSvcApplication.java
├── ServletInitializer.java
├── config/
│   └── OpenApiConfig.java
├── controller/
│   ├── GroupViewRestController.java
│   └── GroupAdminRestController.java
└── doc/
    └── GroupViewApi.java          ← all Swagger annotations live here
```

---

## REST Controllers

### Class Declaration

Every controller:
- Annotates with `@RestController` and `@RequestMapping` at class level
- Implements a corresponding API interface from the `doc/` package
- Uses `private static final Logger logger = LogManager.getLogger(ClassName.class)` (Log4j2, **not** SLF4J/Logback)
- Injects service dependencies with `@Autowired` field injection (no constructor injection, no Lombok)

```java
@RestController
@RequestMapping("/group")
public class GroupViewRestController implements GroupViewApi {

    private static final Logger logger = LogManager.getLogger(GroupViewRestController.class);

    @Autowired
    private GroupViewService groupViewService;
```

### Method Pattern

Each controller method follows exactly this pattern:
1. `@Override` the API interface method
2. Add the HTTP mapping annotation (`@GetMapping`, `@PostMapping`, etc.) with path
3. Log entry: `logger.info("Entered methodName")`
4. (Optional) mutate request fields from path variables (e.g. `transaction.setGroupId(groupId)`)
5. Delegate to service and return — no additional logic

```java
@Override
@GetMapping("/{groupId}/transaction-list")
public ResponseStructure fetchTransactionList(@PathVariable String groupId) {
    logger.info("Entered fetchTransactionList");
    return groupViewService.getTransactions(groupId);
}

@Override
@PostMapping("/{groupId}/insert-transaction")
public ResponseStructure insertTransaction(
        @PathVariable String groupId,
        @Validated @RequestBody Transaction transaction) {
    logger.info("Entered insertTransaction");
    transaction.setGroupId(groupId);
    return groupViewService.insertTransaction(transaction);
}
```

### Response Type

- **Always return `ResponseStructure`** — never `ResponseEntity<T>`, never raw primitives or plain strings
- Use `@Validated` (Spring's, from `org.springframework.validation.annotation`) on request body parameters — not `@Valid`

---

## Swagger / OpenAPI Contract Interface (`doc/`)

All Swagger annotations live on **the interface in `doc/`**, never on the controller class itself. The controller is annotation-free except for `@RestController`, `@RequestMapping`, `@Override`, and the HTTP method annotations.

### Interface Structure

```java
@Tag(
    name = "Group View",
    description = "APIs for managing expense groups ..."
)
public interface GroupViewApi {

    // JSON example strings as interface-level constants
    String EXAMPLE_TRANSACTION_LIST_SUCCESS = "{\"data\":{...}}";
    String EXAMPLE_FAILURE = "{\"data\":null,\"responseStatus\":\"FAILURE\",...}";

    @Operation(
        summary = "Short action phrase",
        description = "Markdown-supported full description. Use **bold** for key terms."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Human-readable outcome description",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ResponseStructure.class),
                examples = {
                    @ExampleObject(name = "success", summary = "...", value = EXAMPLE_TRANSACTION_LIST_SUCCESS),
                    @ExampleObject(name = "failure", summary = "...", value = EXAMPLE_FAILURE)
                }))
    })
    ResponseStructure fetchTransactionList(
        @Parameter(description = "Unique identifier of the expense group", example = "trip2025", required = true)
        String groupId);
}
```

### Swagger Annotation Rules

- Use `@Tag` at the interface level (not per method)
- Use `io.swagger.v3.oas.annotations.parameters.RequestBody` (Swagger's) for documenting request bodies on the interface — not Spring's `@RequestBody`
- Example JSON strings must be stored as **interface-level `String` constants** and referenced via the constant name in `@ExampleObject.value`
- Each endpoint should have at least a `success` and a `failure` example in `@ExampleObject`
- Use `@Schema(implementation = ResponseStructure.class)` as the schema for all 200 responses

### OpenAPI Bean (`config/OpenApiConfig.java`)

Use the fluent programmatic API — not annotations:

```java
@Configuration
public class OpenApiConfig {

    @Value("${spring.application.name:breakdown-dashboard-svc}")
    private String applicationName;

    @Bean
    public OpenAPI breakdownOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Breakdown Dashboard API")
                        .version("0.0.1-SNAPSHOT")
                        .description("...")
                        .contact(new Contact().name("Nihith")))
                .servers(List.of(new Server().url("/").description("Default server")));
    }
}
```

---

## Dependency Injection

- Use `@Autowired` field injection exclusively — no constructor injection, no `@RequiredArgsConstructor`
- No Lombok of any kind

---

## Properties

- Store Spring-managed properties in `application.properties` (flat key-value, not YAML)
- Access single property values with `@Value("${key:defaultValue}")` — include a default
- Do **not** use `@ConfigurationProperties`
- Runtime environment configuration (DB URLs, etc.) is accessed via `EnvironmentUtil.getEnvironmentVariable()` from `breakdown-model`, not via `@Value`

```properties
spring.application.name=breakdown-dashboard-svc
debug=true
logging.level.com.nihith=DEBUG
logging.level.org.springframework.web=DEBUG
springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.operationsSorter=method
springdoc.swagger-ui.docExpansion=list
```

---

## Logging

- Use **Log4j2** (`org.apache.logging.log4j.Logger`, `LogManager.getLogger(...)`) — not SLF4J or Logback
- Declare as `private static final Logger logger = LogManager.getLogger(ClassName.class)`
- Log entry at the start of every controller method: `logger.info("Entered methodName")`
- Logging configuration is in `log4j2.xml` (not `logback.xml`)

---

## JavaDoc

Use full prose JavaDoc on all public methods. Patterns:

- **Class-level**: Single sentence describing the class' responsibility. Reference the API interface with `{@link}`.

  ```java
  /**
   * REST controller that exposes the Group View endpoints under the {@code /group} base path.
   * The OpenAPI contract is defined in {@link GroupViewApi}; this class contains only
   * routing and delegation logic.
   */
  ```

- **Override methods**: Use `{@inheritDoc}` plus a `<p>` tag noting which service method is called:

  ```java
  /**
   * {@inheritDoc}
   * <p>Delegates to {@link GroupViewService#getTransactions(String)}.</p>
   */
  ```

- Use `{@link}`, `{@code}`, `{@value}` inline tags
- Use `@param`, `@return`, `@throws` for method parameters
- No `@author`, `@version`, or `@since` tags

---

## Tests

### Controller Tests

Use `MockMvcBuilders.standaloneSetup(controller).build()` — not `@SpringBootTest`:

```java
@ExtendWith(MockitoExtension.class)
public class GroupViewRestControllerTest {

    @Mock
    private GroupViewService groupViewService;

    @InjectMocks
    private GroupViewRestController groupViewRestController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(groupViewRestController).build();
        objectMapper = new ObjectMapper();
    }
```

### Test Naming Convention

`methodName_condition_expectedOutcome` — all camelCase within each segment:

```
fetchTransactionList_ValidGroupId_ReturnsTransactions
fetchTransactionList_NoTransactions_ReturnsEmptyList
insertTransaction_success
insertTransaction_failure_db
```

### MockMvc Assertions

- Always assert `status().isOk()`
- Assert `content().contentType(MediaType.APPLICATION_JSON)`
- Use `jsonPath()` to verify specific fields in the response
- Use `verify(service, times(1)).methodName(args)` at the end

### JSON Request Bodies in Tests

Inline as string literals in `.content(...)` — not serialized from POJOs:

```java
mockMvc.perform(post("/group/{groupId}/insert-transaction", groupId)
    .contentType(MediaType.APPLICATION_JSON)
    .content("{\n" +
             "  \"transactionName\": \"TestTransaction\",\n" +
             "  \"amount\": 23.04,\n" +
             "  \"paidById\": \"person1\"\n" +
             "}"))
    .andExpect(status().isOk());
```
