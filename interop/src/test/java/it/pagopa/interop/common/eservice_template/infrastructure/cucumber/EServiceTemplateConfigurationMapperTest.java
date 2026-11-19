package it.pagopa.interop.common.eservice_template.infrastructure.cucumber;

import it.pagopa.interop.bff.eservice_template.application.BffEServiceTemplateCreationCommand;
import it.pagopa.interop.bff.eservice_template.application.BffEServiceTemplateInstanceCreationCommand;
import it.pagopa.interop.bff.eservice_template.infrastructure.BffEServiceTemplateRequestFactory;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplatePreparationConfiguration;
import it.pagopa.interop.common.eservice_template.infrastructure.cucumber.dto.EServiceTemplateInstanceConfiguration;
import it.pagopa.interop.common.eservice_template.infrastructure.cucumber.dto.EServiceTemplatePublicationConfiguration;
import it.pagopa.interop.common.eservice_template.infrastructure.cucumber.dto.mapper.EServiceTemplateConfigurationMapper;
import it.pagopa.interop.common.infrastructure.cucumber.DataTableMapper;
import it.pagopa.interop.common.kernel.domain.Tenant;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mapstruct.factory.Mappers;

import java.nio.file.Path;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

class EServiceTemplateConfigurationMapperTest {
    private static final ValidatorFactory VALIDATORS = Validation.buildDefaultValidatorFactory();
    private final DataTableMapper tables = new DataTableMapper(VALIDATORS.getValidator());
    private final EServiceTemplateConfigurationMapper mapper = Mappers.getMapper(EServiceTemplateConfigurationMapper.class);
    private final BffEServiceTemplateRequestFactory factory = new BffEServiceTemplateRequestFactory();

    @AfterAll
    static void closeValidators() {
        VALIDATORS.close();
    }

    @AfterEach
    void clearKeys() {
        DataTableMapper.clearGherkinKeys();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "soap11-un-indirizzo.wsdl", "soap12-un-indirizzo.wsdl",
            "soap11-indirizzo-commentato-prima.wsdl", "soap11-indirizzo-commentato-dopo.wsdl",
            "soap12-indirizzo-commentato-prima.wsdl", "soap12-indirizzo-commentato-dopo.wsdl",
            "soap11-prefisso-alternativo.wsdl", "soap12-prefisso-alternativo.wsdl",
            "soap11-due-indirizzi.wsdl", "soap12-due-indirizzi.wsdl", "soap11-soap12-due-indirizzi.wsdl",
            "soap11-due-indirizzi-originali-uguali.wsdl", "soap12-due-indirizzi-originali-uguali.wsdl",
            "openapi3-riferimenti.yaml", "openapi3-riferimenti.json"
    })
    void mapsExactInterfaceConservationFixtureWithoutReplacingIt(String fixture) {
        var dto = (EServiceTemplatePreparationConfiguration) tables.transform(Map.of(
                "tecnologia", fixture.endsWith(".wsdl") ? "SOAP" : "REST",
                "stato", "PUBLISHED", "interfaccia", fixture), EServiceTemplatePreparationConfiguration.class);
        var command = (BffEServiceTemplateCreationCommand) factory.defaultCreationEServiceTemplateCommand();
        mapper.apply(dto, command);

        assertThat(command.getInterfaceDocumentPath()).isEqualTo("assets/eservice-template/" + fixture);
        assertThat(Path.of("src/main/resources", command.getInterfaceDocumentPath())).exists();
        assertThat(command.getBffCreationPayload().getTechnology().name()).isEqualTo(dto.getTechnology().name());
    }

    @Test
    void publicationDefaultsCreatorToMilanoAndKeepsKeywordInRandomizedName() {
        var dto = (EServiceTemplatePublicationConfiguration) tables.transform(Map.of(
                "alias", "TPL-1", "nome", "Template Stampo Alfa", "tecnologia", "SOAP"),
                EServiceTemplatePublicationConfiguration.class);
        var command = (BffEServiceTemplateCreationCommand) factory.defaultCreationEServiceTemplateCommand();
        mapper.apply(dto, command);

        assertThat(dto.getProducer()).isEqualTo(Tenant.COMUNE_DI_MILANO);
        assertThat(dto.getAlias()).isEqualTo("TPL-1");
        assertThat(command.getBffCreationPayload().getName()).contains("Template Stampo Alfa")
                .isNotEqualTo("Template Stampo Alfa");
        assertThat(command.getBffCreationPayload().getTechnology().name()).isEqualTo("SOAP");
    }

    @Test
    void suffixMapsToSupportedInstanceLabelAndOmissionPreservesUniqueFactoryLabel() {
        var command = (BffEServiceTemplateInstanceCreationCommand) factory.defaultCreationEServiceTemplateInstanceCommand();
        String originalLabel = command.getBffPayload().getInstanceLabel();
        var dto = (EServiceTemplateInstanceConfiguration) tables.transform(Map.of(
                "alias", "ES-1", "template", "TPL-1"), EServiceTemplateInstanceConfiguration.class);
        mapper.apply(dto, command);
        assertThat(command.getBffPayload().getInstanceLabel()).isEqualTo(originalLabel).isNotBlank();

        dto = (EServiceTemplateInstanceConfiguration) tables.transform(Map.of(
                "alias", "ES-2", "template", "TPL-1", "suffisso", "eservice-2"), EServiceTemplateInstanceConfiguration.class);
        mapper.apply(dto, command);
        assertThat(command.getBffPayload().getInstanceLabel()).contains("eservice-2").isNotEqualTo(originalLabel);
    }

    @Test
    void rejectsMissingBlankAndUnknownInputAtDefaultTransformerBoundary() {
        assertThatThrownBy(() -> tables.transform(Map.of("nome", "Template", "tecnologia", "SOAP"),
                EServiceTemplatePublicationConfiguration.class)).isInstanceOf(ConstraintViolationException.class);
        assertThatThrownBy(() -> tables.transform(Map.of("alias", "ES-1", "template", " "),
                EServiceTemplateInstanceConfiguration.class)).isInstanceOf(ConstraintViolationException.class);
        assertThatThrownBy(() -> tables.transform(Map.of("alias", "ES-1", "template", "TPL-1", "suffisso", " "),
                EServiceTemplateInstanceConfiguration.class)).isInstanceOf(ConstraintViolationException.class);
        assertThatThrownBy(() -> tables.transform(Map.of("tecnologia", "SOAP", "stato", "PUBLISHED"),
                EServiceTemplatePreparationConfiguration.class)).isInstanceOf(ConstraintViolationException.class);
        assertThatThrownBy(() -> tables.transform(Map.of("alias", "ES-1", "template", "TPL-1", "unsupported", "x"),
                EServiceTemplateInstanceConfiguration.class)).isInstanceOf(IllegalArgumentException.class);
    }
}
