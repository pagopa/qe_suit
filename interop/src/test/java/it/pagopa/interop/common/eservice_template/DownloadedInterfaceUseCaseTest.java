package it.pagopa.interop.common.eservice_template;

import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.eservice_template.application.OpenApiUseCase;
import it.pagopa.interop.common.eservice_template.application.SoapUseCase;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DownloadedInterfaceUseCaseTest {
    @Test
    void soap_requires_an_explicitly_downloaded_interface() {
        EServiceDescriptor descriptor = EServiceDescriptor.builder().build();
        assertThatThrownBy(() -> new SoapUseCase().shouldBeWellFormed(descriptor))
                .isInstanceOf(AssertionError.class).hasMessageContaining("non è stato scaricato");
    }

    @Test
    void openapi_requires_an_explicitly_downloaded_interface() {
        EServiceDescriptor descriptor = EServiceDescriptor.builder().build();
        assertThatThrownBy(() -> new OpenApiUseCase().shouldBeValid(descriptor))
                .isInstanceOf(AssertionError.class).hasMessageContaining("non è stato scaricato");
    }
}
