package it.pagopa.interop.bff.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.interop.generated.openapi.clients.bff.model.DescriptorAttributesSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceMode;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTechnology;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceDescriptorSeed;
import lombok.RequiredArgsConstructor;
import org.instancio.Instancio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static it.pagopa.interop.generated.openapi.clients.bff.model.AgreementApprovalPolicy.AUTOMATIC;
import static org.instancio.Select.field;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EServiceSeedFactory {

    private final ObjectMapper objectMapper;

    public EServiceSeed fullCreationRequest() {
        return Instancio.of(EServiceSeed.class)
                .set(field(EServiceSeed::getName), "eservice-" + UUID.randomUUID())
                .set(field(EServiceSeed::getDescription), "descrizione test")
                .set(field(EServiceSeed::getTechnology), EServiceTechnology.REST)
                .set(field(EServiceSeed::getMode), EServiceMode.RECEIVE)
                .create();
    }

    public UpdateEServiceDescriptorSeed fullUpdateDescriptorRequest() {
        return Instancio.of(UpdateEServiceDescriptorSeed.class)
                .set(field(UpdateEServiceDescriptorSeed::getDescription), "descrizione test")
                .set(field(UpdateEServiceDescriptorSeed::getAudience), "qa")
                .set(field(UpdateEServiceDescriptorSeed::getVoucherLifespan), 60)
                .set(field(UpdateEServiceDescriptorSeed::getDailyCallsPerConsumer), 1)
                .set(field(UpdateEServiceDescriptorSeed::getDailyCallsTotal), 10)
                .set(field(UpdateEServiceDescriptorSeed::getAsyncExchangeProperties), false)
                .set(field(UpdateEServiceDescriptorSeed::getAgreementApprovalPolicy), AUTOMATIC)
                .set(field(DescriptorAttributesSeed::getCertified), List.of())
                .set(field(DescriptorAttributesSeed::getDeclared), List.of())
                .set(field(DescriptorAttributesSeed::getVerified), List.of())
                .create();
    }

    public EServiceSeed creationSeedFrom(Map<String, String> rawSeed) {
        return objectMapper.convertValue(rawSeed, EServiceSeed.class);
    }

    public UpdateEServiceDescriptorSeed updateDescriptorSeedFrom(Map<String, String> rawSeed) {
        return objectMapper.convertValue(rawSeed, UpdateEServiceDescriptorSeed.class);
    }
}