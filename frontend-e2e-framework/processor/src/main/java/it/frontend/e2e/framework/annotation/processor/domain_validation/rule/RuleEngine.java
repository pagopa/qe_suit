package it.frontend.e2e.framework.annotation.processor.domain_validation.rule;

import it.frontend.e2e.framework.core.meta.Constraint;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.util.Set;

public final class RuleEngine {

    private final ProcessingEnvironment env;
    private final Elements elements;
    private final Types types;
    private final Messager messager;

    public RuleEngine(ProcessingEnvironment env) {
        this.env = env;
        this.elements = env.getElementUtils();
        this.types = env.getTypeUtils();
        this.messager = env.getMessager();
    }

    public void applyMustHaveAnnotation(Constraint constraint, Set<TypeElement> targets) {
        TypeElement annotationType =
                elements.getTypeElement(constraint.mustHaveAnnotation);

        if (annotationType == null) {
            messager.printMessage(
                    Diagnostic.Kind.ERROR,
                    "Annotation not found: " + constraint.mustHaveAnnotation
            );
            return;
        }

        for (TypeElement target : targets) {
            if (!hasAnnotation(target, annotationType)) {
                report(constraint, target);
            }
        }
    }

    public void applyMustNotHaveAnnotation(Constraint constraint, Set<TypeElement> targets) {
        TypeElement annotationType =
                elements.getTypeElement(constraint.mustNotHaveAnnotation);

        if (annotationType == null) {
            messager.printMessage(
                    Diagnostic.Kind.ERROR,
                    "Annotation not found: " + constraint.mustNotHaveAnnotation
            );
            return;
        }

        for (TypeElement target : targets) {
            if (hasAnnotation(target, annotationType)) {
                report(constraint, target);
            }
        }
    }

    private boolean hasAnnotation(TypeElement element, TypeElement annotationType) {
        for (AnnotationMirror mirror : element.getAnnotationMirrors()) {
            if (types.isSameType(
                    mirror.getAnnotationType(),
                    annotationType.asType()
            )) {
                return true;
            }
        }
        return false;
    }

    private void report(Constraint constraint, TypeElement element) {
        Diagnostic.Kind kind =
                "ERROR".equalsIgnoreCase(constraint.severity)
                        ? Diagnostic.Kind.ERROR
                        : Diagnostic.Kind.WARNING;

        String message = constraint.message != null
                ? constraint.message
                : "Constraint '" + constraint.id + "' violated";

        messager.printMessage(
                kind,
                message,
                element
        );
    }


}

