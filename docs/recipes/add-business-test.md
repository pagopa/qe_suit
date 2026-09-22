# Add a Business Test

This recipe describes the operational process for implementing a new business test.

It starts after the Jira ticket has been analyzed and the required activity has been classified as a **Business Test**.

Architecture reference:

[Product Module Architecture](../architecture/product-module-architecture.md)

## 1. Design the Feature

Define the business capability, scenarios, channel configuration, and executable Gherkin steps.

Follow:

[Feature Design](../guides/cucumber/feature-design.md)

Before moving forward, ensure that:

- the Feature is understandable by the Three Amigos
- technical details are not exposed
- existing Steps have been reused where possible
- the minimum required set of new Steps has been identified
- scenarios can run safely in parallel

## 2. Implement the Step class

Implement the Cucumber binding while keeping the Step layer business-oriented.

Follow:

[Step Classes](../guides/cucumber/step-classes.md)

At this stage:

- place the Step class in the correct channel scope
- delegate business operations to UseCases or Journeys
- keep technical channel details out of the Step class
- use ParameterTypes when previously created or updated domain entities are required

Context handling:

[Entity Context](../guides/cucumber/entity-context.md)

## 3. Implement the UseCase

Implement the business intent in the appropriate application layer.

Follow:

[UseCases](../guides/application/use-cases.md)

At this stage:

- keep the operation channel-agnostic
- work with domain entities
- use coarse-grained business-oriented method signatures
- model negative expectations explicitly through a failure `Reason`
- define the Gateway API required by the business operation

## 4. Define and implement the Gateway

Define the Gateway contract in the application layer and implement it for the required execution channels.

Follow:

[Gateways](../guides/application/gateways.md)

At this stage:

- translate business intent into channel-specific interaction
- keep infrastructure concerns out of the UseCase
- use RestClient for BFF/API interactions
- use Page Objects and SUIT for WEB interactions
- handle polling, mapping, synchronization, and `EntityStore` updates inside infrastructure

## 5. Complete channel-specific infrastructure

Implement or extend the required low-level channel components.

For BFF/API:

```text
Gateway
  ↓
RestClient
  ↓
Generated OpenAPI client
  ↓
RestAssured
```

For WEB:

```text
Gateway
  ↓
Page Object / component
  ↓
SUIT
  ↓
Browser
```

Detailed channel-specific guides can be added independently as the repository evolves.

## 6. Validate the implementation

Before considering the business test complete, verify the Definition of Done.

### Definition of Done

- [ ] Feature is valid and readable by the Three Amigos
- [ ] Avoidable duplication has been removed
- [ ] New Steps have been reduced to the minimum required set
- [ ] UseCases are channel-agnostic
- [ ] Gateways are implemented for the required channels
- [ ] Scenarios can run safely in parallel
- [ ] Tests pass locally
- [ ] Regression suite is green
- [ ] CI is green

## Implementation principle

The implementation process is always Top-Down:

```text
Jira Ticket
  ↓
Feature
  ↓
Scenario design
  ↓
Step class
  ↓
UseCase
  ↓
Gateway
  ↓
Channel-specific infrastructure
```

Start from business intent and introduce technical details only while moving toward the infrastructure layer.
