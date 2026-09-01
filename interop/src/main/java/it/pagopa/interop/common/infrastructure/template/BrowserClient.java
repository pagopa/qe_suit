package it.pagopa.interop.common.infrastructure.template;

import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.response.UiResponse;
import it.pagopa.interop.common.infrastructure.template.action.TestChain;
import it.pagopa.interop.common.infrastructure.template.action.TestChainFactory;
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

        Supplier<?> navigateAndAction = () -> {
            page.navigateTo();
            page.assertLoaded();
            return uiAction.get();
        };

        return execute(navigateAndAction, responseClass);
    }

    protected TestChain<Void> execute(Runnable uiAction) {
        uiAction.run();
        return chainFactory.build(() -> new UiResponse(null), Void.class);
    }

    protected TestChain<Void> navigateAndExecute(Page page, Runnable uiAction){
        Runnable navigateAndAction = () -> {
            page.navigateTo();
            page.assertLoaded();
            uiAction.run();
        };

        return execute(navigateAndAction);
    }
}
