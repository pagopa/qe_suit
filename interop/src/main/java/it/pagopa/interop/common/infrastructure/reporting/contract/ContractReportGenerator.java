package it.pagopa.interop.common.infrastructure.reporting.contract;

import it.pagopa.interop.common.infrastructure.reporting.contract.config.ContractReportConfig;
import it.pagopa.interop.common.infrastructure.reporting.contract.config.ContractReportConfigurationLoader;
import it.pagopa.interop.common.infrastructure.reporting.contract.config.ContractReportConfigurationValidator;
import it.pagopa.interop.common.infrastructure.reporting.contract.config.ContractTargetType;
import it.pagopa.interop.common.infrastructure.reporting.contract.model.ContractReport;
import it.pagopa.interop.common.infrastructure.reporting.contract.openapi.OpenApiContractTargetResolver;
import it.pagopa.interop.common.infrastructure.reporting.contract.parser.JUnitExecutionNode;
import it.pagopa.interop.common.infrastructure.reporting.contract.parser.OpenTestReportParser;
import it.pagopa.interop.common.infrastructure.reporting.contract.renderer.ContractReportRenderer;
import it.pagopa.interop.common.infrastructure.reporting.contract.resolver.ContractTargetResolverRegistry;
import it.pagopa.interop.common.infrastructure.reporting.contract.resolver.PageContractTargetResolver;

import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public class ContractReportGenerator {

    private static final Path DEFAULT_OPEN_TEST_REPORT_XML = Path.of("target/junit-platform-reports/open-test-report.xml");
    private static final Path DEFAULT_OUTPUT_HTML = Path.of("target/contract-test/report.html");

    private final ContractReportConfigurationLoader configurationLoader;
    private final ContractReportConfigurationValidator configurationValidator;
    private final OpenTestReportParser parser;
    private final ContractReportBuilder builder;
    private final ContractReportRenderer renderer;

    public ContractReportGenerator() {
        this(
                new ContractReportConfigurationLoader(),
                new ContractReportConfigurationValidator(),
                new OpenTestReportParser(),
                new ContractReportBuilder(
                        new ContractTargetResolverRegistry(Map.of(
                                ContractTargetType.OPENAPI, new OpenApiContractTargetResolver(),
                                ContractTargetType.PAGE, new PageContractTargetResolver()
                        ))
                ),
                new ContractReportRenderer()
        );
    }

    ContractReportGenerator(
            ContractReportConfigurationLoader configurationLoader,
            ContractReportConfigurationValidator configurationValidator,
            OpenTestReportParser parser,
            ContractReportBuilder builder,
            ContractReportRenderer renderer
    ) {
        this.configurationLoader = configurationLoader;
        this.configurationValidator = configurationValidator;
        this.parser = parser;
        this.builder = builder;
        this.renderer = renderer;
    }

    public void generateDefault() {
        generate(DEFAULT_OPEN_TEST_REPORT_XML, DEFAULT_OUTPUT_HTML);
    }

    public void generate(Path openTestReportXml, Path outputHtml) {
        ContractReportConfig config = configurationLoader.load();
        configurationValidator.validate(config);
        List<JUnitExecutionNode> executionNodes = parser.parse(openTestReportXml);
        ContractReport report = builder.build(executionNodes, config, Instant.now());
        renderer.render(report, outputHtml);
    }
}
