package it.frontend.e2e.framework.annotation.processor.xpath.graph;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.List;

public class TypeHierarchyInspector {

    private static final String DOMAIN_ELEMENT_CLASS = "it.frontend.e2e.framework.core.model.DomainElement";
    private static final String CAPABILITY_CLASS = "it.frontend.e2e.framework.core.model.Capability";
    private static final String OPTIONAL_CLASS = "java.util.Optional";

    private final Types types;
    private final Elements elements;

    public TypeHierarchyInspector(ProcessingEnvironment processingEnv) {
        this.types = processingEnv.getTypeUtils();
        this.elements = processingEnv.getElementUtils();
    }

    public boolean isDomainElement(TypeElement type) {
        return isSubtype(type, DOMAIN_ELEMENT_CLASS) && !isSameType(type, DOMAIN_ELEMENT_CLASS);
    }

    public boolean isSimpleCapability(TypeElement type) {
        return isSubtype(type, CAPABILITY_CLASS) && !isDomainElement(type);
    }

    public TypeElement getReturnedTypeElement(ExecutableElement method) {
        TypeMirror returnType = method.getReturnType();
        if (!(returnType instanceof DeclaredType declaredType)) {
            return null;
        }

        if (isOptional(declaredType)) {
            return getOptionalInnerTypeElement(declaredType);
        }

        Element element = declaredType.asElement();
        return element instanceof TypeElement ? (TypeElement) element : null;
    }

    public List<ExecutableElement> childMethodsOf(TypeElement type) {
        return type.getEnclosedElements().stream()
                .filter(e -> e.getKind() == ElementKind.METHOD)
                .map(e -> (ExecutableElement) e)
                .filter(this::isGraphMethod)
                .toList();
    }

    private boolean isGraphMethod(ExecutableElement method) {
        return method.getModifiers().contains(Modifier.ABSTRACT)
                && !method.getModifiers().contains(Modifier.STATIC)
                && method.getParameters().isEmpty()
                && method.getReturnType().getKind() != TypeKind.VOID;
    }

    private boolean isOptional(DeclaredType type) {
        Element element = type.asElement();
        if (!(element instanceof TypeElement typeElement)) {
            return false;
        }
        return OPTIONAL_CLASS.equals(typeElement.getQualifiedName().toString());
    }

    private TypeElement getOptionalInnerTypeElement(DeclaredType optionalType) {
        List<? extends TypeMirror> typeArguments = optionalType.getTypeArguments();
        if (typeArguments.size() != 1) {
            return null;
        }

        TypeMirror innerType = typeArguments.get(0);
        if (!(innerType instanceof DeclaredType innerDeclaredType)) {
            return null;
        }

        Element innerElement = innerDeclaredType.asElement();
        return innerElement instanceof TypeElement ? (TypeElement) innerElement : null;
    }

    private boolean isSubtype(TypeElement type, String fqcn) {
        TypeElement target = elements.getTypeElement(fqcn);
        if (target == null) {
            return false;
        }
        return types.isAssignable(types.erasure(type.asType()), types.erasure(target.asType()));
    }

    private boolean isSameType(TypeElement type, String fqcn) {
        TypeElement target = elements.getTypeElement(fqcn);
        if (target == null) {
            return false;
        }
        return types.isSameType(types.erasure(type.asType()), types.erasure(target.asType()));
    }
}
