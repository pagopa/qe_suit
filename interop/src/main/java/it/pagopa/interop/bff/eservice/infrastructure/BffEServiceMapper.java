package it.pagopa.interop.new_arch.bff.eservice.infrastructure;

import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceDoc;
import it.pagopa.interop.generated.openapi.clients.bff.model.ProducerEServiceDetails;
import it.pagopa.interop.new_arch.bff.infrastructure.mapping.BffCommonMapper;
import it.pagopa.interop.new_arch.common.eservice.domain.EService;
import it.pagopa.interop.new_arch.common.infrastructure.config.TestMapperConfig;
import it.pagopa.interop.new_arch.common.infrastructure.mapping.SharedMapperUtils;
import it.pagopa.interop.new_arch.common.kernel.domain.DocumentRef;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(
        config = TestMapperConfig.class,
        uses = {SharedMapperUtils.class, BffCommonMapper.class}
)
public interface BffEServiceMapper {

    DocumentRef toDocumentRef(EServiceDoc source);

    default EService toEServicePreservingDescriptors(ProducerEServiceDetails source, EService existingEService) {
        if (source == null) {
            return existingEService;
        }

        // 1. Mappa i dati freschi dal BFF (i campi 'producerId' e 'descriptors' saranno null qui)
        EService mapped = toEService(source);

        // 2. Se non c'era uno stato precedente nel test, restituiamo direttamente l'oggetto mappato
        if (existingEService == null) {
            return mapped;
        }

        // 3. Uniamo le due informazioni: prendiamo la base aggiornata dal BFF e ripristiniamo
        // lo stato dei descrittori e del producerId dal vecchio EService del test
        return mapped.toBuilder()
                .producerId(existingEService.getProducerId())
                .descriptors(existingEService.getDescriptors() != null ? List.copyOf(existingEService.getDescriptors()) : List.of())
                .build();
    }

    @Mapping(target = "producerId", ignore = true)
    @Mapping(target = "descriptors", ignore = true)
    EService toEService(ProducerEServiceDetails producerEServiceDetails);
}