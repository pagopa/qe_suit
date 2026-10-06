# WEB Contract Tests

This guide describes how to implement frontend/WEB contract tests.

## Purpose

WEB contract tests validate technical UI behavior such as:

- input validation
- error messages
- helper text
- component-level interaction constraints

Unlike API contracts, the UI contract is not structurally uniform enough to derive all test cases automatically from a schema.

For this reason, WEB contract scenarios are declared explicitly.

## Location

Contract test file lives under:

```text
src/test/java/.../suite/contract/<channel-prefix><domain-entity>ContractTest.java
```

The file name must represent the business entity under test.

Examples:

```text
WebDebugClientAssertionContractTest.java
```

## Test structure

WEB contract tests are implemented as JUnit 5 dynamic tests through `WebBrowserContractValidator`.

Example:

```java
@Execution(ExecutionMode.CONCURRENT)
@SpringBootTest(
        classes = {
                TestBootApp.class,
                JunitContextConfig.class,
                WebJUnitSuitConfig.class
        },
        properties = "spring.profiles.include=junit"
)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class WebDebugClientAssertionContractTest {

    private final WebBrowserContractValidator webContractValidator;

    @TestFactory
    Stream<DynamicTest> shouldValidateDebugClientAssertionForm() {
        return webContractValidator
                .as(
                        User.getTenantAdmin(
                                Tenant.COMUNE_DI_MILANO
                        ),
                        Tenant.COMUNE_DI_MILANO
                )
                .on(DebugClientAssertionPage.class)
                .tests(scenarios());
    }

    private Stream<WebScenario<DebugClientAssertionPage>> scenarios() {
        return Stream.of(
                new WebScenario<>(
                        "client assertion vuota",
                        page -> {
                            page.clientAssertionInput().fill(" ");
                            page.submitButton().click();
                        },
                        page -> Assertions.assertThat(
                                page.getClientAssertionErrorMessage()
                        ).isEqualTo("Inserisci un JWT valido.")
                )
        );
    }
}
```

## WebScenario

A WEB contract scenario is represented by:

```java
public record WebScenario<P>(
        String name,
        Consumer<P> action,
        Consumer<P> assertion
) {}
```

Each scenario contains:

- `name`: human-readable test-case name
- `action`: interaction that puts the UI into the condition under test
- `assertion`: verification of the expected technical contract

Conceptually:

```text
Scenario name
    ↓
Action
    ↓
Assertion
```

## Validator flow

The validator receives:

- the user / tenant context
- the Page Object under test
- the declared scenarios

Example:

```java
webContractValidator
        .as(user, tenant)
        .on(DebugClientAssertionPage.class)
        .tests(scenarios());
```

Conceptually:

```text
User / Tenant
      ↓
Page Object
      ↓
WebScenario[]
      ↓
WebBrowserContractValidator
      ↓
DynamicTest[]
```

## Scenario declaration

Declare each contract case explicitly.

Example:

```java
new WebScenario<>(
        "client id vuoto",
        page -> {
            page.clientIdInput().fill(" ");
            page.submitButton().click();
        },
        page -> Assertions.assertThat(
                page.getClientIdErrorMessage()
        ).isEqualTo("Inserisci un UUID valido.")
)
```

Keep the scenario focused on one technical UI contract.

Typical examples include:

- required-field validation
- invalid-format validation
- disabled/enabled component behavior
- helper-text verification
- technical form validation

## API vs WEB contract generation

API contract tests can derive many cases automatically from structured request objects and mutation rules.

WEB contract tests require explicit scenario declaration because the UI contract is less homogeneous and cannot be inferred reliably from a single structural schema.

The difference is:

```text
API
valid structure
+ mutation rules
→ generated scenarios
```

```text
WEB
explicit WebScenario definitions
→ executed scenarios
```

Both approaches produce:

```java
Stream<DynamicTest>
```

and both remain independent from the Cucumber business-test flow.
