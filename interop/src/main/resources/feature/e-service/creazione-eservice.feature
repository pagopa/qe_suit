@eservice
@channel:Given=BFF,When=BFF,Then=BFF
Feature: Creazione di un e-service sulla piattaforma INTEROP
  Background: un utente autenticato via SPID con ruolo di amministratore
#Scenario Outline: [ESERVICE_DATAPROVIDER_CREATION] - Creazione di un e-service che eroga dati
#  Given  il Comune di Milano intraprende la creazione di un nuovo e-service e inserisce i seguenti dettagli:
#
#    |Field                | Value
#    |Nome                 | Nome dell'eservice di test
#    |Descrizione          | Descrizione dell'eservice di test
#    |Erogatore/Ricevitore | Erogatore
#
#  And seleziona la modalità di scambio dei dati "<mode>"
#  And seleziona la tecnologia dell'API "<tech>"
#  And seleziona la gestione dei dati personali "<personal>"
#  Then l'utente completa la creazione dell'e-service
#  Examples:
#    | mode      | tech  | personal
#    | sincrono  | REST  | si
#    | asincrono | REST  | no
##    | sincrono  | REST  | no
##    | asincrono | REST  | no
##    | sincrono  | SOAP  | no
#    #   | asincrono | SOAP  | no
##    | sincrono  | SOAP  | si
##    | asincrono | SOAP  | si

  Scenario Outline: [ESERVICE_DATAPROVIDER_CREATION] - Creazione di un e-service che eroga dati
    Given  il Comune di Milano che intraprende la creazione di un nuovo e-service erogatore
    And seleziona la modalità di scambio dei dati "<mode>" per l'e-service
    And seleziona la tecnologia dell'API "<tech>"
    And seleziona la gestione dei dati personali "<personal>"
    Then l'utente completa la creazione dell'e-service
    Examples:
      | mode      | tech  | personal
      | sincrono  | REST  | si
      | asincrono | REST  | no