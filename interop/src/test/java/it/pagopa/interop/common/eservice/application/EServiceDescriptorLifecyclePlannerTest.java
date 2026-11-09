package it.pagopa.interop.common.eservice.application;

import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorTransitions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static it.pagopa.interop.common.eservice.application.EServiceDescriptorLifecycleStep.Action.*;
import static it.pagopa.interop.common.eservice.domain.EServiceDescriptorState.*;
import static org.assertj.core.api.Assertions.*;

class EServiceDescriptorLifecyclePlannerTest {
    private final EServiceDescriptorLifecyclePlanner planner = new EServiceDescriptorLifecyclePlanner();

    private record Route(EServiceDescriptorState source, EServiceDescriptorState target) {}

    private static final Map<Route, List<EServiceDescriptorLifecycleStep>> EXPECTED = Map.ofEntries(
            Map.entry(new Route(DRAFT, PUBLISHED), List.of(new EServiceDescriptorLifecycleStep(PUBLISH, PUBLISHED))),
            Map.entry(new Route(DRAFT, WAITING_FOR_APPROVAL), List.of(
                    new EServiceDescriptorLifecycleStep(PUBLISH_FOR_APPROVAL, WAITING_FOR_APPROVAL))),
            Map.entry(new Route(DRAFT, DEPRECATED), List.of(
                    new EServiceDescriptorLifecycleStep(PUBLISH, PUBLISHED),
                    new EServiceDescriptorLifecycleStep(PUBLISH_NEXT_VERSION, DEPRECATED))),
            Map.entry(new Route(DRAFT, SUSPENDED), List.of(
                    new EServiceDescriptorLifecycleStep(PUBLISH, PUBLISHED),
                    new EServiceDescriptorLifecycleStep(SUSPEND, SUSPENDED))),
            Map.entry(new Route(DRAFT, ARCHIVING), List.of(
                    new EServiceDescriptorLifecycleStep(PUBLISH, PUBLISHED),
                    new EServiceDescriptorLifecycleStep(ARCHIVE, ARCHIVING))),
            Map.entry(new Route(DRAFT, ARCHIVING_SUSPENDED), List.of(
                    new EServiceDescriptorLifecycleStep(PUBLISH, PUBLISHED),
                    new EServiceDescriptorLifecycleStep(ARCHIVE, ARCHIVING),
                    new EServiceDescriptorLifecycleStep(SUSPEND, ARCHIVING_SUSPENDED))),
            Map.entry(new Route(PUBLISHED, SUSPENDED), List.of(new EServiceDescriptorLifecycleStep(SUSPEND, SUSPENDED))),
            Map.entry(new Route(PUBLISHED, DEPRECATED),
                    List.of(new EServiceDescriptorLifecycleStep(PUBLISH_NEXT_VERSION, DEPRECATED))),
            Map.entry(new Route(PUBLISHED, ARCHIVING), List.of(new EServiceDescriptorLifecycleStep(ARCHIVE, ARCHIVING))),
            Map.entry(new Route(DEPRECATED, SUSPENDED), List.of(new EServiceDescriptorLifecycleStep(SUSPEND, SUSPENDED))),
            Map.entry(new Route(DEPRECATED, ARCHIVING), List.of(new EServiceDescriptorLifecycleStep(ARCHIVE, ARCHIVING))),
            Map.entry(new Route(ARCHIVING, ARCHIVING_SUSPENDED),
                    List.of(new EServiceDescriptorLifecycleStep(SUSPEND, ARCHIVING_SUSPENDED))),
            Map.entry(new Route(PUBLISHED, ARCHIVING_SUSPENDED), List.of(
                    new EServiceDescriptorLifecycleStep(ARCHIVE, ARCHIVING),
                    new EServiceDescriptorLifecycleStep(SUSPEND, ARCHIVING_SUSPENDED))),
            Map.entry(new Route(DEPRECATED, ARCHIVING_SUSPENDED), List.of(
                    new EServiceDescriptorLifecycleStep(ARCHIVE, ARCHIVING),
                    new EServiceDescriptorLifecycleStep(SUSPEND, ARCHIVING_SUSPENDED)))
    );

    @ParameterizedTest
    @MethodSource("statePairs")
    void plansOnlyExecutablePaths(EServiceDescriptorState source, EServiceDescriptorState target) {
        if (source == target) {
            assertThat(planner.planToReachState(source, target)).isEmpty();
        } else if (EXPECTED.containsKey(new Route(source, target))) {
            List<EServiceDescriptorLifecycleStep> plan = planner.planToReachState(source, target);
            assertThat(plan).containsExactlyElementsOf(EXPECTED.get(new Route(source, target)));
            EServiceDescriptorState current = source;
            for (EServiceDescriptorLifecycleStep step : plan) {
                assertThat(EServiceDescriptorTransitions.canTransitionDirectlyTo(current, step.expectedState())).isTrue();
                current = step.expectedState();
            }
            assertThat(current).isEqualTo(target);
        } else {
            assertThatThrownBy(() -> planner.planToReachState(source, target))
                    .isInstanceOf(UnsupportedOperationException.class)
                    .hasMessage("Unsupported setup path: " + source + " -> " + target);
        }
    }

    @Test
    void plansCannotBeModifiedByCallers() {
        assertThatThrownBy(() -> planner.planToReachState(DRAFT, SUSPENDED).clear())
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void doesNotTreatScheduledArchivingAsTerminalArchiving() {
        assertThatThrownBy(() -> planner.planToReachState(ARCHIVING, ARCHIVED))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessage("Unsupported setup path: ARCHIVING -> ARCHIVED");
    }

    @Test
    void plansDelegatedPublicationForApprovalWithoutApprovingIt() {
        assertThat(planner.planToReachState(DRAFT, WAITING_FOR_APPROVAL))
                .containsExactly(new EServiceDescriptorLifecycleStep(
                        PUBLISH_FOR_APPROVAL, WAITING_FOR_APPROVAL
                ));
    }

    private static Stream<Arguments> statePairs() {
        return Arrays.stream(EServiceDescriptorState.values()).flatMap(source ->
                Arrays.stream(EServiceDescriptorState.values()).map(target -> Arguments.of(source, target)));
    }
}
