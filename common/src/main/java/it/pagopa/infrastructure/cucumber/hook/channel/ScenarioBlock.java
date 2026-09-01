package it.pagopa.infrastructure.cucumber.hook.channel;

import it.pagopa.application.ChannelKind;

import java.util.List;

record ScenarioBlock<C extends Enum<C> & ChannelKind>(
        int startLine,
        int endLine,
        int keywordLine,
        List<ChannelConfig<C>> configs
) {}
