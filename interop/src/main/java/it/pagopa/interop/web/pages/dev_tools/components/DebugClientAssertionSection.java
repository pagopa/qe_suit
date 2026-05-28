package it.pagopa.interop.web.pages.dev_tools.components;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.interop.web.commons.component.Button;
import org.assertj.core.api.SoftAssertions;

public interface DebugClientAssertionSection extends Component {

    @XPath(".//h2")
    Readable<String> sectionTitle();

    @XPath(".//p")
    Readable<String> description();

    @XPath("//a[contains(@class,'MuiButton-root') and contains(normalize-space(.), 'Effettua il debug')]")
    Button debugClientAssertionButton();

    @Override
    default void assertLoaded() {

        sectionTitle().readAndAssert("Debug Client Assertion");

        description().readAndAssert("Lo strumento di debug ti consente di evidenziare eventuali anomalie contenute nella tua client assertion necessaria per l’ottenimento dell’access token.");

        debugClientAssertionButton().get().ifPresent(we ->
                SoftAssertions.assertSoftly(softly -> {
                    softly.assertThat(we.getText()).isEqualToIgnoringCase("Effettua il debug");
                    softly.assertThat(we.getAttributes().get("href")).isEqualToIgnoringCase("/ui/it/tool-sviluppo/debug-voucher");
                })
        );
    }
}
