package it.frontend.e2e.framework.annotation.processor.xpath.graph;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.WildcardType;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("TypeHierarchyInspector")
class TypeHierarchyInspectorTest {

    @Test
    @DisplayName("dovrebbe estrarre il tipo interno da Optional<T>")
    void shouldExtractInnerTypeFromOptionalReturnType() {
        TypeHierarchyInspector inspector = createInspector();

        ExecutableElement method = mock(ExecutableElement.class);
        DeclaredType optionalDeclaredType = mock(DeclaredType.class);
        TypeElement optionalTypeElement = mock(TypeElement.class);
        Name optionalQualifiedName = mock(Name.class);

        DeclaredType innerDeclaredType = mock(DeclaredType.class);
        TypeElement innerTypeElement = mock(TypeElement.class);

        when(method.getReturnType()).thenReturn(optionalDeclaredType);
        when(optionalDeclaredType.asElement()).thenReturn(optionalTypeElement);
        when(optionalTypeElement.getQualifiedName()).thenReturn(optionalQualifiedName);
        when(optionalQualifiedName.toString()).thenReturn("java.util.Optional");
        doReturn(List.of(innerDeclaredType)).when(optionalDeclaredType).getTypeArguments();
        when(innerDeclaredType.asElement()).thenReturn(innerTypeElement);

        TypeElement result = inspector.getReturnedTypeElement(method);

        assertEquals(innerTypeElement, result);
    }

    @Test
    @DisplayName("dovrebbe restituire null quando Optional contiene wildcard")
    void shouldReturnNullWhenOptionalContainsWildcard() {
        TypeHierarchyInspector inspector = createInspector();

        ExecutableElement method = mock(ExecutableElement.class);
        DeclaredType optionalDeclaredType = mock(DeclaredType.class);
        TypeElement optionalTypeElement = mock(TypeElement.class);
        Name optionalQualifiedName = mock(Name.class);

        when(method.getReturnType()).thenReturn(optionalDeclaredType);
        when(optionalDeclaredType.asElement()).thenReturn(optionalTypeElement);
        when(optionalTypeElement.getQualifiedName()).thenReturn(optionalQualifiedName);
        when(optionalQualifiedName.toString()).thenReturn("java.util.Optional");
        doReturn(List.of(mock(WildcardType.class))).when(optionalDeclaredType).getTypeArguments();

        TypeElement result = inspector.getReturnedTypeElement(method);

        assertNull(result);
    }

    private TypeHierarchyInspector createInspector() {
        ProcessingEnvironment env = mock(ProcessingEnvironment.class);
        when(env.getTypeUtils()).thenReturn(mock(Types.class));
        when(env.getElementUtils()).thenReturn(mock(Elements.class));
        return new TypeHierarchyInspector(env);
    }
}
