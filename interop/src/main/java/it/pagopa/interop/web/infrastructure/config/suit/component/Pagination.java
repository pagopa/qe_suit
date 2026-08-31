package it.pagopa.interop.web.infrastructure.config.suit.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;

@XPath(".//nav[contains(@class, 'MuiPagination-root')]")
public interface Pagination extends Component {

    String SELECTED_CLASS = "Mui-selected";

    @XPath("(.//li)[last()]")
    Button nextBtn();

    @XPath("(.//li)[1]")
    Button prevBtn();

    default boolean hasNext(){
        return !nextBtn().isDisabled();
    }

    default boolean hasPrevious(){
        return !prevBtn().isDisabled();
    }
}
