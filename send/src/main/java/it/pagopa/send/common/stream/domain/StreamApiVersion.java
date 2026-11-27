package it.pagopa.send.common.stream.domain;

/**
 * Versioni legacy delle API B2B pn-stream ({@code pn-stream-api-external-older-version.yaml}), tutte
 * instradate dalla lambda {@code pn-versioningV1V2StreamLambda} verso il backend v3.0.
 * <p>
 * Il backend lega ogni stream alla versione con cui e' stato creato e rifiuta lettura e scrittura
 * da una versione diversa: le precondizioni creano quindi lo stream con la stessa versione
 * dell'operazione sotto test.
 */
public enum StreamApiVersion {
    V10, V23, V24, V25, V26, V27, V28, V29;

    /**
     * {@code POST /streams/{streamId}/action/disable} non esiste in v1.0.
     */
    public boolean supportsDisable() {
        return this != V10;
    }

    /**
     * {@code groups} (obbligatorio) e {@code replacedStreamId} esistono da v2.3.
     */
    public boolean supportsGroups() {
        return this != V10;
    }

    /**
     * {@code waitForAccepted} esiste da v2.7.
     */
    public boolean supportsWaitForAccepted() {
        return compareTo(V27) >= 0;
    }
}
