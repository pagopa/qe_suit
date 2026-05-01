package it.pagopa.send.factory;

import it.pagopa.send.model.NotificationData;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

@Component
public class NotificationFactory {

   private static final String NOTIFICATION_TEMPLATE_FILE = "notifications/simple_notification.yaml";

    @SuppressWarnings("unchecked")
    public NotificationData load(String templateName) {
        Yaml yaml = new Yaml();
        try (InputStream is = new ClassPathResource(NOTIFICATION_TEMPLATE_FILE).getInputStream()) {

            Map<String, Object> root = yaml.load(is);

            Map<String, Object> n = (Map<String, Object>) root.get("notification");
            Map<String, Object> r = (Map<String, Object>) root.get("recipient");
            Map<String, Object> d = (Map<String, Object>) root.get("document");

            NotificationData data = new NotificationData();

            data.setSubject((String) n.get("subject"));
            data.setProtocolNumber(resolve(n.get("protocolNumber")));
            data.setTaxonomyCode((String) n.get("taxonomyCode"));
            data.setGroup((String) n.get("group"));
            data.setLanguage((String) n.get("language"));
            data.setPhysicalCommunicationType((String) n.get("physicalCommunicationType"));
            data.setPaymentType((String) n.get("paymentType"));

            data.setRecipientType((String) r.get("type"));
            data.setTaxId((String) r.get("taxId"));
            data.setFirstName((String) r.get("firstName"));
            data.setLastName((String) r.get("lastName"));
            data.setAddressLookup((String) r.get("addressLookup"));
            data.setPec((String) r.get("pec"));

            data.setDocumentTitle((String) d.get("title"));

            String classpathPath = (String) d.get("filePath");
            URL resource = getClass().getClassLoader().getResource(classpathPath);
            if (resource == null) {
                throw new RuntimeException("Document not found on classpath: " + classpathPath);
            }
            try {
                data.setDocumentFilePath(new File(resource.toURI()).getAbsolutePath());
            } catch (Exception e) {
                throw new RuntimeException("Cannot resolve path for: " + classpathPath, e);
            }

            return data;

        } catch (IOException e) {
            throw new RuntimeException(
                    "Cannot load notification template: " + templateName, e);
        }
    }

    public NotificationData loadWithOverrides(
            String templateName, 
            Map<String, String> overrides) {
        
        NotificationData notification = load(templateName);
        
        overrides.forEach((key, value) -> {
            switch(key) {
                case "pec":
                    notification.setPec((String) value);
                    break;
                case "subject":
                    notification.setSubject((String) value);
                    break;
                // ...
            }
        });
        
        return notification;
    }

    private static String resolve(Object value) {
        return PlaceholderResolver.resolve((String) value);
    }
}
