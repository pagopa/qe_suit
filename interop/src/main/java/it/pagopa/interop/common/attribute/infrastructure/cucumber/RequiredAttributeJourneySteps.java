package it.pagopa.interop.common.attribute.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.RequiredAttributeConfiguration;
import it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.SelectedEServiceDescriptor;
import it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.mapper.RequiredAttributeConfigurationMapper;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.TenantRef;
import it.pagopa.interop.common.kernel.domain.UserRole;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class RequiredAttributeJourneySteps {
    private final InteropJourney interopJourney;
    private final RequiredAttributeConfigurationMapper mapper;

    @Given("{eServiceDescriptorByVersionAndAlias} del {tenant} configurata con i seguenti attributi richiesti:")
    public void configureRequiredAttributes(
            SelectedEServiceDescriptor selected,
            Tenant producer,
            List<RequiredAttributeConfiguration> configurations
    ) {
        if (!producer.getOrganizationId().toString().equals(selected.getEService().getProducerId())) {
            throw new IllegalArgumentException("The stated producer does not own the selected e-service");
        }
        interopJourney.withProducer(producer, UserRole.ADMIN)
                .configureRequiredAttributes(selected.getEService(), selected.getVersion(),
                        TenantRef.of(producer.getOrganizationId()), mapper.toRequests(configurations));
    }
}
