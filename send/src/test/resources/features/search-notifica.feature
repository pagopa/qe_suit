@searchNotification
Feature: Ricerca nella sezione notifiche
    
    Background: 
    Given l'utente è un "admin" di "Comune di Verona"
    When naviga alla pagina Dashboard

    Scenario: [SEARCH_NOTIFICATION_IUN] L'utente mittente effettua la ricerca di una notifica tramite IUN e TAX_CODE
        Given leggo la prima notifica in lista
        When filtra per IUN dalla notifica letta
        Then i risultati contengono l'IUN della notifica letta
    
    Scenario: [SEARCH_NOTIFICATION_TAX_CODE] L'utente mittente effettua la ricerca di una notifica tramite TAX_CODE
        Given leggo la prima notifica in lista
        When filtra per TAX_CODE dalla notifica letta
        Then i risultati contengono il TAX_CODE della notifica letta

    Scenario: [SEARCH_NOTIFICATION_NEGATIVE_IUN] L'utente mittente effettua la ricerca di una notifica tramite IUN e TAX_CODE non esistenti
        When filtra per IUN con valore "XXXX-XXXX-XXXX-000000-X-0"
        Then la lista notifiche è vuota

    Scenario: [SEARCH_NOTIFICATION_NEGATIVE_TAX_CODE] L'utente mittente effettua la ricerca di una notifica tramite IUN e TAX_CODE non esistenti
        When filtra per TAX_CODE con valore "XXXXXX00A00X000Z"
        Then la lista notifiche è vuota

    Scenario: [SEARCH_DATE] Ricerca per arco temporale valido restituisce notifiche nel periodo
        Given imposta arco temporale dall'ultimo anno
        And clicca su Filtra
        Then tutti i risultati visibili hanno data compresa nell'arco temporale impostato
        
    @searchDateInvalid
    Scenario: [SEARCH_DATE_INVALID] Ricerca per arco temporale con fine antecedente all'inizio disabilita il filtro
        When imposta arco temporale da "01/03/2025" a "01/02/2025"
        Then il bottone Filtra è disabilitato

    Scenario: [SEARCH_STATUS] Ricerca per stato restituisce solo notifiche con quello stato
        Given leggo la prima notifica in lista
        When filtra per stato della notifica letta
        Then tutti i risultati visibili hanno stato della notifica letta