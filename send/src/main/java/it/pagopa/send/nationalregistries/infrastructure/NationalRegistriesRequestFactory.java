package it.pagopa.send.nationalregistries.infrastructure;

import it.pagopa.send.generated.openapi.clients.nationalregistries.model.*;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Payload baseline delle 11 operazioni di PN-NationalRegistries, usati come punto di partenza dai
 * contract test: ogni richiesta e' valida per contratto, cosi' che un fallimento sia sempre
 * attribuibile alla mutazione applicata dal fuzzer e non al fixture.
 */
@Component
public class NationalRegistriesRequestFactory {

    private static final String TAX_ID = "RSSMRA85T10A562S";
    private static final String VAT_NUMBER = "12345678903";

    public String correlationId() {
        return UUID.randomUUID().toString();
    }

    public IPARequestBody ipaRequestBody() {
        return new IPARequestBody().filter(new TaxIdFilter().taxId("10959831008").correlationId(correlationId()));
    }

    public ADELegalRequestBody adeLegalRequestBody() {
        return new ADELegalRequestBody().filter(new ADELegalRequestBodyFilter()
                .taxId(TAX_ID)
                .vatNumber(VAT_NUMBER)
                .correlationId(correlationId()));
    }

    public InfoCamereLegalRequestBody infoCamereLegalRequestBody() {
        return new InfoCamereLegalRequestBody().filter(new InfoCamereLegalRequestBodyFilter()
                .taxId(TAX_ID)
                .vatNumber(VAT_NUMBER)
                .correlationId(correlationId()));
    }

    public InfoCamereLegalInstitutionsRequestBody infoCamereLegalInstitutionsRequestBody() {
        return new InfoCamereLegalInstitutionsRequestBody().filter(new TaxIdFilter().taxId(TAX_ID).correlationId(correlationId()));
    }

    public AddressRequestBody addressRequestBody() {
        return new AddressRequestBody().filter(new AddressRequestBodyFilter()
                .taxId(TAX_ID)
                .referenceRequestDate(OffsetDateTime.now())
                .correlationId(correlationId())
                .domicileType(AddressRequestBodyFilter.DomicileTypeEnum.DIGITAL));
    }

    public PhysicalAddressesRequestBody physicalAddressesRequestBody() {
        return new PhysicalAddressesRequestBody()
                .correlationId(correlationId())
                .referenceRequestDate(OffsetDateTime.now())
                .addresses(List.of(new RecipientAddressRequestBody()
                        .taxId(TAX_ID)
                        .recIndex(0)
                        .recipientType(RecipientAddressRequestBody.RecipientTypeEnum.PF)));
    }

    public CheckTaxIdRequestBody checkTaxIdRequestBody() {
        return new CheckTaxIdRequestBody().filter(new TaxIdFilter().taxId(TAX_ID).correlationId(correlationId()));
    }

    public GetAddressANPRRequestBody addressANPRRequestBody() {
        return new GetAddressANPRRequestBody().filter(new GetAddressANPRRequestBodyFilter()
                .taxId("PRTCAE90A01D086M")
                .referenceRequestDate(OffsetDateTime.now().toString())
                .requestReason("contract-test")
                .correlationId(correlationId()));
    }

    public GetDigitalAddressINADRequestBody digitalAddressINADRequestBody() {
        return new GetDigitalAddressINADRequestBody().filter(new GetDigitalAddressINADRequestBodyFilter()
                .taxId("RNORNO80A41F979F")
                .practicalReference(UUID.randomUUID().toString())
                .correlationId(correlationId()));
    }

    public GetDigitalAddressIniPECRequestBody digitalAddressIniPECRequestBody() {
        return new GetDigitalAddressIniPECRequestBody().filter(new GetDigitalAddressIniPECRequestBodyFilter()
                .taxId(TAX_ID)
                .correlationId(correlationId()));
    }

    public GetAddressRegistroImpreseRequestBody addressRegistroImpreseRequestBody() {
        return new GetAddressRegistroImpreseRequestBody().filter(new TaxIdFilter().taxId("12345678903").correlationId(correlationId()));
    }
}
