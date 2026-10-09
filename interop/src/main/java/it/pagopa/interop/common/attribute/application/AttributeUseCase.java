package it.pagopa.interop.common.attribute.application;

import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.kernel.domain.EServiceDescriptorRef;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttributeUseCase {
    private final AttributeGateway attributeGateway;

    public void removeCertifiedAttributeThreshold(EService eService, int groupIndex, int attributeIndex) {

        // Questo codice è solo per testare la funzionalità utilizzando un e-service disponibile,
        // in attesa di avere gli step per la creazione degli e-service.
        UUID eServiceId;
        UUID descriptorId;
        boolean useDraft = true;
        boolean useDev = true;
        if (useDraft) {
            if (useDev) {
                eServiceId = UUID.fromString("ae3dcd20-5c40-484e-9826-2011715da9be");
                descriptorId = UUID.fromString("c996379d-230a-4f0c-92ee-8634e97a3eab");
            } else {
                eServiceId = UUID.fromString("02cc44fd-8af1-4f2f-a6ca-6956066672d7");
                descriptorId = UUID.fromString("7a57c4df-f4f4-4b84-adc5-696ff49123eb");
            }
            attributeGateway.removeCertifiedAttributeThresholdFromDraftEService(
                    new EServiceRef(eServiceId), new EServiceDescriptorRef(descriptorId),
                    groupIndex, attributeIndex
            );
        } else {
            if (useDev) {
                eServiceId = UUID.fromString("6a9df521-ad5b-4311-94ac-9504c1f4adc6");
                descriptorId = UUID.fromString("17a18130-b8d3-4b35-8548-6de3b0e65216");
            } else {
                throw new RuntimeException("Unsupported e-service");
            }
            attributeGateway.removeCertifiedAttributeThresholdFromPublishedEService(
                    new EServiceRef(eServiceId), new EServiceDescriptorRef(descriptorId),
                    groupIndex, attributeIndex
            );
        }


        /*switch (eService.getActiveDescriptor().getState()) {
            case PUBLISHED -> eServiceGateway.removeCertifiedAttributeThresholdFromPublishedEService(eService.getRef(), eService.getActiveDescriptor().getRef(), groupIndex, attributeIndex);
            case DRAFT -> eServiceGateway.removeCertifiedAttributeThresholdFromDraftEService(eService.getRef(), eService.getActiveDescriptor().getRef(), groupIndex, attributeIndex);
            default -> throw new IllegalStateException("Unexpected value: " + eService.getActiveDescriptor().getState());
        }*/
    }

    public void certifiedAttributeThresholdRemoved() {
        attributeGateway.certifiedAttributeThresholdRemoved();
    }
}
