# Add a Contract Test

This recipe describes the operational process for implementing a new contract test.

It starts after the Jira ticket has been analyzed and the required activity has been classified as a **Contract Test**.

Contract tests validate the technical contract exposed by a channel and remain independent from business-test implementation flows.

## 1. Identify the contract type

Determine which channel contract is under test:

- API / backend contract
- WEB / frontend contract

Follow the corresponding guide:

- [API Contract Tests](../guides/contract/api-contract-tests.md)
- [WEB Contract Tests](../guides/contract/web-contract-tests.md)

## 2. Create the JUnit contract test class

Contract tests are implemented through JUnit classes.

The test class should:

- run through JUnit 5
- use `@TestFactory`
- return `Stream<DynamicTest>`
- support concurrent execution when the test data and target system allow it
- rely on the appropriate contract validator for the channel

Typical structure:

```java
@Execution(ExecutionMode.CONCURRENT)
@SpringBootTest(...)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class <Channel><Capability>ContractTest {

    @TestFactory
    Stream<DynamicTest> contractTests() {
        // validator configuration
        // dynamic test generation
    }
}
```

## 3. Prepare required preconditions

A contract test may require valid domain data before the contract itself can be exercised.

When preconditions are required:

- reuse existing business UseCases or Journeys
- use them only to create or retrieve valid test data
- keep the actual contract assertion inside the contract-test flow

Using business abstractions for setup does not turn the test into a business test.

The contract remains the object under test.

## 4. Configure the contract validator

For API contracts, configure the `HttpContractValidator`.

For WEB contracts, configure the `WebBrowserContractValidator`.

The validator is responsible for generating or executing the JUnit `DynamicTest` instances according to the declared contract scenarios.

## 5. Generate the Dynamic Tests

The final result of a contract-test definition is a stream of JUnit dynamic tests:

```java
Stream<DynamicTest>
```

For API contracts, the validator generates test cases from valid request data and declared mutation rules.

For WEB contracts, the validator executes explicitly declared `WebScenario` instances.

## 6. Validate the implementation

### Definition of Done

- [ ] Contract type is correctly identified
- [ ] Test is implemented as a JUnit contract test
- [ ] Preconditions reuse existing business abstractions where required
- [ ] Contract assertions remain independent from business-test flow
- [ ] Dynamic tests are generated through the appropriate validator
- [ ] Test data does not introduce avoidable shared-state conflicts
- [ ] Tests can run concurrently where applicable
- [ ] Tests pass locally
- [ ] Regression suite is green
- [ ] CI is green

## Implementation principle

The contract test starts from the technical contract, not from a Cucumber Feature.

```text
Jira Ticket
  ↓
Contract Test
  ↓
JUnit class
  ↓
Contract validator
  ↓
DynamicTest[]
```

When setup data is required:

```text
JUnit class
  ↓
UseCase / Journey
  ↓
valid test data
  ↓
Contract validator
  ↓
DynamicTest[]
```
