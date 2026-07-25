package it.pagopa.interop.new_arch.web.eservice.infrastructure.page.component.creation_wizard.technical;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.interop.new_arch.web.infrastructure.component.TextField;
import org.assertj.core.api.SoftAssertions;

public interface TechnicalVoucherSection extends Component {
    @XPath("//*[@id=\"voucherLifespan\"]")
    TextField voucherLifespan();

    @XPath("//*[@id=\"audience\"]")
    TextField audience();

    default String getVoucherLifespanHelperText(){
        return voucherLifespan().getHelperText("voucherLifespan-infoLabel");
    }

    default String getVoucherLifespanErrorText() {
        return voucherLifespan().getErrorMessage("voucherLifespan-error");
    }

    default String getAudienceHelperText() {
        return audience().getHelperText("audience-infoLabel");
    }

    default String getAudienceErrorText() {
        return audience().getErrorMessage("audience-error");
    }

    @Override
    default void assertLoaded() {
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(getAudienceHelperText())
                    .isEqualTo("L’audience rappresenta la tua risorsa di destinazione. Per tutte le informazioni, consulta la guida");

            softly.assertThat(getVoucherLifespanHelperText())
                    .isEqualTo("Valore massimo: 1440 minuti (24 ore)");
        });
    }
}
