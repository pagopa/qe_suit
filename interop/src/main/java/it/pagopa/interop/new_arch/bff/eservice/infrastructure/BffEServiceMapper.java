package it.pagopa.interop.new_arch.bff.eservice.infrastructure;


import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import it.pagopa.interop.new_arch.common.attribute.domain.Attributes;
import it.pagopa.interop.new_arch.common.eservice.domain.EService;
import it.pagopa.interop.new_arch.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.new_arch.common.infrastructure.config.TestMapperConfig;
import it.pagopa.interop.new_arch.common.infrastructure.utils.SharedMapperUtils;
import it.pagopa.interop.new_arch.common.kernel.domain.DelegationRef;
import it.pagopa.interop.new_arch.common.kernel.domain.DelegationTenantRef;
import it.pagopa.interop.new_arch.common.kernel.domain.DocumentRef;
import org.mapstruct.*;

import java.util.ArrayList;
import java.util.List;

@Mapper(config = TestMapperConfig.class, uses = {SharedMapperUtils.class})
public interface BffEServiceMapper {

    /**
     * Prende il nuovo descrittore dal BFF e l'EService attualmente salvato nel contesto del test.
     * Se il descrittore esiste già (stesso ID), lo aggiorna. Se non esiste, lo aggiunge alla lista.
     */
    default EService toDomainWithUpsert(ProducerEServiceDescriptor source, EService existingEService) {
        if (source == null) return existingEService;

        // 1. Mappiamo i dati generali aggiornati dell'E-Service dal payload del BFF
        EService updatedBase = toDomainBase(source);

        // 2. Convertiamo la sorgente nel descrittore di dominio custom
        EServiceDescriptor newDescriptor = toEServiceDescriptor(source);

        // 3. Prepariamo la nuova lista unificata dei descrittori
        List<EServiceDescriptor> finalDescriptors = new ArrayList<>();

        if (existingEService != null && existingEService.getDescriptors() != null) {
            boolean idTrovato = false;

            for (EServiceDescriptor currentDescriptor : existingEService.getDescriptors()) {
                if (currentDescriptor.getId().equals(newDescriptor.getId())) {
                    // MODIFICA: L'ID esiste già, inseriamo il descrittore aggiornato
                    finalDescriptors.add(newDescriptor);
                    idTrovato = true;
                } else {
                    // Mantieni invariati i descrittori delle altre versioni
                    finalDescriptors.add(currentDescriptor);
                }
            }

            // AGGIUNTA: Se l'ID non è stato trovato in tutta la lista, lo appendiamo alla fine
            if (!idTrovato) {
                finalDescriptors.add(newDescriptor);
            }
        } else {
            // Se non c'era uno stato precedente nel test, iniziamo la lista con questo descrittore
            finalDescriptors.add(newDescriptor);
        }

        // 4. Restituiamo l'oggetto EService finale usando il toBuilder() di Lombok
        return updatedBase.toBuilder()
                .descriptors(List.copyOf(finalDescriptors)) // Crea una lista immutabile
                .build();
    }

    /**
     * Mappa i dati generali dell'E-Service.
     * La lista 'descriptors' viene ignorata qui perché viene gestita sopra nel metodo smart.
     */
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

    // ==========================================
    // MAPPATURA DEL SINGOLO DESCRITTORE
    // ==========================================
    @Mapping(target = "interfaceDocument", source = "interface") // Corretto senza underscore
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

    it.pagopa.interop.new_arch.common.attribute.domain.Attribute toAttribute(DescriptorAttribute source);

    DelegationRef toDelegationRef(it.pagopa.interop.generated.openapi.clients.bff.model.DelegationWithCompactTenants source);

    DelegationTenantRef toDelegationTenantRef(it.pagopa.interop.generated.openapi.clients.bff.model.CompactOrganization source);

    @ValueMapping(source = MappingConstants.ANY_REMAINING, target = MappingConstants.NULL)
    it.pagopa.interop.new_arch.common.kernel.domain.TenantKind toDomainTenantKind(it.pagopa.interop.generated.openapi.clients.bff.model.TenantKind source);

    it.pagopa.interop.new_arch.common.kernel.domain.EServiceTemplateRef toTemplateRef(it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateRef source);

    // ==========================================
    // LOGICA DI FLATTENING ATTRIBUTI + INDICE GRUPPO
    // ==========================================
    @Named("flattenAttributes")
    default List<it.pagopa.interop.new_arch.common.attribute.domain.Attribute> flattenAttributes(List<List<DescriptorAttribute>> nestedList) {
        if (nestedList == null) {
            return List.of();
        }

        List<it.pagopa.interop.new_arch.common.attribute.domain.Attribute> flattenedList = new ArrayList<>();

        for (int i = 0; i < nestedList.size(); i++) {
            List<DescriptorAttribute> innerList = nestedList.get(i);

            if (innerList != null) {
                for (DescriptorAttribute bffAttr : innerList) {
                    if (bffAttr != null) {

                        it.pagopa.interop.new_arch.common.attribute.domain.Attribute domainAttr = this.toAttribute(bffAttr);

                        // Applica l'indice della lista esterna al campo group dell'Attribute di Dominio
                        it.pagopa.interop.new_arch.common.attribute.domain.Attribute attrWithGroup = domainAttr.toBuilder()
                                .group(i)
                                .build();

                        flattenedList.add(attrWithGroup);
                    }
                }
            }
        }

        return flattenedList;
    }

    @Mapping(target = "producerId", ignore = true)
    @Mapping(target = "descriptors", ignore = true)
    EService toEService(ProducerEServiceDetails producerEServiceDetails);
}