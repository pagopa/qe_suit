package it.frontend.e2e.framework.annotation.selector;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates a PresentationElement and specifies the selector to locate it.
 *
 * <p>This annotation can be applied to methods or interfaces to define
 * the selector string needed to identify the element during test execution.</p>
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XPath {
    /**
     * The selector string to locate the element.
     */
    String value();
}



