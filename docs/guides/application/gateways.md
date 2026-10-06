# Gateways

This guide defines the responsibility and implementation rules for Gateways.

## Placement

The Gateway interface belongs to the `application` layer.

Concrete Gateway implementations belong to the `infrastructure` layer of each execution channel.

Example:

```text
common/<business-entity>/application/
└── AgreementGateway.java

bff/<business-entity>/infrastructure/
└── BffAgreementGateway.java

web/<business-entity>/infrastructure/
└── WebAgreementGateway.java
```

## Responsibility

A Gateway represents the boundary between application-level business intent and channel-specific infrastructure.

Each channel-specific Gateway implements the same business contract through its own technical channel.

```text
UseCase
   ↓
Gateway interface
   ↓
┌───────────────────┬───────────────────┐
│ BFF Gateway       │ WEB Gateway       │
│       ↓           │       ↓           │
│ RestClient        │ SUIT / WebPage    │
└───────────────────┴───────────────────┘
```

A Gateway is responsible for:

- adapting the business request to the underlying channel
- mapping technical models to domain entities
- managing QA-specific infrastructure concerns
- updating shared test contexts such as the `EntityStore`
- handling polling and synchronization when required
- delegating low-level interaction to RestClient or SUIT/Page components

The UseCase must remain unaware of these technical details.

## EntityStore ownership

Gateways are responsible for writing updated domain entities into the `EntityStore` when the result of an operation must be available to subsequent test steps.

See:

[Entity Context](../cucumber/entity-context.md)

## BFF Gateway

A BFF Gateway uses a RestClient.

Conceptually:

```text
BFF Gateway
  ↓
RestClient
  ↓
Generated OpenAPI client
  ↓
RestAssured
```

The RestClient is a QA abstraction around an API generated through OpenAPI Generator and configured with RestAssured.

It exposes a fluent interface for recurring infrastructure behavior.

Example:

```java
restClient.create(payload)
        .withPolling(PollingStrategy.UNTIL_SUCCESS)
        .map(...)
        .get();
```

## WEB Gateway

A WEB Gateway implements the same business contract through the frontend channel.

It delegates browser interaction to Page Objects and reusable components built on top of SUIT.

Conceptually:

```text
WEB Gateway
  ↓
Page Object / component
  ↓
SUIT
  ↓
Browser
```

The business contract remains stable while the channel-specific implementation changes.
