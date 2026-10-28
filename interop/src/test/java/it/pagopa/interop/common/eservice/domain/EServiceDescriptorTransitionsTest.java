package it.pagopa.interop.common.eservice.domain;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.Arguments;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Stream;

import static it.pagopa.interop.common.eservice.domain.EServiceDescriptorState.*;
import static org.assertj.core.api.Assertions.*;

class EServiceDescriptorTransitionsTest {
    private record Transition(EServiceDescriptorState source, EServiceDescriptorState target) {}

    private static final Set<Transition> ALLOWED = Set.of(
            new Transition(DRAFT, PUBLISHED),
            new Transition(DRAFT, WAITING_FOR_APPROVAL),
            new Transition(PUBLISHED, DEPRECATED),
            new Transition(PUBLISHED, SUSPENDED),
            new Transition(PUBLISHED, ARCHIVING),
            new Transition(DEPRECATED, SUSPENDED),
            new Transition(DEPRECATED, ARCHIVING),
            new Transition(ARCHIVING, ARCHIVING_SUSPENDED),
            new Transition(ARCHIVING, ARCHIVED)
    );

    @ParameterizedTest
    @MethodSource("statePairs")
    void enforcesTheDirectTransitionMatrix(EServiceDescriptorState source, EServiceDescriptorState target) {
        boolean allowed = ALLOWED.contains(new Transition(source, target));

        assertThat(EServiceDescriptorTransitions.canTransitionDirectlyTo(source, target)).isEqualTo(allowed);
        if (allowed) {
            assertThatCode(() -> EServiceDescriptorTransitions.requireDirectTransition(source, target))
                    .doesNotThrowAnyException();
        } else {
            assertThatThrownBy(() -> EServiceDescriptorTransitions.requireDirectTransition(source, target))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @ParameterizedTest
    @EnumSource(EServiceDescriptorState.class)
    void archivedIsTerminal(EServiceDescriptorState target) {
        assertThat(EServiceDescriptorTransitions.canTransitionDirectlyTo(ARCHIVED, target)).isFalse();
    }

    private static Stream<Arguments> statePairs() {
        return Arrays.stream(EServiceDescriptorState.values()).flatMap(source ->
                Arrays.stream(EServiceDescriptorState.values()).map(target -> Arguments.of(source, target)));
    }
}
