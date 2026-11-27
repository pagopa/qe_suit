package it.pagopa.send.stream.infrastructure;

import it.pagopa.send.common.stream.domain.StreamApiVersion;

/**
 * Status HTTP delle API pn-stream legacy, in un solo punto. In v1.0 la lambda
 * {@code pn-versioningV1V2StreamLambda} converte in 400 i 403 e 404 del backend (lo spec v1.0 non li
 * dichiara), lasciando nel corpo il problema originale.
 */
public final class StreamLegacyStatusCodes {

    public static final int SUCCESS = 200;
    public static final int REMOVE_SUCCESS = 204;
    public static final int VALIDATION_ERROR = 400;

    /**
     * Richiesta bloccata dal WAF del gateway, prima della lambda: 403 anche in v1.0.
     */
    public static final int WAF_BLOCKED = 403;

    /**
     * Codice del problema restituito dal backend per uno stream inesistente.
     */
    static final String STREAM_NOT_FOUND_CODE = "PN_STREAM_STREAMNOTFOUND";

    private StreamLegacyStatusCodes() {
    }

    /**
     * Stream inesistente: 404, 400 in v1.0.
     */
    public static int notFound(StreamApiVersion version) {
        return translatesErrorsToBadRequest(version) ? VALIDATION_ERROR : 404;
    }

    /**
     * Operazione rifiutata dal backend per stato o versione dello stream: 403, 400 in v1.0.
     */
    public static int forbidden(StreamApiVersion version) {
        return translatesErrorsToBadRequest(version) ? VALIDATION_ERROR : 403;
    }

    /**
     * Vero solo per uno stream inesistente: in v1.0 il 400 vale come assenza solo se il corpo riporta
     * il codice {@value #STREAM_NOT_FOUND_CODE}, altrimenti e' un vero errore di validazione.
     */
    public static boolean isStreamNotFound(StreamApiVersion version, int status, String body) {
        if (status != notFound(version)) {
            return false;
        }
        return !translatesErrorsToBadRequest(version) || (body != null && body.contains(STREAM_NOT_FOUND_CODE));
    }

    private static boolean translatesErrorsToBadRequest(StreamApiVersion version) {
        return version == StreamApiVersion.V10;
    }
}
