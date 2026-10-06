# Step Classes

This guide defines the responsibility, placement, and implementation rules for Cucumber Step classes.

## Responsibility

A Step class translates Gherkin business language into application-level operations.

Step classes must:

- express business intent
- delegate business operations to UseCases
- delegate multi-UseCase orchestration to Journeys when required
- remain free from channel-specific technical implementation details

Business assertions are allowed inside Step classes.

## Product module organization

Product modules are organized first by execution channel and then vertically by business entity or capability.

Example:

```text
interop/
├── common/
├── bff/
└── web/
```

## Placement

A capability shared across multiple channels must be placed under `common`.

```text
common/<business-entity>/infrastructure/cucumber/<Name>Steps.java
```

A capability exclusive to one channel must be placed under the corresponding channel package.

```text
<channel>/<business-entity>/infrastructure/cucumber/<Name>Steps.java
```

## Naming

Step classes must use the `Steps` suffix.

Example:

```text
AgreementCreationSteps
```

## Top-down implementation

When implementing a Step method, define the business operation you want to invoke before considering its technical implementation.

The Step may call a UseCase API that does not exist yet.

The corresponding UseCase method is implemented in the following phase.

Conceptually:

```text
Feature
  ↓
Step class
  ↓
business intent
  ↓
UseCase / Journey
```

This prevents lower-level technical details from influencing the Gherkin-facing layer.

## Accessing data from previous steps

Step classes must not manually retrieve or reconstruct domain entities produced by previous operations.

Previously created or updated domain entities are accessed through Cucumber ParameterTypes backed by the `EntityStore`.

See:

[Entity Context](./entity-context.md)
