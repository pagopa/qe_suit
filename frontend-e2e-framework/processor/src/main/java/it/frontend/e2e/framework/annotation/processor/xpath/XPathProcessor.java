package it.frontend.e2e.framework.annotation.processor.xpath;

import it.frontend.e2e.framework.annotation.processor.xpath.exception.XPathValidationException;
import it.frontend.e2e.framework.annotation.processor.xpath.filter.DomainElementFilter;
import it.frontend.e2e.framework.annotation.processor.xpath.graph.DomainGraphValidator;
import it.frontend.e2e.framework.annotation.processor.xpath.graph.TypeHierarchyInspector;
import it.frontend.e2e.framework.annotation.processor.xpath.model.ValidationFailure;
import it.frontend.e2e.framework.annotation.processor.xpath.rule.XPathRuleEvaluator;
import it.frontend.e2e.framework.annotation.processor.xpath.rule.XPathValidator;
import it.frontend.e2e.framework.annotation.processor.xpath.util.XPathAnnotationHelper;
import it.frontend.e2e.framework.annotation.processor.xpath.util.XPathAnnotationReader;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.HashSet;
import java.util.Set;

/**
 * Annotation Processor per la validazione di annotazioni @XPath.
 * Valida le espressioni XPath sui DomainElement durante la compilazione.
 */
public class XPathProcessor extends AbstractProcessor {

    private DomainElementFilter domainElementFilter;
    private DomainGraphValidator domainGraphValidator;
    private final Set<String> reportedErrors = new HashSet<>();

    private DomainElementFilter domainElementFilter() {
        if (domainElementFilter == null) {
            domainElementFilter = new DomainElementFilter(processingEnv);
        }
        return domainElementFilter;
    }

    private DomainGraphValidator domainGraphValidator() {
        if (domainGraphValidator == null) {
            TypeHierarchyInspector typeInspector = new TypeHierarchyInspector(processingEnv);
            XPathAnnotationReader annotationReader = new XPathAnnotationReader(new XPathAnnotationHelper());
            XPathRuleEvaluator ruleEvaluator = new XPathRuleEvaluator(new XPathValidator(), annotationReader);
            domainGraphValidator = new DomainGraphValidator(typeInspector, ruleEvaluator);
        }
        return domainGraphValidator;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Messager messager = processingEnv.getMessager();

        for (TypeElement type : domainElementFilter().filterDomainElements(roundEnv)) {
            try {
                if (!domainGraphValidator().validate(type, new HashSet<>())) {
                    String details = domainGraphValidator().lastFailure()
                            .map(this::formatFailure)
                            .orElse("reason=Unknown validation failure");

                    messager.printMessage(
                            Diagnostic.Kind.ERROR,
                            "Invalid DomainElement graph: " + details,
                            type
                    );
                }
            } catch (XPathValidationException ex) {
                // Report error on the exact type/method where the invalid XPath was found
                TypeElement errorType = ex.getType() != null ? ex.getType() : type;

                // Create unique key to avoid duplicate error messages
                String errorKey = (errorType != null ? errorType.getQualifiedName() : "null") +
                                  ":" + (ex.getMethod() != null ? ex.getMethod().getSimpleName() : "type") +
                                  ":" + ex.getMessage();

                if (reportedErrors.add(errorKey)) {
                    if (ex.getMethod() != null) {
                        messager.printMessage(
                                Diagnostic.Kind.ERROR,
                                formatXPathError(ex),
                                ex.getMethod()
                        );
                    } else {
                        messager.printMessage(
                                Diagnostic.Kind.ERROR,
                                formatXPathError(ex),
                                errorType
                        );
                    }
                }
            } catch (IllegalArgumentException ex) {
                String errorKey = (type != null ? type.getQualifiedName() : "null") + ":type:" + ex.getMessage();
                if (reportedErrors.add(errorKey)) {
                    messager.printMessage(
                            Diagnostic.Kind.ERROR,
                            "Invalid @XPath: " + compactError(ex),
                            type
                    );
                }
            }
        }

        return false;
    }

    private String formatFailure(ValidationFailure failure) {
        String typeName = failure.type() != null ? failure.type().getQualifiedName().toString() : "<null>";
        String methodName = failure.method() != null ? failure.method().getSimpleName().toString() : "<n/a>";
        return "type=" + typeName + ", method=" + methodName + ", reason=" + failure.reason();
    }

    private String formatXPathError(XPathValidationException ex) {
        StringBuilder sb = new StringBuilder("Invalid @XPath");

        if (ex.getXPathValue() != null) {
            sb.append(" value=\"")
              .append(ex.getXPathValue())
              .append("\"");
        }

        if (ex.getMethod() != null) {
            sb.append(" on method '")
              .append(ex.getMethod().getSimpleName())
              .append("()'");
        }

        if (ex.getType() != null) {
            sb.append(" in ")
              .append(ex.getType().getSimpleName());
        }

        sb.append(": ").append(compactError(ex));

        return sb.toString();
    }

    private String compactError(Throwable error) {
        String message = error.getMessage();
        if (message == null || message.isBlank()) {
            return "Unexpected XPath validation error";
        }

        // Avoid dumping full nested exception chains in compiler output.
        int separator = message.indexOf('\n');
        return separator >= 0 ? message.substring(0, separator) : message;
    }
}