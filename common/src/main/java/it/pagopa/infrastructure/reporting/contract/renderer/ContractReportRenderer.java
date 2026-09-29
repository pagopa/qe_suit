package it.pagopa.infrastructure.reporting.contract.renderer;

import it.pagopa.infrastructure.reporting.HtmlTemplateEngineFactory;
import it.pagopa.infrastructure.reporting.contract.model.ContractReport;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class ContractReportRenderer {

    private final TemplateEngine templateEngine;
    private final ContractReportFormatters formatters = new ContractReportFormatters();

    public ContractReportRenderer() {
        templateEngine = HtmlTemplateEngineFactory.classpathHtmlEngine();
    }

    public void render(ContractReport report, Path outputPath) {
        Context context = new Context();
        context.setVariable("report", report);
        context.setVariable("fmt", formatters);
        String html = templateEngine.process("contract-report/report", context);
        try {
            Files.createDirectories(outputPath.getParent());
            Files.writeString(outputPath, html, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new IllegalStateException("Cannot write contract report HTML: " + outputPath, ex);
        }
    }
}
