package it.pagopa.infrastructure.suit.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;

import java.util.List;

@XPath(".//div[contains(@class, 'MuiStepper-root')]")
public interface Stepper extends Component {
    List<Step> steps();

    @XPath(".//div[contains(@class, 'MuiStep-root')]")
    interface Step extends Component{

        @XPath(".//*[contains(@class, 'MuiStepLabel-label')]")
        Readable<String> label();

        default boolean isActive(){
            return get()
                    .map(we -> we.getClasses().contains("Mui-active"))
                    .orElse(false);
        }
    }
}
