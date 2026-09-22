# Entity Context

This guide describes how domain entities are shared between operations and Cucumber steps.

## Purpose

Business scenarios frequently need to reuse domain entities created or updated by previous operations.

The repository centralizes this state through the `EntityStore`.

The expected flow is:

```text
Gateway
   │
   │ writes
   ▼
EntityStore
   │
   │ reads
   ▼
ParameterType
   │
   ▼
Cucumber Step
```

## Write ownership

Gateways are the only components allowed to write updated domain entities into the `EntityStore`.

A Gateway is expected to persist the resulting domain entity after performing an operation when that entity must be available to subsequent test steps.

This keeps context updates inside the infrastructure boundary and prevents Cucumber Steps from managing test state directly.

## Reading from Cucumber

Cucumber Steps access previously created or updated domain entities through centralized ParameterTypes.

ParameterTypes use the interface exposed by the `EntityStore`.

Examples:

```java
@ParameterType("dpop proof|dpop proof creata")
public DPoPProof currentDpopProof(String token) {
    return entityStore.getLastOrThrow(DPoPProof.class);
}

@ParameterType("client|client creato")
public Client currentClient(String token) {
    return entityStore.getLastOrThrow(Client.class);
}

@ParameterType("purpose|purpose creata|finalità|finalità creata")
public Purpose currentPurpose(String token) {
    return entityStore.getLastOrThrow(Purpose.class);
}

@ParameterType("client assertion|client assertion creata")
public ClientAssertion currentClientAssertion(String token) {
    return entityStore.getLastOrThrow(ClientAssertion.class);
}
```

## Rule

If a Cucumber step needs information produced by a previous operation:

1. the producing Gateway stores the updated domain entity in the `EntityStore`
2. a ParameterType resolves the required entity from the `EntityStore`
3. the Step receives the resolved domain entity and continues expressing business intent

Step classes should not access technical clients, static state, or ad-hoc lookup mechanisms to recover previously produced entities.
