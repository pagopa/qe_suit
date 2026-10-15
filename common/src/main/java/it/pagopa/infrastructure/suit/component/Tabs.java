package it.pagopa.infrastructure.suit.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;

import java.util.List;

@XPath(".//*[contains(@class, 'MuiTabs-root')]")
public interface Tabs extends Component {

    List<Tab> tabs();

    @XPath(".//*[contains(@class, 'MuiTab-root')]")
    interface Tab extends Button {

        default boolean isActive() {
          return isSelected();
        }

        default String getLabel() {
            return this.read();
        }
    }
}
