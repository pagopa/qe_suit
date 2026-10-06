# Feature Design

This guide defines how to design and refine Cucumber Feature files for business tests.

## Purpose

A Feature file describes a business capability and its expected behavior without exposing technical implementation details.

It must remain understandable by the Three Amigos:

- business / product stakeholders
- developers
- testers

## Location

Feature files live under:

```text
src/main/resources/feature/<business-entity>/<business-capability>.feature
```

The file name must represent the business capability under test.

Examples:

```text
agreement-create.feature
agreement-activate.feature
```

## Feature description

Start by defining the business capability at a high level.

Example:

```gherkin
Feature: Creazione di una richiesta di fruizione verso un EService

  Come Aderente interessato alla fruizione di un EService in catalogo
  Voglio inoltrare una richiesta di fruizione verso l'erogatore del servizio
  Al fine di instaurare un Agreement che abiliti alle fasi successive necessarie a raggiungere la fruizione di un EService.
```

The description must focus on business intent and must not include technical implementation details.

## Scenario definition

Before implementing executable Gherkin steps, define every scenario at business level.

Each scenario must contain:

- a unique scenario ID
- a descriptive title
- a high-level Given / When / Then description written in Italian

Example:

```gherkin
Scenario: [AGREEMENT_DEPRECATED_DESCRIPTOR_1] - Impossibilità di richiedere la fruizione di una versione obsoleta dell'EService
Dato un EService con una versione deprecata
quando un Fruitore tenta di inoltrare una richiesta di fruizione per tale versione
allora il sistema impedisce l'inoltro della richiesta
```

## Scenario ID convention

Scenario IDs must:

- be uppercase
- use `_` as separator
- end with a progressive number when multiple similar scenarios exist

Example:

```text
AGREEMENT_DEPRECATED_DESCRIPTOR_1
```

## Channel configuration

Execution channels are configured through Cucumber tags.

Example:

```gherkin
@channel:Given=BFF,When=WEB,Then=WEB
```

Channel configuration can be applied:

- at Feature level
- at Scenario level

Multiple channel configurations can be associated with the same scenario, directly or through Feature-level inheritance.

Each configuration can produce an additional execution of the same scenario in the same test run using a different runtime channel combination.

## Step design iterations

Executable Given / When / Then steps are refined through successive iterations.

### Iteration 1 — Readability

Optimize scenarios for readability and business comprehension.

The scenario must remain understandable by the Three Amigos and must not expose technical implementation details.

### Iteration 2 — Step reuse

Maximize reuse of existing steps.

Reuse should be evaluated first for preconditions and then for the remaining parts of the scenario, wherever possible.

Reuse can be achieved by:

- reusing an existing step as-is
- refactoring an existing step through parameterization
- reusing the existing Java method in the Step class and exposing a different Cucumber/Gherkin interface through a new `@Given`, `@When`, or `@Then` annotation string

### Iteration 3 — New step identification

Introduce new steps only when existing steps cannot be reused or generalized.

Analyze all scenarios together and identify whether a subset of the required new steps can be generalized and reused.

The output of this iteration must be the smallest possible set of new steps to implement, in addition to any existing steps that will be reused or refactored.

The goal is to minimize:

- implementation effort
- duplicated behavior
- delivery time

## Parallel execution

Before considering the Feature complete, verify that its scenarios can run safely in parallel.

Check for:

- technical race conditions
- shared mutable state
- collisions on test data
- dependencies between scenarios

Refactor the Feature when necessary.

Parallel execution has priority over scenario atomicity.

A scenario may become longer if this is required to make execution independent and parallelizable. A small loss of readability is acceptable when necessary.

BDD readability for the Three Amigos is not negotiable.
