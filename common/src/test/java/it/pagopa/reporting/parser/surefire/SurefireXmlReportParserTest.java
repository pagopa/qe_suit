package it.pagopa.reporting.parser.surefire;

import it.pagopa.reporting.dto.ConcreteCaseReport;
import it.pagopa.reporting.dto.EnvironmentProperty;
import it.pagopa.reporting.dto.ExecutionStatus;
import it.pagopa.reporting.dto.ReportDocument;
import it.pagopa.reporting.dto.TestClassReport;
import it.pagopa.reporting.dto.TestFactoryReport;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class SurefireXmlReportParserTest {

    @Test
    void parseShouldBuildExpectedHierarchyAndFilterSensitiveProperties() throws Exception {
        SurefireXmlReportParser parser = new SurefireXmlReportParser();

        Path sampleXml = resourcePath("it/pagopa/reporting/surefire-sample.xml");
        ReportDocument report = parser.parse(sampleXml);

        assertThat(report.summary().tests()).isEqualTo(2);
        assertThat(report.summary().failures()).isEqualTo(1);
        assertThat(report.summary().errors()).isZero();
        assertThat(report.summary().skipped()).isZero();

        assertThat(report.environmentProperties())
                .extracting(EnvironmentProperty::key)
                .contains("java.version", "os.name", "test")
                .doesNotContain("user.home");

        assertThat(report.testClasses()).hasSize(1);
        TestClassReport classReport = report.testClasses().get(0);
        assertThat(classReport.className()).isEqualTo("it.pagopa.reporting.SampleContractTest");

        assertThat(classReport.factories()).hasSize(1);
        TestFactoryReport factoryReport = classReport.factories().get(0);
        assertThat(factoryReport.factoryName()).isEqualTo("createSomething");
        assertThat(factoryReport.counts().total()).isEqualTo(2);
        assertThat(factoryReport.counts().failed()).isEqualTo(1);

        assertThat(factoryReport.concreteCases()).hasSizeGreaterThanOrEqualTo(3);

        Optional<ConcreteCaseReport> failingCase = factoryReport.concreteCases().stream()
                .filter(testCase -> "REMOVED @ /name".equals(testCase.displayName()))
                .findFirst();

        assertThat(failingCase).isPresent();
        assertThat(failingCase.orElseThrow().status()).isEqualTo(ExecutionStatus.FAILED);

        Optional<ConcreteCaseReport> rootCase = factoryReport.concreteCases().stream()
                .filter(testCase -> "REPLACED_WITH_NULL @ <root>".equals(testCase.displayName()))
                .findFirst();

        assertThat(rootCase).isPresent();
        assertThat(rootCase.orElseThrow().caseLog()).contains("Request Method: POST");
    }

    @Test
    void parseShouldExposeFactoryLevelErrorsAsStandaloneFactories() throws Exception {
        SurefireXmlReportParser parser = new SurefireXmlReportParser();

        Path realXml = resourcePath("it/pagopa/reporting/TEST-it.pagopa.interop.suite.contract.BffEServiceTemplateContractTest.xml");
        ReportDocument report = parser.parse(realXml);

        TestClassReport classReport = report.testClasses().stream()
                .filter(testClass -> "it.pagopa.interop.suite.contract.BffEServiceTemplateContractTest".equals(testClass.className()))
                .findFirst()
                .orElseThrow();

        Map<String, TestFactoryReport> factoriesByName = classReport.factories().stream()
                .collect(Collectors.toMap(TestFactoryReport::factoryName, factory -> factory));

        assertThat(factoriesByName.keySet()).contains(
                "createEServiceTemplate",
                "updateEServiceTemplateName",
                "updateEServiceTemplateDescription",
                "updateEServiceTemplateIntendedTarget"
        );

        assertFactoryError(factoriesByName.get("updateEServiceTemplateName"), "updateEServiceTemplateName");
        assertFactoryError(factoriesByName.get("updateEServiceTemplateDescription"), "updateEServiceTemplateDescription");
        assertFactoryError(factoriesByName.get("updateEServiceTemplateIntendedTarget"), "updateEServiceTemplateIntendedTarget");
    }

    private void assertFactoryError(TestFactoryReport factoryReport, String testcaseName) {
        assertThat(factoryReport).isNotNull();
        assertThat(factoryReport.counts().total()).isEqualTo(1);
        assertThat(factoryReport.counts().errors()).isEqualTo(1);

        assertThat(factoryReport.concreteCases()).hasSize(1);
        ConcreteCaseReport concreteCase = factoryReport.concreteCases().get(0);
        assertThat(concreteCase.displayName()).isEqualTo(testcaseName);
        assertThat(concreteCase.sourceTestcaseName()).isEqualTo(testcaseName);
        assertThat(concreteCase.status()).isEqualTo(ExecutionStatus.ERROR);
        assertThat(concreteCase.failureMessage()).contains("pathParams supplier failed during discovery");
    }

    private Path resourcePath(String resource) throws URISyntaxException {
        return Path.of(Objects.requireNonNull(getClass().getClassLoader().getResource(resource)).toURI());
    }
}


