package it.pagopa.interop.bff.service.action;

import lombok.Setter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

@Service
public class TestChainFactory {

    @Setter(onMethod_ = {@Autowired})
    private ObjectProvider<TestChain> testActionChainProvider;

    @SuppressWarnings("unchecked")
    public <Entity> TestChain<Entity> build(Supplier<ResponseEntity<Entity>> httpCall) {
        return testActionChainProvider.getObject().handle(httpCall);
    }
}
