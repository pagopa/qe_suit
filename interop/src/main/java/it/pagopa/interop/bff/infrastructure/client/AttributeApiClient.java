package it.pagopa.interop.bff.infrastructure.client;

import it.pagopa.interop.common.contract.model.Attribute;
import it.pagopa.interop.generated.openapi.clients.bff.api.AttributesApi;
import it.pagopa.interop.common.contract.template.action.TestChain;
import it.pagopa.interop.common.contract.template.rest.RestService;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import java.util.UUID;
import java.util.List;
import java.io.File;

@Component
@RequiredArgsConstructor
public class AttributeApiClient extends RestService {

    private final AttributesApi api;
    private final AttributeMapper mapper;

    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.Attribute, Attribute> createCertifiedAttribute(it.pagopa.interop.generated.openapi.clients.bff.model.CertifiedAttributeSeed certifiedAttributeSeed) {
        return super.create(
            () -> api.createCertifiedAttributeWithHttpInfo(certifiedAttributeSeed),
            mapper::toDomain
        );
    }
    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.Attribute, Attribute> createVerifiedAttribute(it.pagopa.interop.generated.openapi.clients.bff.model.AttributeSeed attributeSeed) {
        return super.create(
            () -> api.createVerifiedAttributeWithHttpInfo(attributeSeed),
            mapper::toDomain
        );
    }
    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.Attribute, Attribute> createDeclaredAttribute(it.pagopa.interop.generated.openapi.clients.bff.model.AttributeSeed attributeSeed) {
        return super.create(
            () -> api.createDeclaredAttributeWithHttpInfo(attributeSeed),
            mapper::toDomain
        );
    }
    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.Attributes, Attribute> getAttributes(Integer limit, Integer offset, List<it.pagopa.interop.generated.openapi.clients.bff.model.AttributeKind> kinds, String q, String origin) {
        return super.readAll(
            () -> api.getAttributesWithHttpInfo(limit, offset, kinds, q, origin),
            mapper::toDomainList
        );
    }
    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.Attribute, Attribute> getAttributeById(UUID attributeId) {
        return super.read(
            () -> api.getAttributeByIdWithHttpInfo(attributeId),
            mapper::toDomain
        );
    }
    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.Attribute, Attribute> getAttributeByOriginAndCode(String origin, String code) {
        return super.read(
            () -> api.getAttributeByOriginAndCodeWithHttpInfo(origin, code),
            mapper::toDomain
        );
    }
}