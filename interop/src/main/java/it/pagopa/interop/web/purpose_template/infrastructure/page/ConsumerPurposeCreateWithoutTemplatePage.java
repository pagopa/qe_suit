package it.pagopa.interop.web.purpose_template.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.interop.web.purpose.infrastructure.page.component.consumer_purpose_base.ConsumerPurposeCreateBase;
import org.assertj.core.api.Assertions;


@Url("${interop.web.base-url}/fruizione/finalita/crea")
public interface ConsumerPurposeCreateWithoutTemplatePage extends Page, ConsumerPurposeCreateBase {

    @Override
    default void assertLoaded() {
        Assertions.assertThat(breadcrumbs().items().stream().map(Readable::read).toList())
                .as("Breadcrumbs")
                .containsExactly("Fruizione", "Finalità inoltrate", "Crea finalità");
        pageTitle().readAndAssert("Crea finalità");
    }

}