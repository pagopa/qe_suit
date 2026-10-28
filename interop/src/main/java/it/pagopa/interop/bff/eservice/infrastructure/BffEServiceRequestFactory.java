package it.pagopa.interop.bff.eservice.infrastructure;

import it.pagopa.interop.bff.eservice.application.BffEServiceCreationCommand;
import it.pagopa.interop.bff.eservice.application.BffEServiceTemplateInstanceUpdateDescriptorCommand;
import it.pagopa.interop.bff.eservice.application.BffSearchEServiceCatalogCommand;
import it.pagopa.interop.bff.eservice.application.BffUpdateEServiceDescriptorCommand;
import it.pagopa.interop.common.eservice.application.EServiceRequestFactory;
import it.pagopa.interop.common.eservice.application.command.EServiceCreationCommand;
import it.pagopa.interop.common.eservice.application.command.EServiceTemplateInstanceUpdateDescriptorCommand;
import it.pagopa.interop.common.eservice.application.command.SearchEServiceCatalogCommand;
import it.pagopa.interop.common.eservice.application.command.UpdateEServiceDescriptorCommand;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import org.instancio.Instancio;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static it.pagopa.utils.RandomUtils.randomAlphanumericName;
import static org.instancio.Select.field;

@Component
public class BffEServiceRequestFactory implements EServiceRequestFactory {

    public EServiceSeed creationRequest() {
        return defaultEServiceSeed();
    }

    public UpdateEServiceSeed updateRequest() {
        EServiceSeed seed = defaultEServiceSeed();
        return Instancio.of(UpdateEServiceSeed.class)
                .set(field(UpdateEServiceSeed::getName), seed.getName())
                .set(field(UpdateEServiceSeed::getDescription), seed.getDescription())
                .set(field(UpdateEServiceSeed::getTechnology), seed.getTechnology())
                .set(field(UpdateEServiceSeed::getMode), seed.getMode())
                .set(field(UpdateEServiceSeed::getIsSignalHubEnabled), seed.getIsSignalHubEnabled())
                .set(field(UpdateEServiceSeed::getIsConsumerDelegable), seed.getIsConsumerDelegable())
                .set(field(UpdateEServiceSeed::getIsClientAccessDelegable), seed.getIsClientAccessDelegable())
                .set(field(UpdateEServiceSeed::getPersonalData), seed.getPersonalData())
                .set(field(UpdateEServiceSeed::getAsyncExchange), seed.getAsyncExchange())
                .create();
    }

    public EServiceNameUpdateSeed updateNameRequest() {
        return Instancio.of(EServiceNameUpdateSeed.class)
                .set(field(EServiceNameUpdateSeed::getName), randomAlphanumericName("eservice-name", 20))
                .create();
    }

    public EServiceDescriptionUpdateSeed updateDescriptionRequest() {
        return Instancio.of(EServiceDescriptionUpdateSeed.class)
                .set(field(EServiceDescriptionUpdateSeed::getDescription), randomAlphanumericName("description", 40))
                .create();
    }

    public CatalogFilterPayload searchCatalogRequest() {
        CatalogFilterPayload payload = new CatalogFilterPayload();
        payload.setOffset(0);
        payload.setLimit(200);
        payload.setKeyword("catalogo");
        payload.setSortBy(EServiceSortBy.NAME_ASC);
        payload.setMode(EServiceMode.DELIVER);
        payload.setProducerCategories(new ArrayList<>(List.of(EServiceProducerCategory.COMUNI)));
        payload.setRequesterDelegationRoles(new ArrayList<>(List.of(RequesterDelegationRole.DELEGATE)));
        payload.setOnlyActiveEservices(false);
        payload.setAvailableForRequester(false);
        payload.setSubscribedByRequester(false);
        payload.setHasLinkedPurposeTemplates(false);
        payload.setOnlyTemplateInstances(false);
        payload.setAsyncExchange(false);
        payload.setOnlySignalHubEnabled(false);
        return payload;
    }

    @Override
    public EServiceCreationCommand defaultCreationEServiceCommand() {return BffEServiceCreationCommand.from(defaultEServiceSeed());}

    @Override
    public UpdateEServiceDescriptorCommand defaultUpdateDescriptorCommand() {
        var payload = Instancio.of(UpdateEServiceDescriptorSeed.class)
                .set(field(UpdateEServiceDescriptorSeed::getVoucherLifespan), 60)
                .set(field(UpdateEServiceDescriptorSeed::getDailyCallsTotal), 10)
                .set(field(UpdateEServiceDescriptorSeed::getDailyCallsPerConsumer), 1)
                .set(field(UpdateEServiceDescriptorSeed::getAudience), List.of("QA"))
                .set(field(UpdateEServiceDescriptorSeed::getAgreementApprovalPolicy), AgreementApprovalPolicy.AUTOMATIC)
                .set(field(UpdateEServiceDescriptorSeed::getDescription), "default description")
                .set(field(UpdateEServiceDescriptorSeed::getAttributes),
                        new DescriptorAttributesSeed()
                                .certified(List.of())
                                .declared(List.of())
                                .verified(List.of())
                )
                .ignore(field(UpdateEServiceDescriptorSeed::getAsyncExchangeProperties))
                .create();

        return BffUpdateEServiceDescriptorCommand.from(payload);
    }

    @Override
    public SearchEServiceCatalogCommand defaultSearchEServiceCatalogCommand() {
        return new BffSearchEServiceCatalogCommand().offset(0).limit(200);
    }

    @Override
    public SearchEServiceCatalogCommand searchEServiceCatalogCommandForOffset(
            SearchEServiceCatalogCommand original, Integer offset) {
        if (!(original instanceof BffSearchEServiceCatalogCommand bffCommand)) {
            throw new IllegalArgumentException("Command must be an instance of BffSearchEServiceCatalogCommand");
        }
        BffSearchEServiceCatalogCommand copy = new BffSearchEServiceCatalogCommand();
        copy.offset(offset);
        CatalogFilterPayload source = bffCommand.getBffPayload();
        CatalogFilterPayload target = copy.getBffPayload();
        target.setLimit(source.getLimit());
        target.setKeyword(source.getKeyword());
        target.setSortBy(source.getSortBy());
        target.setOnlyActiveEservices(source.getOnlyActiveEservices());
        target.setAvailableForRequester(source.getAvailableForRequester());
        target.setSubscribedByRequester(source.getSubscribedByRequester());
        target.setOnlyTemplateInstances(source.getOnlyTemplateInstances());
        target.setHasLinkedPurposeTemplates(source.getHasLinkedPurposeTemplates());
        target.setProducerCategories(copyList(source.getProducerCategories()));
        target.setAsyncExchange(source.getAsyncExchange());
        target.setMode(source.getMode());
        target.setOnlySignalHubEnabled(source.getOnlySignalHubEnabled());
        target.setRequesterDelegationRoles(copyList(source.getRequesterDelegationRoles()));
        target.setTemplateId(source.getTemplateId());
        target.setProducersIds(copyList(source.getProducersIds()));
        return copy;
    }

    private static <T> List<T> copyList(List<T> values) {
        return values == null ? null : List.copyOf(values);
    }

    @Override
    public UpdateEServiceDescriptorCommand prepareDescriptorForPublicationCommand() {
        return defaultUpdateDescriptorCommand().interfaceDocumentPath("assets/origin-interface.yaml");
    }

    @Override
    public EServiceTemplateInstanceUpdateDescriptorCommand defaultUpdateDescriptorInstanceCommand(it.pagopa.interop.common.eservice.domain.EServiceDescriptorState currentState) {

        BffEServiceTemplateInstanceUpdateDescriptorCommand command = switch (currentState) {
            case PUBLISHED -> {
                var publishedInstancePayload = Instancio.of(UpdateEServiceTemplateInstanceDescriptorQuotas.class)
                        .set(field(UpdateEServiceTemplateInstanceDescriptorQuotas::getDailyCallsTotal), 1000)
                        .set(field(UpdateEServiceTemplateInstanceDescriptorQuotas::getDailyCallsPerConsumer), 100)
                        .set(field(UpdateEServiceTemplateInstanceDescriptorQuotas::getAttributes),
                                new DescriptorAttributesSeed()
                                        .certified(List.of())
                                        .declared(List.of())
                                        .verified(List.of())
                        )
                        .create();
                yield BffEServiceTemplateInstanceUpdateDescriptorCommand.from(publishedInstancePayload);
            }
            case DRAFT -> {
                var draftInstancePayload = Instancio.of(UpdateEServiceDescriptorTemplateInstanceSeed.class)
                        .set(field(UpdateEServiceDescriptorTemplateInstanceSeed::getAudience), List.of("QA"))
                        .set(field(UpdateEServiceDescriptorTemplateInstanceSeed::getAgreementApprovalPolicy), AgreementApprovalPolicy.AUTOMATIC)
                        .set(field(UpdateEServiceDescriptorTemplateInstanceSeed::getDailyCallsTotal), 1000)
                        .set(field(UpdateEServiceDescriptorTemplateInstanceSeed::getDailyCallsPerConsumer), 100)
                        .set(field(UpdateEServiceDescriptorTemplateInstanceSeed::getAttributes),
                                new DescriptorAttributesSeed()
                                        .certified(List.of())
                                        .declared(List.of())
                                        .verified(List.of())
                        )
                        .create();
                yield BffEServiceTemplateInstanceUpdateDescriptorCommand.from(draftInstancePayload);
            }
            default -> throw new IllegalArgumentException("Unsupported state: " + currentState);
        };

        return command;
    }

    private EServiceSeed defaultEServiceSeed() {
        return Instancio.of(EServiceSeed.class)
                .set(field(EServiceSeed::getName), randomAlphanumericName("eservice", 15))
                .set(field(EServiceSeed::getDescription), randomAlphanumericName("description", 20))
                .set(field(EServiceSeed::getTechnology), EServiceTechnology.REST)
                .set(field(EServiceSeed::getMode), EServiceMode.DELIVER)
                .set(field(EServiceSeed::getIsConsumerDelegable), true)
                .set(field(EServiceSeed::getIsClientAccessDelegable), true)
                .set(field(EServiceSeed::getPersonalData), false)
                .set(field(EServiceSeed::getAsyncExchange), false)
                .set(field(EServiceSeed::getIsSignalHubEnabled), false)
                .create();
    }

    @Override
    public boolean supports(@NonNull Channel delimiter) {
        return delimiter == Channel.BFF;
    }
}
