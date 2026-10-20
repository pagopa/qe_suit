# Feature Design

This guide defines how to design and refine Cucumber Feature files for business tests.

## Purpose

A Feature file describes a business capability and its expected behavior.

It is the test layer closest to the Software Requirements Specification (SRS) and must preserve its business meaning, terminology, and rules.

It must remain understandable by the Three Amigos:

- business / product stakeholders
- developers
- testers

Functional language and readability are non-negotiable.

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

## Business language and abstraction level

Feature files describe business conditions, operations, and observable outcomes, not automation instructions.

Use terminology consistent with the SRS and the domain glossary.

Do not expose technical implementation details such as:

- endpoints, HTTP payloads, or transport-level status codes;
- browser selectors, clicks, or navigation sequences;
- polling, retries, waits, or persistence details;
- framework classes, methods, or technical identifiers.

A technical concept may appear when it is itself part of the business requirement. Describe it using the domain terminology, not the mechanics of the test implementation.

DataTables must express business data. They must not become technical DSLs or serialized API requests.

Execution-channel tags are configuration metadata and must remain separate from the functional narrative.

## Feature description

Start by defining the business capability at a high level.

Example:

```gherkin
Feature: Creazione di una richiesta di fruizione verso un e-service

  Come fruitore
  Voglio richiedere la fruizione di un e-service
  Per accedere ai servizi messi a disposizione dall'erogatore.
```

The description must focus on business intent and must not include technical implementation details.

Business assumptions and rules may be documented here when they help explain the scenarios. Keep environment setup and automation mechanics in the relevant technical documentation.

## Scenario definition

Before implementing executable Gherkin steps, define every scenario at business level.

Each scenario must contain:

- a unique scenario ID;
- a descriptive title identifying the business rule;
- a Given / When / Then description written in Italian.

Example:

```gherkin
Scenario: [AGREEMENT_DEPRECATED_DESCRIPTOR_1] Impossibilità di richiedere la fruizione di una versione deprecata
  Given la versione 1 dell'e-service "ES-1" deprecata
  When PagoPA richiede la fruizione della versione 1 dell'e-service "ES-1"
  Then la richiesta di fruizione viene rifiutata
```

Each scenario must verify one recognizable business rule. Multiple assertions are appropriate when they jointly demonstrate that rule.

Use Scenario Outlines when the same rule and flow apply to different data. Keep scenarios separate when combining them would obscure distinct business rules.

## Scenario ID convention

Scenario IDs must:

- be uppercase;
- use `_` as separator;
- end with a progressive number when multiple similar scenarios exist.

Example:

```text
AGREEMENT_DEPRECATED_DESCRIPTOR_1
```

Preserve existing IDs when refactoring scenario wording or setup without changing the rule being verified.

## Preconditions and actions

A scenario has two logical parts:

1. **Preconditions** establish the initial business state.
2. **Actions** exercise the behavior and verify its outcomes.

Assertions belong to the second part, but retain a distinct responsibility: When exercises the behavior; Then verifies it.

| Part | Gherkin keyword | Responsibility | Wording |
|---|---|---|---|
| Preconditions | Given | Guarantee the required initial state | Past participle: `predisposto`, `configurata`, `pubblicata` |
| Actions | When | Exercise the behavior under test | Present tense: `cerca`, `richiede`, `revoca` |
| Assertions | Then | Verify an observable outcome | Present tense: `è presente`, `viene rifiutata`, `non compare` |

And and But inherit the semantic role of the section in which they appear. They do not introduce a different responsibility.

### Preconditions must succeed

Preconditions describe a state to be established, not an operation whose failure is an expected test outcome.

Every setup operation must succeed. If the required state cannot be established, the test must fail with a clear diagnostic.

A precondition may represent a negative business state, such as:

- a rejected request;
- a revoked delegation;
- a tenant without a required attribute.

Successfully establishing such a state is not a setup sad path.

Example:

```gherkin
Given una richiesta di fruizione rifiutata
```

If the rejection itself is the behavior under test, express the request as an action and verify its rejection through an assertion instead.

### Actions may describe sad paths

Actions may exercise successful or unsuccessful business operations.

An expected rejection or denial does not make the test fail when it matches the asserted business outcome.

Express both the attempted operation and its observable result in business terms. Do not replace the expected result with technical error-handling details.

## Step design

Steps must expose stable, reusable business concepts.

Configurability and shortcuts are independent design dimensions. A step may support either or both.

### Configurable steps

A configurable step accepts business data so that scenarios can express relevant variations without introducing a new step for each combination of properties.

Keep variable entity properties in DataTables where appropriate, rather than expanding the step sentence with every possible field.

Example:

```gherkin
Given i seguenti e-service predisposti per la pubblicazione:
  | alias | erogatore        | nome                  | descrizione                       |
  | ES-1  | Comune di Milano | Anagrafe              | Consultazione dei dati anagrafici. |
  | ES-2  | Sogecap          | Servizi assicurativi  | Consultazione delle coperture.     |
```

The table can evolve to support additional business properties without requiring a different sentence for every combination.

Do not introduce generic property bags that accept arbitrary technical fields. Supported columns and their meaning must be defined.

### Shortcut steps

A shortcut step condenses a recurring sequence into a meaningful business precondition or operation.

Shortcut step definitions must be placed in classes whose names end with `JourneySteps`. This also applies to steps that are both configurable and shortcuts. These classes map Cucumber inputs and delegate orchestration to the application layer; they must not compose behavior by calling other step methods.

Its contract must state:

- what entities it creates or uses;
- what relationships it establishes;
- what state it guarantees;
- what remains to be configured or performed;
- any ordering guarantees relevant to the scenario.

For example, `i seguenti e-service predisposti per la pubblicazione` creates the listed e-services and prepares version 1 of each for publication. It does not publish them.

A shortcut must not hide a transition or intermediate state when that behavior is under test.

### Shared implementation

Configurable steps and shortcuts must reuse the same application components and UseCases.

The implementation follows the repository flow:

```text
Feature file -> Step class -> UseCase -> Gateway
```

Use an existing Journey where orchestration composes multiple business operations.

Step classes handle Gherkin input mapping and delegation. They must not duplicate business orchestration or invoke other Cucumber step methods to compose behavior.

Different Gherkin expressions may share a binding method when their semantics and input mapping are equivalent. Otherwise, share behavior through the application layer.

The goal is the smallest coherent and readable set of reusable steps, not the smallest number of step definitions at any cost.

## Model entities and relationships before designing steps

Start from the business model, not from API payloads or existing client methods.

Identify:

- the business entities involved;
- their relevant attributes;
- the relationships between them;
- relationship cardinalities;
- any independent lifecycle or business rule associated with a relationship.

Use an entity-relationship diagram when it helps clarify the model.

Apply the following mapping as a design guideline:

| Business element | Step design |
|---|---|
| Entity attributes | Configurable DataTable columns |
| A 1:1 relationship | A reference to the related entity in a DataTable, when readable |
| A 1:N or N:N relationship | A separate relationship step, optionally configurable through a DataTable |

A 1:1 relationship does not have to be embedded in the entity step. Keep it separate when it has an independent lifecycle or business meaning.

For multi-valued relationships, do not encode lists in cells, embed JSON, or introduce repeated columns such as `attributo1` and `attributo2`.

### Example: entity setup and required attributes

An e-service can have multiple required attributes. Keep entity preparation and attribute configuration in separate, reusable steps.

```gherkin
Given i seguenti e-service predisposti per la pubblicazione:
  | alias | erogatore        | nome            |
  | ES-1  | Comune di Milano | E-service Hiems |
And la versione 1 dell'e-service "ES-1" configurata con i seguenti attributi richiesti:
  | tipo       | gruppo | attributo                           |
  | dichiarato | 1      | un attributo non posseduto da PagoPA |
  | verificato | 1      | un attributo non posseduto da PagoPA |
And la versione 1 dell'e-service "ES-1" pubblicata
```

The first step combines creation and preparation for publication. The relationship table remains focused on required attributes, and publication remains explicit.

## Data semantics, identity, and scenario focus

### Optional data and defaults

Configurable steps must define the meaning of optional inputs explicitly.

Distinguish between:

- **omitted column**: the scenario does not override that property; the step applies its documented default;
- **empty cell**: an explicit empty value, allowed only when meaningful for that property;
- **explicit unset value**, such as `non impostato`: the property must be absent, not replaced by a default.

These cases must not be treated as interchangeable. If a distinction cannot be represented unambiguously in the table, refine the step contract rather than infer the author's intent.

Defaults must be deterministic and must not hide a condition relevant to the behavior under test. Make relevant values explicit in the scenario.

Unknown columns, unsupported values, and missing required data must fail with a clear diagnostic. Validate inputs before performing setup operations whenever possible.

### Entity identity and references

Use scenario-local aliases to identify entities without exposing technical identifiers.

Aliases must be unambiguous within the scenario. Duplicate or unresolved aliases must fail explicitly.

Relationship steps and assertions must reference the intended entity explicitly. They must not depend on implicit conventions such as "the last entity created" when multiple entities can exist.

An alias is a reference, not a replacement for business data: keep names and other properties explicit when they are relevant to the rule being tested.

### Scenario focus

Include the data needed to understand the behavior and distinguish the relevant cases. Delegate incidental setup details to documented defaults or reusable shortcut steps.

Condense repetitive preparation, but do not hide:

- intermediate states or transitions that are themselves under test;
- relationships needed to explain the expected outcome;
- differences between entities that determine their inclusion or exclusion.

Each scenario must establish its own preconditions and remain independent of the execution order or results of other scenarios.

## Channel configuration

Execution channels are configured through Cucumber tags.

Example:

```gherkin
@channel:Given=BFF,When=WEB,Then=WEB
```

Channel configuration can be applied:

- at Feature level;
- at Scenario level.

Multiple channel configurations can be associated with the same scenario, directly or through Feature-level inheritance.

Each configuration can produce an additional execution of the same scenario in the same test run using a different runtime channel combination.

Changing the execution channel must not change the business meaning of the scenario. Only configure channels that can exercise and verify the stated behavior.

## Step design iterations

Executable Given / When / Then steps are refined through successive iterations.

### Iteration 1 — Business model and readability

Identify the business rule, entities, attributes, and relationships.

Separate preconditions from actions and assertions. Express them at the SRS abstraction level using the appropriate wording.

Verify that the scenario remains understandable by the Three Amigos without knowledge of the automation implementation.

### Iteration 2 — Step and component reuse

Evaluate existing steps before introducing new ones.

Reuse should be evaluated first for preconditions and then for the remaining parts of the scenario.

Reuse can be achieved by:

- reusing an existing step as-is;
- extending an existing configurable step with a coherent business property;
- parameterizing an existing step without making its meaning ambiguous;
- exposing equivalent wording through the same binding method;
- composing existing UseCases through an application operation or Journey.

Do not force unrelated business concepts into a generic step merely to avoid adding a definition.

### Iteration 3 — New step identification

Introduce new steps only when existing steps cannot be reused or coherently generalized.

Analyze the scenarios together and identify a reusable set of configurable steps, shortcuts, and relationship steps.

For each new step, define its inputs, defaults, references, guaranteed outcome, and failure behavior.

Minimize duplicated behavior and implementation effort without compromising functional language or readability.

## Parallel execution

Before considering the Feature complete, verify that its scenarios can run safely in parallel.

Check for:

- technical race conditions;
- shared mutable state;
- collisions on test data;
- dependencies between scenarios;
- cleanup that could affect another scenario's resources.

Each scenario must establish or verify the preconditions it relies on and must not depend on another scenario having run first.

Independence may require additional preconditions. Express them through readable, reusable business steps rather than technical instructions.

Parallel execution does not justify weakening assertions, hiding relevant business conditions, or reducing readability for the Three Amigos.
