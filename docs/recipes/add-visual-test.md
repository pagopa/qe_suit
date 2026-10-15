# Add a Visual Test

This recipe applies the [WEB Visual Tests guide](../guides/visual/visual-tests.md). It is intended for developers and AI agents: missing expectations must be clarified, not invented.

## 1. Define the scope

Use the page inventory to identify the area, route, and frontend component. Use the task card to identify context, copy sources, required or prohibited components, initial states, and variants. State which aspects remain the responsibility of manual testers (TM).

A useful expectation-matrix row is:

```text
Context | Element | Presence / copy / state / visibility | Expected result | Source
```

## 2. Inspect the existing implementation

Read Pages, Components, URL configuration, authentication, suites, and comparable tests. Reuse existing elements, checking actual capabilities rather than relying only on interface names.

Derive implicit conventions for XPath, component composition, and lookup policies from the code. Check desktop/mobile variants and applicable breakpoints in the frontend source or DOM. Record available stable attributes and actual differences; do not invent a mobile selector from desktop HTML alone.

Use TOS as an organizational example, not a rigid template: a page without an index does not need an Index component. Check whether the route is stable, requires prior navigation, or redirects.

## 3. Present the changes file by file

List Pages and Components to create or extend, the test class, and any required integration. Identify framework or reporting-convention changes separately. Obtain required approvals before editing.

## 4. Model Pages and Components

Use configured URLs, stable selectors, and methods that describe elements. `assertLoaded()` recognizes page loading. Expected counts and copy belong in the test.

Read required attributes as data. Explicitly check whether capabilities read hidden or disabled components. Do not bypass SUIT with direct Selenium calls or ad hoc scripts inside the test.

Check that XPath selectors preserve component scope, tolerate wrappers with no functional meaning, and include every element to verify. Use stable identifying attributes to decouple containers from layout without indiscriminately generalizing every tag. Apply the [guide conventions](../guides/visual/visual-tests.md#derive-conventions-from-the-codebase).

## 5. Declare Visual scenarios

Use `WebBrowserContractValidator` and `scenarios()` with a JUnit factory. Configure context through `.as(...)` and the page through `.on(...)`.

The class currently follows `Web<Feature>ContractTest` in `suite.contract` for existing integration; scenario names declare the Visual category. Do not add `@Execution` or a fixed environment.

Separate static expectations from runtime reads. Check exceptions explicitly, with understandable messages. Do not claim coverage for copy, roles, states, or viewports that have not been verified.

For responsive variants, distinguish logical components from copies in the DOM, keeping expected counts and states explicit. Reuse the same scenarios if only layout changes. If part of the copy remains to be confirmed, retain a check of element identity.

## 6. Verify without losing evidence

Compile, check selectors, and try representative DOM mutations. Then run the actual test. If `clean` is necessary, preserve reports and evidence first, with any required authorization.

Try at least the regressions relevant to the case: a missing element, different heading, duplicate, incorrect destination, and additional nested link. Also verify that a harmless wrapper does not break the selector. Run the scenarios for each agreed viewport and record actual dimensions; a narrow window or hidden index alone does not demonstrate mobile coverage.

For TOS, from the `interop` directory, with credentials available to the process:

```bash
bash ./mvnw -B -ntp \
  -Dspring.profiles.active=qa \
  -Dchannel.web.headless=false \
  -Dtest=WebTOSContractTest test
```

Use `headless=true` for routine runs. The TOS factory generates three cases: a Maven filter on the factory method selects all of them.

To run the same scenarios with different Chrome window sizes, add one of the following quoted arguments to the command:

```bash
# Desktop window
'-Dchannel.web.arguments[0]=--window-size=1440,900'

# Narrow window
'-Dchannel.web.arguments[0]=--window-size=390,844'
```

This property uses the existing browser configuration. If other browser arguments are required, preserve them in the configured list: an indexed override may replace the profile list. Window size does not guarantee viewport size. In the QA verification on October 2, 2026, Chrome headless returned `innerWidth=500` and `innerHeight=701` for the requested `390×844` window: this execution covers a narrow screen at 500 px, not a 390 px viewport. Mobile coverage at precise dimensions requires a way to set and verify the viewport; these commands do not emulate a mobile device.

When using the local `interop-qa` SSO profile, temporary credentials can be exported within a subprocess:

```bash
(
  credentials_env=$(aws configure export-credentials --profile interop-qa --format env) || exit
  eval "$credentials_env"
  unset credentials_env
  bash ./mvnw -B -ntp \
    -Dspring.profiles.active=qa \
    -Dtest=WebTOSContractTest test
)
```

Run in a terminal without shell tracing and do not print the values. The profile name is local: use the authorized profile for the correct environment. Do not record credentials in the repository, documents, or chat.

## 7. Deliver

- Changed files and rationale.
- Implemented scenarios and a link to the expectation matrix.
- Command, environment, and verified outcome.
- Browser, headless mode, requested and actual dimensions, and verified responsive variants.
- Distinction between HTML checks, Java execution, and the actual test.
- Excluded coverage, copy awaiting confirmation, and other limitations.
- Separate status for the targeted test, regression suite, and CI.

For the next page, reuse this guide and applicable components. Update the guide when concrete verification establishes a new convention; do not create divergent copies in a skill.
