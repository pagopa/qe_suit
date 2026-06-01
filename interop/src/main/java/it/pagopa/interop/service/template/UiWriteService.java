package it.pagopa.interop.service.template;

import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.interop.domain.model.Model;
import it.pagopa.interop.domain.request.UiRequest;

public interface UiWriteService<R extends UiRequest, C extends Component, M extends Model> extends UiReadService<C, M> {

    default M fill(RequestOverride<R> overrides) {
        R defaultRequest = doDefaultRequest();
        R request = overrides.applyTo(defaultRequest);

        C component = getComponent();
        component.assertLoaded();

        doFill(request);
        afterFill(request);

        return mapToModel(component);
    }

    default M fill() {
        return fill(RequestOverride::identity);
    }

    R doDefaultRequest();

    void doFill(R request);

    default void afterFill(R request) {
    }
}
