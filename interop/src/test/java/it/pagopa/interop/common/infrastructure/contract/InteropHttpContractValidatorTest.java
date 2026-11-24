package it.pagopa.interop.common.infrastructure.contract;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import it.pagopa.infrastructure.channel.CurrentChannel;
import it.pagopa.infrastructure.context.InMemoryCurrentChannel;
import it.pagopa.infrastructure.contract.http.HttpContractAuthentication;
import it.pagopa.infrastructure.contract.http.HttpContractPolicy;
import it.pagopa.infrastructure.contract.http.HttpContractStages;
import it.pagopa.infrastructure.contract.http.HttpContractValidator;
import it.pagopa.infrastructure.fuzzing.FuzzEngine;
import it.pagopa.infrastructure.fuzzing.FuzzCase;
import it.pagopa.infrastructure.fuzzing.FuzzMutation;
import it.pagopa.infrastructure.fuzzing.FuzzMutationKind;
import it.pagopa.infrastructure.fuzzing.FuzzScenario;
import it.pagopa.infrastructure.objectgraph.DefaultObjectGraphDecomposer;
import it.pagopa.infrastructure.objectgraph.JacksonObjectDecomposer;
import it.pagopa.infrastructure.objectgraph.NodePath;
import it.pagopa.infrastructure.objectgraph.ObjectGraphDecomposer;
import it.pagopa.interop.bff.infrastructure.config.BffApiContractConfig;
import it.pagopa.interop.common.client.domain.Client;
import it.pagopa.interop.common.infrastructure.context.CucumberUserSession;
import it.pagopa.interop.common.infrastructure.context.InMemoryUserSession;
import it.pagopa.interop.common.kernel.context.AuthSession;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.InteropActor;
import it.pagopa.interop.common.kernel.domain.InteropRole;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.m2m.kernel.domain.M2MRole;
import it.pagopa.interop.m2m.v3.infrastructure.auth.InMemoryM2Mv3Session;
import it.pagopa.interop.m2m.v3.infrastructure.auth.M2Mv3ApiClientProvider;
import it.pagopa.interop.m2m.v3.infrastructure.config.M2Mv3ApiContractConfig;
import it.pagopa.interop.suite.contract.*;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class InteropHttpContractValidatorTest {
    private static final Tenant TENANT = Tenant.COMUNE_DI_MILANO;
    private final HttpContractValidator delegate = mock(HttpContractValidator.class);
    private final AuthSession session = mock(AuthSession.class);
    private final InMemoryCurrentChannel<Channel> channel = new InMemoryCurrentChannel<>();
    private final ObjectProvider<CurrentChannel<Channel>> channelProvider = channelProvider();

    @Test
    void actorAndRolesAreAppliedOnlyWhenEachAuthenticationRuns() throws Exception {
        channel.setCurrentChannel(Channel.BFF);
        var validator = new InteropHttpContractValidator(delegate, session, Channel.BFF, channelProvider);
        var user = User.getTenantAdmin(TENANT);
        var stage = mock(HttpContractStages.ApiCallStage.class);
        when(delegate.apiCall(any(), any())).thenReturn(stage);
        assertThat(validator.as(TENANT, user).apiCall(Object::new)).isSameAs(stage);
        var actorAuthentication = captureAuthentication();
        verifyNoInteractions(session);
        actorAuthentication.authenticate();
        verify(session).set(user, TENANT);

        reset(delegate, session);
        validator.as(TENANT, UserRole.ADMIN).apiCall(Object::new);
        var roleAuthentication = captureAuthentication();
        verifyNoInteractions(session);
        roleAuthentication.authenticate();
        roleAuthentication.authenticate();
        verify(session, times(2)).set(UserRole.ADMIN, TENANT);

        reset(delegate, session);
        channel.setCurrentChannel(Channel.M2M_V3);
        var m2mValidator = new InteropHttpContractValidator(delegate, session, Channel.M2M_V3, channelProvider);
        m2mValidator.as(TENANT, M2MRole.M2M_ADMIN).apiCall(Object::new);
        captureAuthentication().authenticate();
        verify(session).set(M2MRole.M2M_ADMIN, TENANT);
        assertThatThrownBy(() -> InteropHttpContractValidator.class.getMethod("apiCall", Supplier.class))
                .isInstanceOf(NoSuchMethodException.class);
    }

    @Test
    void channelIsCheckedAtExecutionAndMismatchDoesNotChangeSession() {
        channel.setCurrentChannel(Channel.BFF);
        var validator = new InteropHttpContractValidator(delegate, session, Channel.M2M_V3, channelProvider);
        validator.as(TENANT, M2MRole.M2M).apiCall(Object::new);
        var authentication = captureAuthentication();
        assertThatThrownBy(authentication::authenticate)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("M2M_V3").hasMessageContaining("BFF");
        verifyNoInteractions(session);
        channel.setCurrentChannel(Channel.M2M_V3);
        authentication.authenticate();
        verify(session).set(M2MRole.M2M, TENANT);
    }

    @Test
    void actorAndRoleNullsFailAtDeclaration() {
        var validator = new InteropHttpContractValidator(delegate, session, Channel.BFF, channelProvider);
        assertThatNullPointerException().isThrownBy(() -> validator.as(null, UserRole.ADMIN));
        assertThatNullPointerException().isThrownBy(() -> validator.as(TENANT, (InteropRole) null));
        assertThatNullPointerException().isThrownBy(() -> validator.as(TENANT, (InteropActor) null));
    }

    @Test
    void realDynamicCasesAuthenticateAfterSetupAndRejectUnrestoredChannel() throws Throwable {
        var objectMapper = new ObjectMapper();
        var path = NodePath.fromPointer("/name");
        FuzzEngine fuzz = source -> List.of(
                new FuzzCase(path, new FuzzMutation(FuzzScenario.REPLACED_WITH_EMPTY_STRING,
                        FuzzMutationKind.REPLACE, ""), objectMapper.createObjectNode().put("name", "")),
                new FuzzCase(path, new FuzzMutation(FuzzScenario.REPLACED_WITH_NULL,
                        FuzzMutationKind.REPLACE, null), objectMapper.createObjectNode().putNull("name"))
        );
        var policy = HttpContractPolicy.builder().success(response -> {});
        for (var scenario : FuzzScenario.values()) {
            policy.scenario(scenario, response -> {});
        }
        var realDelegate = new HttpContractValidator(objectMapper, fuzz, fuzz,
                new DefaultObjectGraphDecomposer(new JacksonObjectDecomposer(objectMapper)), policy.build());
        var userSession = new InMemoryUserSession();
        var validator = new InteropHttpContractValidator(realDelegate, userSession, Channel.BFF, channelProvider);
        var events = new ArrayList<String>();
        channel.setCurrentChannel(Channel.BFF);
        var tests = validator.as(TENANT, UserRole.ADMIN)
                .apiCall(() -> {
                    events.add("operation");
                    assertThat(channel.getCurrentChannel()).isEqualTo(Channel.BFF);
                    assertThat(userSession.getTenant()).isEqualTo(TENANT);
                    assertThat(userSession.getRole()).isEqualTo(UserRole.ADMIN);
                    return new TestOperation();
                })
                .payload(() -> {
                    events.add("setup");
                    channel.setCurrentChannel(Channel.M2M_V3);
                    userSession.set(UserRole.ADMIN, Tenant.PAGO_PA);
                    channel.setCurrentChannel(Channel.BFF);
                    return new Payload("valid");
                }).tests().toList();
        assertThat(tests).hasSize(2);
        events.clear();
        for (var test : tests) {
            test.getExecutable().execute();
        }
        assertThat(events).containsExactly("setup", "operation", "setup", "operation");

        var count = new AtomicInteger();
        var invalid = validator.as(TENANT, UserRole.ADMIN)
                .apiCall(() -> {
                    events.add("unexpected operation");
                    return new TestOperation();
                })
                .payload(() -> {
                    if (count.incrementAndGet() > 1) channel.setCurrentChannel(Channel.M2M_V3);
                    return new Payload("valid");
                }).tests().findFirst().orElseThrow();
        assertThatThrownBy(() -> invalid.getExecutable().execute())
                .isInstanceOf(IllegalStateException.class).hasMessageContaining("Expected channel BFF");
        assertThat(events).doesNotContain("unexpected operation");
    }

    @Test
    void userSessionsRejectClientActorsWithoutChangingExistingIdentity() {
        for (AuthSession userSession : new AuthSession[]{new CucumberUserSession(), new InMemoryUserSession()}) {
            userSession.set(UserRole.ADMIN, TENANT);
            assertThatThrownBy(() -> userSession.set(Client.builder().build(), Tenant.PAGO_PA))
                    .isInstanceOf(IllegalArgumentException.class);
            assertThat(userSession.getActor()).isEqualTo(User.getTenantAdmin(TENANT));
            assertThat(userSession.getRole()).isEqualTo(UserRole.ADMIN);
            assertThat(userSession.getTenant()).isEqualTo(TENANT);
        }
    }

    @Test
    void incompatibleRolesAndActorsAreRejectedByTheirSessionsAtAuthentication() {
        var userSession = new InMemoryUserSession();
        userSession.set(UserRole.ADMIN, TENANT);
        channel.setCurrentChannel(Channel.BFF);
        var bff = new InteropHttpContractValidator(delegate, userSession, Channel.BFF, channelProvider);
        bff.as(TENANT, M2MRole.M2M).apiCall(Object::new);
        assertThatThrownBy(captureAuthentication()::authenticate).isInstanceOf(IllegalArgumentException.class);
        assertThat(userSession.getRole()).isEqualTo(UserRole.ADMIN);

        reset(delegate);
        var provider = mock(M2Mv3ApiClientProvider.class);
        var client = Client.builder().build();
        when(provider.getOrCreate(TENANT, M2MRole.M2M_ADMIN)).thenReturn(client);
        try (var context = new AnnotationConfigApplicationContext()) {
            context.registerBean(M2Mv3ApiClientProvider.class, () -> provider);
            context.refresh();
            var machineSession = new InMemoryM2Mv3Session(context.getBeanProvider(M2Mv3ApiClientProvider.class));
            machineSession.set(M2MRole.M2M_ADMIN, TENANT);
            channel.setCurrentChannel(Channel.M2M_V3);
            var m2m = new InteropHttpContractValidator(delegate, machineSession, Channel.M2M_V3, channelProvider);
            m2m.as(TENANT, UserRole.ADMIN).apiCall(Object::new);
            assertThatThrownBy(captureAuthentication()::authenticate).isInstanceOf(IllegalArgumentException.class);
            reset(delegate);
            m2m.as(TENANT, User.getTenantAdmin(TENANT)).apiCall(Object::new);
            assertThatThrownBy(captureAuthentication()::authenticate).isInstanceOf(IllegalArgumentException.class);
            assertThat(machineSession.getActor()).isSameAs(client);
            assertThat(machineSession.getRole()).isEqualTo(M2MRole.M2M_ADMIN);
        }
    }

    @Test
    void contractTestConstructorsSelectTheValidatorForTheirChannel() {
        var bffTests = List.of(BffAgreementContractTest.class, BffCertifiedAttributeContractTest.class,
                BffCertifiedDiscreteAttributeContractTest.class, BffClientContractTest.class,
                BffDeclaredAttributeContractTest.class, BffDelegationContractTest.class,
                BffEServiceContractTest.class, BffEServiceTemplateContractTest.class,
                BffProducerKeychainContractTest.class, BffPurposeContractTest.class,
                BffPurposeTemplateContractTest.class, BffVerifiedAttributeContractTest.class);
        var m2mTests = List.of(M2Mv3AgreementContractTest.class, M2Mv3EServiceTemplateInterfaceContractTest.class);
        bffTests.forEach(test -> assertValidatorQualifier(test, "bffInteropHttpContractValidator"));
        m2mTests.forEach(test -> assertValidatorQualifier(test, "m2mV3InteropHttpContractValidator"));
    }

    @Test
    void configuredValidatorsUseTheirOwnDelegateSessionAndExpectedChannel() {
        try (var context = new AnnotationConfigApplicationContext()) {
            var userSession = new InMemoryUserSession();
            var clientProvider = mock(M2Mv3ApiClientProvider.class);
            var client = Client.builder().build();
            when(clientProvider.getOrCreate(TENANT, M2MRole.M2M_ADMIN)).thenReturn(client);
            context.registerBean("userAuthSession", AuthSession.class, () -> userSession);
            context.registerBean(M2Mv3ApiClientProvider.class, () -> clientProvider);
            context.registerBean("m2mV3AuthSession", AuthSession.class,
                    () -> new InMemoryM2Mv3Session(context.getBeanProvider(M2Mv3ApiClientProvider.class)));
            context.registerBean(CurrentChannel.class, () -> channel);
            context.registerBean(ObjectMapper.class, () -> new ObjectMapper());
            context.registerBean("payloadFuzzEngine", FuzzEngine.class, () -> source -> java.util.List.of());
            context.registerBean("pathParamsFuzzEngine", FuzzEngine.class, () -> source -> java.util.List.of());
            context.registerBean("queryParamsFuzzEngine", FuzzEngine.class, () -> source -> java.util.List.of());
            context.registerBean(ObjectGraphDecomposer.class, () -> mock(ObjectGraphDecomposer.class));
            context.register(BffApiContractConfig.class, M2Mv3ApiContractConfig.class);
            context.refresh();

            var bff = context.getBean("bffInteropHttpContractValidator", InteropHttpContractValidator.class);
            var m2m = context.getBean("m2mV3InteropHttpContractValidator", InteropHttpContractValidator.class);
            assertThat(ReflectionTestUtils.getField(bff, "delegate"))
                    .isSameAs(context.getBean("bffApiContract"));
            assertThat(ReflectionTestUtils.getField(m2m, "delegate"))
                    .isSameAs(context.getBean("m2mV3ApiContractValidator"));
            assertThat(ReflectionTestUtils.getField(bff, "authSession")).isSameAs(userSession);
            assertThat(ReflectionTestUtils.getField(m2m, "authSession"))
                    .isSameAs(context.getBean("m2mV3AuthSession"));
            assertThat(ReflectionTestUtils.getField(bff, "expectedChannel")).isEqualTo(Channel.BFF);
            assertThat(ReflectionTestUtils.getField(m2m, "expectedChannel")).isEqualTo(Channel.M2M_V3);
            assertThatThrownBy(() -> context.getBean(InteropHttpContractValidator.class))
                    .isInstanceOf(org.springframework.beans.factory.NoUniqueBeanDefinitionException.class);

            channel.setCurrentChannel(Channel.BFF);
            var bffStage = bff.as(TENANT, UserRole.ADMIN).apiCall(Object::new);
            ((HttpContractAuthentication) ReflectionTestUtils.getField(bffStage, "authentication")).authenticate();
            assertThat(userSession.getRole()).isEqualTo(UserRole.ADMIN);
            channel.setCurrentChannel(Channel.M2M_V3);
            var m2mStage = m2m.as(TENANT, M2MRole.M2M_ADMIN).apiCall(Object::new);
            ((HttpContractAuthentication) ReflectionTestUtils.getField(m2mStage, "authentication")).authenticate();
            assertThat(context.getBean("m2mV3AuthSession", AuthSession.class).getActor()).isSameAs(client);
            verify(clientProvider).getOrCreate(TENANT, M2MRole.M2M_ADMIN);
        }
    }

    private HttpContractAuthentication captureAuthentication() {
        var captor = ArgumentCaptor.forClass(HttpContractAuthentication.class);
        verify(delegate).apiCall(captor.capture(), any());
        return captor.getValue();
    }

    private static void assertValidatorQualifier(Class<?> testClass, String expected) {
        var parameters = testClass.getDeclaredConstructors()[0].getParameters();
        var validatorParameters = java.util.Arrays.stream(parameters)
                .filter(parameter -> parameter.getType() == InteropHttpContractValidator.class)
                .toList();
        assertThat(validatorParameters).hasSize(1);
        var qualifier = validatorParameters.get(0).getAnnotation(Qualifier.class);
        assertThat(qualifier).isNotNull();
        assertThat(qualifier.value()).isEqualTo(expected);
    }

    @SuppressWarnings("unchecked")
    private ObjectProvider<CurrentChannel<Channel>> channelProvider() {
        ObjectProvider<CurrentChannel<Channel>> provider = mock(ObjectProvider.class);
        when(provider.getObject()).thenReturn(channel);
        return provider;
    }

    record Payload(String name) {}

    public static class TestOperation {
        public TestOperation reqSpec(Consumer<RequestSpecBuilder> customizer) {
            customizer.accept(new RequestSpecBuilder());
            return this;
        }

        public <T> T execute(Function<Response, T> handler) {
            return handler.apply(mock(Response.class));
        }
    }
}
