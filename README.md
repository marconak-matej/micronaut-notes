# Micronaut Notes

A comprehensive exploration of Micronaut 5.x features and improvements through practical examples and demonstrations, contrasted with Spring Boot equivalents.

## Overview

This project serves as a practical guide to Micronaut 5.x, containing multiple modules that each focus on different aspects of the framework. Each module mirrors a corresponding Spring Boot module from the [boot-notes](../spring-boot-4-boot-notes) project, highlighting the differences in approach between the two frameworks.

## Prerequisites

- Java 25 or higher
- Maven 3.8+
- Micronaut 5.0.4

## Project Structure

```
micronaut-notes/
├── start-up/             # Application startup lifecycle hooks and callbacks
└── [future-modules]      # Placeholder for upcoming modules
```

## Getting Started

1. Clone the repository
2. Build all modules:
   ```bash
   ./mvnw clean install
   ```
3. Run a specific module:
   ```bash
   ./mvnw mn:run -pl <module-name>
   ```

## Modules

### 1. Startup Module

The start-up module demonstrates the **Micronaut application startup lifecycle**, showcasing the precise order in which initialization hooks, event listeners, and callbacks are invoked during application bootstrap. This is the Micronaut equivalent of the Spring Boot startup lifecycle module.

#### Features
- **Lifecycle Hook Execution Order**
  - Application context starting
  - `@Factory` + `@Bean` -- Factory method for external/unmodifiable classes (resolved first via dependency)
  - Constructor -- Bean instantiation
  - `BeanInitializedEventListener` -- Fires before `@PostConstruct`
  - `@PostConstruct` -- Jakarta lifecycle callback
  - `BeanCreatedEventListener` -- Fires after `@PostConstruct`
  - `StartupEvent` -- Context fully loaded (analogous to `SmartInitializingSingleton` + `ContextRefreshedEvent`)
  - `ServerStartupEvent` -- HTTP server ready (analogous to `ApplicationReadyEvent`)

- **Key Differences from Spring Boot**
  - No `ApplicationContextInitializer` -- Use `Micronaut.build()` directly
  - No `InitializingBean` -- Consolidate into `@PostConstruct` or `BeanInitializedEventListener`
  - `BeanInitializedEventListener` fires **before** `@PostConstruct` (opposite of Spring!)
  - `BeanInitializedEventListener` requires `@PostConstruct` to fire (Spring's `InitializingBean` fires regardless)
  - Post-startup execution via `@EventListener(StartupEvent)` instead of `ApplicationRunner`/`CommandLineRunner`

## Build and Development

### Build System
- Maven-based multi-module project
- Spotless for code formatting with Palantir Java format rules
- Module-specific dependencies managed through individual POMs

### Code Style
- Consistent formatting using Spotless
- Palantir Java format rules applied
- Automated formatting during compile phase

## Version Information

- Micronaut: 5.x.x
- Java: 25
