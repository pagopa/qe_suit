package it.pagopa.interop.web.tos.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.frontend.e2e.framework.web.domain.DomNode;
import it.pagopa.infrastructure.suit.component.Link;

import java.util.List;

public interface TOSIndex extends Component {

    Link links();

    /** Reads attributes, including links in the hidden responsive index. */
    default List<String> hrefs() {
        return links().getAll()
                .orElseThrow(() -> new IllegalStateException("Impossibile leggere i collegamenti dell'indice TOS"))
                .stream()
                .map(link -> link.getAttributes().get("href"))
                .toList();
    }
}
