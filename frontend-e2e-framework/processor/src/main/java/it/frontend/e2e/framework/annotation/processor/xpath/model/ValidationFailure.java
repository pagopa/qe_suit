package it.frontend.e2e.framework.annotation.processor.xpath.model;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

public record ValidationFailure(TypeElement type, ExecutableElement method, String reason) {
}

