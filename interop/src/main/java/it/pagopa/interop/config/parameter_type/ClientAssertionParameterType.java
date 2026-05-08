package it.pagopa.interop.config.parameter_type;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.DataTableType;
import io.cucumber.java.ParameterType;
import it.pagopa.interop.config.parameter_type.mapper.ClientAssertionValidationResultMapper;
import it.pagopa.interop.domain.context.ClientAssertionContext;
import it.pagopa.interop.domain.model.ClientAssertion;
import it.pagopa.interop.domain.model.ClientAssertionValidationResult;
import it.pagopa.interop.utils.JwtBuilderUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

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

    @DataTableType
    public JwtBuilderUtils.JwtClaimOverride jwtBuilder(Map<String, String> row) {
        String claim = row.get("claim");
        String value = row.get("value");

        if (claim == null || claim.isBlank()) {
            throw new IllegalArgumentException("Il campo 'claim' è obbligatorio");
        }

        // value può essere vuoto/null: utile per simulare claim mancanti o header non valorizzati
        return new JwtBuilderUtils.JwtClaimOverride(claim.trim(), value);
    }
}
