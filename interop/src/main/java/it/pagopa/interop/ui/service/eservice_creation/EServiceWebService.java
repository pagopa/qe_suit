package it.pagopa.interop.ui.service.eservice_creation;

import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.pagopa.interop.common.domain.context.EserviceContext;
import it.pagopa.interop.common.domain.model.Eservice;
import it.pagopa.interop.ui.domain.request.eservice_creation.GeneralDataStepSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.ProducerEServiceDescriptor;
import it.pagopa.interop.bff.service.EserviceDataPreparationService;
import it.pagopa.interop.ui.util.EServiceUrlUtils;
import it.pagopa.interop.ui.domain.page.eservice_creation.EServiceCreationPage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

import static it.pagopa.interop.ui.domain.page.eservice_creation.step.AdditionalInformationStepComponent.AdditionalInformationStepSeed;
import static it.pagopa.interop.ui.domain.page.eservice_creation.step.technical.TechnicalSpecificationStepComponent.TechnicalSpecificationStepSeed;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EServiceWebService {

    private final EServiceCreationPage creationPage;
    private final WebPresentationGateway webPresentationGateway;
    private final EserviceContext eserviceContext;
    private final EserviceDataPreparationService eserviceDataPreparationService;

    public void fillTechnicalSpecification(Consumer<TechnicalSpecificationStepSeed> customizer) {
        TechnicalSpecificationStepSeed seed = TechnicalSpecificationStepSeed.buildDefault();
        customizer.accept(seed);

        creationPage.fillTechnicalSpecification(seed);
    }

    public void fillAdditionalInformation(Consumer<AdditionalInformationStepSeed> customizer) {
        AdditionalInformationStepSeed seed = AdditionalInformationStepSeed.buildDefault();
        customizer.accept(seed);

        creationPage.fillAdditionalInformation(seed);
    }

    private Eservice getEservice() {
        try {
            String currentUrl = webPresentationGateway.getLocation().getUrl();
            EServiceUrlUtils.EServiceData eserviceData = EServiceUrlUtils.extractData(currentUrl);

            return eserviceDataPreparationService.getEservice(eserviceData.eserviceId(), eserviceData.descriptorId());
        } catch (IllegalStateException e) {
            try {
                return eserviceContext.getLast();
            } catch (Exception contextException) {
                return new Eservice(new ProducerEServiceDescriptor());
            }
        }
    }
}
