package it.pagopa.send.bff.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.infrastructure.contract.http.HttpContractPolicy;
import it.pagopa.infrastructure.contract.http.HttpContractValidator;
import it.pagopa.infrastructure.fuzzing.FuzzEngine;
import it.pagopa.infrastructure.fuzzing.FuzzScenario;
import it.pagopa.infrastructure.objectgraph.ObjectGraphDecomposer;
import it.pagopa.send.common.infrastructure.contract.SendHttpContractValidator;
import it.pagopa.send.common.kernel.context.CurrentUserSession;
import it.pagopa.send.common.user.domain.UserProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration(proxyBeanMethods = false)
public class BffApiContractConfig {

    /**
     * Costruttore a cinque argomenti: lo scope delle query riusa il {@code pathParamsFuzzEngine},
     * perche' {@code FuzzingConfig} del modulo send non espone un engine dedicato.
     */
    @Bean("bffApiContract")
    HttpContractValidator bffApiContract(
            ObjectMapper objectMapper,
            @Qualifier("payloadFuzzEngine") FuzzEngine payloadFuzzEngine,
            @Qualifier("pathParamsFuzzEngine") FuzzEngine pathParamsFuzzEngine,
            ObjectGraphDecomposer objectGraphDecomposer,
            @Qualifier("bffApiContractPolicy") HttpContractPolicy bffApiContractPolicy
    ) {
        return new HttpContractValidator(
                objectMapper,
                payloadFuzzEngine,
                pathParamsFuzzEngine,
                objectGraphDecomposer,
                bffApiContractPolicy
        );
    }

    @Bean
    SendHttpContractValidator sendHttpContractValidator(
            @Qualifier("bffApiContract") HttpContractValidator bffApiContract,
            CurrentUserSession currentUserSession,
            UserProvider userProvider
    ) {
        return new SendHttpContractValidator(bffApiContract, currentUserSession, userProvider);
    }

    /**
     * Il costruttore del validator pretende una policy completa su tutti gli scenari di fuzzing,
     * che la modalita' autorizzazione non consulta (verifica uno status esatto dichiarato dal
     * test). Finche' in send non esistono contract test di fuzzing, questa resta una policy
     * minima: va sostituita con le attese reali per endpoint quando serviranno.
     */
    @Bean("bffApiContractPolicy")
    HttpContractPolicy bffApiContractPolicy() {
        return HttpContractPolicy.builder()
                .successStatus(200)
                .scenarioStatus(List.of(FuzzScenario.values()), 400)
                .build();
    }
}
