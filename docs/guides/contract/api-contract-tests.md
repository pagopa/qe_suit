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

`payload(...)`, `pathParams(...)` and `queryParams(...)` are runtime suppliers. They can execute setup logic and temporarily switch session context.

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

## Query parameters

Query parameters are supplied through `queryParams(...)`, following exactly the same rules as `pathParams(...)`: a map keyed by the OpenAPI parameter name, or a POJO/record whose property names match the parameter names.

Preferred form:

```java
.queryParams(() -> Map.of(
        "offset", 0,
        "limit", 10,
        "states", List.of("ACTIVE")
))
```

Values are bound to the generated `<name>Query(...)` methods. Lists are expanded into varargs, so array parameters are decomposed and fuzzed element by element as well.

The supplied map is the baseline: the unmutated call must succeed. Query parameters have no generated DTO, therefore no validation annotation is available. At runtime every supplied parameter is treated as optional, so removing it or setting it to null is expected to succeed, while every other scenario is resolved by the contract policy. Use `scenario(...)` or `targets(...)` to override a specific expectation, for example when a parameter is in fact required:

```java
.queryParams(() -> Map.of("filter", "abc", "page", 1))
.targets(
        FuzzScenario.REMOVED,
        response -> response.then().statusCode(400),
        List.of(query -> query.get("filter"))
)
```

The scopes are composable and order independent: `payload(...)`, `pathParams(...)` and `queryParams(...)` can be chained on the same API call, and each one contributes its own dynamic tests.

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

## Authorization tests (identity x API)

`as(identities).apiCalls(...)` generates one dynamic test for every identity x API combination.
Each case authenticates the identity, creates a fresh operation from the supplier, sends **one**
request exactly as the supplier prepared it (body, path, query and headers are not mutated, validated or
fixed) and checks the exact HTTP status. The default expected status is `403`; use `expectedStatus(int)` to
change it for the whole declaration. A `400` instead of `403` is a KO: the system under test must check
permissions before validating the request.

```java
@TestFactory
Stream<DynamicTest> forbiddenForReadOnlyUsers() {
    List<User> users = List.of(
            User.getTenantUser(Tenant.COMUNE_DI_MILANO, UserRole.VIEWER),
            User.getTenantUser(Tenant.COMUNE_DI_MILANO, UserRole.REVIEWER));

    return httpContractValidator
            .as(Tenant.COMUNE_DI_MILANO, users)
            .apiCalls(
                    () -> apiClient.agreements().createAgreement(),
                    () -> apiClient.agreements().getAgreementById().agreementIdPath(agreementId))
            .tests();                       // expected 403
}

@TestFactory
Stream<DynamicTest> unauthorizedAcrossTenants() {
    return httpContractValidator
            .as(List.of(
                    InteropContractIdentity.bff(Tenant.AGID, UserRole.VIEWER),
                    InteropContractIdentity.m2m(Tenant.PAGO_PA, M2MRole.M2M)))
            .apiCalls(() -> apiClient.agreements().createAgreement())
            .expectedStatus(401)
            .tests();
}
```

Notes:

* `tests()` only builds the cases: it does not authenticate, call the suppliers or send requests.
* Authentication runs inside each dynamic test, before the supplier, exactly like `apiCall(...)`; parallelism is left to
  the JUnit configuration.
* Case names look like `[authorization] Comune di Milano / API | api #2 | expected 403`; repeated identity labels are
  disambiguated with `identity #n`.
* The report shows the API each case actually invoked (`METHOD /route`, read from the generated client), one report
  group per API, even though the `@TestFactory` name does not match a single operation. Cases that fail before the
  operation is created are listed under `API not resolved (operation not created)`.
* The factory-name = operation-name architecture rule applies to `apiCall(...)` only; a factory must use either
  `apiCall(...)` or `apiCalls(...)`.
* In SEND, `SendHttpContractValidator.as(users)` sets the user in `CurrentUserSession`; the HTTP request specs read it
  when the operation is created. It needs an `HttpContractValidator` instance (no Spring bean is provided yet).

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
