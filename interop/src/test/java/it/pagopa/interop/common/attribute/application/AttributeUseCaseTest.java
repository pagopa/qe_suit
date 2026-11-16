package it.pagopa.interop.common.attribute.application;

import it.pagopa.interop.common.attribute.domain.Attribute;
import it.pagopa.interop.common.attribute.domain.AttributeKind;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class AttributeUseCaseTest {
    @Test
    void generatesUniqueCertifiedAttributeNamesAndAcceptsExplicitNames() {
        var gateway = mock(AttributeGateway.class);
        when(gateway.createAttribute(any())).thenAnswer(invocation -> invocation.getArgument(0));
        var useCase = new AttributeUseCase(gateway);

        Attribute first = useCase.createCertifiedAttribute();
        Attribute second = useCase.createCertifiedAttribute();
        Attribute named = useCase.createCertifiedAttribute("attribute-CERT-1-CERTIFIED");

        assertThat(first.getName()).isNotEqualTo(second.getName())
                .startsWith("attribute-").endsWith("-CERTIFIED");
        assertThat(named.getName()).isEqualTo("attribute-CERT-1-CERTIFIED");
        assertThatThrownBy(() -> useCase.createCertifiedAttribute(" "))
                .isInstanceOf(IllegalArgumentException.class);
        verify(gateway, times(3)).createAttribute(any());
    }

    @ParameterizedTest
    @EnumSource(AttributeKind.class)
    void createsTheRequestedKindWithoutAssigningIt(AttributeKind kind) {
        var gateway = mock(AttributeGateway.class);
        when(gateway.createAttribute(any())).thenAnswer(invocation -> invocation.getArgument(0));
        var useCase = new AttributeUseCase(gateway);

        Attribute created = useCase.createAttribute(kind, "scenario-attribute");

        assertThat(created.getKind()).isEqualTo(kind);
        assertThat(created.getName()).isEqualTo("scenario-attribute");
        verify(gateway).createAttribute(created);
        verifyNoMoreInteractions(gateway);
        assertThatThrownBy(() -> useCase.createAttribute(kind, " "))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> useCase.createAttribute(null, "scenario"))
                .isInstanceOf(NullPointerException.class);
    }
}
