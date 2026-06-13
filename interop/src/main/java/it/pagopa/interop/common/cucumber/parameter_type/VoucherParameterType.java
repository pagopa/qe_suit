package it.pagopa.interop.common.cucumber.parameter_type;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.DataTableType;
import it.pagopa.interop.common.cucumber.parameter_type.mapper.VoucherRequestValidationResultMapper;
import it.pagopa.interop.common.dev_tools.VoucherRequestValidationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class VoucherParameterType {

    private final VoucherRequestValidationResultMapper voucherRequestValidationResultMapper;

    @DataTableType
    public VoucherRequestValidationResult fromDataTable(DataTable dataTable) {
        return voucherRequestValidationResultMapper.fromDataTable(dataTable);
    }
}
