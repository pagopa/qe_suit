package it.pagopa.interop.m2m.v3.infrastructure.auth;

import io.restassured.filter.Filter;
import it.pagopa.application.TestKind;
import it.pagopa.infrastructure.channel.CurrentChannel;
import it.pagopa.infrastructure.http.restassured.AcceptLanguageFilter;
import it.pagopa.application.context.TestContext;
import it.pagopa.infrastructure.http.restassured.TestPolicyFilterResolver;
import it.pagopa.interop.common.client.domain.Client;
import it.pagopa.interop.common.kernel.context.AuthSession;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.kernel.security.AccessToken;
import it.pagopa.kernel.security.DPoPProofService;
import it.pagopa.kernel.security.TokenType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.KeyPair;
import java.time.Instant;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

class M2Mv3AuthenticatedRequestFactoryTest {
    @Test
    @SuppressWarnings("unchecked")
    void obtainsANewTokenForEachRequest() throws Exception {
        ObjectProvider<TestContext> testContextProvider = mock(ObjectProvider.class);
        ObjectProvider<AuthSession> sessionProvider = mock(ObjectProvider.class);
        var testContext = mock(TestContext.class);
        var session = mock(AuthSession.class);
        var tokenProvider = mock(M2Mv3AccessTokenProvider.class);
        var client = Client.builder().build();
        var token = new M2Mv3AccessTokenContext(client, new AccessToken("token", TokenType.DPOP, 60),
                mock(KeyPair.class), Instant.now().plusSeconds(60));
        when(testContextProvider.getObject()).thenReturn(testContext);
        when(sessionProvider.getObject()).thenReturn(session);
        when(testContext.getCurrentTestKind()).thenReturn(TestKind.FLOW);
        when(session.getActor()).thenReturn(client);
        when(tokenProvider.create(client)).thenReturn(token);
        Filter filter = mock(Filter.class);
        ObjectProvider<CurrentChannel<Channel>> channelProvider = mock(ObjectProvider.class);
        CurrentChannel<Channel> channel = mock(CurrentChannel.class);
        when(channelProvider.getObject()).thenReturn(channel);
        when(channel.getCurrentLanguage()).thenReturn(Locale.ENGLISH);
        var factory = new M2Mv3AuthenticatedRequestFactory(testContextProvider, sessionProvider,
                new TestPolicyFilterResolver(filter, filter), tokenProvider, mock(DPoPProofService.class),
                channelProvider);
        ReflectionTestUtils.setField(factory, "basePath", "http://localhost");
        var first = (io.restassured.specification.FilterableRequestSpecification) factory.create().build();
        factory.create();
        var filters = first.getDefinedFilters();
        assertEquals(4, filters.size());
        assertInstanceOf(AcceptLanguageFilter.class, filters.get(0));
        assertInstanceOf(it.pagopa.infrastructure.http.restassured.DPoPAuthenticationFilter.class, filters.get(1));
        assertSame(filter, filters.get(2));
        assertInstanceOf(it.pagopa.infrastructure.http.restassured.HttpLoggingFilter.class, filters.get(3));
        var responseSpec = mock(io.restassured.specification.FilterableResponseSpecification.class);
        var context = mock(io.restassured.filter.FilterContext.class);
        filters.get(0).filter(first, responseSpec, context);
        assertEquals("en", first.getHeaders().getValue("Accept-Language"));
        verify(tokenProvider, times(2)).create(client);
        verify(session, times(2)).getActor();
        verifyNoMoreInteractions(session);
    }
}
