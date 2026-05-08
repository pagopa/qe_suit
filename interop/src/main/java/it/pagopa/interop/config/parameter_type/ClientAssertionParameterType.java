package it.pagopa.interop.config.parameter_type;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.DataTableType;
import io.cucumber.java.ParameterType;
import it.pagopa.interop.config.parameter_type.mapper.ClientAssertionValidationResultMapper;
import it.pagopa.interop.domain.context.ClientAssertionContext;
import it.pagopa.interop.domain.model.ClientAssertion;
import it.pagopa.interop.domain.model.ClientAssertionValidationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ClientAssertionParameterType {

    private final ClientAssertionContext clientAssertionContext;

    @ParameterType("client assertion|client assertion creata")
    public ClientAssertion currentClientAssertion(String token) {
        return clientAssertionContext.getLast();
    }

    @DataTableType
    public static ClientAssertionValidationResult fromDataTable(DataTable dataTable) {
        return ClientAssertionValidationResultMapper.fromDataTable(dataTable);
    }
}
