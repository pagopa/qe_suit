package it.frontend.e2e.framework.annotation.processor.xpath.graph;

import it.frontend.e2e.framework.annotation.processor.xpath.exception.XPathValidationException;
import it.frontend.e2e.framework.annotation.processor.xpath.model.ValidationFailure;
import it.frontend.e2e.framework.annotation.processor.xpath.rule.XPathRuleEvaluator;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import java.util.Optional;
import java.util.Set;

public class DomainGraphValidator {

    private final TypeHierarchyInspector typeInspector;
    private final XPathRuleEvaluator ruleEvaluator;
    private ValidationFailure lastFailure;

    public DomainGraphValidator(TypeHierarchyInspector typeInspector, XPathRuleEvaluator ruleEvaluator) {
        this.typeInspector = typeInspector;
        this.ruleEvaluator = ruleEvaluator;
    }

    public Optional<ValidationFailure> lastFailure() {
        return Optional.ofNullable(lastFailure);
    }

    public boolean validate(TypeElement type, Set<TypeElement> visiting) {
        lastFailure = null;
        return validateInternal(type, visiting);
    }

    private boolean validateInternal(TypeElement type, Set<TypeElement> visiting) {
        if (type == null) {
            return fail(null, null, "Current type is null");
        }
        if (typeInspector.isSimpleCapability(type)) {
            return fail(type, null, "Type is a Capability leaf and cannot be validated as DomainElement");
        }
        if (!typeInspector.isDomainElement(type)) {
            return fail(type, null, "Type does not extend DomainElement");
        }
        if (!visiting.add(type)) {
            return fail(type, null, "Cycle detected while traversing DomainElement graph");
        }

        try {
            boolean hasTypeAnnotation;
            try {
                hasTypeAnnotation = ruleEvaluator.hasValidTypeAnnotation(type);
            } catch (IllegalArgumentException ex) {
                String xpathValue = ruleEvaluator.readTypeXPath(type);
                throw new XPathValidationException(type, null, xpathValue, ex.getMessage(), ex);
            }

            for (ExecutableElement method : typeInspector.childMethodsOf(type)) {
                try {
                    if (ruleEvaluator.hasValidMethodAnnotation(method, hasTypeAnnotation)) {
                        continue;
                    }
                } catch (IllegalArgumentException ex) {
                    String xpathValue = ruleEvaluator.readMethodXPath(method);
                    throw new XPathValidationException(type, method, xpathValue, ex.getMessage(), ex);
                }

                TypeElement returnType = typeInspector.getReturnedTypeElement(method);
                if (returnType == null) {
                    return fail(type, method, "Method has no @XPath and does not return a declared type");
                }
                if (typeInspector.isSimpleCapability(returnType)) {
                    return fail(type, method, "Method has no @XPath and returns a Capability leaf");
                }
                if (!typeInspector.isDomainElement(returnType)) {
                    return fail(type, method, "Method has no @XPath and return type is not a DomainElement");
                }

                if (!validateInternal(returnType, visiting)) {
                    return false;
                }
            }

            return true;
        } finally {
            visiting.remove(type);
        }
    }

    private boolean fail(TypeElement type, ExecutableElement method, String reason) {
        if (lastFailure == null) {
            lastFailure = new ValidationFailure(type, method, reason);
        }
        return false;
    }
}
