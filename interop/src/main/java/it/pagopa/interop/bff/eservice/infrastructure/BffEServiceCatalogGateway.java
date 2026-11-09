package it.pagopa.interop.bff.eservice.infrastructure;

import it.pagopa.infrastructure.template.action.strategy.PollingStrategy;
import it.pagopa.interop.bff.eservice.application.BffSearchEServiceCatalogCommand;
import it.pagopa.interop.common.eservice.application.EServiceCatalogGateway;
import it.pagopa.interop.common.eservice.application.EServiceCatalogPageValidator;
import it.pagopa.interop.common.eservice.application.EServiceCatalogPresenceCommand;
import it.pagopa.interop.common.eservice.application.command.SearchEServiceCatalogCommand;
import it.pagopa.interop.common.eservice.domain.EServiceCatalog;
import it.pagopa.interop.common.eservice.application.EServiceCatalogTraversal;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.utils.async.PollingUtils;
import java.time.Duration;
import java.util.List;
import java.util.OptionalInt;
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
    private final BffEServiceRequestFactory requestFactory;
    private final Duration presenceTimeout;
    private final Duration presenceInterval;

    @Autowired
    public BffEServiceCatalogGateway(
            BffEServiceCatalogRestClient restClient,
            BffEServiceCatalogMapper mapper,
            BffEServiceRequestFactory requestFactory
    ) {
        this(restClient, mapper, requestFactory, PRESENCE_TIMEOUT, PRESENCE_INTERVAL);
    }

    BffEServiceCatalogGateway(
            BffEServiceCatalogRestClient restClient,
            BffEServiceCatalogMapper mapper,
            BffEServiceRequestFactory requestFactory,
            Duration presenceTimeout,
            Duration presenceInterval
    ) {
        if (presenceTimeout.isNegative() || presenceTimeout.isZero()
                || presenceInterval.isNegative() || presenceInterval.isZero()) {
            throw new IllegalArgumentException("Catalog presence timeout and interval must be positive");
        }
        this.restClient = restClient;
        this.mapper = mapper;
        this.requestFactory = requestFactory;
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
    public List<EServiceCatalog> awaitPresence(
            SearchEServiceCatalogCommand command,
            EServiceCatalogPresenceCommand expected
    ) {
        BffSearchEServiceCatalogCommand bffCommand = requireBffCommand(command);
        long startedAt = System.nanoTime();
        AtomicReference<EServiceCatalogTraversal> lastAttempt = new AtomicReference<>();
        try {
            return PollingUtils.pollUntil(
                () -> {
                    EServiceCatalogTraversal attempt = scanForPresence(bffCommand, expected);
                    lastAttempt.set(attempt);
                    return attempt;
                },
                attempt -> expected.isSatisfiedBy(attempt.pages()),
                presenceTimeout,
                presenceInterval
            ).pages();
        } catch (IllegalStateException failure) {
            if (!(failure.getCause() instanceof ConditionTimeoutException)) {
                throw failure;
            }
            throw new IllegalStateException(
                    timeoutMessage(expected, lastAttempt.get(), bffCommand, startedAt), failure);
        }
    }

    @Override
    public boolean supports(Channel channel) {
        return channel == Channel.BFF;
    }

    // Every attempt is a fresh traversal: hits are never carried over from previous attempts.
    private EServiceCatalogTraversal scanForPresence(
            BffSearchEServiceCatalogCommand command, EServiceCatalogPresenceCommand expected
    ) {
        EServiceCatalogTraversal traversal = new EServiceCatalogTraversal(command.getOffset(), command.getLimit());
        OptionalInt next = traversal.accept(fetchPage(command));
        while (next.isPresent() && !expected.isSatisfiedBy(traversal.pages())) {
            next = traversal.accept(fetchPage(requireBffCommand(
                    requestFactory.searchEServiceCatalogCommandForOffset(command, next.getAsInt()))));
        }
        return traversal;
    }

    private EServiceCatalog fetchPage(BffSearchEServiceCatalogCommand command) {
        return EServiceCatalogPageValidator.requirePage(
                restClient.queryEServicesCatalog(command.getBffPayload())
                        .withoutPolling()
                        .assertStatusCode(200)
                        .map(mapper::toCatalog)
                        .get(),
                command.getOffset(),
                command.getLimit());
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
            EServiceCatalogTraversal lastAttempt,
            BffSearchEServiceCatalogCommand command,
            long startedAt
    ) {
        long elapsedMillis = Duration.ofNanos(System.nanoTime() - startedAt).toMillis();
        String attempt = lastAttempt == null ? "missing IDs " + expected.expectedIds() + ", last actual IDs not observed"
                : "missing IDs " + expected.missingIds(lastAttempt.pages())
                + ", last actual IDs " + lastAttempt.ids()
                + ", last scanned offsets " + lastAttempt.offsets()
                + ", last scan horizon " + lastAttempt.horizon()
                + (lastAttempt.isAnomalous() ? ", inconclusive: offset pagination anomalies "
                + lastAttempt.anomalies() : "");
        return "Timed out waiting for catalog presence across pages: expected IDs " + expected.expectedIds()
                + ", " + attempt
                + ", offset " + command.getOffset()
                + ", limit " + command.getLimit()
                + ", duration " + elapsedMillis + "ms";
    }
}
