# Architecture Health Checklist

This checklist defines how to assess a product module against
[component responsibilities](component-responsibilities.md) and
[product-module architecture](product-module-architecture.md).
It is a baseline for a subsequent audit, not an assessment of current codebase health.

## Classification

| Category | Meaning | Review treatment |
|---|---|---|
| Rule violation | Evidence contradicts an explicit boundary or repository constraint | Describe the violated rule, scope and impact |
| Guideline deviation | Another design is used where tradeoffs are possible | Evaluate the rationale and maintenance cost |
| Review signal | A metric or pattern suggests a potential problem | Investigate the behaviour before making a finding |
| Unknown | Evidence is missing or execution is environment-dependent | Record the missing evidence; do not mark healthy or defective |

Passing automated rules is necessary for their covered constraints, not sufficient for architectural health.
Class size, method count or dependency count alone does not prove low cohesion or excessive complexity.
Conversely, a small class can still own the wrong responsibility.

## Current automated guardrails in Interop

Source of truth:
[`ArchitectureRulesTest`](../../interop/src/test/java/it/pagopa/interop/architecture/ArchitectureRulesTest.java).
Recheck this inventory when the executable rules change.

| Existing check | Coverage / limitation |
|---|---|
| Application and domain must not depend on infrastructure | Package dependency checks; not semantic responsibility checks |
| Common application/domain must not depend on infrastructure or generated code | Does not prove every possible inward dependency rule |
| Package structure under `common`, `bff`, `web` | Allows global `infrastructure` / `kernel` or vertical slice/layer structure |
| Common component naming | Requires recognized suffixes outside excluded `domain`, `kernel`, `infrastructure` package segments |
| Channel implementation naming | Checks channel prefixes for recognized common component patterns |
| Gateway dependency paths to Cucumber | For classes ending in `Gateway`, traverses Interop dependency paths to Cucumber libraries/glue |
| Production Java file length | Files under `src/main/java` must contain fewer than 200 lines; not a cohesion score |
| Test source naming | Java files under `src/test/java` must end in `Test`, with source-file exceptions |
| Contract execution configuration | Contract classes must not declare `@Execution`; configuration belongs in `junit-platform.properties` |
| Contract channel prefix | Prefix must match configured contract-report channels |
| API contract factory naming | BFF/M2M `@TestFactory` names must match operations recognized in `apiCall(...)` |

These are current checks, not permission to violate a documented rule that is not yet automated.
For example, the intended prohibition on domain-to-application dependencies is broader than the
infrastructure/generated dependency checks above.

## Manual review dimensions

| Dimension | Evidence to inspect | Questions / signals |
|---|---|---|
| Boundaries | Imports, constructor dependencies, call paths, public signatures | Do generated/Cucumber/channel types leak inward? Is a package used to bypass a boundary? |
| Cohesion | Method groups, fields used together, callers, reasons to change | Does one class combine independent capabilities? Do extracted helpers have a real responsibility? |
| Complexity | Branches, nesting, state combinations, temporal coupling | Can the supported paths and failure paths be explained and tested? |
| Commands and factories | Input contracts, defaults, copies, payload adaptation | Does a command describe business intent or an endpoint? Are mutable requests independent? |
| Orchestration | Step/UseCase/Journey/Gateway collaboration | Are business rules duplicated? Is technical call composition leaking into common application code? |
| State and context | Entity writes, mutations, refreshes, aliases, store lifetime | Are Gateways authoritative? Can stale snapshots overwrite current state? Are child IDs unique after repeated reads? |
| Mapping | Partial response merges, document metadata/content, omitted fields | Is field authority explicit? Are collections appended when they should be replaced? |
| Synchronization | Polling predicates, status handling, timeouts | Are definitive failures retried? Is business ordering confused with eventual visibility? |
| Error handling | Exceptions, absence semantics, fallback paths, negative assertions | Is an unexpected failure returned as success/empty data? Is a negative outcome tied to an explicit reason? |
| Reuse and coupling | Similar flows across slices/channels, dependency cycles | Is a shared abstraction useful or merely generic? Are technical concerns duplicated with divergent semantics? |
| Testability | Local regressions, mocked/stubbed boundaries, real transport checks | Can endpoint/payload/context behaviour be verified without QA? Do mocks bypass the defect-prone boundary? |
| Determinism | Data resolution, flags, clocks, concurrent environment changes | Are prerequisites verified? Do tests depend on incidental tenant data or unstable ordering? |
| Cleanup | Ownership tracking, partial setup failures, user/channel restoration | Can every created resource be accounted for? Is retirement distinguished from deletion? |
| Execution safety | Maven selectors, suite discovery, environment wiring | Can a local verification command launch live mutations unintentionally? |

Use complexity and coupling metrics as prioritization aids where tooling already exists.
Do not introduce arbitrary numerical pass/fail thresholds or dependencies merely to produce a score.
The existing file-length rule remains a hard constraint; splitting a class to satisfy it must still preserve cohesion.

## Evidence and finding format

For each finding, record:

| Field | Required content |
|---|---|
| Location | File, symbol and relevant lines |
| Classification | Rule violation, guideline deviation or review signal |
| Evidence | Concrete dependency, code path, state change or regression result |
| Impact | Correctness, reliability, change cost, coupling or execution risk |
| Severity | High, medium or low with an explicit justification |
| Confidence | Confirmed, probable or needs investigation, based on available evidence |
| Recommendation | Smallest cohesive correction, affected contracts and migration considerations |
| Verification | Local regression / architecture check; live evidence only when authorized and needed |

Prioritize correctness and unintended external side effects over cosmetic consistency.
A size/naming violation can be certain without being high severity; a context overwrite can be high impact
even in a short class. Separate severity from confidence.

## Audit procedure and output

1. Record the reviewed revision/worktree scope, excluded/generated code and applicable baseline.
2. Inventory vertical slices and their entry points, application contracts and channel implementations.
3. Run existing architecture checks and inspect critical behaviour paths against the responsibility baseline.
4. Investigate signals with representative callers, failure paths and focused local regressions.
5. Group repeated findings by root cause rather than reporting every occurrence as an unrelated defect.
6. Propose bounded remediation batches with dependencies and preservation of observable behaviour.

The resulting assessment should contain a dimension-by-dimension health summary, evidenced findings,
coverage gaps and a prioritized backlog. Distinguish code proven conforming, code not reviewed and
environment-dependent unknowns. Do not collapse those dimensions into a single health score.

An audit does not automatically authorize refactoring, new runtime dependencies or live suite execution.
Do not change architecture tests to accommodate an implementation defect; proposed rule changes require
their own rationale and approval.
