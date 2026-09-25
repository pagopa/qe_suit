# UseCases

This guide defines the responsibility and implementation rules for UseCases.

## Placement

UseCases belong to the `application` layer of the appropriate vertical slice.

Use `common` when the capability is shared across channels.

Use a channel-specific slice only when the capability itself is exclusive to that channel.

Typical location:

```text
<channel-or-common>/<business-entity>/application/
```

## Responsibility

A UseCase represents a high-level business intent.

Its method signatures should remain coarse-grained and business-oriented.

The UseCase:

- works with domain entities
- remains channel-agnostic
- delegates external interactions to Gateway interfaces
- must not contain HTTP, browser, selector, payload, polling, or other channel-specific details

Example:

```java
@Service
@RequiredArgsConstructor
public class AgreementUseCase {

    private final AgreementGateway agreementGateway;

    public Agreement createAgreement(
            EService eService,
            EServiceDescriptor descriptor,
            @Nullable Delegation delegation) {

        return agreementGateway.createAgreement(
                eService,
                descriptor,
                delegation
        );
    }
}
```

## Sad paths

Because the repository is a test suite, UseCases may explicitly model negative business expectations.

Example:

```java
public void shouldFailToCreateAgreement(
        EService eService,
        EServiceDescriptor descriptor,
        @Nullable Delegation delegation,
        AgreementCreationFailureReason reason) {

    agreementGateway.shouldFailToCreateAgreement(
            eService,
            descriptor,
            delegation,
            reason
    );
}
```

Negative expectations must always expose an explicit failure `Reason`.

The reason makes clear why the operation is expected to fail and keeps the intent business-readable.

## Top-down Gateway design

While implementing a UseCase, define the Gateway method that best expresses the required business operation, even if the concrete Gateway implementation does not exist yet.

Conceptually:

```text
Step class
  ↓
UseCase
  ↓
Gateway interface
```

The Gateway implementation is completed in the following phase.
