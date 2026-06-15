package it.pagopa.interop.common.contract.template.rest;

import it.pagopa.interop.common.contract.template.action.TestChainFactory;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public abstract class RestService {
    @Getter
    @Setter(onMethod_ = {@Autowired})
    protected TestChainFactory chainFactory;
}
