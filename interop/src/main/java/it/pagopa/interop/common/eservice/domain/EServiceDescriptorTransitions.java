package it.pagopa.interop.common.eservice.domain;

public final class EServiceDescriptorTransitions {
    private EServiceDescriptorTransitions() {
    }

    public static boolean canTransitionDirectlyTo(EServiceDescriptorState source, EServiceDescriptorState target) {
        return switch (source) {
            case DRAFT -> target == EServiceDescriptorState.PUBLISHED;
            case PUBLISHED -> target == EServiceDescriptorState.DEPRECATED
                    || target == EServiceDescriptorState.SUSPENDED
                    || target == EServiceDescriptorState.ARCHIVING;
            case DEPRECATED -> target == EServiceDescriptorState.SUSPENDED
                    || target == EServiceDescriptorState.ARCHIVING;
            case ARCHIVING -> target == EServiceDescriptorState.ARCHIVING_SUSPENDED
                    || target == EServiceDescriptorState.ARCHIVED;
            default -> false;
        };
    }

    public static void requireDirectTransition(EServiceDescriptorState source, EServiceDescriptorState target) {
        if (!canTransitionDirectlyTo(source, target)) {
            throw new IllegalStateException("Unsupported direct transition: " + source + " -> " + target);
        }
    }

    public static EServiceDescriptorState suspensionTarget(EServiceDescriptorState source) {
        if (canTransitionDirectlyTo(source, EServiceDescriptorState.SUSPENDED)) {
            return EServiceDescriptorState.SUSPENDED;
        }
        if (canTransitionDirectlyTo(source, EServiceDescriptorState.ARCHIVING_SUSPENDED)) {
            return EServiceDescriptorState.ARCHIVING_SUSPENDED;
        }
        throw new IllegalStateException("Cannot suspend a descriptor in state " + source);
    }
}
