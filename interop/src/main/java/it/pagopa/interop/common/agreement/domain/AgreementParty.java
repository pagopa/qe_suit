package it.pagopa.interop.common.agreement.domain;

import lombok.Getter;

@Getter
public enum AgreementParty {
    PRODUCER("erogatore"),
    CONSUMER("fruitore");

    private final String party;

    AgreementParty(String party) {
        this.party = party;
    }

    public static AgreementParty fromName(String party) {
        // Se il nome inizia con un articolo, lo rimuove
        String[] articles = {"il ", "l'"};
        for (String article : articles) {
            if (party.startsWith(article)) {
                party = party.substring(article.length());
                break;
            }
        }
        for (AgreementParty value : AgreementParty.values()) {
            if (value.getParty().equalsIgnoreCase(party)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Invalid AgreementParty: " + party);
    }
}
