# Component Responsibilities

This document defines the responsibility and boundaries of the main components used in the test implementation flow.

Its purpose is to clarify what each component is responsible for and what should remain outside its scope.

## Responsibility matrix

| Component | Responsibility | Should contain | Should not contain |
|---|---|---|---|
| `Feature file` | Describe the business scenario in Gherkin. | Business-readable scenarios, examples, expected outcomes. | Technical implementation details, selectors, HTTP calls, framework-specific logic. |
| `Step class` | Bind Gherkin steps to application behavior. | Step definitions, input mapping, delegation to UseCases. | Business logic, direct browser/API implementation, duplicated orchestration. |
| `UseCase` | Represent a business operation used by tests. | Business actions, orchestration required to perform a business operation, interaction with Gateways. | Browser selectors, HTTP client details, Cucumber-specific logic. |
| `Gateway` | Define the boundary between application/business logic and technical interactions. | Operations required by UseCases to interact with external systems or UI capabilities. | Business scenario definition, Cucumber step logic, test assertions unrelated to the interaction boundary. |
| `JUnit class` | Implement contract tests directly. | Contract-specific test setup, invocation, assertions, direct selection of the execution channel. | Business test flow implementation through Feature/Step classes. |
| `SUIT` | Provide frontend interaction capabilities. | Browser/UI interactions and reusable frontend automation primitives. | Business logic, business scenario orchestration, API-specific behavior. |
| `RestClient` | Provide backend/API interaction capabilities. | HTTP requests, response handling, API-level technical interactions. | Business scenario orchestration, frontend behavior, Gherkin logic. |
