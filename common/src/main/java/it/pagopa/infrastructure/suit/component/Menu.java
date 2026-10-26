package it.pagopa.infrastructure.suit.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.domain.Component;

import java.util.List;
import java.util.Optional;

public interface Menu extends Component, Clickable {

    List<MenuListItem> menuListItems();

    default List<MenuListItem> open() {
        this.click();
        return this.menuListItems();
    }

    default Optional<MenuListItem> find(String text) {
        return this.menuListItems().stream()
                .filter(item -> item.getMenuText().equals(text))
                .findFirst();
    }

    @XPath(".//li[contains(@class, 'MuiMenuItem-root')]")
    interface MenuListItem extends Button {
        default String getMenuText() {
            return this.read();
        }
    }
}
