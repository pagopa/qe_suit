package it.pagopa.interop.common.infrastructure.suit.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Button;
import org.assertj.core.api.SoftAssertions;

import java.util.List;

@XPath("//*[id='TODO']")
public interface DeclaredAttributeTab extends Component, Clickable {

    @XPath("//button[@role='tab' and normalize-space(.)='Attributi dichiarati']")
    Button tabButton();

    @XPath("//p[contains(normalize-space(text()), 'Sono attributi dichiarati dal fruitore')]")
    Readable<String> description();

    List<DeclaredAttributeGroup> declaredAttributeGroups();

    default Boolean isActive() {
        return tabButton().get()
                .map(we -> we.getClasses().contains("Mui-selected"))
                .orElseThrow(() -> new IllegalStateException("Tab button not found"));
    }

    default void activate() {
        tabButton().click();
    }

    @Override
    default void assertLoaded() {
        SoftAssertions.assertSoftly(softly -> {
            tabButton().readAndAssert("Attributi dichiarati");
        });
    }
}

