package it.pagopa.interop.new_arch.common.infrastructure.template;

import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.interop.new_arch.common.infrastructure.response.UiResponse;
import it.pagopa.interop.new_arch.common.infrastructure.template.action.TestChain;
import it.pagopa.interop.new_arch.common.infrastructure.template.action.TestChainFactory;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
public class BrowserClient {
    @Getter
    @Setter(onMethod_ = {@Autowired})
    protected TestChainFactory chainFactory;

    protected <RESPONSE> TestChain<RESPONSE> execute(
            Supplier<?> uiAction,
            Class<RESPONSE> responseClass) {

        return chainFactory.build(() -> new UiResponse(uiAction.get()), responseClass);
    }

    protected <RESPONSE> TestChain<RESPONSE> navigateAndExecute(
            Page page,
            Supplier<?> uiAction,
            Class<RESPONSE> responseClass) {

        page.navigateTo();
        page.assertLoaded();
        return execute(uiAction, responseClass);
    }

    protected TestChain<Void> execute(Runnable uiAction) {
        uiAction.run();
        return chainFactory.build(() -> new UiResponse(null), Void.class);
    }

    protected TestChain<Void> navigateAndExecute(Page page, Runnable uiAction){
        page.navigateTo();
        page.assertLoaded();
        return execute(uiAction);
    }
}
