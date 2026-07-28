package it.pagopa.send.model;

public record F24PaymentSpec(String title) implements PaymentSpec {

    private static final String DEFAULT_TITLE = "F24 di test";

    public static F24PaymentSpec withDefaultTitle() {
        return new F24PaymentSpec(DEFAULT_TITLE);
    }
}
