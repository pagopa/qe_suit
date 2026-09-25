package it.pagopa.interop.common.infrastructure.reporting.contract.renderer;

import it.pagopa.interop.common.infrastructure.reporting.contract.model.ContractReport;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class ContractReportRenderer {

    private final TemplateEngine templateEngine;
    private final ContractReportFormatters formatters = new ContractReportFormatters();

    public ContractReportRenderer() {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML");
        resolver.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resolver.setCacheable(false);
        templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(resolver);
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
