package it.pagopa.interop.service.eservice;

import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.pagopa.interop.domain.context.EserviceContext;
import it.pagopa.interop.domain.model.Eservice;
import it.pagopa.interop.generated.openapi.clients.bff.model.ProducerEServiceDescriptor;
import it.pagopa.interop.service.eservice.impl.EserviceDataPreparationService;
import it.pagopa.interop.utils.web.EServiceUrlUtils;
import it.pagopa.interop.web.component.Alert;
import it.pagopa.interop.web.pages.eservice_creation.EServiceCreationPage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

import static it.pagopa.interop.web.pages.eservice_creation.step.AdditionalInformationStepComponent.AdditionalInformationStepSeed;
import static it.pagopa.interop.web.pages.eservice_creation.step.GeneralInformationStepComponent.GeneralInformationStepSeed;
import static it.pagopa.interop.web.pages.eservice_creation.step.technical.TechnicalSpecificationStepComponent.TechnicalSpecificationStepSeed;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EServiceWebService {

    private final EServiceCreationPage creationPage;
    private final WebPresentationGateway webPresentationGateway;
    private final EserviceContext eserviceContext;
    private final EserviceDataPreparationService eserviceDataPreparationService;

    public Eservice publishEServiceWithDefault() {
        creationPage
                .fillGeneralInformation(GeneralInformationStepSeed.buildDefault())
                .saveDraft()
                .skipThresholdAndAttribute()
                .fillTechnicalSpecification(TechnicalSpecificationStepSeed.buildDefault())
                .saveDraft()
                .fillAdditionalInformation(AdditionalInformationStepSeed.buildDefault())
                .publish();

        return getEservice();
    }

    public void fillGeneralInformation(Consumer<GeneralInformationStepSeed> customizer) {
        GeneralInformationStepSeed seed = GeneralInformationStepSeed.buildDefault();
        customizer.accept(seed);

        creationPage.fillGeneralInformation(seed);
        getEservice();
    }

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
