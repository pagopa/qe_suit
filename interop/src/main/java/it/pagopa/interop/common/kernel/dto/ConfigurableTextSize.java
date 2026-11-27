package it.pagopa.interop.common.kernel.dto;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD, METHOD})
@Retention(RUNTIME)
@Constraint(validatedBy = ConfigurableTextSizeValidator.class)
public @interface ConfigurableTextSize {
    String message() default "text must fit within {max} characters, including a hyphen and at least one random character in RANDOM mode";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int max();
}
