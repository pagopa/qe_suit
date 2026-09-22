package it.pagopa.send.web.campagne.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;

import java.util.List;

@XPath("//div[contains(@class,'MuiBox-root')]")
public interface CampagneTable extends Component {

    List<CampagneElement> elements();
}
