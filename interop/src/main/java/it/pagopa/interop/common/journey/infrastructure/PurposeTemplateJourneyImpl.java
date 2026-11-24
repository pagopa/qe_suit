package it.pagopa.interop.common.journey.infrastructure;

import static org.instancio.internal.util.StringUtils.isBlank;

import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.journey.application.PurposeTemplateJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.purpose_template.application.PurposeTemplateUseCase;
import it.pagopa.interop.common.purpose_template.domain.PurposeTemplate;
import it.pagopa.interop.common.purpose_template.domain.PurposeTemplateState;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PurposeTemplateJourneyImpl implements PurposeTemplateJourney<PurposeTemplateJourneyImpl> {

    private final PurposeTemplateUseCase purposeTemplateUseCase;
    private final EntityStore entityStore;

    @Override
    public PurposeTemplateJourneyImpl createPurposeTemplate(Tenant targetTenant, PurposeTemplateState state) {
        return createPurposeTemplate(targetTenant, state, null);
    }

    @Override
    public PurposeTemplateJourneyImpl createPurposeTemplate(
            Tenant targetTenant,
            PurposeTemplateState state,
            @Nullable String alias
    ) {
        PurposeTemplate draft = purposeTemplateUseCase.createPurposeTemplate(targetTenant);
        registerAliasIfPresent(alias, draft);

        switch (state) {
            case DRAFT -> {
                // Already in DRAFT
            }
            case PUBLISHED -> purposeTemplateUseCase.publishPurposeTemplate(draft);
            case SUSPENDED -> {
                PurposeTemplate published = purposeTemplateUseCase.publishPurposeTemplate(draft);
                purposeTemplateUseCase.suspendPurposeTemplate(published);
            }
            case ARCHIVED -> {
                PurposeTemplate published = purposeTemplateUseCase.publishPurposeTemplate(draft);
                purposeTemplateUseCase.archivePurposeTemplate(published);
            }
            default -> throw new UnsupportedOperationException(
                    String.format("La transizione allo stato %s non ancora supportata nel Journey.", state)
            );
        }

        return this;
    }

    private void registerAliasIfPresent(@Nullable String alias, PurposeTemplate purposeTemplate) {
        if (isBlank(alias)) {
            return;
        }
        PurposeTemplate stored = entityStore.getById(purposeTemplate.getId(), PurposeTemplate.class)
                .orElse(purposeTemplate);
        entityStore.upsert(alias, stored);
    }

    @Override
    public PurposeTemplateJourneyImpl linkEServiceToPurposeTemplate(EService eService) {
        PurposeTemplate purposeTemplate = entityStore.getLastOrThrow(PurposeTemplate.class);
        purposeTemplateUseCase.linkEService(purposeTemplate, eService);
        return this;
    }

    @Override
    public PurposeTemplateJourneyImpl addAnnotationToPurposeTemplateRiskAnalysisAnswer() {
        PurposeTemplate purposeTemplate = entityStore.getLastOrThrow(PurposeTemplate.class);
        purposeTemplateUseCase.addRiskAnalysisAnswerAnnotation(purposeTemplate);
        return this;
    }
}
