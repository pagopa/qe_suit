@searchNotification
Feature: Ricerca nella sezione notifiche
    
    Background: 
        Given l'utente Grossini effettua l'accesso a SelfCare con autenticazione SPID
        When l'utente accede alla Dashboard selezionando "Comune di Palermo"
        And l'utente accede alla area riservata e seleziona il prodotto SEND

   # Scenario: [SEARCH_NOTIFICATION] L'utente mittente effettua la ricerca di una notifica tramite IUN e TAX_CODE
   #     When filtra per IUN con valore "YKGK-KEHU-AZTZ-202604-W-1"
   #     When filtra per TAX_CODE con valore "CLMCST42R12D969Z"


    Scenario: [SEARCH_NOTIFICATION] L'utente mittente effettua la ricerca di una notifica tramite IUN e TAX_CODE
        Given leggo la prima notifica in lista
        When filtra per IUN dalla notifica letta
        Then i risultati contengono l'IUN della notifica letta
        When filtra per TAX_CODE dalla notifica letta
        Then i risultati contengono il TAX_CODE della notifica letta