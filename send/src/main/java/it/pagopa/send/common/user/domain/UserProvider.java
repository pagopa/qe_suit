package it.pagopa.send.common.user.domain;

/**
 * Porta di risoluzione delle utenze generiche: restituisce i dati concreti dell'utenza {@link UserAlias} per l'ambiente
 * attivo. Il dominio non conosce gli ambienti: la scelta dell'utenza concreta spetta all'implementazione
 * infrastrutturale.
 */
public interface UserProvider {
    User resolve(UserAlias alias);
}
