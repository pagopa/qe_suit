package it.pagopa.interop.web.service;

import it.pagopa.interop.generated.openapi.clients.bff.model.AsyncExchangeProperties;
import it.pagopa.interop.web.domain.model.EServiceTechnicalModel;
import it.pagopa.interop.web.page.eservice_creation.step.technical.TechnicalSpecStep;
import it.pagopa.interop.web.service.template.UiService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static it.pagopa.interop.common.utils.TypeUtils.safeParseInt;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EServiceTechnicalDataService implements UiService<EServiceTechnicalModel, TechnicalSpecStep> {

    private final TechnicalSpecStep techStepComponent;

    @Override
    public EServiceTechnicalModel doDefaultModel() {
        return EServiceTechnicalModel.buildDefault();
    }

    @Override
    public void doFill(EServiceTechnicalModel model) {
        var voucherComponent = techStepComponent.voucherComponent();
        var interfaceComponent = techStepComponent.interfaceComponent();

        voucherComponent.audience().fill(model.aud());
        voucherComponent.voucherLifespan().fill(model.voucherLifespan());

        Optional.ofNullable(model.interfaceAttachmentPath())
                .filter(path -> !path.isEmpty())
                .ifPresent(interfaceComponent::uploadApiInterface);

        doFillAsyncProperties(model.asyncExchangeProperties(), model.callbackInterfaceAttachmentPath());
    }

    private void doFillAsyncProperties(AsyncExchangeProperties seed, String callbackInterfacePath) {
        if (seed == null) return;

        var asyncComponent = techStepComponent.asyncComponent();

        asyncComponent.responseTime().fill(seed.getResponseTime());
        asyncComponent.resourceAvailableTime().fill(seed.getResourceAvailableTime());
        asyncComponent.maxResultSet().fill(seed.getMaxResultSet());

        asyncComponent.confirmation().setChecked(Boolean.TRUE.equals(seed.getConfirmation()));
        asyncComponent.bulk().setChecked(Boolean.TRUE.equals(seed.getBulk()));

        Optional.ofNullable(callbackInterfacePath)
                .filter(path -> !path.isEmpty())
                .ifPresent(asyncComponent.callbackInterface()::uploadApiInterface);
    }

    @Override
    public TechnicalSpecStep getComponent() {
        return techStepComponent;
    }

    @Override
    public EServiceTechnicalModel mapToModel(TechnicalSpecStep component) {
        var voucherComp = component.voucherComponent();
        var asyncComp = component.asyncComponent();

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

        return new EServiceTechnicalModel(aud, lifespan, null, asyncProps, null);
    }
}
