package it.pagopa.interop.common.contract.template.rest;

import it.pagopa.interop.common.contract.model.Identifiable;
import it.pagopa.interop.common.contract.template.action.TestChain;
import it.pagopa.interop.common.contract.template.action.TestChainFactory;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

@Component
public class RestService {
    @Getter
    @Setter(onMethod_ = {@Autowired})
    protected TestChainFactory chainFactory;

    protected <RESPONSE, MODEL extends Identifiable> TestChain<RESPONSE, MODEL> create(
            Supplier<ResponseEntity<RESPONSE>> apiCall,
            Function<RESPONSE, MODEL> domainMapper) {

        return execute(apiCall, resp -> List.of(domainMapper.apply(resp)));
    }

    protected <RESPONSE, MODEL extends Identifiable> TestChain<RESPONSE, MODEL> update(
            Supplier<ResponseEntity<RESPONSE>> apiCall,
            Function<RESPONSE, MODEL> domainMapper) {

        return execute(apiCall, resp -> List.of(domainMapper.apply(resp)));
    }

    protected <RESPONSE, MODEL extends Identifiable> TestChain<RESPONSE, MODEL> read(
            Supplier<ResponseEntity<RESPONSE>> apiCall,
            Function<RESPONSE, MODEL> domainMapper) {

        return execute(apiCall, resp -> List.of(domainMapper.apply(resp)));
    }

    protected <RESPONSE, MODEL extends Identifiable> TestChain<RESPONSE, MODEL> readAll(
            Supplier<ResponseEntity<RESPONSE>> apiCall,
            Function<RESPONSE, List<MODEL>> domainMapper) {

        return execute(apiCall, domainMapper);
    }

    protected <RESPONSE, MODEL extends Identifiable> TestChain<RESPONSE, MODEL> delete(
            Supplier<ResponseEntity<RESPONSE>> apiCall,
            Function<RESPONSE, MODEL> domainMapper) {

        return execute(apiCall, resp -> List.of(domainMapper.apply(resp)));
    }

    /**
     * Esegue dinamicamente una chiamata API del BFF, la incapsula in una TestChain
     * per gestire logiche di polling/retry e ne mappa il risultato nel modello di dominio.
     *
     * @param <RESPONSE> Il DTO di risposta della rete (es. ProducerEServiceDetails, AgreementDetails)
     * @param <MODEL>    Il tuo modello di business core (es. EService, Agreement)
     *                   * @param apiInstance L'istanza del client OpenAPI iniettata da Spring
     * @param apiCall    La lambda che definisce QUALE endpoint chiamare
     * @param mapper     La lambda del tuo Handler che definisce COME mappare il DTO nel dominio
     * @return Una TestChain pronta per essere eseguita o configurata con politiche di polling
     */
    private <RESPONSE, MODEL extends Identifiable> TestChain<RESPONSE, MODEL> execute(
            Supplier<ResponseEntity<RESPONSE>> apiCall,
            Function<RESPONSE, List<MODEL>> mapper) {

        return chainFactory.build(apiCall, mapper);
    }
}
