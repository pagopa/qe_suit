package it.pagopa.interop.new_arch.web.eservice.infrastructure;

import it.pagopa.interop.generated.openapi.clients.bff.model.AsyncExchangeProperties;
import it.pagopa.interop.new_arch.web.eservice.application.WebEServiceTechnicalData;
import it.pagopa.interop.new_arch.web.eservice.infrastructure.suit.EServiceCreationPage;
import it.pagopa.interop.new_arch.web.eservice.infrastructure.suit.component.creation_wizard.technical.TechnicalSpecWizard;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;


@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WebEServiceTechDataGateway {

    private final EServiceCreationPage eServiceCreationPage;

    public void fillEServiceTechData(WebEServiceTechnicalData model) {
        TechnicalSpecWizard technicalSpecWizard = eServiceCreationPage.technicalSpecificationStep();
        var voucherComponent = technicalSpecWizard.voucherComponent();
        var interfaceComponent = technicalSpecWizard.interfaceComponent();

        voucherComponent.audience().fill(model.aud());
        voucherComponent.voucherLifespan().fill(model.voucherLifespan());

        Optional.ofNullable(model.interfaceAttachmentPath())
                .filter(path -> !path.isEmpty())
                .ifPresent(interfaceComponent::uploadApiInterface);

        fillAsyncProperties(model.asyncExchangeProperties(), model.callbackInterfaceAttachmentPath());
    }

    public WebEServiceTechnicalData readEServiceTechData() {
        TechnicalSpecWizard technicalSpecWizard = eServiceCreationPage.technicalSpecificationStep();
        var voucherComp = technicalSpecWizard.voucherComponent();
        var asyncComp = technicalSpecWizard.asyncComponent();

        String aud = voucherComp != null ? voucherComp.audience().read() : null;
        String lifespan = voucherComp != null ? voucherComp.voucherLifespan().read() : null;

        AsyncExchangeProperties asyncProps = null;
        if (asyncComp != null) {
            asyncProps = new AsyncExchangeProperties()
                    .responseTime(safeParseInt(asyncComp.responseTime().read()))
                    .maxResultSet(safeParseInt(asyncComp.maxResultSet().read()))
                    .resourceAvailableTime(safeParseInt(asyncComp.resourceAvailableTime().read()))
                    .bulk(asyncComp.bulk().isChecked())
                    .confirmation(asyncComp.confirmation().isChecked());
        }

        return new WebEServiceTechnicalData(aud, lifespan, null, asyncProps, null);
    }

    private void fillAsyncProperties(AsyncExchangeProperties seed, String callbackInterfacePath) {
        if (seed == null) return;

        TechnicalSpecWizard technicalSpecWizard = eServiceCreationPage.technicalSpecificationStep();
        var asyncComponent = technicalSpecWizard.asyncComponent();

        asyncComponent.responseTime().fill(seed.getResponseTime());
        asyncComponent.resourceAvailableTime().fill(seed.getResourceAvailableTime());
        asyncComponent.maxResultSet().fill(seed.getMaxResultSet());

        asyncComponent.confirmation().setChecked(seed.getConfirmation());
        asyncComponent.bulk().setChecked(seed.getBulk());

        Optional.ofNullable(callbackInterfacePath)
                .filter(path -> !path.isEmpty())
                .ifPresent(asyncComponent.callbackInterface()::uploadApiInterface);
    }

    private static Integer safeParseInt(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}