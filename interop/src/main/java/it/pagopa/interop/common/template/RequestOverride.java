package it.pagopa.interop.common.template;

@FunctionalInterface
public interface RequestOverride<Request> {
    void applyTo(Request request);
}