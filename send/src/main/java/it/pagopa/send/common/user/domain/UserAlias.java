package it.pagopa.send.common.user.domain;

import java.util.Locale;

/**
 * Utenza generica di test, indipendente dall'ambiente: identifica il "cosa" (una persona fisica, un'impresa, un ente)
 * mentre i dati concreti dell'utenza per l'ambiente attivo sono risolti da {@link UserProvider}.
 */
public enum UserAlias {
    USER_1, USER_2,         // PF
    IMPRESA_1, IMPRESA_2,   // PG
    COMUNE_1, COMUNE_2;     // PA

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

    /**
     * Chiave dell'utenza nella configurazione: il nome in minuscolo e in kebab-case ({@code USER_1} → {@code user-1}).
     */
    public String key() {
        // Locale.ROOT: la chiave non deve dipendere dalla lingua della JVM (con il turco IMPRESA_1 → "ımpresa-1")
        return name().toLowerCase(Locale.ROOT).replace('_', '-');
    }

    /**
     * Utenza a partire dalla sua chiave, come scritta nelle feature e nella configurazione ({@code user-1} →
     * {@code USER_1}).
     */
    public static UserAlias fromKey(String key) {
        for (UserAlias alias : values()) {
            if (alias.key().equals(key)) {
                return alias;
            }
        }
        throw new IllegalArgumentException("Utenza generica non riconosciuta: " + key);
    }
}
