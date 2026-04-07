package it.frontend.e2e.framework.annotation.processor.domain_validation.utils.descriptor_parser;

import it.frontend.e2e.framework.core.meta.Descriptor;

import java.io.InputStream;

public interface DomainDescriptorParser {
    Descriptor parse(InputStream in) throws Exception;
}
