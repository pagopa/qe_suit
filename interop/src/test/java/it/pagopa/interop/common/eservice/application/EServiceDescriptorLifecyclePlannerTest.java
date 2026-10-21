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

    private static final Map<Route, List<EServiceDescriptorLifecycleStep>> EXPECTED = Map.of(
            new Route(DRAFT, PUBLISHED), List.of(new EServiceDescriptorLifecycleStep(PUBLISH, PUBLISHED)),
            new Route(DRAFT, SUSPENDED), List.of(
                    new EServiceDescriptorLifecycleStep(PUBLISH, PUBLISHED),
                    new EServiceDescriptorLifecycleStep(SUSPEND, SUSPENDED)),
            new Route(PUBLISHED, SUSPENDED), List.of(new EServiceDescriptorLifecycleStep(SUSPEND, SUSPENDED)),
            new Route(DEPRECATED, SUSPENDED), List.of(new EServiceDescriptorLifecycleStep(SUSPEND, SUSPENDED)),
            new Route(ARCHIVING, ARCHIVING_SUSPENDED),
                    List.of(new EServiceDescriptorLifecycleStep(SUSPEND, ARCHIVING_SUSPENDED))
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

    private static Stream<Arguments> statePairs() {
        return Arrays.stream(EServiceDescriptorState.values()).flatMap(source ->
                Arrays.stream(EServiceDescriptorState.values()).map(target -> Arguments.of(source, target)));
    }
}
