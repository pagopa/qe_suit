package it.pagopa.interop.web.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;

import java.util.List;

@XPath(".//div[contains(@class, 'MuiRadioGroup-root')]")
public interface RadioGroup extends Component {

    List<RadioButton> radioButtons();

    default void select(String value) {
        radioButtons().stream()
                .filter(radioButton -> radioButton.getLabel().equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Radio button with value " + value + " not found"))
                .select();
    }

    default void selectLike(String value) {
        radioButtons().stream()
                .filter(radioButton -> radioButton.getLabel().toLowerCase().contains(value.toLowerCase()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Radio button with value " + value + " not found"))
                .select();
    }

    default boolean isDisabled() {
        return radioButtons().stream()
                .map(RadioButton::isDisabled)
                .reduce(Boolean::logicalAnd)
                .orElseThrow(() -> new IllegalStateException("No radio buttons found"));
    }

}
