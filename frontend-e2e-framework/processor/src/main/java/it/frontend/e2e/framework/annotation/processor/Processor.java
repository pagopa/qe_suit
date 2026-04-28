package it.frontend.e2e.framework.annotation.processor;

import com.google.auto.service.AutoService;
import it.frontend.e2e.framework.annotation.processor.domain_validation.DomainValidationProcessor;
import it.frontend.e2e.framework.annotation.processor.xpath.XPathProcessor;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main annotation processor that delegates to multiple specialized processors.
 * Manages the lifecycle of all registered processors.
 */
@AutoService(javax.annotation.processing.Processor.class)
@SupportedAnnotationTypes("*")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public final class Processor extends AbstractProcessor {

    private static final Logger LOGGER = Logger.getLogger(Processor.class.getName());

    private final List<javax.annotation.processing.Processor> processors = List.of(
            new DomainValidationProcessor(),
            new XPathProcessor()
    );

    @Override
    public synchronized void init(ProcessingEnvironment env) {
        LOGGER.log(Level.INFO, "Initializing FrameworkProcessor with " + processors.size() + " sub-processors");

        for (javax.annotation.processing.Processor processor : processors) {
            try {
                LOGGER.log(Level.FINE, "Initializing processor: " + processor.getClass().getName());
                processor.init(env);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Failed to initialize processor: " + processor.getClass().getName(), e);
                env.getMessager().printMessage(Diagnostic.Kind.ERROR,
                        "Failed to initialize processor: " + processor.getClass().getName() + " - " + e.getMessage());
            }
        }
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        boolean result = true;

        for (javax.annotation.processing.Processor processor : processors) {
            LOGGER.log(Level.FINE, "Processing annotations with: " + processor.getClass().getName());
            boolean processorResult = processor.process(annotations, roundEnv);
            result = result && processorResult;
        }

        return result;
    }
}
