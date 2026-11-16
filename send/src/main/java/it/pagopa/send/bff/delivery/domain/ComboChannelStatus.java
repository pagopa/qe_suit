package it.pagopa.send.bff.delivery.domain;

public enum ComboChannelStatus {
    IN_ATTESA("IN ATTESA"),
    INVIATA("INVIATA"),
    DEPOSITATA("DEPOSITATA"),
    CONSEGNATA("CONSEGNATA"),
    LETTA("LETTA"),
    NON_DISPONIBILE("NON DISPONIBILE"),
    FALLITO("FALLITO");

    private final String label;

    ComboChannelStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
