package it.pagopa.reporting.dto.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.RECORD_COMPONENT})
public @interface ReportField {
    String label() default "";

    int order() default 100;

    ReportFieldFormat format() default ReportFieldFormat.AUTO;

    boolean filterable() default false;
}

