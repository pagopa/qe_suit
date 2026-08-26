package it.pagopa.send.utils.factory;

import it.pagopa.send.model.LegalNotificationType;
import it.pagopa.send.model.NotificationDefaults;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Carica i default statici di un {@link LegalNotificationType} da
 * {@code notifications/templates/<nome-tipo>.yaml}. Aggiungere un tipo di notifica con una base di
 * default diversa richiede solo un nuovo file YAML e un nuovo case in {@link #templateFileName};
 * non tocca {@link LegalNotificationRequestFactory}.
 */
@Component
public class NotificationDefaultsLoader {

    private static final String TEMPLATE_PATH = "notifications/templates/%s.yaml";

    @SuppressWarnings("unchecked")
    public NotificationDefaults load(LegalNotificationType type) {
        String path = String.format(TEMPLATE_PATH, templateFileName(type));

        try (InputStream is = new ClassPathResource(path).getInputStream()) {
            Map<String, Object> root = new Yaml().load(is);
            Map<String, Object> address = (Map<String, Object>) root.get("physicalAddress");

            return new NotificationDefaults(
                    (String) root.get("subject"),
                    (String) root.get("abstractText"),
                    (String) root.get("taxonomyCode"),
                    (String) root.get("notificationFeePolicy"),
                    (String) root.get("physicalCommunicationType"),
                    (Integer) root.get("paFee"),
                    (Integer) root.get("vat"),
                    (Integer) root.get("physicalCommunicationPriority"),
                    (String) root.get("pagoPaIntMode"),
                    new NotificationDefaults.PhysicalAddressDefaults(
                            (String) address.get("at"),
                            (String) address.get("address"),
                            (String) address.get("addressDetails"),
                            (String) address.get("zip"),
                            (String) address.get("municipality"),
                            (String) address.get("municipalityDetails"),
                            (String) address.get("province"),
                            (String) address.get("foreignState")
                    )
            );
        } catch (IOException e) {
            throw new RuntimeException("Impossibile caricare il template di default per " + type, e);
        }
    }

    private String templateFileName(LegalNotificationType type) {
        return switch (type) {
            case SIMPLE -> "simple";
            case SINGLE_RECIPIENT_WITH_PAGOPA_PAYMENT -> "single-recipient-pagopa";
            case SINGLE_RECIPIENT_WITH_F24_PAYMENT -> "single-recipient-f24";
        };
    }
}
