package it.pagopa.infrastructure.reporting.contract.openapi;

import it.pagopa.infrastructure.reporting.contract.config.ContractChannelConfig;
import it.pagopa.infrastructure.reporting.contract.config.ContractTargetType;
import it.pagopa.infrastructure.reporting.contract.resolver.ContractFactoryContext;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OpenApiContractTargetResolverTest {

    @Test
    void resolvesMethodNameToHttpMethodAndPath() {
        String spec = Path.of("src/test/resources/reporting/openapi/bff-openapi.yaml").toAbsolutePath().toString();
        ContractChannelConfig channel = new ContractChannelConfig("bff", "BFF", "Bff", ContractTargetType.OPENAPI, spec);
        OpenApiContractTargetResolver resolver = new OpenApiContractTargetResolver();

        String target = resolver.resolveTarget(new ContractFactoryContext(
                channel,
                "it.pagopa.interop.suite.contract.BffAgreementContractTest",
                "createAgreement"
        ));

        assertEquals("POST /agreements", target);
    }

    @Test
    void duplicateOperationIdFailsFast() {
        String spec = Path.of("src/test/resources/reporting/openapi/duplicate-operation-id.yaml").toAbsolutePath().toString();
        ContractChannelConfig channel = new ContractChannelConfig("bff", "BFF", "Bff", ContractTargetType.OPENAPI, spec);
        OpenApiContractTargetResolver resolver = new OpenApiContractTargetResolver();
        assertThrows(IllegalStateException.class, () -> resolver.resolveTarget(new ContractFactoryContext(
                channel,
                "it.pagopa.interop.suite.contract.BffAgreementContractTest",
                "createAgreement"
        )));
    }
}
