package it.pagopa.send.service;

import it.pagopa.send.generated.openapi.clients.gpd.api.DebtPositionsApiApi;
import it.pagopa.send.generated.openapi.clients.gpd.model.PaymentOptionModel;
import it.pagopa.send.generated.openapi.clients.gpd.model.PaymentPositionModel;
import it.pagopa.send.generated.openapi.clients.gpd.model.TransferModel;
import it.pagopa.send.utils.RandomNumericGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class DebtPositionService {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    private final DebtPositionsApiApi debtPositionsApiApi;

    /**
     * Crea una posizione debitoria su GPD per l'ente creditore indicato e restituisce lo IUV
     * generato. Il chiamante che deve costruire un noticeCode pagoPA deve prefissare "3"
     * (convenzione mutuata da pn-b2b-client).
     */
    public String createDebtPosition(String organizationFiscalCode, String debtorFullName, String debtorTaxId, long amount) {
        String iuv = generateRandomIuv();
        String iupd = organizationFiscalCode + "-64c8e41bfec846e04" + iuv + System.currentTimeMillis();

        PaymentPositionModel request = new PaymentPositionModel()
                .iupd(iupd)
                .type(PaymentPositionModel.TypeEnum.F)
                .companyName("Automation")
                .fullName(debtorFullName)
                .fiscalCode(debtorTaxId)
                .addPaymentOptionItem(new PaymentOptionModel()
                        .iuv(iuv)
                        .amount(amount)
                        .description("Test Automation")
                        .isPartialPayment(false)
                        .dueDate(DATE_FORMAT.format(OffsetDateTime.now().plusDays(2)))
                        .retentionDate(DATE_FORMAT.format(OffsetDateTime.now().plusDays(2)))
                        .addTransferItem(new TransferModel()
                                .idTransfer(TransferModel.IdTransferEnum._1)
                                .organizationFiscalCode(organizationFiscalCode)
                                .amount(amount)
                                .remittanceInformation("Test Automation")
                                .category("9/0301100TS/")
                                .iban("IT30N0103076271000001823603")));

        debtPositionsApiApi.createPosition()
                .organizationfiscalcodePath(organizationFiscalCode)
                .toPublishQuery(true)
                .body(request)
                .executeAs(Function.identity());

        return iuv;
    }

    private String generateRandomIuv() {
        return RandomNumericGenerator.generate(17);
    }
}
