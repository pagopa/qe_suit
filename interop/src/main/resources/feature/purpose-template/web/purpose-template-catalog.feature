@purpose-template
@channel:Given=WEB,When=WEB,Then=WEB
Feature: Autorizzazione al catalogo dei template finalità
  L'accesso al catalogo è consentito soltanto ai ruoli autorizzati.

  Scenario Outline: [PURPOSE_TEMPLATE_CATALOG_PAGE_AUTH] Accesso diretto al catalogo dei template finalità in base al ruolo
    Given un utente autenticato con ruolo <userRole> di un tenant di tipo PA
    When apre il catalogo dei template finalità tramite URL diretto
    Then l'accesso alla pagine del catalogo dei template finalità è <accessResult>

    Examples:
      | userRole     | accessResult |
      | admin        | consentito   |
      | api,security | consentito   |
      | security     | negato       |
      | support      | consentito   |
      | reviewer     | consentito   |
      | viewer       | consentito   |
      | api          | negato       |

