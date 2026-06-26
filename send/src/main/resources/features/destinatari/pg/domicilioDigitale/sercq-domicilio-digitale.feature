Feature: Abilitazione Domicilio Digitale (SERCQ) per persone giuridiche
   In qualità di un utente di una PG
   voglio ricevere le notifiche al mio domicilio digitale
   così posso visualizzarle direttamente sulla piattaforma senza doverle scaricare

  @sercqDomicilioDigitale @removeCourtesyAddress
  Scenario: [SERCQ_DOMICILIO_DIGITALE_1] Abilitazione Domicilio Digitale per SERCQ
    Given la PG FrancescoPetrarca effettua l'accesso a SelfCare con autenticazione SPID
    And se presente, viene saltata la configurazione del prodotto SEND
    Then la pagina Notifications è caricata con successo
    When naviga alla pagina Address
    Then la pagina deve caricarsi correttamente
    And se presente viene rimosso l'indirizzo di cortesia
    And viene abilitato il domicilio digitale SERCQ utilizzando la seguente email: "provaemail@test.it"
    Then viene disabilitato il domicilio digitale SERCQ
