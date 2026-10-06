# API Contract Tests

This guide describes how to implement backend/API contract tests.

## Purpose

API contract tests validate the technical contract of an endpoint independently from business logic.

The main principle is:

> Start from a semantically valid Java object and generate atomic variations of its structure.

Each generated test mutates one target at a time.

If the mutation produces an invalid request, the observed behavior can be attributed to that specific variation.

Conceptually:

```text
Valid request
    ↓
Atomic mutation
    ↓
Generated test case
    ↓
API call
    ↓
Contract assertion
```
## Location

Contract test file lives under:

```text
src/test/java/.../suite/contract/<channel-prefix><domain-entity>ContractTest.java
```

The file name must represent the business entity under test.

Examples:

```text
BffAgreementContractTest.java
```

## Test structure

API contract tests are implemented as JUnit 5 dynamic tests.

Example:

```java
@Execution(ExecutionMode.CONCURRENT)
@SpringBootTest(
        classes = {
                TestBootApp.class,
                JunitContextConfig.class,
                BffApiContractConfig.class
        }
)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class BffAgreementContractTest {

    private final ApiClient apiClient;
    private final HttpContractValidator httpContractValidator;
    private final InteropJourney interopJourney;
    private final BffAgreementRequestFactory requestFactory;

    @TestFactory
    Stream<DynamicTest> createAgreement() {

        return httpContractValidator
                .apiCall(() -> {
                    interopJourney.withProducer(
                            Tenant.COMUNE_DI_MILANO,
                            UserRole.ADMIN
                    );

                    return apiClient
                            .agreements()
                            .createAgreement();
                })
                .payload(() -> {
                    EService createdEservice = interopJourney
                            .withProducer(
                                    Tenant.COMUNE_DI_MILANO,
                                    UserRole.ADMIN
                            )
                            .createEService(
                                    EServiceDescriptorState.PUBLISHED
                            )
                            .get(EService.class);

                    return requestFactory.creationRequest(
                            createdEservice,
                            createdEservice.getActiveDescriptor(),
                            null
                    );
                })
                .tests();
    }
}
```

## Preconditions

A contract test may need existing domain state before the endpoint can be invoked correctly.

Reuse existing business UseCases or Journeys for this purpose.

Example:

```java
EService createdEservice = interopJourney
        .withProducer(
                Tenant.COMUNE_DI_MILANO,
                UserRole.ADMIN
        )
        .createEService(
                EServiceDescriptorState.PUBLISHED
        )
        .get(EService.class);
```

This code creates valid test data.

The contract test still validates the API contract rather than the business flow used to create the precondition.

## API call definition

Use `apiCall(...)` to provide the generated OpenAPI operation used by the test.

Example:

```java
.apiCall(() ->
        apiClient
                .agreements()
                .createAgreement()
)
```

The API client is generated through OpenAPI Generator and configured to use RestAssured.

## Payload

The `payload(...)` stage provides the semantically valid request object used as the starting point for generated mutations.

Example:

```java
.payload(() ->
        requestFactory.creationRequest(
                createdEservice,
                createdEservice.getActiveDescriptor(),
                null
        )
)
```

Whenever possible, use an existing request factory instead of constructing technical payloads directly inside the test class.

## Path parameters

Path parameters are supplied through `pathParams(...)`.

`pathParams` expects a JSON-like object with named properties.

Do not pass an unnamed list of values.

The property names must correspond to the endpoint path-parameter names.

Example:

```java
record AgreementPathParams(
        String agreementId,
        String descriptorId
) {}
```

Usage:

```java
.pathParams(() ->
        new AgreementPathParams(
                agreementId,
                descriptorId
        )
)
```

Conceptually:

```text
POJO / record property name
          =
OpenAPI path parameter name
```

This allows path parameters to participate in the same structural mutation model used for payloads.

## Scenarios

Use `scenario(...)` to declare a mutation rule and the expected contract response.

Example:

```java
.scenario(
        FuzzScenario.NULL_REQUIRED,
        response -> {
            // assert expected contract behavior
        }
)
```

A scenario describes which mutation should be generated and how the resulting response must be validated.

## Targeted mutations

Use `targets(...)` when a mutation must be restricted to specific fields.

Example:

```java
.targets(
        FuzzScenario.INVALID_FORMAT,
        response -> {
            // assert expected contract behavior
        },
        List.of(
                TargetExpression.field(
                        AgreementPathParams::agreementId
                ),
                TargetExpression.field(
                        AgreementPathParams::descriptorId
                )
        )
)
```

This keeps contract tests declarative.

The QA Engineer specifies:

- the mutation rule
- the target fields
- the expected response

The framework generates the concrete dynamic tests.

## Dynamic test generation

Complete the definition with:

```java
.tests();
```

The validator generates:

```java
Stream<DynamicTest>
```

The QA Engineer should not manually duplicate one JUnit test for every structural variation when the validator can derive those cases from the declared rules.

## Design principle

Prefer declarative contract definitions over imperative test duplication.

```text
valid request
+ mutation rules
+ target fields
+ expected response
        ↓
HttpContractValidator
        ↓
DynamicTest[]
```
