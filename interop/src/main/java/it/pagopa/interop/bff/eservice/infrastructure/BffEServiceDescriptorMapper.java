package it.pagopa.interop.bff.eservice.infrastructure;


import it.pagopa.interop.common.infrastructure.SharedMapper;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import it.pagopa.interop.bff.infrastructure.mapping.BffCommonMapper;
import it.pagopa.interop.common.attribute.domain.Attributes;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.infrastructure.config.TestMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Mapper(
        config = TestMapperConfig.class,
        uses = {SharedMapper.class, BffCommonMapper.class}
)
public interface BffEServiceDescriptorMapper {

    @Mapping(target = "interfaceDocument", source = "interface")
    @Mapping(target = "publishedAt", source = "publishedAt", qualifiedByName = "mapStringToInstant")
    @Mapping(target = "deprecatedAt", source = "deprecatedAt", qualifiedByName = "mapStringToInstant")
    @Mapping(target = "archivedAt", source = "archivedAt", qualifiedByName = "mapStringToInstant")
    @Mapping(target = "suspendedAt", source = "suspendedAt", qualifiedByName = "mapStringToInstant")
    @Mapping(target = "serverUrls", source = "serverUrls", qualifiedByName = "mapServerUrls")
    EServiceDescriptor toEServiceDescriptor(ProducerEServiceDescriptor source);

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
    @Mapping(target = "descriptors", ignore = true)
    EService toDomainBase(ProducerEServiceDescriptor source);

    /**
     * Prende il nuovo descrittore dal BFF e l'EService attualmente salvato nel contesto del test.
     * Se il descrittore esiste già (stesso ID), lo aggiorna. Se non esiste, lo aggiunge alla lista.
     */
    default EService toEServiceWithUpsert(ProducerEServiceDescriptor source, EService existingEService) {
        if (source == null) return existingEService;

        // 1. Mappiamo i dati generali aggiornati dell'E-Service dal payload del BFF
        EService updatedBase = toDomainBase(source);

        // 2. Convertiamo la sorgente nel descrittore di dominio custom (Generato da MapStruct)
        EServiceDescriptor newDescriptor = toEServiceDescriptor(source);

        // 3. Prepariamo la nuova lista unificata dei descrittori
        List<EServiceDescriptor> finalDescriptors = new ArrayList<>();

        if (existingEService != null && existingEService.getDescriptors() != null) {
            boolean idTrovato = false;

            for (EServiceDescriptor currentDescriptor : existingEService.getDescriptors()) {
                if (currentDescriptor.getId().equals(newDescriptor.getId())) {
                    finalDescriptors.add(newDescriptor);
                    idTrovato = true;
                } else {
                    finalDescriptors.add(currentDescriptor);
                }
            }

            if (!idTrovato) {
                finalDescriptors.add(newDescriptor);
            }
        } else {
            finalDescriptors.add(newDescriptor);
        }

        return updatedBase.toBuilder()
                .descriptors(new ArrayList<>(finalDescriptors))
                .build();
    }

    @Named("mapServerUrls")
    default List<String> mapServerUrls(Collection<? extends ProducerEServiceDescriptorServerUrlsInner> serverUrls) {
        if (serverUrls == null) {
            return List.of();
        }
        return serverUrls.stream()
                .map(ProducerEServiceDescriptorServerUrlsInner::getUrl)
                .toList();
    }

    @Mapping(target = "certified", source = "certified", qualifiedByName = "flattenAttributes")
    @Mapping(target = "declared", source = "declared", qualifiedByName = "flattenAttributes")
    @Mapping(target = "verified", source = "verified", qualifiedByName = "flattenAttributes")
    Attributes toAttributes(DescriptorAttributes source);

    it.pagopa.interop.common.attribute.domain.Attribute toAttribute(DescriptorAttribute source);

    @Named("flattenAttributes")
    default List<it.pagopa.interop.common.attribute.domain.Attribute> flattenAttributes(List<List<DescriptorAttribute>> nestedList) {
        if (nestedList == null) {
            return List.of();
        }

        List<it.pagopa.interop.common.attribute.domain.Attribute> flattenedList = new ArrayList<>();

        for (int i = 0; i < nestedList.size(); i++) {
            List<DescriptorAttribute> innerList = nestedList.get(i);

            if (innerList != null) {
                for (DescriptorAttribute bffAttr : innerList) {
                    if (bffAttr != null) {

                        it.pagopa.interop.common.attribute.domain.Attribute domainAttr = this.toAttribute(bffAttr);

                        // Applica l'indice della lista esterna al campo group dell'Attribute di Dominio
                        it.pagopa.interop.common.attribute.domain.Attribute attrWithGroup = domainAttr.toBuilder()
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