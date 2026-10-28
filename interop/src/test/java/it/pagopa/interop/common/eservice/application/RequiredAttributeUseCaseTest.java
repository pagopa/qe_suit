package it.pagopa.interop.common.eservice.application;

import it.pagopa.interop.common.attribute.application.AttributeGateway;
import it.pagopa.interop.common.attribute.domain.Attribute;
import it.pagopa.interop.common.attribute.domain.AttributeKind;
import it.pagopa.interop.common.eservice.application.command.RequiredAttributeCommand;
import it.pagopa.interop.common.kernel.domain.AttributeRef;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class RequiredAttributeUseCaseTest {
    private final AttributeGateway gateway = mock(AttributeGateway.class);
    private final RequiredAttributeUseCase useCase = new RequiredAttributeUseCase(gateway);

    @Test
    void resolvesAllKindsByExplicitReferenceAndPreservesGroupsWithoutMutatingSnapshots() {
        List<RequiredAttributeCommand> requests = new ArrayList<>();
        List<Attribute> snapshots = new ArrayList<>();
        for (AttributeKind kind : AttributeKind.values()) {
            Attribute attribute = Attribute.builder().id(UUID.randomUUID()).kind(kind).group(9).build();
            snapshots.add(attribute);
            when(gateway.getAttribute(attribute.getRef())).thenReturn(attribute);
            requests.add(new RequiredAttributeCommand(kind, 1, attribute.getRef()));
        }

        var attributes = useCase.resolve(requests);

        assertThat(attributes.getCertified()).extracting(Attribute::getKind)
                .containsExactly(AttributeKind.CERTIFIED, AttributeKind.CERTIFIED_DISCRETE);
        assertThat(attributes.getDeclared()).extracting(Attribute::getKind).containsExactly(AttributeKind.DECLARED);
        assertThat(attributes.getVerified()).extracting(Attribute::getKind).containsExactly(AttributeKind.VERIFIED);
        assertThat(attributes.getCertified()).extracting(Attribute::getGroup).containsExactly(1, 1);
        assertThat(attributes.getDeclared()).extracting(Attribute::getGroup).containsExactly(1);
        assertThat(attributes.getVerified()).extracting(Attribute::getGroup).containsExactly(1);
        assertThat(snapshots).extracting(Attribute::getGroup).containsOnly(9);
        requests.forEach(request -> verify(gateway).getAttribute(request.getAttributeRef()));
        verifyNoMoreInteractions(gateway);
    }

    @Test
    void rejectsWrongKindsAndRepeatedReferencesAcrossGroups() {
        AttributeRef ref = new AttributeRef(UUID.randomUUID());
        var request = new RequiredAttributeCommand(AttributeKind.CERTIFIED, 1, ref);
        when(gateway.getAttribute(ref)).thenReturn(
                Attribute.builder().id(ref.id()).kind(AttributeKind.DECLARED).build());
        assertThatThrownBy(() -> useCase.resolve(List.of(request)))
                .isInstanceOf(IllegalStateException.class).hasMessageContaining("expected CERTIFIED");

        when(gateway.getAttribute(ref)).thenReturn(
                Attribute.builder().id(ref.id()).kind(AttributeKind.CERTIFIED).build());
        assertThatThrownBy(() -> useCase.resolve(List.of(request,
                new RequiredAttributeCommand(AttributeKind.CERTIFIED, 2, ref))))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("only once");
    }

    @Test
    void rejectsMissingRowsAndInvalidCommands() {
        AttributeRef ref = new AttributeRef(UUID.randomUUID());
        assertThatThrownBy(() -> useCase.resolve(null)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> useCase.resolve(List.of())).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> useCase.resolve(java.util.Arrays.asList((RequiredAttributeCommand) null)))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new RequiredAttributeCommand(null, 1, ref))
                .isInstanceOf(NullPointerException.class).hasMessageContaining("kind");
        assertThatThrownBy(() -> new RequiredAttributeCommand(AttributeKind.CERTIFIED, 0, ref))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("group");
        assertThatThrownBy(() -> new RequiredAttributeCommand(AttributeKind.CERTIFIED, 1, null))
                .isInstanceOf(NullPointerException.class).hasMessageContaining("reference");
        verifyNoInteractions(gateway);
    }
}
