@channel:Given=WEB,When=WEB,Then=WEB
Feature: Login pagoPA demo

Scenario: Login pagoPA mittente
  Given la PA Comune_1 effettua l'accesso a SelfCare con autenticazione SPID
  When l'utente accede alla Dashboard selezionando "Comune di Palermo"

