# Component Responsibilities

This is the responsibility baseline for development and architectural review in product modules.
It describes the intended architecture, not a claim that every existing implementation conforms.

Read it with [Product module architecture](product-module-architecture.md),
[Test implementation flow](../flows/test-implementation-flow.md), and the
[Architecture health checklist](architecture-health-checklist.md).

## How to use this baseline

- **Rule**: a required responsibility or dependency boundary. Exceptions need an explicit rationale and scope.
- **Guideline**: a preferred design; a different design needs a concrete benefit, not necessarily a new rule.
- **Review signal**: evidence to investigate, not an automatic defect.

The boundaries below are rules unless explicitly described as guidelines or review signals.
Automated coverage is listed in the health checklist; a rule is not necessarily enforced by a test.

## Responsibility matrix

| Component | Owns | Does not own |
|---|---|---|
| Feature | Business preconditions, actions and observable expectations | Endpoints, payloads, selectors, context mechanics |
| Step / parameter type | Gherkin binding, input conversion and delegation | Business workflows, transport or browser calls |
| Cucumber DTO / validator | Input shape, required fields, allowed values and combinations | Application operations or remote fixture discovery |
| Cucumber DTO mapper | Conversion to application inputs and resolution of registered aliases | Remote calls, entity creation or lifecycle transitions |
| Command | Parameters of an application intention | Execution, polling, context writes or orchestration |
| Request factory | Command construction, explicit defaults and independent request copies | Business workflows or execution |
| UseCase | A business operation, its rules and business expectations | Channel protocols, generated models or context synchronization |
| Journey | Reusable composition of application operations | Endpoint composition or duplicated UseCase rules |
| Gateway interface | A channel-independent capability needed by the application | An HTTP endpoint inventory |
| Gateway implementation | Channel adaptation, technical synchronization, domain mapping and entity context updates | Scenario definition or Gherkin interpretation |
| RestClient | One HTTP interaction and its transport configuration | Business decisions or entity context updates |
| Channel mapper | Technical/domain conversion and explicit merge policies | I/O, polling or context writes |
| Domain model / planner | Test-domain concepts, invariants and transition plans | Transport details or execution of plans |
| Page / UI component | Page structure and concrete UI interactions | Business workflows or scenario expectations |
| SUIT / frontend framework | Reusable browser automation capabilities | Product-specific rules |
| Hook | Scenario-boundary setup/teardown, restoration and failure aggregation | Inline endpoint implementation or hidden failures |
| JUnit test | Verification at a declared test level | An implicit classification as a contract or live test |

## Collaboration and placement

| Component | Normal collaborators | Placement |
|---|---|---|
| Step | DTO/validator, Cucumber DTO mapper, application factories, UseCases or Journeys | Common infrastructure Cucumber glue |
| UseCase | Domain values/planners, application ports/factories and cohesive application operations | Common application |
| Journey | UseCases, application factories and scenario bookkeeping | Common application contract; infrastructure implementation/wiring |
| Gateway implementation | Application contracts, domain, channel mappers, RestClients or Pages, entity store | Channel infrastructure |
| RestClient | Generated client, request specification, TestChain infrastructure | Channel infrastructure |
| Page/component | Other UI components and framework capabilities | WEB infrastructure |
| Domain model/planner | Domain values; a planner can live in application to express an application transition plan | Common domain / application respectively |

This is not a requirement to instantiate every collaborator for every operation.
Keep public common contracts channel-independent and dependencies inward; infrastructure can depend on
application/domain, not the reverse. Domain models must not depend on application.
Consult the product-module guide for channel-specific command placement and global package exceptions.

## Feature, Step and scenario input

Features explain what matters to the business and distinguish fixture setup from the action under test.
Technical flags and environment assumptions must be explicit when they affect whether a scenario is valid.

Steps bind phrases to application operations or Journeys. Iterating over mapped table rows to delegate
the same operation is acceptable; implementing a publication state machine inside that loop is not.
Parameter types resolve textual references, not remote business preconditions.

Cucumber DTOs remain in infrastructure. Validate syntax and table structure before execution:
required fields, supported keys, duplicate configuration and incompatible values.
The application still enforces business invariants for callers that do not use Cucumber.

Cucumber DTO mappers translate validated DTOs to commands or domain values. Resolving an alias already stored
in the scenario context is input translation. Searching a registry for an eligible attribute, checking
tenant membership or creating a missing entity is an application operation through a Gateway.
Neither application contracts nor Gateway dependencies may leak back to Cucumber DTOs or glue.

## Commands and factories

A command represents an application intention, such as preparing a descriptor for publication.
Its public common contract uses business/domain values and stays stable when a channel needs a different
endpoint or several technical interactions.

A channel-specific command implementation may carry a generated payload internally.
That representation must not leak into common UseCases, Journeys or domain models.
Do not create a new application command solely because an HTTP API has another request body.
Conversely, genuinely different business intentions should not be hidden behind an overloaded generic command.

Factories construct commands with documented defaults. A default is not evidence that a precondition holds.
For mutable commands, independent requests must not share mutable lists or payloads unintentionally.
Copying a search request for a new offset belongs to the request factory; the command's offset setter
only sets that parameter. Commands do not execute or create copies of themselves as orchestration.

**Guideline:** keep defaults minimal and explicit. Preserve the distinction between omitted, empty,
false and explicitly configured values when the contract assigns them different meanings.
Randomized fixture names must retain the scenario's searchable text; assertions use stable identities.

## UseCases and Journeys

A UseCase implements one cohesive business operation. It may coordinate multiple Gateway capabilities
or other cohesive application operations when that coordination is itself a business rule.
Its granularity is not determined by the number of HTTP requests.

A Journey composes reusable operations to obtain a broader setup or path, for example creating and
publishing several related entities. It delegates rules and lifecycle transitions rather than reproducing them.
A Journey implementation may live in infrastructure for wiring, but its orchestration remains
channel-independent. A Journey is not a mandatory pass-through layer for every Step.

Neither component selects endpoints, builds generated payloads, performs browser interactions, implements
technical retry loops or synchronizes stored entities. They may pass returned entities or identity references
to the next operation; they must not assume an old object automatically reflects a later Gateway refresh.

**Review signals:** a UseCase with several independent reasons to change; a Journey containing protocol
branches; chains of wrappers that only rename the same call without providing a useful contract.

## Gateways, RestClients and channel mapping

The Gateway interface is an application port. Its implementation decides how the channel fulfils that port.
One operation may require one or several HTTP requests or UI interactions.

For example, the same descriptor-preparation command can be adapted by the BFF Gateway to an ordinary
descriptor endpoint or a template-instance endpoint. Different generated payloads are a BFF concern.
The adapter must honour supported semantics and explicitly reject unsupported requests rather than silently
drop a requested business effect. Fields fixed by the template contract must be distinguished from editable fields.

RestClients configure method/operation, path and query parameters, headers, body, multipart parts and response type.
They expose the existing TestChain abstraction; Gateways choose the polling policy and consume/map the result.
Generated clients are not edited manually to repair transport configuration.

Channel mappers translate models without side effects. When merging partial responses, document which
fields are authoritative and which enrichment is preserved. Collection replacement and append are different
operations: repeated refreshes must not duplicate child IDs or preserve stale state accidentally.
Downloaded content may be retained only while its document identity/metadata still justify that retention.

**Guideline:** extract a technical collaborator when it owns a coherent mapping or interaction concern.
Do not create arbitrary helpers only to satisfy a file-length limit, or move business orchestration into an adapter.

## Context ownership and object lifetime

The Gateway owns synchronization of domain entities in the `EntityStore`, including refreshes after writes
and technical enrichment such as downloaded interface content. A delegated technical collaborator can perform
that work on its behalf. RestClients and pure mappers do not write entity state.

UseCases and Journeys must not rebuild an aggregate and save it over a newer Gateway snapshot, nor mutate
an old returned object as a substitute for Gateway synchronization.
Reads from context for entity resolution do not transfer update ownership.

Alias registration is scenario bookkeeping, not a refresh. Glue/Journeys may register an alias against
the fresh entity already saved by the Gateway. If registration uses an upsert API, it must not replace
a newer snapshot with an older returned instance. Missing required context entries are explicit errors.

Distinguish entity state from scenario execution state: the current user/channel, requested filters,
expected aliases and page traversal bookkeeping have their own scope. They are not reasons to store
remote entity updates outside a Gateway.

## Domain and lifecycle planning

Domain entities represent the concepts needed by the test suite, not copies of every backend field.
They own identity and meaningful invariants; they do not depend on Cucumber, channel models or transport.

A lifecycle planner describes supported transitions without I/O. An application operation executes
that plan through Gateways and verifies the resulting business state.
Do not invent a path merely because an enum contains the target state: scheduled archiving is not
terminal archiving, and deprecation may require another version or prerequisite.
Unsupported paths and missing prerequisites must be explicit.

## Assertions, synchronization and errors

Business expectations belong in application operations and use domain concepts, including explicit
failure reasons for negative scenarios. Steps delegate them.
Gateways verify technical evidence needed to fulfil the application contract and translate expected
negative outcomes into those reasons. Contract tests assert protocol-specific behaviour directly.

Technical synchronization belongs in the Gateway: wait for a write to become visible or a UI operation
to finish. A business wait exposed by a UseCase/Journey expresses a business condition, delegating
channel reads and synchronization rather than implementing HTTP retries.
Retry policies must distinguish transient visibility failures from definitive validation/authorization failures.
A generic success-only retry is not a substitute for that decision.

Never sort or filter an API response locally to make a server-side search assertion pass.
Shared-environment drift is not automatically a product defect: report insufficient evidence separately
from a proven wrong result. Do not silently convert it to success.

Errors must remain visible and contextual. Do not catch broadly to return an empty model, ignore a failed
precondition or treat an unexpected status as expected absence. Keep primary failures and cleanup failures
distinguishable, and do not expose credentials in diagnostics.

## UI and framework boundary

Page Objects and reusable components encapsulate selectors and concrete interactions using SUIT.
WEB Gateways adapt application operations to those capabilities and map observable UI state to the
application contract. The shared framework provides automation primitives, not product-specific workflows.
An application capability unavailable through a channel must be explicit, not simulated with unrelated evidence.

## Hooks and cleanup

Hooks control scenario-boundary orchestration. They delegate retirement/deletion to application operations,
preserve the original user/channel and restore them even on failure.
Cleanup targets must be traceable to the scenario's resources; context persistence across scenarios is not
evidence that every stored entity belongs to the current scenario.

Attempt independent cleanup operations when safe and aggregate their failures without suppressing the original
scenario failure. Do not label suspension or scheduled archiving as immediate deletion.
External resources created before a later failure must remain identifiable for cleanup.

## Test levels and execution safety

JUnit is a runner, not a test level. Distinguish local unit tests, local integration/transport tests,
architecture tests, live business tests and live contract tests.
Local tests must not accidentally invoke a configured QA client.

Contract tests exercise the technical contract through RestClient/generated clients or UI capabilities,
not through Gherkin glue. Their fixture setup must remain distinct from the operation being verified.
Business tests exercise application behaviour through UseCases/Journeys.

Test selection must make external side effects explicit. Prefer exact local class names for targeted
verification; do not assume a name wildcard excludes Cucumber suites or live contract tests.
