package it.pagopa.interop.common.eservice_template.domain;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.cucumber.java.eo.Se;
import it.pagopa.interop.common.eservice.domain.EServiceTechnology;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EServiceTemplatePreparationConfiguration {
    @JsonAlias({"tecnologia"})
    private EServiceTechnology technology;

    @JsonAlias({"stato"})
    private EServiceTemplateVersionState state;

    @JsonAlias({"interfaccia", "fixtureInterfaccia"})
    private String interfaceFixture;
}
