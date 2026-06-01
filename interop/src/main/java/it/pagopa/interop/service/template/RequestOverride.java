package it.pagopa.interop.service.template;

@FunctionalInterface
public interface RequestOverride<Request> {

    Request applyTo(Request request);

    /**
     * Consente l'utilizzo come Method Reference.
     * Esempio: eServiceService.createResource(RequestOverride::identity)
     */
    static <R> R identity(R request) {
        return request;
    }

    /**
     * Consente l'utilizzo come chiamata di metodo diretta.
     * Esempio: eServiceService.createResource(RequestOverride.identity())
     */
    static <R> RequestOverride<R> identity() {
        return request -> request;
    }
}