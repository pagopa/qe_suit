package it.pagopa.send.common.user.domain;

/**
 * Utenza generica di test, indipendente dall'ambiente: identifica il "cosa" (una persona fisica, un'impresa, un ente)
 * mentre i dati concreti dell'utenza per l'ambiente attivo sono risolti da {@link UserProvider}.
 */
public enum UserAlias {
    USER_1, USER_2,         // PF
    IMPRESA_1, IMPRESA_2,   // PG
    COMUNE_1, COMUNE_2;     // PA

    /**
     * Alias a partire dalla chiave scritta nelle feature e nei file delle utenze ({@code user-1} → {@link #USER_1}).
     */
    public static UserAlias fromKey(String key) {
        return valueOf(key.trim().toUpperCase().replace('-', '_'));
    }

    /**
     * Tipologia dell'utenza, ricavata dal prefisso del nome: {@code USER_} persona fisica, {@code IMPRESA_} persona
     * giuridica, {@code COMUNE_} pubblica amministrazione.
     */
    public UserType getType() {
        String name = name();
        if (name.startsWith("USER_")) {
            return UserType.PF;
        }
        if (name.startsWith("IMPRESA_")) {
            return UserType.PG;
        }
        if (name.startsWith("COMUNE_")) {
            return UserType.PA;
        }
        throw new IllegalStateException("Prefisso non riconosciuto per l'utenza " + name);
    }
}
