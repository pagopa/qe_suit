package it.pagopa.infrastructure.channel;

public interface ChannelPlugin<C extends Enum<C>> {
    boolean supports(C channel);
}