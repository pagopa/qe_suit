@purpose-template
@channel:Given=WEB,When=WEB,Then=WEB
Feature: Autorizzazione al catalogo dei template finalità
  L'accesso al catalogo è consentito soltanto ai ruoli autorizzati.

  Scenario Outline: [PURPOSE_TEMPLATE_CATALOG_PAGE_AUTH] Accesso diretto al catalogo dei template finalità in base al ruolo
    Given un utente autenticato con ruolo <userRole> del tenant Comune di Pozzallo
    When apre il catalogo dei template finalità tramite URL diretto
    Then l'accesso alla pagina del catalogo dei template finalità è <accessResult>

    Examples:
      | userRole     | accessResult |
      | admin        | consentito   |
      | api,security | negato       |
      | security     | negato       |
      | support      | consentito   |
      | reviewer     | negato       |
      | viewer       | consentito   |
      | api          | negato       |

  Scenario Outline: [MY_PURPOSE_TEMPLATE_PAGE_AUTH] Accesso diretto ai miei template finalità in base al ruolo
    Given un utente autenticato con ruolo <userRole> del tenant Comune di Pozzallo
    When apre i miei template finalità tramite URL diretto
    Then l'accesso alla pagina dei miei template finalità è <accessResult>

    Examples:
      | userRole     | accessResult |
      | admin        | consentito   |
      | api,security | negato       |
      | security     | negato       |
      | support      | consentito   |
      | reviewer     | negato       |
      | viewer       | consentito   |
      | api          | negato       |
