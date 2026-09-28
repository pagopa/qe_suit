package it.pagopa.infrastructure.pst.report;

import it.pagopa.infrastructure.pst.model.PstDocument;
import it.pagopa.infrastructure.reporting.HtmlTemplateEngineFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public final class PstReportRenderer {

    public static final String DEFAULT_TITLE = "Progettazione Scenari di Test";

    private final TemplateEngine templateEngine;

    public PstReportRenderer() {
        this.templateEngine = HtmlTemplateEngineFactory.classpathHtmlEngine();
    }

    public void render(PstDocument document, Path outputPath) {
        render(document, DEFAULT_TITLE, outputPath);
    }

    public void render(PstDocument document, String title, Path outputPath) {
        Objects.requireNonNull(outputPath, "outputPath must not be null");
        String html = renderToString(document, title);
        try {
            Path parent = outputPath.toAbsolutePath().getParent();
            if (parent != null) Files.createDirectories(parent);
            Files.writeString(outputPath, html, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new IllegalStateException("Cannot write PST report HTML: " + outputPath, exception);
        }
    }

    public String renderToString(PstDocument document, String title) {
        Context context = new Context();
        context.setVariable("report", PstReportView.from(title, document));
        return templateEngine.process("pst-report/report", context);
    }
}
