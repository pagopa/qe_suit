package it.pagopa.interop.ui.domain.page.eservice_creation.step.technical;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.interop.generated.openapi.clients.bff.model.AsyncExchangeProperties;
import it.pagopa.interop.ui.domain.component.InterfaceComponent;
import org.assertj.core.api.SoftAssertions;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

public interface TechnicalSpecificationStepComponent extends Component {

    @XPath("//section[.//h2[text()='Interfaccia']]")
    InterfaceComponent interfaceComponent();

    @XPath("//section[.//h2[text()='Voucher']]")
    VoucherComponent voucherComponent();

    @XPath("//section[.//h2[text()='Scambi asincroni e massivi']]")
    AsyncComponent asyncComponent();

    record TechnicalSpecificationStepSeed(String aud, String voucherLifespan, String interfaceAttachmentPath, AsyncExchangeProperties asyncExchangeProperties, String callbackInterfaceAttachmentPath) {
        public static TechnicalSpecificationStepSeed buildDefault() {
            try {
                return new TechnicalSpecificationStepSeed(
                        "quality-assurance",
                        "1",
                        new ClassPathResource("assets/origin-interface.yaml").getFilePath().toAbsolutePath().toString(),
                        new AsyncExchangeProperties()
                                .responseTime(60)
                                .maxResultSet(1)
                                .resourceAvailableTime(60)
                                .bulk(false)
                                .confirmation(false),
                        new ClassPathResource("assets/origin-interface.yaml").getFilePath().toAbsolutePath().toString()
                );
            } catch (IOException e) {
                throw new IllegalStateException("Unable to load assets from file", e);
            }
        }
    }

    default void fillTechnicalSpecification(TechnicalSpecificationStepSeed seed) {
        voucherComponent().audience().cleanAndWriteAndAssert(seed.aud);
        voucherComponent().voucherLifespan().cleanAndWriteAndAssert(seed.voucherLifespan);
        interfaceComponent().uploadApiInterface(seed.interfaceAttachmentPath);
        asyncComponent().fillAsyncProperties(seed.asyncExchangeProperties, seed.callbackInterfaceAttachmentPath);
    }

    @Override
    default void assertLoaded() {
        SoftAssertions.assertSoftly(softly -> {
            voucherComponent().assertLoaded();
            interfaceComponent().assertLoaded();
        });
    }
}
