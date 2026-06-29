package it.pagopa.interop.bff.infrastructure.client;

import it.pagopa.interop.bff.infrastructure.config.TestMapperConfig;
import it.pagopa.interop.common.contract.model.attribute.Attributes;
import it.pagopa.interop.common.contract.model.eservice.EService;
import it.pagopa.interop.common.contract.model.eservice.EServiceDescriptor;
import it.pagopa.interop.common.contract.model.shared.DelegationRef;
import it.pagopa.interop.common.contract.model.shared.DelegationTenantRef;
import it.pagopa.interop.common.contract.model.shared.DocumentRef;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import org.mapstruct.*;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;

@Mapper(config = TestMapperConfig.class)
public interface EServiceMapper {
    EService toDomain(CreatedEServiceDescriptor createdEServiceDescriptor);

    EService toDomain(ProducerEServiceDetails producerEServiceDetails);

    @Mapping(target = "id", source = "eservice.id")
    @Mapping(target = "name", source = "eservice.name")
    @Mapping(target = "producerId", source = "eservice.producer.id")
    @Mapping(target = "technology", source = "eservice.technology")
    @Mapping(target = "mode", source = "eservice.mode")
    @Mapping(target = "description", source = "eservice.description")
    @Mapping(target = "personalData", source = "eservice.personalData")
    @Mapping(target = "asyncExchange", source = "eservice.asyncExchange")
    @Mapping(target = "isSignalHubEnabled", source = "eservice.isSignalHubEnabled")
    @Mapping(target = "isConsumerDelegable", source = "eservice.isConsumerDelegable")
    @Mapping(target = "isClientAccessDelegable", source = "eservice.isClientAccessDelegable")
    EService toDomain(ProducerEServiceDescriptor producerEServiceDescriptor);

    @AfterMapping
    default void aggiungiDescrittoreAllaLista(ProducerEServiceDescriptor source, @MappingTarget EService.EServiceBuilder targetBuilder) {
        EServiceDescriptor descrittoreCustom = toEServiceDescriptor(source);
        targetBuilder.descriptors(List.of(descrittoreCustom));
    }

    @Mapping(target = "interfaceDocument", source = "_interface")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "publishedAt", source = "publishedAt", qualifiedByName = "mapStringToInstant")
    @Mapping(target = "deprecatedAt", source = "deprecatedAt", qualifiedByName = "mapStringToInstant")
    @Mapping(target = "archivedAt", source = "archivedAt", qualifiedByName = "mapStringToInstant")
    @Mapping(target = "suspendedAt", source = "suspendedAt", qualifiedByName = "mapStringToInstant")
    EServiceDescriptor toEServiceDescriptor(ProducerEServiceDescriptor source);

    // ==========================================
    // METODI DI SUPPORTO AUTOMATICI / NESTED
    // ==========================================

    DocumentRef toDocumentRef(EServiceDoc source);

    @Mapping(target = "certified", source = "certified", qualifiedByName = "flattenAttributes")
    @Mapping(target = "declared", source = "declared", qualifiedByName = "flattenAttributes")
    @Mapping(target = "verified", source = "verified", qualifiedByName = "flattenAttributes")
    Attributes toAttributes(DescriptorAttributes source);

    it.pagopa.interop.common.contract.model.attribute.Attribute toAttribute(DescriptorAttribute source);

    DelegationRef toDelegationRef(it.pagopa.interop.generated.openapi.clients.bff.model.DelegationWithCompactTenants source);

    DelegationTenantRef toDelegationTenantRef(it.pagopa.interop.generated.openapi.clients.bff.model.CompactOrganization source);

    it.pagopa.interop.common.contract.model.eservice_template.EServiceTemplateRef toTemplateRef(it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateRef source);

    // ==========================================
    //  LOGICA DI CONVERSIONE STRING -> INSTANT
    // ==========================================
    @Named("mapStringToInstant")
    default Instant mapStringToInstant(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) {
            return null;
        }
        // Usiamo OffsetDateTime perché gestisce le stringhe ISO-8601 con fuso orario generate da OpenAPI
        return OffsetDateTime.parse(dateStr).toInstant();
    }

    /**
     * Metodo di utilità personalizzato per trasformare la List<List<DescriptorAttribute>>
     * in una List<Attribute> piatta usando i Java Stream.
     */
    @Named("flattenAttributes")
    default List<it.pagopa.interop.common.contract.model.attribute.Attribute> flattenAttributes(List<List<DescriptorAttribute>> nestedList) {
        if (nestedList == null) {
            return List.of();
        }

        List<it.pagopa.interop.common.contract.model.attribute.Attribute> flattenedList = new java.util.ArrayList<>();

        for (int i = 0; i < nestedList.size(); i++) {
            List<DescriptorAttribute> innerList = nestedList.get(i);

            if (innerList != null) {
                for (DescriptorAttribute bffAttr : innerList) {
                    if (bffAttr != null) {

                        it.pagopa.interop.common.contract.model.attribute.Attribute domainAttr = this.toAttribute(bffAttr);

                        it.pagopa.interop.common.contract.model.attribute.Attribute attrWithGroup = domainAttr.toBuilder()
                                .group(i)
                                .build();

                        flattenedList.add(attrWithGroup);
                    }
                }
            }
        }

        return flattenedList;
    }

}