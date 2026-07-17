# Shutdown Module

The shutdown module demonstrates the **Micronaut application shutdown lifecycle**, showcasing every mechanism available for running cleanup code when the application stops.

## Features

- ✅ **@PreDestroy** — Standard Jakarta annotation for local bean cleanup
- ✅ **LifeCycle.stop()** — `LifeCycle` interface called during context shutdown
- ✅ **@Bean(preDestroy="...")** — Named cleanup method for third-party types from a `@Factory`
- ✅ **@EventListener(ServerShutdownEvent)** — Fires when the HTTP server stops
- ✅ **@EventListener(ApplicationShutdownEvent)** — Fires when the embedded application stops
- ✅ **@EventListener(ShutdownEvent)** — Fires when the `BeanContext` begins closing
- ✅ **BeanPreDestroyEventListener\<T\>** — Programmatic listener reacting to another bean's destruction
- ✅ **JVM shutdown hook** — `Runtime.getRuntime().addShutdownHook(...)` for last-resort cleanup

## Technology Stack

- **Micronaut 5.x.x** — Application framework
- **Java 25** — Runtime
- **Micronaut HTTP Server Netty** — Web server

## Running the Application

```bash
./mvnw mn:run -pl shut-down
```

The application runs on a random port. Press `Ctrl+C` to trigger the shutdown sequence.

## Shutdown Mechanisms Reference

| Mechanism                                  | Type              | When it fires              | Use case                                  |
|--------------------------------------------|-------------------|----------------------------|-------------------------------------------|
| `@PreDestroy`                              | Annotation        | Bean destruction           | Release this bean's resources             |
| `LifeCycle.stop()`                         | Interface         | Context shutdown phase     | Context-aware shutdown                    |
| `@Bean(preDestroy="...")`                  | `@Bean` attribute | Bean destruction           | Third-party type cleanup                  |
| `@EventListener(ShutdownEvent)`            | Event             | Context closing            | Global shutdown, no HTTP server needed    |
| `@EventListener(ApplicationShutdownEvent)` | Event             | Application stopping       | App-level shutdown logic                  |
| `@EventListener(ServerShutdownEvent)`      | Event             | Server stopping            | Deregister from discovery, drain requests |
| `BeanPreDestroyEventListener<T>`           | Listener          | Before target bean destroy | React to another bean's destruction       |
| JVM shutdown hook                          | Runtime API       | Ctrl+C / SIGTERM           | Last-resort cleanup                       |

Micronaut does not guarantee shutdown/destruction order beyond what bean dependency graphs dictate.