package it.pagopa.infrastructure.reporting.contract.config;

import it.pagopa.infrastructure.reporting.contract.resolver.ContractChannelResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ContractReportConfigurationTest {

    @TempDir
    Path tempDir;

    @Test
    void channelResolverUsesConfigDrivenPrefixesIncludingNewPartnerChannel() throws Exception {
        Path yaml = tempDir.resolve("application.yaml");
        Files.writeString(yaml, """
                contract-report:
                  channels:
                    bff:
                      label: BFF
                      class-prefix: Bff
                      target-type: OPENAPI
                      openapi: src/test/resources/openapi/minimal-api.yaml
                    m2m:
                      label: M2M
                      class-prefix: M2M
                      target-type: OPENAPI
                      openapi: src/test/resources/openapi/minimal-api.yaml
                    web:
                      label: WEB
                      class-prefix: Web
                      target-type: PAGE
                    mobile:
                      label: MOBILE
                      class-prefix: Mobile
                      target-type: PAGE
                    partner:
                      label: PARTNER
                      class-prefix: Partner
                      target-type: OPENAPI
                      openapi: src/test/resources/openapi/minimal-api.yaml
                """);

        ContractReportConfig config = new ContractReportConfigurationLoader(yaml).load();
        new ContractReportConfigurationValidator().validate(config);
        ContractChannelResolver resolver = new ContractChannelResolver(config);

        assertEquals("bff", resolver.resolveByClassName("BffAgreementContractTest").orElseThrow().key());
        assertEquals("m2m", resolver.resolveByClassName("M2MAgreementContractTest").orElseThrow().key());
        assertEquals("web", resolver.resolveByClassName("WebFooContractTest").orElseThrow().key());
        assertEquals("mobile", resolver.resolveByClassName("MobileFooContractTest").orElseThrow().key());
        assertEquals("partner", resolver.resolveByClassName("PartnerExampleContractTest").orElseThrow().key());
    }

    @Test
    void openApiChannelWithoutOpenApiFailsValidation() throws Exception {
        Path yaml = tempDir.resolve("application.yaml");
        Files.writeString(yaml, """
                contract-report:
                  channels:
                    bff:
                      label: BFF
                      class-prefix: Bff
                      target-type: OPENAPI
                """);
        ContractReportConfig config = new ContractReportConfigurationLoader(yaml).load();
        assertThrows(IllegalStateException.class, () -> new ContractReportConfigurationValidator().validate(config));
    }

    @Test
    void pageChannelWithoutOpenApiIsValid() throws Exception {
        Path yaml = tempDir.resolve("application.yaml");
        Files.writeString(yaml, """
                contract-report:
                  channels:
                    web:
                      label: WEB
                      class-prefix: Web
                      target-type: PAGE
                """);
        ContractReportConfig config = new ContractReportConfigurationLoader(yaml).load();
        new ContractReportConfigurationValidator().validate(config);
    }

    @Test
    void unknownTargetTypeFailsLoading() throws Exception {
        Path yaml = tempDir.resolve("application.yaml");
        Files.writeString(yaml, """
                contract-report:
                  channels:
                    web:
                      label: WEB
                      class-prefix: Web
                      target-type: SOMETHING_ELSE
                """);
        ContractReportConfigurationLoader loader = new ContractReportConfigurationLoader(yaml);
        assertThrows(IllegalArgumentException.class, loader::load);
    }

    @Test
    void duplicateClassPrefixFailsValidation() throws Exception {
        Path yaml = tempDir.resolve("application.yaml");
        Files.writeString(yaml, """
                contract-report:
                  channels:
                    a:
                      label: A
                      class-prefix: Web
                      target-type: PAGE
                    b:
                      label: B
                      class-prefix: Web
                      target-type: PAGE
                """);
        ContractReportConfig config = new ContractReportConfigurationLoader(yaml).load();
        assertThrows(IllegalStateException.class, () -> new ContractReportConfigurationValidator().validate(config));
    }
}
