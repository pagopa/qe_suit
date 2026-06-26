Feature: Abilitazione Domicilio Digitale (SERCQ) per persone fisiche
   In qualità di un utente di una PF
   voglio ricevere le notifiche al mio domicilio digitale
   così posso visualizzarle direttamente sulla piattaforma senza doverle scaricare

  @sercqDomicilioDigitale @removeCourtesyAddress
  Scenario: [SERCQ_DOMICILIO_DIGITALE_1] Abilitazione Domicilio Digitale per SERCQ
    Given l'utente Cesare effettua l'accesso a SelfCare con autenticazione SPID
    And se presente, viene saltata la configurazione del prodotto SEND
    Then la pagina NotificationPF è caricata con successo
    When naviga alla pagina AddressPF
    Then la pagina deve caricarsi correttamente
    And se presente viene disabilitato il domicilio digitale SERCQ
    And se presente viene rimosso l'indirizzo di cortesia
    And viene abilitato il domicilio digitale SERCQ utilizzando la seguente email: "provaemail@test.it"
