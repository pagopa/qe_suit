package it.pagopa.interop.common.infrastructure.cucumber.channel;

import java.util.List;

/**
 * Represents a single Scenario / Scenario Outline block within a feature's source text,
 * identified by 1-based line ranges, together with the list of {@link ChannelConfig}
 * effectively applicable to it (already resolved via {@link EffectiveChannelConfigResolver}).
 *
 * @param startLine   first line of the block (first tag line, or keyword line if no tags)
 * @param endLine     last line of the block (inclusive), trailing blank lines trimmed
 * @param keywordLine line containing the {@code Scenario:} / {@code Scenario Outline:} keyword
 * @param configs     effective channel configs for this scenario; empty means "no channel"
 */
record ScenarioBlock(int startLine, int endLine, int keywordLine, List<ChannelConfig> configs) {
}
