package it.pagopa.interop.ui.domain.page.eservice_creation.step.technical;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.interop.ui.domain.component.TextField;
import org.assertj.core.api.SoftAssertions;

public interface VoucherComponent extends Component {
    @XPath("//*[@id=\"voucherLifespan\"]")
    TextField voucherLifespan();

    @XPath("//*[@id=\"audience\"]")
    TextField audience();

    default String getAudienceHelperText() {
        return audience().getHelperText("audience-infoLabel");
    }

    @Override
    default void assertLoaded() {
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(getAudienceHelperText())
                    .isEqualTo("L’audience rappresenta la tua risorsa di destinazione. Per tutte le informazioni, consulta la guida");
        });
    }
}
