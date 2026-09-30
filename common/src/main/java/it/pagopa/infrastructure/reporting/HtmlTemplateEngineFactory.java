package it.pagopa.infrastructure.reporting;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.nio.charset.StandardCharsets;

public final class HtmlTemplateEngineFactory {

    private HtmlTemplateEngineFactory() {
    }

    public static TemplateEngine classpathHtmlEngine() {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML");
        resolver.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resolver.setCacheable(false);
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(resolver);
        return templateEngine;
    }
}
