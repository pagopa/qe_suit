package it.pagopa.send.common.delegation.application;

import java.time.LocalDate;

/**
 * Dati per creare una delega, indipendenti dal canale.
 *
 * @param verificationCode codice di 5 cifre scelto dal delegante, che il delegato ripete per accettare la delega
 * @param dateTo           ultimo giorno di validità della delega
 */
public record DelegationCreationCommand(String verificationCode, LocalDate dateTo) {
}
