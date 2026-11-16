package it.pagopa.infrastructure.contract.http;

/**
 * Identita' con cui eseguire un caso di contratto: un nome stabile, usato nel display name del
 * test dinamico e nella diagnostica, piu' l'azione che predispone l'autenticazione.
 * <p>
 * Il nome deve identificare l'utenza (es. l'alias {@code USER_1}), <strong>non</strong> contenere
 * token, password o altre credenziali: finisce nei report JUnit e nei log.
 */
public record HttpContractIdentity(String name, HttpContractAuthentication authentication) {

    public HttpContractIdentity {
        if (name == null || name.isBlank()) {
            throw new ContractHttpException("identity name must not be blank");
        }
        if (authentication == null) {
            throw new ContractHttpException("identity authentication must not be null");
        }
    }
}
