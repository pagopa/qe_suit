package it.pagopa.interop.common.contract.template.ui;

import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.interop.common.cucumber.CucumberConfig;
import it.pagopa.interop.common.contract.model.request.RequestOverride;
import it.pagopa.interop.common.utils.DeepMerger;

public interface UiWriter<M, C extends Component> extends UiReader<C, M> {

    /**
     * COMPORTAMENTO 1: Il modello in input vince sempre (Compilazione Assoluta).
     */
    default M fill(M model) {
        C component = getComponent();
        component.assertLoaded();

        doFill(model);
        afterFill(model);

        return mapToModel(component);
    }

    /**
     * Carica il default, applica il deep merge intelligente (no-null, no-override cieco)
     * e filla lo schermo con il risultato.
     */
    default M fillWithOverrides(M overrideModel) {
        M defaultModel = doDefaultModel();
        M fuzedModel = DeepMerger.merge(overrideModel, defaultModel);

        // Puliamo il ThreadLocal subito dopo il merge
        CucumberConfig.clearGherkinKeys();

        return fill(fuzedModel);
    }

    default M fill(RequestOverride<M> overrides) {
        M model = doDefaultModel();
        overrides.applyTo(model);
        return fill(model);
    }

    default M fill() {
        return fill(model -> {});
    }

    M doDefaultModel();

    void doFill(M model);

    default void afterFill(M model) {
    }
}