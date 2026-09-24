package it.pagopa.reporting.writer.html;

import it.pagopa.reporting.dto.ReportDocument;
import it.pagopa.reporting.parser.surefire.SurefireXmlReportParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

class HtmlSidebarReportWriterTest {

    @TempDir
    Path tempDir;

    @Test
    void writeShouldGenerateNavigableHtmlFile() throws Exception {
        SurefireXmlReportParser parser = new SurefireXmlReportParser();
        ReportDocument report = parser.parse(resourcePath("it/pagopa/reporting/surefire-sample.xml"));

        HtmlSidebarReportWriter writer = new HtmlSidebarReportWriter(true);
        Path output = tempDir.resolve("sample-report.html");

        writer.write(report, output);

        assertThat(Files.exists(output)).isTrue();

        String html = Files.readString(output);
        assertThat(html).contains("QE SUIT Contract Test HTML Report");
        assertThat(html).contains("Navigation");
        assertThat(html).contains("createSomething");
        assertThat(html).contains("F:");
        assertThat(html).contains("class=\"cases-table\"");
        assertThat(html).contains("class=\"case-expander\"");
        assertThat(html).contains("REMOVED @ /name");
        assertThat(html).contains("DTO debug dump");
        assertThat(html).contains("Environment properties (safe subset)");
        assertThat(html).contains("Only non-sensitive values are shown");
        assertThat(html).contains("<summary>Show safe properties (");
        assertThat(html).contains("<summary>Excluded property keys for this run (");
        assertThat(html).contains("<li>user.home</li>");
        assertThat(html).doesNotContain("Class -> Factory -> Concrete case");

        assertThat(html.indexOf("Run summary")).isLessThan(html.indexOf("Filters"));
        assertThat(html.indexOf("Filters")).isLessThan(html.indexOf("Navigation"));
    }

    private Path resourcePath(String resource) throws URISyntaxException {
        return Path.of(Objects.requireNonNull(getClass().getClassLoader().getResource(resource)).toURI());
    }
}


