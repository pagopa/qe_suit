package it.pagopa.interop.common.tenant.domain;

import lombok.Getter;

import java.util.Set;

@Getter
public enum TenantCategory {
    ALTRE_PUBBLICHE_AMMINISTRAZIONI_LOCALI("C3", "C7", "C13", "C14", "L1", "L2", "L10", "L11",
            "L12", "L13", "L16", "L19", "L20", "L21", "L24", "L31", "L34", "L35", "L36",
            "L38", "L39", "L40", "L42", "L44", "L46", "L47"),
    AZIENDE_OSPEDALIERE_ASL("L7", "L8", "L22"),
    COMUNI("L6", "L18"),
    PROVINCE_CITTA_METROPOLITANE("L5", "L45"),
    PUBBLICHE_AMMINISTRAZIONI_CENTRALI("C1", "C2", "C5", "C10", "C11"),
    ENTI_NAZIONALI_PREVIDENZA_ASSISTENZA("C16", "C17"),
    REGIONI_PROVINCE_AUTONOME("L4"),
    CONSORZI_ASSOCIAZIONI_REGIONALI("L4"),
    SCUOLE("L33"),
    UNIVERSITA_AFAM("L15", "L17", "L43"),
    ISTITUTI_RICERCA("C8", "C12", "L28"),
    STAZIONI_APPALTANTI_GESTORI_PUBBLICI_SERVIZI("L37", "S01", "SA", "SAG");

    private final Set<String> ipaCodes;

    TenantCategory(String... ipaCodes) {
        this.ipaCodes = Set.of(ipaCodes);
    }
}
