package it.pagopa.interop.common.infrastructure.suit.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import org.assertj.core.api.SoftAssertions;

import java.util.List;

@XPath(".//button[contains(@class, 'MuiTab-root') and contains(normalize-space(text()), 'Attributi Certificati')]")
public interface CertifiedAttributeTab extends Component, Clickable {

    @XPath(".//*[contains(@class, 'MuiTab-root') and contains(normalize-space(text()), 'Attributi Certificati')]")
    Readable<String> title();

    @XPath(".//*[contains(@class, 'MuiTab-root') and contains(normalize-space(text()), 'Attributi Certificati')]/following::p[1]")
    Readable<String> description();

    default Boolean isActive() {
        return false;
    }

    List<CertifiedAttributeGroup> certifiedAttributeGroups();

    @Override
    default void assertLoaded() {
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(title()).as("Title must be \"Attributi certificati\"").isEqualTo("Attributi certificati");
        });
    }
}
