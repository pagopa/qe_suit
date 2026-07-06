package it.pagopa.interop.new_arch.common.infrastructure.template.action;

import it.pagopa.interop.new_arch.common.infrastructure.http.ApiResponse;
import it.pagopa.interop.new_arch.common.infrastructure.template.action.context.BaseActionContext;
import it.pagopa.interop.new_arch.common.infrastructure.template.action.strategy.AssertionStrategy;
import it.pagopa.interop.new_arch.common.kernel.domain.Identifiable;
import lombok.Getter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Getter
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AssertAction<Response, Model extends Identifiable> implements Finalizer<Response, Model> {

    private ApiResponse rawResponse;
    private BaseActionContext context;

    AssertAction<Response, Model> handle(ApiResponse response, BaseActionContext baseContext, AssertionStrategy strategy) {
        this.rawResponse = response;
        this.context = baseContext;
        strategy.assertThat(response);
        return this;
    }

}