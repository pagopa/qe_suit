package it.pagopa.send.nationalregistries.infrastructure.config;

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
public class NationalRegistriesApiContractConfig {

    @Bean("nationalRegistriesApiContract")
    HttpContractValidator nationalRegistriesApiContract(
            ObjectMapper objectMapper,
            @Qualifier("payloadFuzzEngine") FuzzEngine payloadFuzzEngine,
            @Qualifier("pathParamsFuzzEngine") FuzzEngine pathParamsFuzzEngine,
            @Qualifier("queryParamsFuzzEngine") FuzzEngine queryParamsFuzzEngine,
            ObjectGraphDecomposer objectGraphDecomposer,
            @Qualifier("nationalRegistriesApiContractPolicy") HttpContractPolicy policy
    ) {
        return new HttpContractValidator(objectMapper, payloadFuzzEngine, pathParamsFuzzEngine, queryParamsFuzzEngine, objectGraphDecomposer, policy);
    }

    @Bean("nationalRegistriesHttpContractValidator")
    SendHttpContractValidator nationalRegistriesHttpContractValidator(
            @Qualifier("nationalRegistriesApiContract") HttpContractValidator contract,
            CurrentUserSession currentUserSession,
            UserProvider userProvider
    ) {
        return new SendHttpContractValidator(contract, currentUserSession, userProvider);
    }

    @Bean("nationalRegistriesApiContractPolicy")
    HttpContractPolicy nationalRegistriesApiContractPolicy() {
        return HttpContractPolicy.builder()
                .successStatus(200)
                .scenarioStatus(List.of(FuzzScenario.values()), 400)
                .build();
    }
}
