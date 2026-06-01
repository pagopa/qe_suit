package it.pagopa.interop.ui.service.template;

import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.interop.common.service.template.RequestOverride;
import it.pagopa.interop.common.utils.DeepMerger;

public interface UiWriteService<M, C extends Component> extends UiReadService<C, M> {

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
        it.pagopa.interop.common.config.CucumberConfig.clearGherkinKeys();

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