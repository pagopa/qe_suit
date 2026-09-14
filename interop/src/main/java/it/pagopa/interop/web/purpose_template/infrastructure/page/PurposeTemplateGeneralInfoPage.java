package it.pagopa.interop.web.purpose_template.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.web.domain.Page;

/**
 * Page Object per lo step "Informazioni Generali" del wizard di creazione
 * di un Template di Finalità, visualizzato dopo la conferma della creazione
 * dalla {@link it.pagopa.interop.web.purpose_template.infrastructure.page.PurposeTemplateCatalogPage}.
 * <p>
 */
@Url("${interop.web.template-purpose}/${templatePurposeId}/modifica")
public interface PurposeTemplateGeneralInfoPage extends Page {

}

