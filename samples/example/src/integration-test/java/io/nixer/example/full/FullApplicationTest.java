package io.nixer.example.full;

import java.util.Random;

import com.google.common.base.Joiner;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.nixer.nixerplugin.core.detection.config.AnomalyRulesProperties;
import io.nixer.nixerplugin.core.detection.filter.behavior.Behaviors;
import io.nixer.nixerplugin.core.login.metrics.LoginCounters;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.metrics.export.influx.InfluxMetricsExportAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.integration.test.matcher.MapContentMatchers;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.SmartRequestBuilder;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static io.nixer.example.LoginRequestBuilder.formLogin;
import static io.nixer.nixerplugin.core.detection.config.AnomalyRulesProperties.Name.useragent;
import static io.nixer.nixerplugin.core.detection.filter.RequestMetadata.USER_AGENT_FAILED_LOGIN_OVER_THRESHOLD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.springframework.http.HttpHeaders.USER_AGENT;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@EnableAutoConfiguration(exclude = InfluxMetricsExportAutoConfiguration.class)
public class FullApplicationTest {

    private static final String FAKE_USER_AGENT = "user-agent";
    private static final String BLACKLISTED_IP_V6 = "5555:5555:5555:5555:5555:5555:5555:5555";
    private static final String BLACKLISTED_IP_V4 = "5.5.5.5";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MeterRegistry meterRegistry;

    @Autowired
    private AnomalyRulesProperties ruleProperties;

    @AfterEach
    void tearDown() throws Exception {
        mockMvc.perform(logout());
    }

    @Test
    void shouldLoginSuccessfully() throws Exception {
        // @formatter:off
        loginSuccessfully();
        // @formatter:on
    }

    @Test
    void shouldSetFlagThatUserAgentOverThreshold() throws Exception {
        // @formatter:on
        for (int i = 0; i < ruleProperties.getFailedLoginThreshold().get(useragent).getThreshold() + 1; i++) {
            this.mockMvc.perform(formLogin().user("user").password("guess").build()
                    .header(USER_AGENT, FAKE_USER_AGENT)
                    .with(remoteAddress(randomIp())))
                    .andExpect(unauthenticated())
                    .andExpect(request().attribute(USER_AGENT_FAILED_LOGIN_OVER_THRESHOLD, false));
        }
        // @formatter:off

        this.mockMvc.perform(formLogin().user("user").password("guess").build()
                .header(USER_AGENT, FAKE_USER_AGENT))
                .andExpect(unauthenticated())
                .andExpect(request().attribute(USER_AGENT_FAILED_LOGIN_OVER_THRESHOLD, true));
    }

    @Test
    void loginUserAccessProtected() throws Exception {
        // @formatter:off
        final SmartRequestBuilder loginRequest = formLogin().user("user").password("user").build();
        MvcResult mvcResult = this.mockMvc.perform(loginRequest)
                .andExpect(authenticated()).andReturn();
        // @formatter:on

        MockHttpSession httpSession = (MockHttpSession) mvcResult.getRequest().getSession(false);

        // @formatter:off
        this.mockMvc.perform(get("/").session(httpSession))
                .andExpect(status().isOk());
        // @formatter:on
    }

    @Test
    void shouldReportLoginSuccessMetric() throws Exception {
        final Counter successLoginCounter = successLoginCounter();
        double successLoginCount = successLoginCounter.count();

        loginSuccessfully();

        assertThat(successLoginCounter.count()).isEqualTo(successLoginCount + 1);
    }

    @Test
    void shouldReportLoginFailedMetric() throws Exception {
        final Counter failLoginCounter = badPasswordLoginFailCounter();
        double failedLoginCount = failLoginCounter.count();

        loginFailure();

        assertThat(failLoginCounter.count()).isEqualTo(failedLoginCount + 1);
    }

    private Counter successLoginCounter() {
        return LoginCounters.LOGIN_SUCCESS.register(meterRegistry);
    }

    private Counter badPasswordLoginFailCounter() {
        return LoginCounters.LOGIN_FAILED_BAD_PASSWORD.register(meterRegistry);
    }

    @Test
    void shouldFailLoginFromBlacklistedIpv4() throws Exception {
        // @formatter:off
        this.mockMvc.perform(
                formLogin().user("user").password("fake").build()
                        .with(remoteAddress(BLACKLISTED_IP_V4)))
                .andExpect(isBlocked());
        // @formatter:on
    }

    @Test
    void shouldFailLoginFromBlacklistedIpv6() throws Exception {
        // @formatter:off
        this.mockMvc.perform(
                formLogin().user("user").password("fake").build()
                        .with(remoteAddress(BLACKLISTED_IP_V6)))
                .andExpect(isBlocked());
        // @formatter:on
    }

    @Test
    void behaviorsEndpointToReturnBehaviorsAndRules() throws Exception {
        this.mockMvc.perform(get("/actuator/behaviors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.behaviors", hasItems(Behaviors.CAPTCHA.name(), Behaviors.LOG.name())))
                .andExpect(jsonPath("$.rules", MapContentMatchers.hasKey("credentialStuffingActive")))
        ;
    }

    @Test
    void behaviorsEndpointToUpdateBehavior() throws Exception {
        final String newBehavior = "{ \"rule\": \"credentialStuffingActive\", \"behavior\": \"LOG\"}";
        this.mockMvc.perform(post("/actuator/behaviors")
                .content(newBehavior)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());

        this.mockMvc.perform(get("/actuator/behaviors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rules", MapContentMatchers.hasEntry("credentialStuffingActive", Behaviors.LOG.name())));
    }

    private RequestPostProcessor remoteAddress(String ip) {
        return request -> {
            request.setRemoteAddr(ip);
            return request;
        };
    }

    private String randomIp() {
        final Random random = new Random();

        return Joiner.on('.').join(
                random.nextInt(256),
                random.nextInt(256),
                random.nextInt(256),
                random.nextInt(256)
        );
    }

    private ResultActions loginSuccessfully() throws Exception {
        return this.mockMvc
                .perform(formLogin().user("user").password("user").build())
                .andExpect(authenticated());
    }

    private ResultActions loginFailure() throws Exception {
        return this.mockMvc
                .perform(formLogin().user("user").password("bad-password").build())
                .andExpect(unauthenticated());
    }

    private ResultMatcher isBlocked() {
        return redirectedUrl("/login?blockedError");
    }
}
