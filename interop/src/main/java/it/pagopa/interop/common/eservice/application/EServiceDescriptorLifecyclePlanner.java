package it.pagopa.interop.common.eservice.application;

import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorTransitions;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static it.pagopa.interop.common.eservice.application.EServiceDescriptorLifecycleStep.Action.*;
import static it.pagopa.interop.common.eservice.domain.EServiceDescriptorState.*;

@Component
public class EServiceDescriptorLifecyclePlanner {
    private static final EServiceDescriptorLifecycleStep PUBLISH_STEP =
            new EServiceDescriptorLifecycleStep(PUBLISH, PUBLISHED);
    private static final EServiceDescriptorLifecycleStep SUSPEND_STEP =
            new EServiceDescriptorLifecycleStep(SUSPEND, SUSPENDED);
    private static final EServiceDescriptorLifecycleStep SUSPEND_ARCHIVING_STEP =
            new EServiceDescriptorLifecycleStep(SUSPEND, ARCHIVING_SUSPENDED);
    private static final EServiceDescriptorLifecycleStep ARCHIVE_STEP =
            new EServiceDescriptorLifecycleStep(ARCHIVE, ARCHIVING);

    private static final Map<EServiceDescriptorState, Map<EServiceDescriptorState, List<EServiceDescriptorLifecycleStep>>> PLANS =
            Map.of(
                    DRAFT, Map.of(
                            PUBLISHED, List.of(PUBLISH_STEP),
                            WAITING_FOR_APPROVAL, List.of(
                                    new EServiceDescriptorLifecycleStep(PUBLISH_FOR_APPROVAL, WAITING_FOR_APPROVAL)),
                            DEPRECATED, List.of(
                                    PUBLISH_STEP,
                                    new EServiceDescriptorLifecycleStep(PUBLISH_NEXT_VERSION, DEPRECATED)),
                            SUSPENDED, List.of(PUBLISH_STEP, SUSPEND_STEP),
                            ARCHIVING, List.of(PUBLISH_STEP, ARCHIVE_STEP),
                            ARCHIVING_SUSPENDED, List.of(
                                    PUBLISH_STEP, ARCHIVE_STEP, SUSPEND_ARCHIVING_STEP)
                    ),
                    PUBLISHED, Map.of(
                            DEPRECATED, List.of(
                                    new EServiceDescriptorLifecycleStep(PUBLISH_NEXT_VERSION, DEPRECATED)),
                            SUSPENDED, List.of(SUSPEND_STEP),
                            ARCHIVING, List.of(ARCHIVE_STEP),
                            ARCHIVING_SUSPENDED, List.of(ARCHIVE_STEP, SUSPEND_ARCHIVING_STEP)
                    ),
                    DEPRECATED, Map.of(
                            SUSPENDED, List.of(SUSPEND_STEP),
                            ARCHIVING, List.of(ARCHIVE_STEP),
                            ARCHIVING_SUSPENDED, List.of(ARCHIVE_STEP, SUSPEND_ARCHIVING_STEP)
                    ),
                    ARCHIVING, Map.of(ARCHIVING_SUSPENDED, List.of(SUSPEND_ARCHIVING_STEP))
            );

    public List<EServiceDescriptorLifecycleStep> planToReachState(
            EServiceDescriptorState source, EServiceDescriptorState target
    ) {
        Objects.requireNonNull(source, "Source state is required");
        Objects.requireNonNull(target, "Target state is required");
        if (source == target) {
            return List.of();
        }
        List<EServiceDescriptorLifecycleStep> plan = PLANS.getOrDefault(source, Map.of()).get(target);
        if (plan == null) {
            throw new UnsupportedOperationException("Unsupported setup path: " + source + " -> " + target);
        }
        EServiceDescriptorState current = source;
        for (EServiceDescriptorLifecycleStep step : plan) {
            EServiceDescriptorTransitions.requireDirectTransition(current, step.expectedState());
            current = step.expectedState();
        }
        return plan;
    }
}
