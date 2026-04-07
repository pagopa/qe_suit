package it.frontend.e2e.framework.annotation.selector;

import java.lang.annotation.*;

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



