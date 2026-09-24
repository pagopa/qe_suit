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
    private final InteropHttpContractValidator httpContractValidator;
    private final InteropJourney interopJourney;
    private final BffAgreementRequestFactory requestFactory;

    @TestFactory
    Stream<DynamicTest> createAgreement() {

        return httpContractValidator
                .as(
                        Tenant.COMUNE_DI_MILANO,
                        UserRole.ADMIN
                )
                .apiCall(() ->
                        apiClient
                                .agreements()
                                .createAgreement()
                )
                .payload(() -> {
                    EService createdEservice = interopJourney
                            .withProducer(
                                    Tenant.COMUNE_DI_TORINO,
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

## Authentication and final API-call identity

The final identity of the HTTP request is declared explicitly in the DSL through:

```java
.as(Tenant, User)
```

or:

```java
.as(Tenant, UserRole)
```

This is the identity that the generated API call must execute with.

It is not just an initial setup step. It represents the final API-call identity for the generated `DynamicTest` execution.

The recommended pattern is:

```java
return httpContractValidator
        .as(
                Tenant.COMUNE_DI_MILANO,
                UserRole.ADMIN
        )
        .apiCall(() ->
                apiClient
                        .agreements()
                        .createAgreement()
        )
        .payload(...)
        .tests();
```

The authentication declared through `as(...)` is reapplied for every generated `DynamicTest` immediately before the corresponding API operation is materialized and executed.

### Temporary session used by a precondition

`payload(...)` and `pathParams(...)` are runtime suppliers. They can execute setup logic and temporarily switch session context.

This is useful when a precondition needs a different user or tenant than the one used by the final API call.

Example:

```java
return httpContractValidator
        .as(
                Tenant.COMUNE_DI_MILANO,
                UserRole.ADMIN
        )
        .apiCall(() ->
                apiClient
                        .agreements()
                        .createAgreement()
        )
        .payload(() -> {
            EService createdEservice = interopJourney
                    .withProducer(
                            Tenant.COMUNE_DI_TORINO,
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
```

In this case:

- `COMUNE_DI_TORINO` is the temporary session used only for the setup step;
- that session does not become the authentication of the API call;
- before the actual request is executed, the framework restores the identity declared in `as(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN)`;
- the final HTTP request is executed as `COMUNE_DI_MILANO / ADMIN`.

This is the fundamental semantic rule: the supplier used to build the valid request may authenticate differently during setup, but the request itself is always executed with the final identity declared in `as(...)`.

### Anti-pattern: authenticating manually inside apiCall

Avoid patterns like this:

```java
.apiCall(() -> {
    interopJourney.withProducer(
            Tenant.COMUNE_DI_MILANO,
            UserRole.ADMIN
    );
    return apiClient.agreements().createAgreement();
})
```

This is an anti-pattern for a contract test. The authentication of the final request must be declared with `as(...)`.

The framework applies it automatically for each generated test case.

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

The same principle applies here: if a `pathParams(...)` supplier changes the session temporarily during precondition setup, that temporary session does not become the final API-call identity.

Example:

```java
.pathParams(() -> {
    // temporary session changes are allowed here
    return Map.of(
            "agreementId", agreementId,
            "descriptorId", descriptorId
    );
})
```

The final request still uses the identity declared through `as(...)`.

The public contract is intentionally simple: pass a map keyed by the OpenAPI path-parameter name, or provide a POJO/record whose property names match the path-parameter names.

Preferred form for readability and clarity:

```java
.pathParams(() -> Map.of(
        "agreementId", agreementId,
        "descriptorId", descriptorId
))
```

This is the recommended style for contract tests because the mapping is explicit and mirrors the generated OpenAPI operation signature (`agreementIdPath(...)`, `descriptorIdPath(...)`).

The framework also supports POJO/record usage:

```java
record AgreementPathParams(
        String agreementId,
        String descriptorId
) {}
```

Usage:

```java
.pathParams(() -> new AgreementPathParams(
        agreementId,
        descriptorId
))
```

Conceptually:

```text
POJO / record property name
          =
OpenAPI path parameter name
```

The framework converts the supplied values to the concrete generated setter type before invoking the operation. For example, a UUID path parameter is bound to a method like:

```java
clientIdPath(UUID clientId)
```

not to `clientIdPath(Object)`. This is what makes the binding reliable for generated OpenAPI clients.

This allows path parameters to participate in the same structural mutation model used for payloads, while keeping the API ergonomic for the test author.

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
