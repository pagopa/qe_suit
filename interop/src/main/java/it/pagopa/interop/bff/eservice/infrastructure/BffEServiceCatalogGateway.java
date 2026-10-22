package it.pagopa.interop.bff.eservice.infrastructure;

import it.pagopa.infrastructure.template.action.strategy.PollingStrategy;
import it.pagopa.interop.bff.eservice.application.BffSearchEServiceCatalogCommand;
import it.pagopa.interop.common.eservice.application.EServiceCatalogGateway;
import it.pagopa.interop.common.eservice.application.EServiceCatalogPageValidator;
import it.pagopa.interop.common.eservice.application.EServiceCatalogPresenceCommand;
import it.pagopa.interop.common.eservice.application.command.SearchEServiceCatalogCommand;
import it.pagopa.interop.common.eservice.domain.EServiceCatalog;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.utils.async.PollingUtils;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;
import org.awaitility.core.ConditionTimeoutException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BffEServiceCatalogGateway implements EServiceCatalogGateway {
    private static final Duration PRESENCE_TIMEOUT = Duration.ofSeconds(30);
    private static final Duration PRESENCE_INTERVAL = Duration.ofSeconds(1);

    private final BffEServiceCatalogRestClient restClient;
    private final BffEServiceCatalogMapper mapper;
    private final Duration presenceTimeout;
    private final Duration presenceInterval;

    @Autowired
    public BffEServiceCatalogGateway(
            BffEServiceCatalogRestClient restClient,
            BffEServiceCatalogMapper mapper
    ) {
        this(restClient, mapper, PRESENCE_TIMEOUT, PRESENCE_INTERVAL);
    }

    BffEServiceCatalogGateway(
            BffEServiceCatalogRestClient restClient,
            BffEServiceCatalogMapper mapper,
            Duration presenceTimeout,
            Duration presenceInterval
    ) {
        if (presenceTimeout.isNegative() || presenceTimeout.isZero()
                || presenceInterval.isNegative() || presenceInterval.isZero()) {
            throw new IllegalArgumentException("Catalog presence timeout and interval must be positive");
        }
        this.restClient = restClient;
        this.mapper = mapper;
        this.presenceTimeout = presenceTimeout;
        this.presenceInterval = presenceInterval;
    }

    @Override
    public EServiceCatalog search(SearchEServiceCatalogCommand command) {
        BffSearchEServiceCatalogCommand bffCommand = requireBffCommand(command);
        return restClient.queryEServicesCatalog(bffCommand.getBffPayload())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(mapper::toCatalog)
                .get();
    }

    @Override
    public EServiceCatalog awaitPresence(
            SearchEServiceCatalogCommand command,
            EServiceCatalogPresenceCommand expected
    ) {
        BffSearchEServiceCatalogCommand bffCommand = requireBffCommand(command);
        long startedAt = System.nanoTime();
        AtomicReference<EServiceCatalog> lastPage = new AtomicReference<>();
        try {
            return PollingUtils.pollUntil(
                () -> {
                    EServiceCatalog page = EServiceCatalogPageValidator.requirePage(
                        restClient.queryEServicesCatalog(bffCommand.getBffPayload())
                                .withoutPolling()
                                .assertStatusCode(200)
                                .map(mapper::toCatalog)
                                .get(),
                        bffCommand.getOffset(),
                        bffCommand.getLimit());
                    lastPage.set(page);
                    return page;
                },
                expected::isSatisfiedBy,
                presenceTimeout,
                presenceInterval
            );
        } catch (IllegalStateException failure) {
            if (!(failure.getCause() instanceof ConditionTimeoutException)) {
                throw failure;
            }
            throw new IllegalStateException(
                    timeoutMessage(expected, lastPage.get(), bffCommand, startedAt), failure);
        }
    }

    @Override
    public boolean supports(Channel channel) {
        return channel == Channel.BFF;
    }

    private static BffSearchEServiceCatalogCommand requireBffCommand(SearchEServiceCatalogCommand command) {
        if (!(command instanceof BffSearchEServiceCatalogCommand bffCommand)) {
            throw new IllegalArgumentException("Command must be an instance of BffSearchEServiceCatalogCommand");
        }
        if (bffCommand.getBffPayload().getOffset() == null || bffCommand.getBffPayload().getLimit() == null) {
            throw new IllegalArgumentException("Catalog offset and limit are required");
        }
        return bffCommand;
    }

    private static String timeoutMessage(
            EServiceCatalogPresenceCommand expected,
            EServiceCatalog lastPage,
            BffSearchEServiceCatalogCommand command,
            long startedAt
    ) {
        long elapsedMillis = Duration.ofNanos(System.nanoTime() - startedAt).toMillis();
        return "Timed out waiting for catalog presence: expected IDs " + expected.expectedIds()
                + ", missing IDs " + (lastPage == null ? expected.expectedIds() : expected.missingIds(lastPage))
                + ", last actual IDs " + (lastPage == null ? "not observed" : lastPage.getResults().stream()
                .map(EServiceCatalog.Entry::getId).toList())
                + ", offset " + command.getOffset()
                + ", limit " + command.getLimit()
                + ", duration " + elapsedMillis + "ms";
    }
}
