package it.pagopa.interop.new_arch.web.eservice.infrastructure;

import it.pagopa.interop.generated.openapi.clients.bff.model.AsyncExchangeProperties;
import it.pagopa.interop.new_arch.web.eservice.domain.WebEServiceTechnicalData;
import it.pagopa.interop.new_arch.web.eservice.infrastructure.suit.component.creation_wizard.technical.TechnicalSpecWizard;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;


@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WebEServiceTechDataGateway {

    private final TechnicalSpecWizard techStepComponent;

//    public WebEServiceTechnicalData getDefaultModel() {
//        return WebEServiceTechnicalData.buildDefault();
//    }
//
//    public void fillWizard(WebEServiceTechnicalData model) {
//        var voucherComponent = techStepComponent.voucherComponent();
//        var interfaceComponent = techStepComponent.interfaceComponent();
//
//        voucherComponent.audience().fill(model.aud());
//        voucherComponent.voucherLifespan().fill(model.voucherLifespan());
//
//        Optional.ofNullable(model.interfaceAttachmentPath())
//                .filter(path -> !path.isEmpty())
//                .ifPresent(interfaceComponent::uploadApiInterface);
//
//        fillAsyncProperties(model.asyncExchangeProperties(), model.callbackInterfaceAttachmentPath());
//    }
//
//    public WebEServiceTechnicalData readWizard() {
//        var voucherComp = techStepComponent.voucherComponent();
//        var asyncComp = techStepComponent.asyncComponent();
//
//        String aud = voucherComp != null ? voucherComp.audience().read() : null;
//        String lifespan = voucherComp != null ? voucherComp.voucherLifespan().read() : null;
//
//        AsyncExchangeProperties asyncProps = null;
//        if (asyncComp != null) {
//            asyncProps = new AsyncExchangeProperties()
//                    .responseTime(safeParseInt(asyncComp.responseTime().read()))
//                    .maxResultSet(safeParseInt(asyncComp.maxResultSet().read()))
//                    .resourceAvailableTime(safeParseInt(asyncComp.resourceAvailableTime().read()))
//                    .bulk(asyncComp.bulk().isChecked())
//                    .confirmation(asyncComp.confirmation().isChecked());
//        }
//
//        return new WebEServiceTechnicalData(aud, lifespan, null, asyncProps, null);
//    }
//
//    private void fillAsyncProperties(AsyncExchangeProperties seed, String callbackInterfacePath) {
//        if (seed == null) return;
//
//        var asyncComponent = techStepComponent.asyncComponent();
//
//        asyncComponent.responseTime().fill(seed.getResponseTime());
//        asyncComponent.resourceAvailableTime().fill(seed.getResourceAvailableTime());
//        asyncComponent.maxResultSet().fill(seed.getMaxResultSet());
//
//        asyncComponent.confirmation().setChecked(Boolean.TRUE.equals(seed.getConfirmation()));
//        asyncComponent.bulk().setChecked(Boolean.TRUE.equals(seed.getBulk()));
//
//        Optional.ofNullable(callbackInterfacePath)
//                .filter(path -> !path.isEmpty())
//                .ifPresent(asyncComponent.callbackInterface()::uploadApiInterface);
//    }
}