package it.pagopa.interop.common.infrastructure.fuzzing;

public record FuzzMutation(
        FuzzScenario scenario,
        FuzzMutationKind kind,
        Object value
) {
    public FuzzMutation {
        if (scenario == null) {
            throw new IllegalArgumentException("scenario must not be null");
        }
        if (kind == null) {
            throw new IllegalArgumentException("kind must not be null");
        }
        if (kind == FuzzMutationKind.REMOVE && value != null) {
            throw new IllegalArgumentException("REMOVE mutation value must be null");
        }
    }
}
