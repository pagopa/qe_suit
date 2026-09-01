package it.pagopa.infrastructure.cucumber.hook.channel;

import it.pagopa.application.ChannelKind;

public class ChannelScenarioContext<C extends Enum<C> & ChannelKind> {

    private ChannelConfig<C> config;

    public ChannelConfig<C> getConfig() {
        return config;
    }

    public void setConfig(ChannelConfig<C> config) {
        this.config = config;
    }
}