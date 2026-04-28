package it.frontend.e2e.framework.web.domain.mui;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.domain.Component;

import java.util.List;

public interface MuiList extends Component {
    @XPath("//*[contains(@class, 'MuiList-root')]//li")
    List<Clickable> items();

    default void selectItem(String item) {
        items().stream()
                //.filter(i -> i.getText().equals(item)) TODO: inserire capability
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Item not found: " + item))
                .click();
    }
}
