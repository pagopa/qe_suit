package it.pagopa.interop.ui.service.eservice_creation;

import it.pagopa.interop.generated.openapi.clients.bff.model.AsyncExchangeProperties;
import it.pagopa.interop.ui.domain.model.eservice_creation.TechnicalSpecModel;
import it.pagopa.interop.ui.domain.page.eservice_creation.EServiceCreationPage;
import it.pagopa.interop.ui.domain.page.eservice_creation.step.technical.TechnicalSpecificationStepComponent;
import it.pagopa.interop.ui.service.template.UiService;
import org.springframework.stereotype.Service;

@Service
public class TechnicalDataService implements UiService<TechnicalSpecModel, TechnicalSpecificationStepComponent> {

    private final TechnicalSpecificationStepComponent techStepComponent;

    public TechnicalDataService(EServiceCreationPage creationPage) {
        this.techStepComponent = creationPage.technicalSpecificationStep();
    }

    @Override
    public TechnicalSpecModel doDefaultModel() {
        return TechnicalSpecModel.buildDefault();
    }

    @Override
    public void doFill(TechnicalSpecModel model) {
        var voucherComponent = techStepComponent.voucherComponent();
        var interfaceComponent = techStepComponent.interfaceComponent();

        voucherComponent.audience().cleanAndWriteAndAssert(model.aud());
        voucherComponent.voucherLifespan().cleanAndWriteAndAssert(model.voucherLifespan());
        interfaceComponent.uploadApiInterface(model.interfaceAttachmentPath());

        doFillAsyncProperties(model.asyncExchangeProperties(), model.callbackInterfaceAttachmentPath());
    }

    private void doFillAsyncProperties(AsyncExchangeProperties seed, String callbackInterfacePath) {
        var asyncComponent = techStepComponent.asyncComponent();

        asyncComponent.responseTime().cleanAndWriteAndAssert(seed.getResponseTime().toString());
        asyncComponent.resourceAvailableTime().cleanAndWriteAndAssert(seed.getResourceAvailableTime().toString());
        asyncComponent.maxResultSet().cleanAndWriteAndAssert(seed.getMaxResultSet().toString());
        asyncComponent.confirmation().setChecked(seed.getConfirmation());
        asyncComponent.bulk().setChecked(seed.getBulk());

        asyncComponent.callbackInterface().uploadApiInterface(callbackInterfacePath);
    }

    @Override
    public TechnicalSpecificationStepComponent getComponent() {
        return techStepComponent;
    }

    @Override
    public TechnicalSpecModel mapToModel(TechnicalSpecificationStepComponent component) {
        var voucherComp = component.voucherComponent();
        var asyncComp = component.asyncComponent();

        String aud = voucherComp != null ? voucherComp.audience().read() : null;
        String lifespan = voucherComp != null ? voucherComp.voucherLifespan().read() : null;

        AsyncExchangeProperties asyncProps = null;
        if (asyncComp != null) {
            asyncProps = new AsyncExchangeProperties()
                    .responseTime(Integer.parseInt(asyncComp.responseTime().read().trim()))
                    .maxResultSet(Integer.parseInt(asyncComp.maxResultSet().read().trim()))
                    .resourceAvailableTime(Integer.parseInt(asyncComp.resourceAvailableTime().read().trim()))
                    .bulk(asyncComp.bulk().isChecked())
                    .confirmation(asyncComp.confirmation().isChecked());
        }
        
        return new TechnicalSpecModel(aud, lifespan, null, asyncProps, null);
    }
}
