package it.pagopa.send.model;

import it.frontend.e2e.framework.core.capability.Capability;
import it.frontend.e2e.framework.web.model.location.Url;

public record PageInfo(Url location, Class<? extends Capability> pageClass){}
