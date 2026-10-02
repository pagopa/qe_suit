# WEB Visual Tests

This guide defines the conventions for implementing **Visual** tests for Interop pages. It is the shared reference for developers and AI agents. The operational procedure is described in [Add a Visual Test](../../recipes/add-visual-test.md).

## Test categories and scope

| Test type | What it verifies |
|---|---|
| Visual | Components present or absent in the DOM, selected UI copy, and initial component states. |
| Contract | Input validation, errors, and technical constraints in APIs or UIs. |
| Business E2E | A business flow with actions and observable outcomes. |

Layout, colors, and fonts remain the responsibility of manual testers (TM) in the current Visual scope. Visibility is checked only when required: a hidden element can still be present in the DOM.

Visual tests reuse JUnit, `WebScenario`, and `WebBrowserContractValidator`. In the current repository, they live in `suite.contract` and use the `ContractTest` suffix to integrate with existing test selection and reporting. This technical convention does not change the test category.

## Define the contract before writing code

The page inventory identifies the area, route, and frontend component. Each page also needs an expectation matrix containing:

- context: session, tenant, role, language, and data state;
- element and property to verify;
- expected result and its source;
- applicable variants, including responsive variants;
- exclusions and any expectations still awaiting approval.

A DOM snapshot helps identify selectors and content, but it may contain defects. Do not automatically turn everything in the snapshot into an expectation. Agree which UI copy requires exact comparison: checking headings and the presence of paragraphs does not verify the full text.

Declare expectations independently of the values read during the test. Relationships such as “each link targets an existing section” can compare observed elements, but must also include independent expectations about counts and content.

## Responsibilities and location

| Element | Responsibility | Location |
|---|---|---|
| Page | URL, page elements, and loading criteria. | `web/<feature>/infrastructure/page` |
| Component | Elements and technical reads for part of a page. | `web/<feature>/infrastructure/page/component` |
| Test | Context, approved expectations, scenarios, and diagnostics. | `src/test/java/.../suite/contract` |
| Journey / UseCase | Data preparation, when needed. | Existing application layers |

The Page exposes what is present, such as `indexes()` and `sections()`. It does not select “valid” or “invalid” elements based on expected counts or copy. Those criteria belong in the test.

Create a shared component only when there is a concrete reuse opportunity. `TOSIndex` remains specific to TOS: it does not impose an index on other pages in the inventory.

## Selectors and DOM reads

- Prefer stable attributes and semantic structure, with selectors relative to the component.
- Identify the same logical component in desktop and mobile variants: avoid dependencies on layout containers or tag types when a stable identifying attribute is available. TOS sections are identified by the `otnotice-section-` prefix, regardless of the container tag.
- Avoid generated CSS classes, deep positional paths, and UUIDs hardcoded in the test.
- Do not include the expected copy in the selector used to read it: incorrect copy must produce an understandable difference.
- Read identifiers and links as data; verify counts, order, duplicates, and destinations in the test.
- Resolve relative links against the document base URL without hardcoding the host or environment.

SUIT capabilities do not all have the same semantics. In the current Selenium adapter, `getAll()` uses the `CLICKABLE` policy: for collections, this checks presence and `isEnabled()` without requiring visibility. It can read enabled links in the TOS indexes, including the hidden variant, but is not a universal reader for disabled elements.

`getText()` may return empty text for a hidden element. The TOS test reads the `href` attributes of both indexes and the section headings; it does not verify the copy in the hidden index. For new requirements, check the available capabilities and propose a SUIT extension only when necessary.

### Derive conventions from the codebase

Before choosing an XPath or capability, read comparable Pages and Components, selector composition, and the implementation of the adapter in use. Conventions may be implicit in the code: do not assume that a policy name alone describes its behavior, or that an existing selector is automatically suitable for a new case. Document verified behavior and where it applies.

The XPath identifies the component; the framework capability determines how it is located and read; the test verifies the required property. DOM presence, visibility, enabled state, and ability to interact are distinct properties. Do not introduce visibility or enabled-state filters into the selector to make a presence check pass.

When a stable attribute is sufficient to identify a container, do not also bind it to a layout tag. Within a component, use descendant relationships when intermediate wrappers have no functional meaning: `.//a` includes nested links, whereas `./li/a` requires a specific structure. Preserve component scope: a broader selector must not include ancestors, unrelated components, or duplicates. Semantic tags such as `h1`, `h2`, and `ul` remain appropriate when they describe the actual contract; do not replace them with `*` without verification.

### Desktop and mobile

Define the variants required by Interop behavior and breakpoints, checking them in the frontend source or in the DOM at the agreed viewports. A single HTML snapshot does not prove that the structure is identical on mobile. Reuse Pages, Components, and scenarios when the logical contract is the same; explicitly model expected functional differences without duplicating tests solely because of layout.

Distinguish a logical component from any responsive copies in the DOM. Declare expected counts and states for each verified variant. Do not weaken an exact count to `isNotEmpty()` just to accommodate mobile: this would lose detection of missing or additional components.

Record requested window dimensions and actual viewport dimensions (`innerWidth`, `innerHeight`), along with the browser and headless mode. Run the same scenarios for each required viewport. Report responsive checks in a desktop browser separately from mobile device emulation or execution on a mobile device. If the actual viewport does not match the requested one, coverage of that dimension remains pending.

## Synchronization and absence

`assertLoaded()` waits through existing capabilities and recognizes the page and its content. It must not execute the entire Visual expectation matrix. Do not use fixed sleeps.

Where possible, verify absence through an already loaded collection or an explicit state. Searching for an element that must be absent may consume the full lookup timeout.

Do not interpret a technical read failure as correct absence. In the current adapter, some lookups return `Optional.empty()` after an error: where data is required, fail with an explicit message instead of using `orElse(List.of())`. Optional components require a read that distinguishes absence from a technical error; request framework support if it is missing.

## Scenario structure

Follow the existing pattern:

```text
@TestFactory
  → validator.as(user, tenant)
  → on(Page.class)
  → tests(scenarios())
```

`scenarios()` declares readable, focused cases. Initial-state actions are no-ops when opening the page is sufficient. Do not add interactions that change state before the relevant assertions.

The validator manages navigation, the session, and browser shutdown for each case. Configure parallelism through `junit-platform.properties`; Interop rules prohibit `@Execution` on `ContractTest` classes. The environment comes from Spring profiles, without a fixed `@ActiveProfiles("qa")`.

Failure messages must identify the page, element or section, property, and expectation. Declare copy exceptions in the expectation data, avoiding branches based on numeric indices with no meaningful name.

An exception for part of the copy must not remove element identification: if only numbering remains to be confirmed, exclude that prefix and still verify the semantic heading. Avoid normalization beyond the agreed scope, as it could hide real text errors.

## TOS reference

The initial reference is `/termini-di-servizio`, with `TOSPage`, `TOSSection`, `TOSIndex`, and `WebTOSContractTest`. The baseline was reconstructed from the DOM supplied on October 2, 2026, and verified in QA as Comune di Milano / Administrator, in Italian.

| Scenario | Coverage |
|---|---|
| Title | Exact page-title copy. |
| Document | Eleven sections in the expected order, selected headings, and nonempty paragraphs. |
| Indexes | Two indexes in the DOM, eleven links each, targeting the same page and matching the sections one to one in the same order. |

The index count is specific to the TOS baseline. If the responsive structure changes intentionally, update the expectation and its source first. The same three scenarios passed with requested Chrome headless window sizes of `1440×900` and `390×844`. For the narrow window, the measured viewport was `500×701`: the DOM contains eleven sections and two indexes with eleven links each. This verifies the DOM contract at a narrow screen width of 500 px, not device emulation or a viewport width of 390 px. Actual desktop viewport dimensions were not measured.

The indexes in the available HTML have no stable identifying attributes: the selector still relies on the semantic `ul` list and links to sections. Do not replace `ul` indiscriminately with `*`, as this would also select ancestor containers. The selector tolerates intermediate wrappers around links; a variant that replaces the list requires checking the DOM or frontend source first. DOM presence of the indexes does not prove their visibility or accessibility at every viewport.

The numbering of “Proprietà intellettuale” remains to be confirmed. For that section, the exact heading is checked after removing any numeric prefix, along with the presence of content. Indexes include all descendant links, including those within an intermediate element: an additional link must fail the count check. `TIMESTAMP`, full paragraph text, index-entry copy, and global header/footer components are outside this coverage.

## Verification and completion

1. Verify compilation and selectors against the reference DOM.
2. Use targeted mutations to check detection of missing elements, duplicates, incorrect order, and incorrect destinations.
3. Run the scenarios in the agreed environment and inspect the report.
4. Distinguish successful compilation, actual execution, and excluded coverage.
5. Report CI and regression status separately: a passing targeted test does not prove that the entire suite is green.

Mutations must also check that a different heading is rejected, an additional nested link is detected, and a wrapper with no functional meaning does not break the selector. Distinguish XPath checks on HTML modified in memory from execution of Java assertions or the actual test: each provides different evidence. Preserve the command, environment, actual dimensions, and outcome; state which checks remain pending.

Credentials remain in local or CI configuration. Do not add SSO dependencies to the POM solely for a local authentication method: the CLI can provide temporary credentials to the Maven process.

VS Code automatic Java compilation may interfere with Maven output directories. If classes are missing during test startup, check for concurrent compilation; disable `java.autobuild.enabled` in user settings before recompiling. `surefire:test` uses existing artifacts and does not replace compilation after changes.

Respect session authorization requirements before making changes. In this work, the user requires explicit approval; do not delete reports or other local evidence without authorization and prior preservation.
