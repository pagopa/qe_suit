package it.pagopa.infrastructure.suit.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;

import java.util.List;

@XPath(".//div[contains(@class, 'MuiStepper-root')]")
public interface Stepper extends Component {
    List<Step> steps();

    @XPath(".//div[contains(@class, 'MuiStep-root')]")
    interface Step extends Component{
        @XPath(".//*[contains(@class, 'MuiStepIcon-text')]")
        Label number();

        @XPath(".//*[contains(concat(' ', normalize-space(@class), ' '), ' MuiStepLabel-label ')]")
        Label label();

        default boolean isActive(){
            return get()
                    .map(we -> we.getClasses().contains("Mui-active"))
                    .orElse(false);
        }

        default boolean isActiveOnLabel(){
            return label().get()
                    .map(we -> we.getClasses().contains("Mui-active"))
                    .orElse(false);
        }
    }
}
