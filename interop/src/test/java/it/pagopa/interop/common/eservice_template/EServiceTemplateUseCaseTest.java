package it.pagopa.interop.common.eservice_template;

import it.pagopa.interop.common.eservice_template.application.EServiceTemplateGateway;
import it.pagopa.interop.common.eservice_template.application.EServiceTemplateRequestFactory;
import it.pagopa.interop.common.eservice_template.application.EServiceTemplateUseCase;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateInterface;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersion;
import it.pagopa.interop.common.kernel.domain.EServiceTemplateRef;
import it.pagopa.interop.common.kernel.domain.EServiceTemplateVersionRef;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EServiceTemplateUseCaseTest {
    @Test
    void unchanged_interface_read_compares_fresh_bytes_without_mutating_baseline() {
        EServiceTemplateGateway gateway = mock(EServiceTemplateGateway.class);
        EServiceTemplateInterface baseline = interfaceSnapshot(UUID.randomUUID(), new byte[]{0, 1});
        EServiceTemplateUseCase useCase = new EServiceTemplateUseCase(
                gateway, mock(EServiceTemplateRequestFactory.class)
        );
        when(gateway.readCurrentInterface(baseline.getTemplateRef(), baseline.getVersionRef()))
                .thenReturn(Optional.of(interfaceSnapshot(baseline.getId(), new byte[]{0, 1})));

        useCase.shouldHaveUnchangedInterface(version(baseline));

        byte[] returnedContent = baseline.getContent();
        returnedContent[0] = 9;
        assertThat(baseline.getContent()).containsExactly(0, 1);
        verify(gateway).readCurrentInterface(baseline.getTemplateRef(), baseline.getVersionRef());
    }

    @Test
    void missing_or_replaced_interface_fails_against_the_baseline() {
        EServiceTemplateGateway gateway = mock(EServiceTemplateGateway.class);
        EServiceTemplateInterface baseline = interfaceSnapshot(UUID.randomUUID(), new byte[]{1});
        EServiceTemplateUseCase useCase = new EServiceTemplateUseCase(
                gateway, mock(EServiceTemplateRequestFactory.class)
        );
        when(gateway.readCurrentInterface(baseline.getTemplateRef(), baseline.getVersionRef()))
                .thenReturn(Optional.empty());
        assertThatThrownBy(() -> useCase.shouldHaveUnchangedInterface(version(baseline)))
                .isInstanceOf(AssertionError.class).hasMessageContaining("non è più presente");

        when(gateway.readCurrentInterface(baseline.getTemplateRef(), baseline.getVersionRef()))
                .thenReturn(Optional.of(interfaceSnapshot(UUID.randomUUID(), new byte[]{1})));
        assertThatThrownBy(() -> useCase.shouldHaveUnchangedInterface(version(baseline)))
                .isInstanceOf(AssertionError.class).hasMessageContaining("sostituita");
    }

    @Test
    void baseline_without_downloaded_bytes_fails_before_a_fresh_read() {
        EServiceTemplateGateway gateway = mock(EServiceTemplateGateway.class);
        EServiceTemplateInterface baseline = interfaceSnapshot(UUID.randomUUID(), null);
        EServiceTemplateUseCase useCase = new EServiceTemplateUseCase(
                gateway, mock(EServiceTemplateRequestFactory.class)
        );

        assertThatThrownBy(() -> useCase.shouldHaveUnchangedInterface(version(baseline)))
                .isInstanceOf(IllegalStateException.class).hasMessageContaining("non è stato scaricato");
        verify(gateway, never()).readCurrentInterface(baseline.getTemplateRef(), baseline.getVersionRef());
    }

    private static EServiceTemplateVersion version(EServiceTemplateInterface document) {
        return EServiceTemplateVersion.builder().id(document.getVersionRef().versionId())
                .version("1").interfaceDocument(document).build();
    }

    private static EServiceTemplateInterface interfaceSnapshot(UUID documentId, byte[] content) {
        return new EServiceTemplateInterface(
                documentId,
                new EServiceTemplateRef(UUID.fromString("00000000-0000-0000-0000-000000000001")),
                new EServiceTemplateVersionRef(UUID.fromString("00000000-0000-0000-0000-000000000002")),
                "interface.wsdl", "application/xml", "interface.wsdl", "sha256:stable", content
        );
    }
}
