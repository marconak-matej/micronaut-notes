# Startup Module

The start-up module demonstrates the **Micronaut application startup lifecycle**, showcasing the precise order in which initialization hooks, event listeners, and callbacks are invoked during application bootstrap.

## Features

- ✅ **Application Context Starting** -- Entry point, log before Micronaut boots
- ✅ **`@Factory` + `@Bean`** -- Factory method for external/unmodifiable classes (resolved first via dependency)
- ✅ **Constructor** -- Bean constructor invoked during instantiation
- ✅ **BeanInitializedEventListener** -- Fires before `@PostConstruct` (opposite of Spring!)
- ✅ **@PostConstruct** -- Jakarta lifecycle callback (required for `BeanInitializedEventListener` to fire)
- ✅ **BeanCreatedEventListener** -- Fires after `@PostConstruct`, bean fully initialized
- ✅ **StartupEvent** -- Fired once all singletons are initialized (analogous to `SmartInitializingSingleton` + `ContextRefreshedEvent`)
- ✅ **ServerStartupEvent** -- Fired when the embedded HTTP server is ready (analogous to `ApplicationReadyEvent`)

> **Important:** `BeanInitializedEventListener` only fires if the bean has an initialization method (`@PostConstruct` or similar). Without it, Micronaut skips the initialization phase entirely.

## Technology Stack

- **Micronaut 5.x.x** - Application framework
- **Java 25** - Runtime
- **Micronaut HTTP Server Netty** - Embedded server

## Running the Application

```bash
./mvnw mn:run -pl start-up
```

The application starts on port **8080**.

## Startup Lifecycle Execution Order

When you run the application, the startup hooks fire in this exact sequence:

| Order | Hook | Timing |
|-------|------|--------|
| 01 | Application Context Starting | Before Micronaut boots |
| 02 | `@Factory` `@Bean` method | Factory method for external class (dependency resolution) |
| 03 | Constructor | Bean instantiation |
| 04 | `BeanInitializedEventListener` | Fires before `@PostConstruct` |
| 05 | `@PostConstruct` | Jakarta lifecycle callback |
| 06 | `BeanCreatedEventListener` | Fires after `@PostConstruct` |
| 07 | `StartupEvent` | All singletons initialized (once) |
| 08 | `ServerStartupEvent` | HTTP server ready and accepting traffic |

> **Note:** `02 @Factory` fires before `03 Constructor` because `LifecycleBean` depends on `ExternalClass`. Micronaut's dependency resolution forces the `@Factory` method to execute first to satisfy the dependency.

### Expected Console Output

```
01 Application Context Starting         -> before Micronaut boots
02 @Bean factory method                 -> dependency resolved first (LifecycleBean needs ExternalClass)
03 Constructor                          -> bean being instantiated
04 BeanInitializedEventListener         -> before @PostConstruct (per-bean)
05 @PostConstruct                       -> jakarta lifecycle callback (per-bean)
06 BeanCreatedEventListener             -> after @PostConstruct (per-bean)
07 StartupEvent                         -> all singletons ready (once)
08 ServerStartupEvent                   -> HTTP server accepting traffic (last)
```

## External Class Pattern

This module demonstrates the `@Factory` + `@Bean` pattern for creating beans from external/unmodifiable classes:

```java
class ExternalClass {}  // No @Singleton - external/unmodifiable

@Singleton
class LifecycleBean {
    private final ExternalClass externalClass;

    public LifecycleBean(ExternalClass externalClass) {
        log.info("03 Constructor");
        this.externalClass = externalClass;
    }
}

@Factory
class ExternalClassFactory {
    @Singleton
    ExternalClass externalClass() {
        log.info("02 @Bean factory method (dependency resolved first)");
        return new ExternalClass();
    }
}
```

**When to use `@Factory` + `@Singleton`:**
- Third-party libraries where you can't add `@Singleton`
- Legacy code you can't modify
- Classes from external JARs

## Key Differences from Spring Boot

Micronaut's startup lifecycle is **linear and streamlined** compared to Spring Boot's dynamic, reflection-heavy approach. The key architectural differences:

- **AOT Compilation**: Micronaut uses Ahead-of-Time compilation instead of runtime reflection and proxy generation
- **No `InitializingBean`**: Consolidate into `@PostConstruct` or `BeanInitializedEventListener`
- **Event listener ordering**: `BeanInitializedEventListener` fires **before** `@PostConstruct` (opposite of Spring's `InitializingBean.afterPropertiesSet()`)
- **No `@Bean(initMethod)`**: Use `@PostConstruct` for initialization callbacks
- **`Micronaut.build()` replaces `SpringApplication`**: Configure the context builder directly in `main()`
- **Initialization phase dependency**: `BeanInitializedEventListener` only fires if the bean has `@PostConstruct` or similar. Without it, the initialization phase is skipped entirely. Spring Boot's `InitializingBean.afterPropertiesSet()` fires regardless.
- **Post-startup execution**: Unlike Spring Boot's dedicated `ApplicationRunner`/`CommandLineRunner` interfaces, Micronaut relies on standard event listeners like `@EventListener(StartupEvent)` for post-startup execution.