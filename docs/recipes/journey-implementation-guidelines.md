# Journey Implementation Guidelines

This document describes a Journey architecture pattern you can reuse across domains.

Domain-specific flows are examples only, not constraints.

## 1) Mental model

A Journey is a fluent orchestration layer that composes use cases into readable, end-to-end flows.

- **Journey interfaces** expose fluent operations (`create...`, `add...`, `publish...`, `waitUntil...`).
- **Journey implementations** orchestrate state transitions and sequencing.
- **Use cases** hold domain application logic and stay channel-agnostic.
- **Gateways** provide channel-specific integration (BFF, Web, other transports).
- **Request factories** provide default commands/payloads for common happy paths.
- **EntityStore** keeps the latest entities in context to enable fluent chaining.

## 2) Layered architecture

### 2.1 Journey layer

- Define one journey module per domain capability.
- Let each journey module interface extend `JourneyModule`.
- Compose modules into a top-level journey interface (for example, `InteropJourney`).
- Keep fluent methods small and expressive; keep orchestration details in private methods.

### 2.2 Application layer

- Use cases should depend on interfaces, not channel implementations.
- Typical dependencies:
  - aggregate gateway interface
  - child/lifecycle gateway interface (if needed)
  - request factory interface
- Keep lifecycle checks and invariants here (for example, "publish only from DRAFT").

### 2.3 Infrastructure layer

- Implement gateways per channel (`Bff...Gateway`, `Web...Gateway`, etc.).
- Isolate generated/OpenAPI client calls inside a dedicated rest client wrapper.
- Keep mapping logic in mappers; keep transport concerns out of use cases.
- When responses are partial, merge with context instead of replacing the full aggregate blindly.

### 2.4 Routing layer

- Register gateway and factory interfaces as plugin registries.
- Expose transparent routing proxies as `@Primary` beans.
- Route by current channel context via interceptor.

## 3) Dynamic proxy composition

The composite journey can be backed by a dynamic proxy that dispatches each method call to the module implementing it.

Benefits:
- one fluent entrypoint for tests
- modular implementation ownership
- no manual boilerplate to forward calls across modules

## 4) Lifecycle orchestration pattern

Use a predictable pipeline whenever a domain has staged states.

1. Create aggregate in initial state (often DRAFT).
2. Prepare child entity for transition (update required fields, link artifacts).
3. Trigger transition (publish/activate/suspend/deprecate/etc.).
4. Poll until expected state is observed.

Example mapping:
- Domain A: aggregate + child lifecycle entity
- Domain B: aggregate + version/revision entity

The pattern is the same even when names differ.

## 5) EntityStore and context safety

`EntityStore` is the backbone of fluent journeys.

- Gateways should update context after successful reads/writes.
- Journey methods should resolve current entities via `getLastOrThrow(...)` when possible.
- Aggregates with nested children should provide upsert helpers (`addX`, `replaceX`, `findX`).
- Collections used by upsert helpers must be mutable.

Without these rules, fluent APIs degrade into manual ID plumbing.

## 6) Design rules for new journeys

1. **Model helpers first**
   - add `getRef()` and child upsert/find helpers where needed
2. **Define command abstractions**
   - keep input contracts independent from transport models
3. **Create request factory interface**
   - provide sensible defaults and optional customizers
4. **Split gateway interfaces by responsibility**
   - aggregate operations vs lifecycle/child operations
5. **Implement channel adapters**
   - gateway + rest client + mapper per channel
6. **Add routing config**
   - plugin registries + transparent proxies
7. **Implement journey module**
   - fluent API + centralized private lifecycle pipelines
8. **Integrate in composite journey**
   - expose the new module through the root journey interface

## 7) Testing strategy

Minimum recommended coverage:

- one focused mapper test for context-preserving upsert behavior
- one use case test for lifecycle guardrails (valid/invalid transitions)
- one gateway-level integration/contract test for critical endpoints

If a flow relies on polling, add at least one test that validates terminal state behavior.

## 8) Common pitfalls

- Replacing aggregates with partial payloads and losing nested state
- Putting channel-specific code in use cases
- Building immutable child lists when journey logic expects runtime upsert
- Scattering lifecycle transitions across many methods instead of one pipeline
- Skipping context updates, then failing later fluent steps with missing entities

## 9) Minimal checklist for a new journey

- [ ] Domain model + refs + helper methods
- [ ] Command interfaces + channel command adapters
- [ ] RequestFactory interface + channel implementations
- [ ] Gateway interfaces + channel gateways
- [ ] Rest client wrapper methods
- [ ] Mapper(s) with context-preserving merge/upsert logic
- [ ] Routing config with plugin registries/proxies
- [ ] Journey interface + implementation + integration in composite journey
- [ ] Focused tests (mapper + lifecycle + critical integration)

## 10) Concrete reference implementation: EServiceTemplateJourney

The following implementation is a complete, working example of the generic pattern described above.

### 10.1 Entry point and journey orchestration

- Composite integration: `interop/src/main/java/it/pagopa/interop/common/journey/application/InteropJourney.java`
  - `InteropJourney` extends `EServiceTemplateJourney<InteropJourney>`.
- Journey contract: `interop/src/main/java/it/pagopa/interop/common/journey/application/EServiceTemplateJourney.java`
  - `createEServiceTemplate(...)`, `addVersion(...)`, `waitUntilEServiceTemplate(...)`.
- Journey implementation: `interop/src/main/java/it/pagopa/interop/common/journey/infrastructure/EServiceTemplateJourneyImpl.java`
  - `processLifecycle(...)` centralizes state switch (`DRAFT`, `PUBLISHED`).
  - `publishPipeline(...)` calls `prepareVersionForPublication(...)` then `publishVersion(...)`.
  - `waitUntilEServiceTemplate(...)` refreshes aggregate + versions with polling.

### 10.2 Application layer (channel-agnostic)

- Aggregate use case: `interop/src/main/java/it/pagopa/interop/common/eservice_template/application/EServiceTemplateUseCase.java`
  - create/get/update operations and specific updates (`updateTemplateName`, `updateTemplateIntendedTarget`, `updateTemplateDescription`).
- Child/lifecycle use case: `interop/src/main/java/it/pagopa/interop/common/eservice_template/application/EServiceTemplateVersionUseCase.java`
  - `addVersion`, `publishVersion`, `updateDraftVersion`, `linkOpenApiInterface`, `prepareVersionForPublication`.
  - includes state guardrail: publish is allowed from `DRAFT` (or no-op if already `PUBLISHED`).

### 10.3 Ports (interfaces) and routing

- Gateway ports:
  - `interop/src/main/java/it/pagopa/interop/common/eservice_template/application/EServiceTemplateGateway.java`
  - `interop/src/main/java/it/pagopa/interop/common/eservice_template/application/EServiceTemplateVersionGateway.java`
- Request factory port:
  - `interop/src/main/java/it/pagopa/interop/common/eservice_template/application/EServiceTemplateRequestFactory.java`
- Routing configuration:
  - `interop/src/main/java/it/pagopa/interop/common/eservice_template/infrastructure/config/EServiceTemplateRoutingConfig.java`
  - registers plugin registries and exposes transparent `@Primary` proxies via `ChannelRoutingInterceptor`.

### 10.4 BFF channel adapters

- Aggregate gateway implementation:
  - `interop/src/main/java/it/pagopa/interop/bff/eservice_template/infrastructure/BffEServiceTemplateGateway.java`
  - notable behavior: `createEServiceTemplate(...)` resolves both template and initial version, then updates context.
- Version gateway implementation:
  - `interop/src/main/java/it/pagopa/interop/bff/eservice_template/infrastructure/BffEServiceTemplateVersionGateway.java`
  - notable behavior: `publishVersion(...)` triggers publish then polls until `PUBLISHED`.
  - notable behavior: `linkOpenApiInterface(...)` uploads interface document from classpath resource.
- Request factory implementation:
  - `interop/src/main/java/it/pagopa/interop/bff/eservice_template/infrastructure/BffEServiceTemplateRequestFactory.java`
  - provides default commands for create/template update/version update.
- REST client wrapper:
  - `interop/src/main/java/it/pagopa/interop/bff/eservice_template/infrastructure/BffEServiceTemplateRestClient.java`
  - wraps generated `EserviceTemplatesApi` with typed `TestChain` methods.

### 10.5 Mapping and context-preserving merge

- Aggregate mapper:
  - `interop/src/main/java/it/pagopa/interop/bff/eservice_template/infrastructure/BffEServiceTemplateMapper.java`
  - `toEServiceTemplatePreservingVersions(...)` merges partial payloads into context without losing known versions.
- Version mapper:
  - `interop/src/main/java/it/pagopa/interop/bff/eservice_template/infrastructure/BffEServiceTemplateVersionMapper.java`
  - `toTemplateWithUpsertVersion(...)` upserts one version into the template aggregate.
  - `flattenAttributes(...)` maps nested descriptor attributes into flat domain groups.

### 10.6 Domain helpers used by the journey

- Aggregate helpers:
  - `interop/src/main/java/it/pagopa/interop/common/eservice_template/domain/EServiceTemplate.java`
  - `findVersion(...)`, `getLastDraftVersion()`, `addVersion(...)`, `getRef()`.
- Child helper:
  - `interop/src/main/java/it/pagopa/interop/common/eservice_template/domain/EServiceTemplateVersion.java`
  - `getRef()`.
- Lifecycle enum:
  - `interop/src/main/java/it/pagopa/interop/common/eservice_template/domain/EServiceTemplateVersionState.java`
  - includes `DRAFT`, `PUBLISHED`, `DEPRECATED`, `SUSPENDED`, `ARCHIVED`, `UNKNOWN`.

### 10.7 Command abstractions and channel command adapters

- Channel-agnostic command interfaces:
  - `interop/src/main/java/it/pagopa/interop/common/eservice_template/application/command/EServiceTemplateCreationCommand.java`
  - `interop/src/main/java/it/pagopa/interop/common/eservice_template/application/command/UpdateEServiceTemplateCommand.java`
  - `interop/src/main/java/it/pagopa/interop/common/eservice_template/application/command/UpdateEServiceTemplateNameCommand.java`
  - `interop/src/main/java/it/pagopa/interop/common/eservice_template/application/command/UpdateEServiceTemplateIntendedTargetCommand.java`
  - `interop/src/main/java/it/pagopa/interop/common/eservice_template/application/command/UpdateEServiceTemplateDescriptionCommand.java`
  - `interop/src/main/java/it/pagopa/interop/common/eservice_template/application/command/UpdateEServiceTemplateVersionCommand.java`
- BFF adapters:
  - `interop/src/main/java/it/pagopa/interop/bff/eservice_template/application/BffEServiceTemplateCreationCommand.java`
  - `interop/src/main/java/it/pagopa/interop/bff/eservice_template/application/BffUpdateEServiceTemplateCommand.java`
  - `interop/src/main/java/it/pagopa/interop/bff/eservice_template/application/BffUpdateEServiceTemplateNameCommand.java`
  - `interop/src/main/java/it/pagopa/interop/bff/eservice_template/application/BffUpdateEServiceTemplateIntendedTargetCommand.java`
  - `interop/src/main/java/it/pagopa/interop/bff/eservice_template/application/BffUpdateEServiceTemplateDescriptionCommand.java`
  - `interop/src/main/java/it/pagopa/interop/bff/eservice_template/application/BffUpdateEServiceTemplateVersionCommand.java`

### 10.8 Focused example test

- Mapper behavior test:
  - `interop/src/test/java/it/pagopa/interop/bff/eservice_template/infrastructure/BffEServiceTemplateMapperTest.java`
  - validates that version-preserving mapping keeps collection behavior compatible with runtime upsert (`addVersion(...)`).


