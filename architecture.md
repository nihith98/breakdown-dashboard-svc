# breakdown-dashboard-svc — Module Architecture

> For coding style rules, see `CLAUDE.md` in this directory.

---

## Module Role

This is the **REST/web layer** — the only deployable WAR in the expense system. It owns:
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
